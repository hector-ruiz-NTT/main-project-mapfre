import axios, { AxiosInstance, AxiosRequestConfig } from 'axios'
import * as core from '@actions/core'
import * as https from 'https'

import { parseString } from 'xml2js';
import { create } from 'xmlbuilder2';

export interface OctaneConfig {
  url: string
  sharedSpace: string
  workspace: string
  clientId: string
  clientSecret: string
  timeout?: number
  skipSslVerify?: boolean
}

export interface TestResult {
  name: string
  className?: string
  status: 'passed' | 'failed' | 'skipped'
  duration?: number
  errorMessage?: string
  stackTrace?: string
}

export interface CommitInfo {
  commitSha: string
  branch: string
  author: string
  message: string
}

export class OctaneClient {
  private client: AxiosInstance
  private config: OctaneConfig
  private sessionCookies: string = '' // Para manejo manual de cookies como Python

  constructor(config: OctaneConfig) {
    this.config = config

    // Asegurar que la URL termine con /
    if (!config.url.endsWith('/')) {
      this.config.url = config.url + '/'
    }

    core.info(`🔧 Octane Client Configuration:`)
    core.info(`   • Server URL: ${this.config.url}`)
    core.info(`   • Shared Space: ${this.config.sharedSpace}`)
    core.info(`   • Workspace: ${this.config.workspace}`)
    core.info(`   • Timeout: ${this.config.timeout || 60}s`)
    core.info(`   • Skip SSL Verify: ${this.config.skipSslVerify || false}`)

    const axiosConfig: AxiosRequestConfig = {
      baseURL: this.config.url,
      timeout: (config.timeout || 60) * 1000,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'ALM-OCTANE-TECH-PREVIEW': 'true',
        'ALM-OCTANE-PRIVATE': 'true'  // Esta cabecera es clave según el código de Python
      }
      // SIN withCredentials - Python no lo usa, manejamos cookies manualmente
    }

    if (config.skipSslVerify) {
      axiosConfig.httpsAgent = new (require('https').Agent)({
        rejectUnauthorized: false
      })
      core.info('⚠️ SSL certificate verification is disabled')
    }

