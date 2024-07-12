#!groovy
def BN = (BRANCH_NAME == 'master' || BRANCH_NAME.startsWith('releases/')) ? BRANCH_NAME : 'releases/2024-12'

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
            repositories: [
                'knime-cef',
                'knime-chemistry',
                'knime-database',
                'knime-datageneration',
                'knime-distance',
                'knime-filehandling',
                'knime-jep',
                'knime-jfreechart',
                'knime-js-base',
                'knime-js-core',
                'knime-js-labs',
                'knime-kerberos',
                'knime-python',
                'knime-r',
                'knime-stats', 
                'knime-textprocessing',
                'knime-timeseries',
                'knime-xml'
            ],
            ius: [
                'org.knime.features.browser.cef.feature.group',
                'org.knime.features.chem.types.feature.group'
            ]
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
