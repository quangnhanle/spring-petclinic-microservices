pipeline {
    agent any

    tools {
        jdk 'java-21'
    }

    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Branch cần build')
    }

    environment {
        DOCKERHUB_USERNAME = 'nhanleeq'
        DOCKERHUB_CREDENTIALS = 'dockerhub-credentials'
        GIT_REPO = 'https://github.com/quangnhanle/spring-petclinic-microservices.git'
    }

    stages {
        stage('Checkout Source Code') {
            steps {
                checkout scm
            }
        }

        stage('Get Commit ID') {
            steps {
                script {
                    env.COMMIT_ID = sh(
                        script: "git rev-parse --short HEAD",
                        returnStdout: true
                    ).trim()

                    echo "Current branch: ${params.BRANCH_NAME}"
                    echo "Commit ID: ${env.COMMIT_ID}"
                }
            }
        }

        stage('Detect Changed Services') {
            steps {
                script {
                    def changedFiles = sh(
                        script: "git diff --name-only HEAD~1 HEAD || true",
                        returnStdout: true
                    ).trim()

                    echo "Changed files:"
                    echo changedFiles

                    env.BUILD_CUSTOMERS = changedFiles.contains("spring-petclinic-customers-service") ? "true" : "false"
                    env.BUILD_VETS = changedFiles.contains("spring-petclinic-vets-service") ? "true" : "false"
                    env.BUILD_VISITS = changedFiles.contains("spring-petclinic-visits-service") ? "true" : "false"
                    env.BUILD_API_GATEWAY = changedFiles.contains("spring-petclinic-api-gateway") ? "true" : "false"
                    env.BUILD_DISCOVERY = changedFiles.contains("spring-petclinic-discovery-server") ? "true" : "false"
                    env.BUILD_ADMIN = changedFiles.contains("spring-petclinic-admin-server") ? "true" : "false"

                    echo "BUILD_CUSTOMERS = ${env.BUILD_CUSTOMERS}"
                    echo "BUILD_VETS = ${env.BUILD_VETS}"
                    echo "BUILD_VISITS = ${env.BUILD_VISITS}"
                    echo "BUILD_API_GATEWAY = ${env.BUILD_API_GATEWAY}"
                    echo "BUILD_DISCOVERY = ${env.BUILD_DISCOVERY}"
                    echo "BUILD_ADMIN = ${env.BUILD_ADMIN}"
                }
            }
        }

        stage('Maven Build') {
            steps {
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Docker Login') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: "${env.DOCKERHUB_CREDENTIALS}",
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                    '''
                }
            }
        }

        stage('Build and Push Customers Service') {
            when {
                expression { env.BUILD_CUSTOMERS == 'true' }
            }
            steps {
                script {
                    buildAndPushImage('./spring-petclinic-customers-service', 'spring-petclinic-customers-service', '8081', env.COMMIT_ID)
                }
            }
        }

        stage('Build and Push Vets Service') {
            when {
                expression { env.BUILD_VETS == 'true' }
            }
            steps {
                script {
                    buildAndPushImage('./spring-petclinic-vets-service', 'spring-petclinic-vets-service', '8081', env.COMMIT_ID)
                }
            }
        }

        stage('Build and Push Visits Service') {
            when {
                expression { env.BUILD_VISITS == 'true' }
            }
            steps {
                script {
                    buildAndPushImage('./spring-petclinic-visits-service', 'spring-petclinic-visits-service', '8081', env.COMMIT_ID)
                }
            }
        }

        stage('Build and Push API Gateway') {
            when {
                expression { env.BUILD_API_GATEWAY == 'true' }
            }
            steps {
                script {
                    buildAndPushImage('./spring-petclinic-api-gateway', 'spring-petclinic-api-gateway', '8081', env.COMMIT_ID)
                }
            }
        }

        stage('Build and Push Discovery Server') {
            when {
                expression { env.BUILD_DISCOVERY == 'true' }
            }
            steps {
                script {
                    buildAndPushImage('./spring-petclinic-discovery-server', 'spring-petclinic-discovery-server', '8761', env.COMMIT_ID)
                }
            }
        }

        stage('Build and Push Admin Server') {
            when {
                expression { env.BUILD_ADMIN == 'true' }
            }
            steps {
                script {
                    buildAndPushImage('./spring-petclinic-admin-server', 'spring-petclinic-admin-server', '9090', env.COMMIT_ID)
                }
            }
        }
    }

    post {
        success {
            echo "CI completed successfully."
            echo "Image tag used: ${env.COMMIT_ID}"
        }

        failure {
            echo "CI failed. Please check Jenkins logs."
        }
    }
}

def buildAndPushImage(String serviceDir, String imageName, String exposedPort, String commitId) {
    def artifactName = sh(
        script: "find ${serviceDir}/target -maxdepth 1 -name '*.jar' ! -name '*.original' -exec basename {} .jar \\; | head -n 1",
        returnStdout: true
    ).trim()

    if (!artifactName) {
        error "No packaged jar found in ${serviceDir}/target"
    }

    sh """
        docker build \
          -f ./docker/Dockerfile \
          --build-arg ARTIFACT_NAME=${artifactName} \
          --build-arg EXPOSED_PORT=${exposedPort} \
          -t ${env.DOCKERHUB_USERNAME}/${imageName}:${commitId} \
          ${serviceDir}/target

        docker push ${env.DOCKERHUB_USERNAME}/${imageName}:${commitId}
    """
}