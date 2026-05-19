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
                git branch: "${params.BRANCH_NAME}",
                    url: "${env.GIT_REPO}"
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

                    def buildAll = !changedFiles

                    env.BUILD_CUSTOMERS = buildAll || changedFiles.contains("spring-petclinic-customers-service") ? "true" : "false"
                    env.BUILD_VETS = buildAll || changedFiles.contains("spring-petclinic-vets-service") ? "true" : "false"
                    env.BUILD_VISITS = buildAll || changedFiles.contains("spring-petclinic-visits-service") ? "true" : "false"
                    env.BUILD_API_GATEWAY = buildAll || changedFiles.contains("spring-petclinic-api-gateway") ? "true" : "false"
                    env.BUILD_CONFIG_SERVER = buildAll || changedFiles.contains("spring-petclinic-config-server") ? "true" : "false"
                    env.BUILD_DISCOVERY = buildAll || changedFiles.contains("spring-petclinic-discovery-server") ? "true" : "false"
                    env.BUILD_GENAI = buildAll || changedFiles.contains("spring-petclinic-genai-service") ? "true" : "false"
                    env.BUILD_ADMIN = buildAll || changedFiles.contains("spring-petclinic-admin-server") ? "true" : "false"

                    echo "BUILD_CUSTOMERS = ${env.BUILD_CUSTOMERS}"
                    echo "BUILD_VETS = ${env.BUILD_VETS}"
                    echo "BUILD_VISITS = ${env.BUILD_VISITS}"
                    echo "BUILD_API_GATEWAY = ${env.BUILD_API_GATEWAY}"
                    echo "BUILD_CONFIG_SERVER = ${env.BUILD_CONFIG_SERVER}"
                    echo "BUILD_DISCOVERY = ${env.BUILD_DISCOVERY}"
                    echo "BUILD_GENAI = ${env.BUILD_GENAI}"
                    echo "BUILD_ADMIN = ${env.BUILD_ADMIN}"
                }
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

        stage('Test Discovery Server') {
            when {
                expression { env.BUILD_DISCOVERY == 'true' }
            }
            steps {
                dir('spring-petclinic-discovery-server') {
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'spring-petclinic-discovery-server/target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'spring-petclinic-discovery-server/target/site/jacoco/**', allowEmptyArchive: true
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

        stage('Test Config Server') {
            when {
                expression { env.BUILD_CONFIG_SERVER == 'true' }
            }
            steps {
                dir('spring-petclinic-config-server') {
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'spring-petclinic-config-server/target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'spring-petclinic-config-server/target/site/jacoco/**', allowEmptyArchive: true
                }
            }
        }

        stage('Build and Push Config Server') {
            when {
                expression { env.BUILD_CONFIG_SERVER == 'true' }
            }
            steps {
                script {
                    buildAndPushImage('./spring-petclinic-config-server', 'spring-petclinic-config-server', '8888', env.COMMIT_ID)
                }
            }
        }

        stage('Test Customers Service') {
            when {
                expression { env.BUILD_CUSTOMERS == 'true' }
            }
            steps {
                dir('spring-petclinic-customers-service') {
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'spring-petclinic-customers-service/target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'spring-petclinic-customers-service/target/site/jacoco/**', allowEmptyArchive: true
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

        stage('Test Vets Service') {
            when {
                expression { env.BUILD_VETS == 'true' }
            }
            steps {
                dir('spring-petclinic-vets-service') {
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'spring-petclinic-vets-service/target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'spring-petclinic-vets-service/target/site/jacoco/**', allowEmptyArchive: true
                }
            }
        }

        stage('Build and Push Vets Service') {
            when {
                expression { env.BUILD_VETS == 'true' }
            }
            steps {
                script {
                    buildAndPushImage('./spring-petclinic-vets-service', 'spring-petclinic-vets-service', '8083', env.COMMIT_ID)
                }
            }
        }

        stage('Test Visits Service') {
            when {
                expression { env.BUILD_VISITS == 'true' }
            }
            steps {
                dir('spring-petclinic-visits-service') {
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'spring-petclinic-visits-service/target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'spring-petclinic-visits-service/target/site/jacoco/**', allowEmptyArchive: true
                }
            }
        }

        stage('Build and Push Visits Service') {
            when {
                expression { env.BUILD_VISITS == 'true' }
            }
            steps {
                script {
                    buildAndPushImage('./spring-petclinic-visits-service', 'spring-petclinic-visits-service', '8082', env.COMMIT_ID)
                }
            }
        }

        stage('Test GenAI Service') {
            when {
                expression { env.BUILD_GENAI == 'true' }
            }
            steps {
                dir('spring-petclinic-genai-service') {
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'spring-petclinic-genai-service/target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'spring-petclinic-genai-service/target/site/jacoco/**', allowEmptyArchive: true
                }
            }
        }

        stage('Build and Push GenAI Service') {
            when {
                expression { env.BUILD_GENAI == 'true' }
            }
            steps {
                script {
                    buildAndPushImage('./spring-petclinic-genai-service', 'spring-petclinic-genai-service', '8084', env.COMMIT_ID)
                }
            }
        }

        stage('Test API Gateway') {
            when {
                expression { env.BUILD_API_GATEWAY == 'true' }
            }
            steps {
                dir('spring-petclinic-api-gateway') {
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'spring-petclinic-api-gateway/target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'spring-petclinic-api-gateway/target/site/jacoco/**', allowEmptyArchive: true
                }
            }
        }

        stage('Build and Push API Gateway') {
            when {
                expression { env.BUILD_API_GATEWAY == 'true' }
            }
            steps {
                script {
                    buildAndPushImage('./spring-petclinic-api-gateway', 'spring-petclinic-api-gateway', '8080', env.COMMIT_ID)
                }
            }        
        }

        stage('Test Admin Server') {
            when {
                expression { env.BUILD_ADMIN == 'true' }
            }
            steps {
                dir('spring-petclinic-admin-server') {
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'spring-petclinic-admin-server/target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'spring-petclinic-admin-server/target/site/jacoco/**', allowEmptyArchive: true
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