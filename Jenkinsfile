#!groovy
def BN = BRANCH_NAME == "master" || BRANCH_NAME.startsWith("releases/") ? BRANCH_NAME : "master"

library "knime-pipeline@$BN"

properties([
    pipelineTriggers([
        upstream('knime-js-base/' + env.BRANCH_NAME.replaceAll('/', '%2F'))
    ]),
    buildDiscarder(logRotator(numToKeepStr: '5')),
    disableConcurrentBuilds()
])

try {
    knimetools.defaultTychoBuild('org.knime.update.js.views.labs')

    workflowTests.runTests(
        dependencies: [
            repositories: ['knime-js-labs', 'knime-js-core', 'knime-chromium', 'knime-timeseries',
                'knime-r', 'knime-xml', 'knime-jep', 'knime-datageneration', 'knime-textprocessing',
                'knime-chemistry']
        ]
    )

    stage('Sonarqube analysis') {
        env.lastStage = env.STAGE_NAME
        workflowTests.runSonar()
    }
} catch (ex) {
    currentBuild.result = 'FAILURE'
    throw ex
} finally {
    notifications.notifyBuild(currentBuild.result)
}
/* vim: set shiftwidth=4 expandtab smarttab: */
