parameters {
    string(name: 'namespace', description: 'Namespace, in welchem das Projekt deployt werden soll.')
}

pipeline {

    agent {
        node {
            label 'maven'
        }
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    checkout scm
                }
            }
        }

        stage('Prepare Environment') {
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject(params.namespace) {
                            openshift.apply(readFile("src/main/openshift/imagestream.yaml"))
                            openshift.apply(readFile("src/main/openshift/buildconfig.yaml"))
                        }
                    }
                }
            }
        }

        stage('Build Application') {
            steps {
                script {
                    sh 'mvn clean package'
                }
            }
        }

        stage('Build Image') {
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject(params.namespace) {
                            def bc = openshift.selector("buildconfig", "todo-api-quarkus")
                            def build = bc.startBuild("--from-dir=.", "--wait")

                            build.logs('-f')
                        }
                    }
                }
            }
        }

        stage("Prepare Deployment") {
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject(params.namespace) {
                            openshift.apply(readFile("src/main/openshift/deployment.yaml"))
                            openshift.apply(readFile("src/main/openshift/service.yaml"))
                            openshift.apply(readFile("src/main/openshift/route.yaml"))
                            openshift.apply(readFile("src/main/openshift/role.yaml"))
                            openshift.apply(readFile("src/main/openshift/rolebinding.yaml"))
                            openshift.apply(readFile("src/main/openshift/serviceaccount.yaml"))
                            openshift.apply(readFile("src/main/openshift/configmap.yaml"))
                        }
                    }
                }
            }
        }

        stage('Deploy Application') {
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject(params.namespace) {
                            def istag = openshift.selector("imagestreamtag", "todo-api-quarkus:latest")
                            def imageReference = istag.object().image.dockerImageReference

                            def deployment = openshift.selector("deployment", "todo-api-quarkus").object()
                            deployment.spec.template.spec.containers[0].image = imageReference
                            deployment.spec.paused = false
                            openshift.apply(deployment)
                        }
                    }
                }
            }
        }
    }
}
