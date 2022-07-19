#!groovy
def BN = (BRANCH_NAME == 'master' || BRANCH_NAME.startsWith('releases/')) ? BRANCH_NAME : 'releases/2022-09'

library "knime-pipeline@$BN"

properties([
    pipelineTriggers([
        upstream('knime-js-base/' + env.BRANCH_NAME.replaceAll('/', '%2F'))
    ]),
    parameters(workflowTests.getConfigurationsAsParameters()),
    buildDiscarder(logRotator(numToKeepStr: '5')),
    disableConcurrentBuilds()
])

try {
    knimetools.defaultTychoBuild('org.knime.update.js.views.labs')

    workflowTests.runTests(
        dependencies: [
            repositories: ['knime-js-labs', 'knime-js-core', 'knime-chromium', 'knime-timeseries',
                'knime-r', 'knime-xml', 'knime-jep', 'knime-datageneration', 'knime-textprocessing',
                'knime-chemistry', 'knime-python', 'knime-conda', 'knime-js-base', 'knime-distance', 'knime-stats', 
                'knime-jfreechart', 'knime-database', 'knime-kerberos', 'knime-filehandling'],
            ius: [ 'org.knime.features.chem.types.feature.group' ]
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
