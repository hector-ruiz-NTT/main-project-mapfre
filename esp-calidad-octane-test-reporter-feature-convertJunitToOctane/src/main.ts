import * as core from '@actions/core'
import * as github from '@actions/github'
import { OctaneClient } from './octane-client'
import { TestResultsParser } from './test-parser'
import { validateInputs, ActionInputs } from './utils'

async function run(): Promise<void> {
  const startTime = Date.now()
  
  try {
    core.info('🚀 Starting ALM Octane Test Reporter...')
    
    // Obtener y validar inputs
    const inputs: ActionInputs = {
      octaneUrl: core.getInput('octane-url', { required: true }),
      sharedSpace: core.getInput('shared-space', { required: true }),
      workspace: core.getInput('workspace', { required: true }),
      clientId: core.getInput('client-id', { required: true }),
      clientSecret: core.getInput('client-secret', { required: true }),
      testResultsPath: core.getInput('test-results-path', { required: true }),
      pipelineName: core.getInput('pipeline-name') || github.context.repo.repo,
      githubServerUrl: core.getInput('github-server-url') || `${github.context.serverUrl}/${github.context.repo.owner}/${github.context.repo.repo}`,
      jobName: core.getInput('job-name') || github.context.job,
      buildNumber: core.getInput('build-number') || github.context.runNumber.toString(),
      timeout: parseInt(core.getInput('timeout') || '60'),
      skipSslVerify: core.getInput('skip-ssl-verify') === 'true'
    }

    // Validar inputs
    validateInputs(inputs)

    core.info(`📋 Configuration:`)
    core.info(`   Octane URL: ${inputs.octaneUrl}`)
    core.info(`   Shared Space: ${inputs.sharedSpace}`)
    core.info(`   Workspace: ${inputs.workspace}`)
    core.info(`   Pipeline: ${inputs.pipelineName}`)
    core.info(`   Job: ${inputs.jobName}`)
    core.info(`   Build: ${inputs.buildNumber}`)
    core.info(`   Test Results: ${inputs.testResultsPath}`)

    // Configurar cliente Octane
    const octaneClient = new OctaneClient({
      url: inputs.octaneUrl,
      sharedSpace: inputs.sharedSpace,
      workspace: inputs.workspace,
      clientId: inputs.clientId,
      clientSecret: inputs.clientSecret,
      timeout: inputs.timeout,
      skipSslVerify: inputs.skipSslVerify
    })

    // === SEGUIR EXACTAMENTE EL FLUJO DEL SCRIPT PYTHON EXITOSO ===
    
    // 1. Authenticate (como Python)
    await octaneClient.authenticate()

    // 2. Get or Create CI Server (como Python)
    const { ciServerId, ciServerInstanceId } = await octaneClient.getOrCreateCiServer()

    // 3. Get or Create Pipeline (como Python) 
    const { pipelineId, jobCiId } = await octaneClient.getOrCreatePipeline(ciServerId, ciServerInstanceId, inputs.jobName)

    // 4. Send 'started' event (como Python)
    await octaneClient.sendCiEvent(ciServerInstanceId, jobCiId, pipelineId, 'started', inputs.testResultsPath)

    // 5. Send test results (como Python)
    await octaneClient.sendTestResults(ciServerInstanceId, jobCiId, pipelineId, inputs.testResultsPath)

    // 6. Send 'finished' event (como Python)
    await octaneClient.sendCiEvent(ciServerInstanceId, jobCiId, pipelineId, 'finished', inputs.testResultsPath)

    const duration = Date.now() - startTime
    core.info(`✅ ALM Octane Test Reporter completed successfully in ${Math.round(duration / 1000)}s`)

  } catch (error: any) {
    const duration = Date.now() - startTime
    core.error(`❌ Action failed after ${Math.round(duration / 1000)}s: ${error.message}`)
    if (error.stack) {
      core.error(`Stack trace: ${error.stack}`)
    }
    core.setFailed(`ALM Octane Test Reporter failed: ${error.message}`)
  }
}

// Handle unhandled promise rejections
process.on('unhandledRejection', (reason, promise) => {
  core.error(`Unhandled promise rejection at: ${promise}, reason: ${reason}`)
  process.exit(1)
})

// Run the action
if (require.main === module) {
  run()
}
