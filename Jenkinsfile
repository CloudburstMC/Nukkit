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
                sh './gradlew shadowJar'
            }
            post {
                success {
//                     junit 'build/test-results/**/*.xml'
                    archiveArtifacts artifacts: 'target/nukkit-*-SNAPSHOT.jar', fingerprint: true
                }
            }
        }

        stage ('Javadocs') {
            when {
                branch "master"
            }

            steps {
                sh './gradlew javadoc'
                step([$class: 'JavadocArchiver', javadocDir: 'build/docs/javadoc', keepAll: false])
            }
        }
    }

    post {
        always {
            deleteDir()
            withCredentials([string(credentialsId: 'nukkitx-discord-webhook', variable: 'DISCORD_WEBHOOK')]) {
                discordSend description: "**Build:** [${currentBuild.id}](${env.BUILD_URL})\n**Status:** [${currentBuild.currentResult}](${env.BUILD_URL})", footer: 'NukkitX Jenkins', link: env.BUILD_URL, successful: currentBuild.resultIsBetterOrEqualTo('SUCCESS'), title: "${env.JOB_NAME} #${currentBuild.id}", webhookURL: DISCORD_WEBHOOK
            }
        }
    }
}