    this.client = axios.create(axiosConfig)
    // Interceptor solo para logging - no para manejo manual de cookies
    this.setupLoggingInterceptor()
  }

  private setupLoggingInterceptor(): void {
    // Request interceptor - añadir cookies manualmente como Python requests.Session()
    this.client.interceptors.request.use(
      (config) => {
        if (this.sessionCookies) {
          config.headers = config.headers || {}
          config.headers['Cookie'] = this.sessionCookies
          core.info(`[Request] 🍪 Sending cookies: ${this.sessionCookies.substring(0, 100)}...`)
        } else {
          core.warning(`[Request] ⚠️ No cookies being sent`)
        }
        return config
      },
      (error) => Promise.reject(error)
    )

    // Response interceptor - capturar y ACUMULAR cookies como Python requests.Session()
    this.client.interceptors.response.use(
      (response) => {
        const setCookieHeaders = response.headers['set-cookie']
        if (setCookieHeaders && setCookieHeaders.length > 0) {
          // Parse existing cookies into a map
          const existingCookies = new Map<string, string>()
          if (this.sessionCookies) {
            this.sessionCookies.split('; ').forEach(cookie => {
              const [name, value] = cookie.split('=')
              if (name && value) {
                existingCookies.set(name, value)
              }
            })
          }

          // Add/update new cookies
          setCookieHeaders.forEach(cookieHeader => {
            const [nameValue] = cookieHeader.split(';')
            const [name, value] = nameValue.split('=')
            if (name && value) {
              existingCookies.set(name, value)
            }
          })

          // Rebuild cookie string with all cookies
          const allCookies = Array.from(existingCookies.entries()).map(([name, value]) => `${name}=${value}`)
          this.sessionCookies = allCookies.join('; ')

          core.info(`[Response] 🍪 Session cookies updated: ${setCookieHeaders.length} new, ${existingCookies.size} total`)
          core.info(`[Response] Cookie string: ${this.sessionCookies.substring(0, 100)}...`)
        }
        return response
      },
      (error) => Promise.reject(error)
    )
  }

  async authenticate(): Promise<void> {
    try {
      const authUrl = '/authentication/sign_in'
      const fullAuthUrl = `${this.config.url}authentication/sign_in`

      core.info(`[Auth] Authenticating with Octane...`)
      core.info(`[Auth] Full URL: ${fullAuthUrl}`)
      core.info(`[Auth] Payload: client_id=${this.config.clientId}, client_secret=***`)

      const response = await this.client.post(authUrl, {
        client_id: this.config.clientId,
        client_secret: this.config.clientSecret
      })

      if (response.status === 200) {
        core.info(`[Auth] Authentication successful. Status: ${response.status}`)
        core.info(`[Auth] Response Headers: Content-Type: ${response.headers['content-type']}, X-Request-ID: ${response.headers['x-request-id']}`)

        // Verificar si recibimos cookies de sesión
        const setCookieHeaders = response.headers['set-cookie']
        if (setCookieHeaders) {
          core.info(`[Auth] ✅ Session cookies received: ${setCookieHeaders.length} cookies`)
          setCookieHeaders.forEach((cookie, index) => {
            const cookieName = cookie.split('=')[0]
            core.info(`[Auth] Cookie ${index + 1}: ${cookieName}=***`)
          })
        } else {
          core.warning(`[Auth] ⚠️ No session cookies received in response`)
        }

        // Verificar el contenido de la respuesta
        if (response.data) {
          core.info(`[Auth] Response data keys: ${Object.keys(response.data).join(', ')}`)
        }
      } else {
        throw new Error(`Authentication failed with status ${response.status}`)
      }
    } catch (error: any) {
      const statusCode = error.response?.status
      const errorMessage = this.getErrorMessage(error)

      core.error(`[Auth] Error authenticating with Octane: ${errorMessage}`)
      if (error.response) {
        core.error(`[Auth] Response Status: ${error.response.status}`)
        core.error(`[Auth] Response Content: ${error.response.data}`)
      }

      throw error
    }
  }

  async getOrCreateCiServer(): Promise<{ ciServerId: string, ciServerInstanceId: string }> {
    const instanceId = `GHA-${process.env.GITHUB_REPOSITORY?.split('/')[0] || 'unknown'}`
    core.info(`[CI Server] Attempting to get or create CI server with instance_id: ${instanceId}`)

    const apiBaseUrl = `/api/shared_spaces/${this.config.sharedSpace}/workspaces/${this.config.workspace}`
    const ciServersUrl = `${apiBaseUrl}/ci_servers`

    const query = `instance_id EQ '${instanceId}'`
    const fullQueryUrl = `${this.config.url}api/shared_spaces/${this.config.sharedSpace}/workspaces/${this.config.workspace}/ci_servers?query="${query}"`

    core.info(`[CI Server] Querying for existing CI server: ${ciServersUrl}?query="${query}"`)
    core.info(`[CI Server] Full URL: ${fullQueryUrl}`)

    try {
      const response = await this.client.get(`${ciServersUrl}?query="${query}"`)
      core.info(`[CI Server] Query successful. Status: ${response.status}`)
      core.info(`[CI Server] Response Headers: Content-Type: ${response.headers['content-type']}, X-Request-ID: ${response.headers['x-request-id']}`)

      const data = response.data
      if (data.total_count && data.total_count > 0) {
        const serverId = data.data[0].id
        core.info(`[CI Server] Found existing CI server with id: ${serverId}`)
        return { ciServerId: serverId, ciServerInstanceId: instanceId }
      }

      core.info('[CI Server] CI server not found, creating a new one...')
      const createPayload = {
        data: [{
          name: instanceId,
          instance_id: instanceId,
          server_type: 'github_actions',
          url: process.env.GITHUB_SERVER_URL
        }]
      }

      core.info(`[CI Server] Creating new CI server with payload: ${JSON.stringify(createPayload, null, 2)}`)
      const createResponse = await this.client.post(ciServersUrl, createPayload)
      core.info(`[CI Server] Creation successful. Status: ${createResponse.status}`)
      core.info(`[CI Server] Response Headers: Content-Type: ${createResponse.headers['content-type']}, X-Request-ID: ${createResponse.headers['x-request-id']}`)

      const serverId = createResponse.data.data[0].id
      core.info(`[CI Server] Created new CI server with id: ${serverId}`)
      return { ciServerId: serverId, ciServerInstanceId: instanceId }

    } catch (error: any) {
      core.error(`[CI Server] Error getting or creating CI server: ${error.message}`)
      if (error.response) {
        core.error(`[CI Server] Response Status: ${error.response.status}`)
        core.error(`[CI Server] Response Content: ${error.response.data}`)
      }
      throw error
    }
  }

  async getOrCreatePipeline(ciServerId: string, ciServerInstanceId: string, desiredJobCiId: string): Promise<{ pipelineId: string, jobCiId: string }> {
    const pipelineName = process.env.PIPELINE_NAME || 'default_pipeline'
    core.info(`[Pipeline] Getting or creating pipeline: ${pipelineName} for CI Server ID: ${ciServerId}, Instance ID: ${ciServerInstanceId}`)

    const apiBaseUrl = `/api/shared_spaces/${this.config.sharedSpace}/workspaces/${this.config.workspace}`
    const pipelinesUrl = `${apiBaseUrl}/pipelines`

    const query = `name EQ '${pipelineName}'`
    try {
      const url = `${pipelinesUrl}?fields=id,name,root_job_ci_id,ci_server&query="${query}"`
      const response = await this.client.get(url)
      core.info(`[Pipeline] Query successful. Status: ${response.status}`)
      core.info(`[Pipeline] Response Headers: Content-Type: ${response.headers['content-type']}, X-Request-ID: ${response.headers['x-request-id']}`)

      const pipelinesData = response.data
      for (const pipeline of pipelinesData.data || []) {
        const server = pipeline.ci_server
        if (server && server.id === ciServerId) {
          core.info(`[Pipeline] Found existing pipeline with id: ${pipeline.id}`)
          const existingJobId = pipeline.root_job_ci_id || desiredJobCiId
          if (pipeline.root_job_ci_id) {
            core.info(`[Pipeline] Using existing root_job_ci_id from Octane: ${pipeline.root_job_ci_id}`)
          } else {
            core.info(`[Pipeline] Octane did not return root_job_ci_id; falling back to: ${desiredJobCiId}`)
          }
          return { pipelineId: pipeline.id, jobCiId: existingJobId }
        }
      }

      core.info('[Pipeline] Pipeline not found, creating a new one...')
    } catch (error: any) {
      core.error(`[Pipeline] Error querying pipelines: ${error.message}`)
      if (error.response) {
        core.error(`[Pipeline] Response Status: ${error.response.status}`)
        core.error(`[Pipeline] Response Content: ${error.response.data}`)
      }
    }

    const jobCiId = desiredJobCiId
    const createPayload = {
      data: [{
        name: pipelineName,
        jobs: [{
          jobCiId: jobCiId,
          name: pipelineName || 'Default Job'
        }],
        root_job_ci_id: jobCiId,
        server_ci_id: ciServerInstanceId,
        ci_server: {
          type: 'ci_server',
          id: ciServerId
        },
        type: 'pipeline'
      }]
    }

    core.info(`[Pipeline] Creating new pipeline with payload: ${JSON.stringify(createPayload, null, 2)}`)
    try {
      const createResponse = await this.client.post(pipelinesUrl, createPayload)
      core.info(`[Pipeline] Creation successful. Status: ${createResponse.status}`)
      core.info(`[Pipeline] Response Headers: Content-Type: ${createResponse.headers['content-type']}, X-Request-ID: ${createResponse.headers['x-request-id']}`)

      const pipelineId = createResponse.data.data[0].id
      core.info(`[Pipeline] Created new pipeline with id: ${pipelineId}`)
      return { pipelineId, jobCiId }
    } catch (error: any) {
      core.error(`[Pipeline] Error creating pipeline: ${error.message}`)
      if (error.response) {
        core.error(`[Pipeline] Response Status: ${error.response.status}`)
        core.error(`[Pipeline] Response Content: ${error.response.data}`)
      }
      throw error
    }
  }

  async sendCiEvent(ciServerInstanceId: string, jobCiId: string, pipelineId: string, eventType: 'started' | 'finished', testResultsPath: string): Promise<void> {
    core.info(`Sending '${eventType}' event for pipeline ${pipelineId}`)
    const eventsUrl = `/internal-api/shared_spaces/${this.config.sharedSpace}/analytics/ci/events`

    const eventTime = Date.now()
    const buildId = process.env.GITHUB_RUN_ID || '1'

    core.info(`[CI Event] Build ID: ${buildId}`)
    core.info(`[CI Event] Job CI ID: ${jobCiId}`)
    core.info(`[CI Event] CI Server Instance: ${ciServerInstanceId}`)
    core.info(`[CI Event] Event Time: ${new Date(eventTime).toISOString()}`)

    const event: any = {
      eventType: eventType,
      project: jobCiId,
      buildCiId: buildId,
      number: buildId,
      startTime: eventTime,
      causes: []
    }

    const pipelineName = process.env.PIPELINE_NAME
    if (pipelineName) {
      event.projectDisplayName = pipelineName
    }

    if (eventType === 'finished') {

        // *******************************************************************
        // AÑADIDO: Retraso de 30 segundos para dar tiempo al servidor de Octane a procesar los resultados
        // (Sólo aplicamos el delay si el evento es 'finished' y se asume que los resultados ya se han enviado)
              core.info('[CI Event] Delaying finished event by 30 seconds to allow test results to process...')
              await this.delay(30000)
              core.info('[CI Event] Delay complete. Sending finished event.')
        // *******************************************************************


//       core.info(`eventType === finished`)
//       event.result = 'failure'  // Or determine from workflow status

//       // Llama a la nueva función
      const finalStatus = await this.determinePipelineResult(testResultsPath);
      event.result = finalStatus; //

//       event.duration = this.calculatePipelineDuration(testResultsPath);
      event.duration = 30000 // Default duration
    }

    const payload = {
      server: {
        instanceId: ciServerInstanceId,
        type: 'github_actions',
        url: process.env.GITHUB_SERVER_URL
      },
      events: [event]
    }

    core.info(`[CI Event] Full payload: ${JSON.stringify(payload, null, 2)}`)

    try {
      const response = await this.client.put(eventsUrl, payload)
      core.info(`Successfully sent '${eventType}' event.`)
      core.info(`[CI Event] Response status: ${response.status}`)
    } catch (error: any) {
      core.error(`Error sending CI event: ${error.message}`)
      if (error.response) {
        core.error(`Response content: ${error.response.data}`)
      }
      throw error
    }
  }

  async sendTestResults(ciServerInstanceId: string, jobCiId: string, pipelineId: string, testResultsPath: string): Promise<void> {
    core.info(`Sending test results from: ${testResultsPath}`)

    const fs = require('fs')
    if (!fs.existsSync(testResultsPath)) {
      core.error(`Test results file not found at: ${testResultsPath}`)
      return
    }

    if (fs.statSync(testResultsPath).size === 0) {
      core.error(`Test results file is empty at: ${testResultsPath}`)
      return
    }

    const xmlContent = fs.readFileSync(testResultsPath, 'utf-8')

    // Define el buildId limpio aquí para reuso
        const ciBuildId = process.env.GITHUB_RUN_ID || '1'; // Esto es el '87912345'


    // Validate XML
    const ET = require('xml2js')
    try {
      await ET.parseStringPromise(xmlContent)
      core.info('XML content is valid JUnit. Converting to Octane format...')
    } catch (e) {
      core.error(`Error: Test results XML content is malformed: ${e}`)
      core.error(`XML content (first 500 chars):\n${xmlContent.substring(0, 500)}`)
      return
    }

    core.info(`Test results XML content (first 500 chars):\n${xmlContent.substring(0, 500)}`)

    let octaneXml: string
    try {
//       octaneXml = this.convertJunitToOctane(xmlContent, ciServerInstanceId, jobCiId, process.env.GITHUB_RUN_ID || '1')
      // PASAR EL ID LIMPIO (ciBuildId)
      octaneXml = this.convertJunitToOctane(xmlContent, ciServerInstanceId, jobCiId, ciBuildId)
    } catch (conversionError: any) {
      core.error(`Error converting JUnit XML to Octane format: ${conversionError.message}`)
      throw conversionError
    }

//     const testResultsUrl =
//       `/internal-api/shared_spaces/${this.config.sharedSpace}/analytics/ci/test-results` +
//       `?skip-errors=true&instance-id=${ciServerInstanceId}` +
//       `&job-ci-id=${jobCiId}&build-ci-id=${process.env.GITHUB_RUN_ID || '1'}`

    const testResultsUrl =
            `/internal-api/shared_spaces/${this.config.sharedSpace}/analytics/ci/test-results` +
            `?skip-errors=true&instance-id=${ciServerInstanceId}` +
            `&job-ci-id=${jobCiId}&build-ci-id=${ciBuildId}` // USAR EL ID LIMPIO EN LA URL

    try {
      const response = await this.client.post(testResultsUrl, octaneXml, {
        headers: {
          'Content-Type': 'application/xml'
        }
      })
      core.info('Successfully sent test results.')
    } catch (error: any) {
      core.error(`Error sending test results: ${error.message}`)
      if (error.response) {
        core.error(`Response content: ${error.response.data}`)
      }
      throw error
    }
  }


  async registerCiServer(serverUrl: string, serverType: string = 'GitHub Actions'): Promise<string | null> {
    try {
      core.info(`🔧 Registering CI Server: ${serverType}`)
      core.info(`🔗 Server URL: ${serverUrl}`)

      // Buscar si ya existe el CI Server
      const searchResponse = await this.client.get(
        `/api/shared_spaces/${this.config.sharedSpace}/workspaces/${this.config.workspace}/ci_servers`,
        {
          params: {
            query: `"name EQ '${serverType}'"`,
            fields: 'id,name,url'
          }
        }
      )

      if (searchResponse.data.data && searchResponse.data.data.length > 0) {
        const existingServer = searchResponse.data.data[0]
        core.info(`✅ CI Server already exists: ${existingServer.id}`)
        return existingServer.id
      }

      // Crear nuevo CI Server
      const createResponse = await this.client.post(
        `/api/shared_spaces/${this.config.sharedSpace}/workspaces/${this.config.workspace}/ci_servers`,
        {
          data: [{
            name: serverType,
            url: serverUrl,
            server_type: 'github_actions'
          }]
        }
      )

      const serverId = createResponse.data.data[0].id
      core.info(`✅ CI Server created with ID: ${serverId}`)
      return serverId
    } catch (error: any) {
      const statusCode = error.response?.status
      const errorMessage = this.getErrorMessage(error)

      core.error(`❌ Failed to register CI Server with status ${statusCode}: ${errorMessage}`)

      if (statusCode === 401) {
        core.warning('🚨 Client lacks permissions to manage CI servers - this is optional')
        core.warning('💡 You can continue without CI server registration')
        return null
      } else if (statusCode === 403) {
        core.warning('🚨 Forbidden - Client may not have access to create CI servers - this is optional')
        return null
      } else if (statusCode === 404) {
        core.error('🚨 Not Found - Check shared space and workspace IDs')
        core.error(`  • Shared Space: ${this.config.sharedSpace}`)
        core.error(`  • Workspace: ${this.config.workspace}`)
        return null
      }

      core.warning('⚠️ CI Server registration failed, but continuing with test upload...')
      return null
    }
  }

  async registerPipeline(pipelineName: string, serverUrl: string, ciServerId: string | null): Promise<string | null> {
    try {
      // Buscar si ya existe el Pipeline
      const searchResponse = await this.client.get(
        `/api/shared_spaces/${this.config.sharedSpace}/workspaces/${this.config.workspace}/pipelines`,
        {
          params: {
            query: `"name EQ '${pipelineName}'"`,
            fields: 'id,name,ci_server'
          }
        }
      )

      if (searchResponse.data.data && searchResponse.data.data.length > 0) {
        const existingPipeline = searchResponse.data.data[0]
        core.info(`✅ Pipeline already exists: ${existingPipeline.id}`)
        return existingPipeline.id
      }

      // Crear nuevo Pipeline (solo si tenemos ciServerId)
      if (!ciServerId) {
        core.warning('⚠️ Cannot create pipeline without CI Server ID - skipping pipeline registration')
        return null
      }

      const createResponse = await this.client.post(
        `/api/shared_spaces/${this.config.sharedSpace}/workspaces/${this.config.workspace}/pipelines`,
        {
          data: [{
            name: pipelineName,
            url: serverUrl,
            ci_server: {
              type: 'ci_server',
              id: ciServerId
            }
          }]
        }
      )

      const pipelineId = createResponse.data.data[0].id
      core.info(`✅ Pipeline created with ID: ${pipelineId}`)
      return pipelineId
    } catch (error: any) {
      const statusCode = error.response?.status
      const errorMessage = this.getErrorMessage(error)

      core.error(`❌ Failed to register Pipeline with status ${statusCode}: ${errorMessage}`)

      if (statusCode === 401) {
        core.warning('🚨 Client lacks permissions to manage pipelines - this is optional')
        core.warning('💡 You can continue without pipeline registration')
        return null
      } else if (statusCode === 403) {
        core.warning('🚨 Forbidden - Client may not have access to create pipelines - this is optional')
        return null
      }

      core.warning('⚠️ Pipeline registration failed, but continuing with test upload...')
      return null
    }
  }

  async uploadTestResults(
    pipelineId: string | null,
    jobName: string,
    buildNumber: string,
    testResults: TestResult[],
    commitInfo: CommitInfo
  ): Promise<string> {
    try {
      core.info(`📤 Uploading test results to Octane (internal API like Python script)...`)
      core.info(`📊 Test Results Summary: ${testResults.length} tests`)

      // Generar XML en formato JUnit como en el script Python
      const junitXml = this.generateJunitXml(testResults, jobName)

      // Convertir JUnit XML a formato ALM Octane como en el código Python
      const octaneXml = this.convertJunitToOctane(junitXml, 'github-actions-instance', jobName, buildNumber)

      // Usar el mismo endpoint que el código Python exitoso
      const testResultsUrl =
        `/internal-api/shared_spaces/${this.config.sharedSpace}/analytics/ci/test-results` +
        `?skip-errors=true&instance-id=github-actions-instance` +
        `&job-ci-id=${jobName}&build-ci-id=${buildNumber}`

      const response = await this.client.post(testResultsUrl, octaneXml, {
        headers: {
          'Content-Type': 'application/xml',
          'ALM-OCTANE-PRIVATE': 'true'
        }
      })

      core.info('✅ Successfully sent test results to ALM Octane')
      return `Test results uploaded successfully`
    } catch (error: any) {
      const statusCode = error.response?.status
      const errorMessage = this.getErrorMessage(error)

      core.error(`❌ Failed to upload test results with status ${statusCode}: ${errorMessage}`)

      if (statusCode === 401) {
        core.error('🚨 Authentication failed during test upload')
        core.error('💡 The session may have expired or the client lacks permissions')
      }

      throw new Error(`Failed to upload test results: ${errorMessage}`)
    }
  }

  private generateJunitXml(testResults: TestResult[], jobName: string): string {
    // Generar XML JUnit simple
    const testsuite = `<testsuite name="${jobName}" tests="${testResults.length}">
${testResults.map(test =>
  `<testcase name="${test.name}" classname="${test.className || 'DefaultClass'}" time="${(test.duration || 0) / 1000}">
${test.status === 'failed' ? `<failure message="${test.errorMessage || 'Test failed'}">${test.stackTrace || ''}</failure>` : ''}
${test.status === 'skipped' ? '<skipped/>' : ''}
</testcase>`
).join('\n')}
</testsuite>`
    return testsuite
  }

  private convertJunitToOctane(xmlContent: string, serverId: string, jobId: string, buildId: string): string {
    // Implementación simplificada pero funcional como el código Python
    // Por ahora, crear el XML básico de ALM Octane con estructura correcta

//     return `<?xml version="1.0" encoding="UTF-8"?>
// <test_result>
//   <build server_id="${serverId}" job_id="${jobId}" build_id="${buildId}"/>
//   <test_fields>
//     <test_field type="Test_Level" value="Unit Test"/>
//     <test_field type="Test_Type" value="Sanity"/>
//     <test_field type="Framework" value="JUnit"/>
//   </test_fields>
//   <test_runs>
//     <test_run module="" name="sample_test" status="Passed" duration="1" package="" class="DefaultClass"/>
//   </test_runs>
// </test_result>`

     // 🔑 Build ID único
//       const uniqueBuildId = buildId;
//       const uniqueBuildId = `${process.env.GITHUB_RUN_ID || 'manual'}-${Date.now()}`

    // MCMILT2 AHORA: Usa el valor que se pasa a la función (que debe ser el ID limpio de GITHUB_RUN_ID)
    const uniqueBuildId = buildId;

      // ✅ Parsear el XML JUnit de forma síncrona
      let parsed: any;
      parseString(xmlContent, { explicitArray: false }, (err, result) => {
        if (err) {
          throw new Error(`Error parsing JUnit XML: ${err.message}`);
        }
        parsed = result;
      });

      const root = parsed.testsuites || parsed.testsuite;
      if (!root) {
        throw new Error(`Unsupported JUnit XML root element`);
      }

      // Normalizar suites
      const suites = Array.isArray(root.testsuite) ? root.testsuite : (root.testsuite ? [root.testsuite] : [root]);

      // 🏗️ Crear estructura XML de Octane
      const doc = create({ version: '1.0', encoding: 'UTF-8' });
      const testResult = doc.ele('test_result');

      // 📦 Build info
      testResult.ele('build', { server_id: serverId, job_id: jobId, build_id: uniqueBuildId });

      // 📊 Campos de test
      const testFields = testResult.ele('test_fields');
      testFields.ele('test_field', { type: 'Test_Level', value: 'Unit Test' });
      testFields.ele('test_field', { type: 'Test_Type', value: 'Sanity' });
      testFields.ele('test_field', { type: 'Framework', value: 'JUnit' });

      // 📋 Runs
      const testRuns = testResult.ele('test_runs');

      // Helpers
      const getStatus = (tc: any) => {
        if (tc.skipped) return 'Skipped';
        if (tc.error || tc.failure) return 'Failed';
        return 'Passed';
      };

      const getDuration = (tc: any) => {
        const time = parseFloat(tc.$?.time || '1');
        return isNaN(time) ? '1' : Math.max(1, Math.round(time)).toString();
      };

      // 🧪 Procesar cada test case
      for (const suite of suites) {
        const cases = suite.testcase;
        if (!cases) continue;

        const casesArray = Array.isArray(cases) ? cases : [cases];

        for (const testCase of casesArray) {
          const attrs: Record<string, string> = {
            module: suite.$?.module || '',
            name: testCase.$?.name || '',
            status: getStatus(testCase),
            duration: getDuration(testCase)
          };

          if (suite.$?.package) attrs.package = suite.$.package;
          if (testCase.$?.classname) attrs.class = testCase.$.classname;

          const testRunEl = testRuns.ele('test_run', attrs);

          const error = testCase.error || testCase.failure;
          if (error) {
            const errorAttrs: Record<string, string> = {};
            if (error.$?.message) errorAttrs.message = error.$.message;
            if (error.$?.type) errorAttrs.type = error.$.type;

            const errEl = testRunEl.ele('error', errorAttrs);
            if (error._) errEl.txt(error._);
          }
        }
      }

      return doc.end({ prettyPrint: true });

  }

  private getErrorMessage(error: any): string {
    if (error.response) {
      return `HTTP ${error.response.status}: ${error.response.data?.message || error.response.statusText}`
    } else if (error.request) {
      return 'No response received from server'
    } else {
      return error.message
    }
  }

  /**
   * Helper privado para introducir un retraso.
   * @param ms Milisegundos de espera.
   */
  private delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

    /**
     * Calcula la duración total de la pipeline sumando los atributos 'time'
     * de todos los tags <testcase> en el XML de resultados.
     * @param testResultsPath Ruta al archivo JUnit XML.
     * @returns Duración total en milisegundos (entero).
     */
    private async calculatePipelineDuration(testResultsPath: string): Promise<number> {
        const fs = require('fs');
        // El valor por defecto si no se puede calcular es 10 segundos (10000 ms)
        const DEFAULT_DURATION_MS = 10000;

        if (!fs.existsSync(testResultsPath)) {
            core.warning(`[Duration Calculation] Test results file not found at: ${testResultsPath}. Returning default duration of ${DEFAULT_DURATION_MS} ms.`);
            return DEFAULT_DURATION_MS;
        }

        try {
            const xmlContent = fs.readFileSync(testResultsPath, 'utf-8');
            let parsed: any;

            // Parsear el XML de forma asíncrona
            await new Promise<void>((resolve) => {
                // Se requiere la función parseString, asumimos que está disponible
                parseString(xmlContent, { explicitArray: false }, (err, result) => {
                    if (err) {
                        core.error(`[Duration Calculation] Error parsing JUnit XML for duration: ${err.message}.`);
                        parsed = null;
                    } else {
                        parsed = result;
                    }
                    resolve();
                });
            });

            const testsuite = parsed?.testsuite;

            if (!testsuite) {
                core.error(`[Duration Calculation] Could not find <testsuite> element in XML.`);
                return DEFAULT_DURATION_MS;
            }

            let totalDurationSeconds = 0;
            const testcases = testsuite.testcase;

            if (testcases) {
                // Aseguramos que testcases sea un array, ya que xml2js lo convierte a objeto si es solo uno
                const casesArray = Array.isArray(testcases) ? testcases : [testcases];

                for (const testcase of casesArray) {
                    // Aseguramos que el atributo time exista
                    if (testcase && testcase.$ && testcase.$.time) {
                        // El tiempo está en segundos (ej: "142.302")
                        const timeInSeconds = parseFloat(testcase.$.time);

                        if (!isNaN(timeInSeconds)) {
                            totalDurationSeconds += timeInSeconds;
                        } else {
                            core.warning(`[Duration Calculation] Found invalid time value: ${testcase.$.time}. Skipping.`);
                        }
                    }
                }
            }

            // Convertir el total de segundos a milisegundos y redondear al entero más cercano
            const totalDurationMs = Math.round(totalDurationSeconds * 1000);

            core.info(`[Duration Calculation] Calculated pipeline duration: ${totalDurationSeconds.toFixed(3)}s (${totalDurationMs}ms) from test results.`);

            // Si el tiempo calculado es 0 (ej: no se encontraron testcases), usamos el valor por defecto.
            return totalDurationMs > 0 ? totalDurationMs : DEFAULT_DURATION_MS;

        } catch (e: any) {
            core.error(`[Duration Calculation] Unexpected error: ${e.message}. Returning default duration of ${DEFAULT_DURATION_MS} ms.`);
            return DEFAULT_DURATION_MS;
        }
    }




