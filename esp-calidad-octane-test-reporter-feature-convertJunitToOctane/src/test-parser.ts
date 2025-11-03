import { promises as fs } from 'fs'
import * as path from 'path'
import { parseString } from 'xml2js'
import { glob } from 'glob'
import * as core from '@actions/core'

export interface TestResult {
  name: string
  className?: string
  status: 'passed' | 'failed' | 'skipped'
  duration?: number
  errorMessage?: string
  stackTrace?: string
}

export class TestResultsParser {
  
  async parseFile(filePath: string): Promise<TestResult[]> {
    core.info(`📄 Parsing test results from: ${filePath}`)
    
    try {
      // Expandir patrones glob si es necesario
      const files = await glob(filePath)
      
      if (files.length === 0) {
        throw new Error(`No files found matching pattern: ${filePath}`)
      }

      const allResults: TestResult[] = []

      for (const file of files) {
        core.info(`   Processing file: ${file}`)
        const results = await this.parseSingleFile(file)
        allResults.push(...results)
        core.info(`   Found ${results.length} tests in ${path.basename(file)}`)
      }

      core.info(`📊 Total tests parsed: ${allResults.length}`)
      return allResults
    } catch (error) {
      core.error(`❌ Failed to parse test results: ${error}`)
      throw new Error(`Failed to parse test results: ${error}`)
    }
  }

  private async parseSingleFile(filePath: string): Promise<TestResult[]> {
    const content = await fs.readFile(filePath, 'utf8')
    const extension = path.extname(filePath).toLowerCase()

    switch (extension) {
      case '.xml':
        return this.parseXmlResults(content)
      case '.json':
        return this.parseJsonResults(content)
      default:
        // Intentar detectar el formato por contenido
        if (content.trim().startsWith('<')) {
          return this.parseXmlResults(content)
        } else if (content.trim().startsWith('{') || content.trim().startsWith('[')) {
          return this.parseJsonResults(content)
        } else {
          throw new Error(`Unsupported file format: ${extension}`)
        }
    }
  }

  private async parseXmlResults(content: string): Promise<TestResult[]> {
    return new Promise((resolve, reject) => {
      parseString(content, { trim: true }, (err, result) => {
        if (err) {
          reject(new Error(`XML parsing error: ${err.message}`))
          return
        }

        try {
          const tests: TestResult[] = []

          // Detectar formato (JUnit, TestNG, etc.)
          if (result.testsuite || result.testsuites) {
            tests.push(...this.parseJUnitXml(result))
          } else if (result['testng-results']) {
            tests.push(...this.parseTestNGXml(result))
          } else {
            core.warning('⚠️ Unknown XML format, attempting JUnit parsing')
            tests.push(...this.parseJUnitXml(result))
          }

          resolve(tests)
        } catch (error) {
          reject(new Error(`Failed to parse XML results: ${error}`))
        }
      })
    })
  }

  private parseJUnitXml(result: any): TestResult[] {
    const tests: TestResult[] = []

    // Manejar estructura JUnit (puede tener testsuite o testsuites)
    const testsuites = result.testsuites ? result.testsuites.testsuite || [] : [result.testsuite || result]
    const suitesToProcess = Array.isArray(testsuites) ? testsuites : [testsuites]

    for (const testsuite of suitesToProcess) {
      if (!testsuite) continue

      const testcases = testsuite.testcase || []
      const casesToProcess = Array.isArray(testcases) ? testcases : [testcases]

      for (const testcase of casesToProcess) {
        if (!testcase || !testcase.$) continue

        const test: TestResult = {
          name: testcase.$.name || 'Unknown Test',
          className: testcase.$.classname || testcase.$.class || testsuite.$.name,
          duration: parseFloat(testcase.$.time || '0') * 1000, // Convert to milliseconds
          status: 'passed' as const
        }

        // Determinar el status basado en los elementos hijos
        if (testcase.failure && testcase.failure.length > 0) {
          test.status = 'failed'
          test.errorMessage = testcase.failure[0].$.message || testcase.failure[0].$.type
          test.stackTrace = testcase.failure[0]._ || testcase.failure[0]
        } else if (testcase.error && testcase.error.length > 0) {
          test.status = 'failed'
          test.errorMessage = testcase.error[0].$.message || testcase.error[0].$.type
          test.stackTrace = testcase.error[0]._ || testcase.error[0]
        } else if (testcase.skipped && testcase.skipped.length > 0) {
          test.status = 'skipped'
          test.errorMessage = testcase.skipped[0].$.message || 'Test skipped'
        }

        tests.push(test)
      }
    }

    return tests
  }

