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
                    def server = Artifactory.server('potestas')
                    def rtMaven = Artifactory.newMavenBuild()
                    rtMaven.resolver server: server, releaseRepo: 'release', snapshotRepo: 'snapshot'
                    rtMaven.deployer server: server, releaseRepo: 'release', snapshotRepo: 'snapshot'
                    rtMaven.tool = 'Maven 3'
                    def buildInfo = rtMaven.run pom: 'pom.xml', goals: 'install'
                    server.publishBuildInfo buildInfo
                }
            }
        }
    }
}