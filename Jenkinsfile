pipeline {
    agent any
    tools {
        maven 'Maven 3'
        jdk 'Java 8'
    }
    options {
        buildDiscarder(logRotator(artifactNumToKeepStr: '5'))
    }
    stages {
        stage ('Build') {
            steps {
                sh 'mvn clean package'
            }
            post {
                success {
                    junit 'target/surefire-reports/**/*.xml'
                    archiveArtifacts artifacts: 'target/nukkit-*-SNAPSHOT.jar', fingerprint: true
                }
            }
        }

        stage ('Deploy') {
//            when {
//                branch "master"
//            }

            steps {
                rtMavenDeployer (
                        id: "MAVEN_DEPLOYER",
                        serverId: "ARTIFACTORY",
                        releaseRepo: "maven-releases",
                        snapshotRepo: "maven-snapshots"
                )
                rtMavenResolver (
                        id: "MAVEN_RESOLVER",
                        serverId: "ARTIFACTORY",
                        releaseRepo: "release",
                        snapshotRepo: "snapshot"
                )
                rtMavenRun (
                        pom: 'pom.xml',
                        goals: 'clean javadoc:javadoc source:jar install -DskipTests',
                        deployerId: "MAVEN_DEPLOYER",
                        resolverId: "MAVEN_RESOLVER"
                )
                rtPublishBuildInfo (
                        serverId: "ARTIFACTORY"
                )
                step([$class: 'JavadocArchiver', javadocDir: 'target/site/apidocs', keepAll: false])
            }
        }
    }

    post {
        always {
            deleteDir()
        }
    }
}