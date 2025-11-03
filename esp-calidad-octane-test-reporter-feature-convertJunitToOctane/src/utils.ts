import * as core from '@actions/core'

export interface ActionInputs {
  octaneUrl: string
  sharedSpace: string
  workspace: string
  clientId: string
  clientSecret: string
  testResultsPath: string
  pipelineName: string
  githubServerUrl: string
  jobName: string
  buildNumber: string
  timeout: number
  skipSslVerify: boolean
}

export function validateInputs(inputs: ActionInputs): void {
  const errors: string[] = []

  // Validar URL de Octane
  if (!isValidUrl(inputs.octaneUrl)) {
    errors.push('octane-url must be a valid URL')
  }

  // Validar GitHub Server URL
  if (!isValidUrl(inputs.githubServerUrl)) {
    errors.push('github-server-url must be a valid URL')
  }

  // Validar IDs numéricos
  if (!isValidId(inputs.sharedSpace)) {
    errors.push('shared-space must be a valid numeric ID')
  }

  if (!isValidId(inputs.workspace)) {
    errors.push('workspace must be a valid numeric ID')
  }

  // Validar credenciales
  if (!inputs.clientId.trim()) {
    errors.push('client-id cannot be empty')
  }

  if (!inputs.clientSecret.trim()) {
    errors.push('client-secret cannot be empty')
  }

  // Validar path de resultados
  if (!inputs.testResultsPath.trim()) {
    errors.push('test-results-path cannot be empty')
  }

  // Validar timeout
  if (inputs.timeout < 10 || inputs.timeout > 600) {
    errors.push('timeout must be between 10 and 600 seconds')
  }

  // Validar nombres
  if (!isValidName(inputs.pipelineName)) {
    errors.push('pipeline-name must be a valid name (alphanumeric, spaces, hyphens, underscores)')
  }

  if (!isValidName(inputs.jobName)) {
    errors.push('job-name must be a valid name (alphanumeric, spaces, hyphens, underscores)')
  }

  if (errors.length > 0) {
    const errorMessage = `Input validation failed:\n${errors.map(e => `  • ${e}`).join('\n')}`
    core.error(errorMessage)
    throw new Error(errorMessage)
  }

  core.info('✅ Input validation passed')
}

function isValidUrl(url: string): boolean {
  try {
    const parsedUrl = new URL(url)
    return ['http:', 'https:'].includes(parsedUrl.protocol)
  } catch {
    return false
  }
}

function isValidId(id: string): boolean {
  return /^[0-9]+$/.test(id.trim())
}

function isValidName(name: string): boolean {
  // Permitir letras, números, espacios, guiones y guiones bajos
  return /^[a-zA-Z0-9\s\-_]+$/.test(name.trim()) && name.trim().length > 0
}

export function sanitizeForOctane(value: string): string {
  // Sanitizar strings para evitar problemas con la API de Octane
  return value
    .replace(/[<>]/g, '') // Remover < >
    .replace(/"/g, "'") // Reemplazar comillas dobles con simples
    .trim()
}

export function formatDuration(milliseconds: number): string {
  if (milliseconds < 1000) {
    return `${milliseconds}ms`
  } else if (milliseconds < 60000) {
    return `${(milliseconds / 1000).toFixed(1)}s`
  } else {
    const minutes = Math.floor(milliseconds / 60000)
    const seconds = Math.floor((milliseconds % 60000) / 1000)
    return `${minutes}m ${seconds}s`
  }
}

export function createTestSummary(tests: Array<{status: string}>): string {
  const passed = tests.filter(t => t.status === 'passed').length
  const failed = tests.filter(t => t.status === 'failed').length
  const skipped = tests.filter(t => t.status === 'skipped').length
  const total = tests.length

  return `Total: ${total}, Passed: ${passed}, Failed: ${failed}, Skipped: ${skipped}`
}

export function maskSensitiveData(data: any): any {
  if (typeof data === 'string') {
    // Enmascarar tokens, passwords, etc.
    if (data.length > 8) {
      return `${data.substring(0, 4)}...${data.substring(data.length - 4)}`
    }
    return '***'
  }
  
  if (typeof data === 'object' && data !== null) {
    const masked = { ...data }
    const sensitiveKeys = ['password', 'secret', 'token', 'key', 'credential']
    
    for (const [key, value] of Object.entries(masked)) {
      if (sensitiveKeys.some(sk => key.toLowerCase().includes(sk))) {
        masked[key] = maskSensitiveData(value)
      }
    }
    
    return masked
  }
  
  return data
}

export class RetryHelper {
  static async retry<T>(
    operation: () => Promise<T>,
    maxRetries: number = 3,
    delayMs: number = 1000
  ): Promise<T> {
    let lastError: Error
    
    for (let attempt = 1; attempt <= maxRetries; attempt++) {
      try {
        return await operation()
      } catch (error) {
        lastError = error as Error
        core.warning(`⚠️ Attempt ${attempt}/${maxRetries} failed: ${error}`)
        
        if (attempt < maxRetries) {
          const delay = delayMs * Math.pow(2, attempt - 1) // Exponential backoff
          core.info(`   Retrying in ${delay}ms...`)
          await new Promise(resolve => setTimeout(resolve, delay))
        }
      }
    }
    
    throw lastError!
  }
}