/**
 * Determina el resultado final de la ejecución ('success' o 'failure') procesando
 * el archivo cucumber-junit-report.xml.
 * Un resultado es 'failure' si hay cualquier test fallido, con error o saltado.
 * @param testResultsPath La ruta completa al archivo cucumber-junit-report.xml.
 * @returns El estado de Octane: 'success' si todos pasaron, 'failure' en caso contrario.
 */
private async determinePipelineResult(testResultsPath: string): Promise<'success' | 'failure'> {
    const fs = require('fs');

    if (!fs.existsSync(testResultsPath)) {
        core.error(`[Status Check] Test results file not found at: ${testResultsPath}. Defaulting to 'failure'.`);
        return 'failure';
    }

    try {
        const xmlContent = fs.readFileSync(testResultsPath, 'utf-8');
        let parsed: any;

        // Parsear el XML de forma asíncrona (como promesa)
        await new Promise<void>((resolve, reject) => {
            parseString(xmlContent, { explicitArray: false }, (err, result) => {
                if (err) {
                    core.error(`[Status Check] Error parsing JUnit XML: ${err.message}. Defaulting to 'failure'.`);
                    return resolve(); // Resuelve para no detener el flujo principal
                }
                parsed = result;
                resolve();
            });
        });

        // La raíz para Cucumber es <testsuite>
        const testsuite = parsed?.testsuite;

        if (!testsuite) {
            core.error(`[Status Check] Could not find <testsuite> element in XML. Defaulting to 'failure'.`);
            return 'failure';
        }

        // Obtener los atributos de la etiqueta <testsuite>
        const failures = parseInt(testsuite.$.failures || '0', 10);
        const errors = parseInt(testsuite.$.errors || '0', 10);
        const skipped = parseInt(testsuite.$.skipped || '0', 10);

        if (failures > 0 || errors > 0 || skipped > 0) {
            core.warning(`[Status Check] ❌ Execution FAILED: Failures: ${failures}, Errors: ${errors}, Skipped: ${skipped}.`);
            return 'failure';
        }

        core.info(`[Status Check] ✅ Execution SUCCESS: All tests passed. (Total Tests: ${testsuite.$.tests})`);
        return 'success';

    } catch (e: any) {
        core.error(`[Status Check] Unexpected error during status check: ${e.message}. Defaulting to 'failure'.`);
        return 'failure';
    }
    }


}
