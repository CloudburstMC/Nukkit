pipeline {
    agent any
    tools {
        maven 'Maven 3'
        jdk 'Java 8'
    }
    options {
        buildDiscarder(logRotator(artifactNumToKeepStr: '1'))
    }
    stages {
        stage ('Build') {
            steps {
                sh 'mvn clean package javadoc:javadoc'
            }
            post {
                success {
                    junit 'target/surefire-reports/**/*.xml'
                    step([
                        $class: 'JavadocArchiver',
                        javadocDir: 'target/site/apidocs',
                        keepAll: true
                    ])
                }
            }
        }

        stage ('Deploy') {
            when {
                branch "artifactory"
            }
            steps {
                script {
                    def server = Artifactory.server "potestas"
                    def buildInfo = Artifactory.newBuildInfo()
                    buildInfo.env.capture = true
                    def rtMaven = Artifactory.newMavenBuild()
                    rtMaven.tool = "Maven 3" // Tool name from Jenkins configuration
                    rtMaven.opts = "-Denv=dev"
                    rtMaven.deployer releaseRepo:'release', snapshotRepo:'snapshot', server: server
                    rtMaven.resolver releaseRepo:'release', snapshotRepo:'snapshot', server: server

                    rtMaven.run pom: 'pom.xml', goals: 'install -DskipTests', buildInfo: buildInfo

                    buildInfo.retention maxBuilds: 10, maxDays: 7, deleteBuildArtifacts: true
                    // Publish build info.
                    server.publishBuildInfo buildInfo
                }
            }
        }
    }
}