  private parseTestNGXml(result: any): TestResult[] {
    const tests: TestResult[] = []
    
    try {
      const testngResults = result['testng-results']
      const suites = testngResults.suite || []
      const suitesToProcess = Array.isArray(suites) ? suites : [suites]

      for (const suite of suitesToProcess) {
        const testNodes = suite.test || []
        const testsToProcess = Array.isArray(testNodes) ? testNodes : [testNodes]

        for (const testNode of testsToProcess) {
          const classes = testNode.class || []
          const classesToProcess = Array.isArray(classes) ? classes : [classes]

          for (const classNode of classesToProcess) {
            const methods = classNode['test-method'] || []
            const methodsToProcess = Array.isArray(methods) ? methods : [methods]

            for (const method of methodsToProcess) {
              if (!method.$ || method.$['is-config'] === 'true') continue

              const test: TestResult = {
                name: method.$.name || 'Unknown Test',
                className: classNode.$.name,
                duration: parseFloat(method.$['duration-ms'] || '0'),
                status: method.$.status === 'PASS' ? 'passed' : 
                        method.$.status === 'SKIP' ? 'skipped' : 'failed'
              }

              // Añadir información de error si existe
              if (method.exception && method.exception.length > 0) {
                test.errorMessage = method.exception[0].$.class
                test.stackTrace = method.exception[0].message ? method.exception[0].message[0] : undefined
              }

              tests.push(test)
            }
          }
        }
      }
    } catch (error) {
      core.warning(`⚠️ Failed to parse TestNG XML, falling back to JUnit format: ${error}`)
      return this.parseJUnitXml(result)
    }

    return tests
  }

  private parseJsonResults(content: string): TestResult[] {
    try {
      const data = JSON.parse(content)
      const tests: TestResult[] = []

      // Detectar formato JSON (Jest, Mocha, custom, etc.)
      if (data.testResults && Array.isArray(data.testResults)) {
        // Formato Jest
        tests.push(...this.parseJestJson(data))
      } else if (data.tests && Array.isArray(data.tests)) {
        // Formato Mocha JSON
        tests.push(...this.parseMochaJson(data))
      } else if (Array.isArray(data)) {
        // Array directo de tests
        tests.push(...this.parseGenericJsonArray(data))
      } else {
        core.warning('⚠️ Unknown JSON format, attempting generic parsing')
        tests.push(...this.parseGenericJson(data))
      }

      return tests
    } catch (error) {
      throw new Error(`JSON parsing error: ${error}`)
    }
  }

  private parseJestJson(data: any): TestResult[] {
    const tests: TestResult[] = []

    for (const testResult of data.testResults) {
      const filePath = testResult.name || 'unknown'
      
      for (const assertionResult of testResult.assertionResults || []) {
        const test: TestResult = {
          name: assertionResult.title || assertionResult.fullName,
          className: path.basename(filePath, path.extname(filePath)),
          duration: assertionResult.duration,
          status: assertionResult.status === 'passed' ? 'passed' :
                  assertionResult.status === 'pending' || assertionResult.status === 'todo' ? 'skipped' : 'failed'
        }

        if (assertionResult.failureMessages && assertionResult.failureMessages.length > 0) {
          test.errorMessage = assertionResult.failureMessages[0].split('\n')[0]
          test.stackTrace = assertionResult.failureMessages[0]
        }

        tests.push(test)
      }
    }

    return tests
  }

  private parseMochaJson(data: any): TestResult[] {
    const tests: TestResult[] = []

    for (const test of data.tests || []) {
      const testResult: TestResult = {
        name: test.title || test.fullTitle || 'Unknown Test',
        className: test.parent?.title || 'Unknown Suite',
        duration: test.duration || 0,
        status: test.state === 'passed' ? 'passed' :
                test.pending ? 'skipped' : 'failed'
      }

      if (test.err) {
        testResult.errorMessage = test.err.message
        testResult.stackTrace = test.err.stack
      }

      tests.push(testResult)
    }

    return tests
  }

  private parseGenericJsonArray(data: any[]): TestResult[] {
    return data.map(item => ({
      name: item.name || item.title || 'Unknown Test',
      className: item.className || item.suite || item.describe || 'Unknown Class',
      duration: item.duration || item.time || 0,
      status: this.normalizeStatus(item.status || item.result || 'unknown'),
      errorMessage: item.error || item.errorMessage || item.failure,
      stackTrace: item.stackTrace || item.stack
    }))
  }

  private parseGenericJson(data: any): TestResult[] {
    // Intentar extraer tests de cualquier estructura JSON
    const tests: TestResult[] = []
    
    const findTests = (obj: any, path: string = ''): void => {
      if (typeof obj !== 'object' || obj === null) return

      // Si parece un test individual
      if (obj.name || obj.title) {
        tests.push({
          name: obj.name || obj.title || 'Unknown Test',
          className: obj.className || obj.suite || path || 'Unknown Class',
          duration: obj.duration || obj.time || 0,
          status: this.normalizeStatus(obj.status || obj.result || 'unknown'),
          errorMessage: obj.error || obj.errorMessage,
          stackTrace: obj.stackTrace || obj.stack
        })
        return
      }

      // Buscar recursivamente
      for (const [key, value] of Object.entries(obj)) {
        if (Array.isArray(value)) {
          value.forEach((item, index) => findTests(item, `${path}.${key}[${index}]`))
        } else if (typeof value === 'object') {
          findTests(value, path ? `${path}.${key}` : key)
        }
      }
    }

    findTests(data)
    return tests
  }

  private normalizeStatus(status: string): 'passed' | 'failed' | 'skipped' {
    const normalizedStatus = status.toLowerCase()
    
    if (['passed', 'pass', 'success', 'ok', 'passed'].includes(normalizedStatus)) {
      return 'passed'
    } else if (['skipped', 'skip', 'pending', 'ignored', 'todo'].includes(normalizedStatus)) {
      return 'skipped'
    } else {
      return 'failed'
    }
  }
}
