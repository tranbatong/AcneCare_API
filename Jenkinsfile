pipeline {
    agent any

    options {
        timestamps()
    }

    environment {
        DEPLOY_DIR = '/var/www/acnecare_api'
        MAVEN_BUILD_IMAGE = 'maven:3.9.14-eclipse-temurin-21-noble'
    }

    parameters {
        string(
            name: 'SOURCE_REL_PATH',
            defaultValue: '.',
        )
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Validate deploy input') {
            steps {
                sh """
                    set -e
                    case '${params.SOURCE_REL_PATH}' in
                        *..*|/*)
                            echo "ERROR: SOURCE_REL_PATH không hợp lệ: ${params.SOURCE_REL_PATH}"
                            exit 1
                            ;;
                    esac
                """
            }
        }

        stage('Build JAR (Maven + JDK 21 in Docker)') {
            steps {
                sh """
                    set -e
                    API_DIR='${env.WORKSPACE}/${params.SOURCE_REL_PATH}'
                    docker pull '${env.MAVEN_BUILD_IMAGE}'
                    docker run --rm \\
                      -u "\$(id -u):\$(id -g)" \\
                      -e HOME=/tmp \\
                      -v "\$API_DIR:/workspace:rw" \\
                      -v "\$HOME/.m2:/tmp/m2:rw" \\
                      -w /workspace \\
                      '${env.MAVEN_BUILD_IMAGE}' \\
                      mvn -B -Dmaven.repo.local=/tmp/m2 clean package -DskipTests
                    ls "\$API_DIR"/target/*.jar
                """
            }
        }

        stage('Sync to deploy dir') {
            steps {
                sh """
                    set -e
                    mkdir -p '${env.DEPLOY_DIR}'
                    if [ ! -w '${env.DEPLOY_DIR}' ]; then
                        echo "ERROR: Jenkins không có quyền ghi vào ${env.DEPLOY_DIR}"
                        exit 1
                    fi
        
                    rsync -a --delete \
                      --no-owner --no-group \
                      --no-times --omit-dir-times --no-perms\
                      --exclude '.env' \
                      --exclude 'mysql-data/' \
                      --filter 'protect mysql-data/' \
                      '${env.WORKSPACE}/${params.SOURCE_REL_PATH}/' '${env.DEPLOY_DIR}/'
        
                    test -f '${env.DEPLOY_DIR}/.env' || (echo "ERROR: thiếu file .env trong ${env.DEPLOY_DIR}" && exit 1)
                """
            }
        }
        stage('Docker: build & deploy') {
            steps {
                sh """
                    set -e
                    cd '${env.DEPLOY_DIR}'
                    docker compose -f docker-compose.yml config -q
                    docker compose -f docker-compose.yml pull mysql-db
                    docker compose -f docker-compose.yml up -d --build --force-recreate
                """
            }
        }

        stage('Verify') {
            steps {
                sh """
                    set -e
                    cd '${env.DEPLOY_DIR}'
                    docker compose -f docker-compose.yml ps
                    test "\$(docker compose -f docker-compose.yml ps --status running --services | wc -l)" -ge 2 || (echo "ERROR: chưa đủ service chạy ổn định" && exit 1)
                """
            }
        }
    }

    post {
        failure {
            echo 'Pipeline failed'
        }
    }
}
