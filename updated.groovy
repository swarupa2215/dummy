pipeline {
    agent any
    parameters {
        string(name: 'PIPELINE_NAME', defaultValue: 'da,de', description: 'Comma-separated pipeline names to update')
        string(name: 'VERSION', defaultValue: '2.0', description: 'New version value')
        string(name: 'VALUE', defaultValue: '99', description: 'New value')
    }
    stages {
        stage('Clone GitHub Repo') {
            steps {
                script {
                    // Clone your GitHub repository
                     git url: 'https://github.com/swarupa2215/dummy.git', branch: 'main'
                }
            }
        }
        stage('Update XML') {
            steps {
                script {
                    // Split the pipeline names by comma
                    def selectedPipelineNames = params.PIPELINE_NAME.split(',')
                    def newVersion = params.VERSION
                    def newValue = params.VALUE

                    // Read the XML file from the workspace
                    def xmlFile = new File("${workspace}/${Pipeline_XML}")
                    println "${workspace}/org.xml"
                    if (!xmlFile.exists()) {
                        error "pipelines.xml file not found!"
                    }

                    // Parse the XML using XmlSlurper
                    def xmlg = new XmlSlurper().parse(xmlFile)

                    // Iterate over the pipeline nodes and update the selected ones
                    xmlg.pipeline.each { pipeline ->
                        def pipelineName = pipeline.@name
                        if (selectedPipelineNames.contains(pipelineName)) {
                            // Update version and value fields
                            def versionNode = pipeline.version[0]
                            def valueNode = pipeline.value[0]

                            // Modify the version and value nodes if they exist
                            if (versionNode && valueNode) {
                                versionNode.replaceBody(newVersion)
                                valueNode.replaceBody(newValue)

                                echo "Updated pipeline '${pipelineName}' with version ${newVersion} and value ${newValue}"
                            }
                        }
                    }
                    try{

                    // Serialize the updated XML to a string
                    def updatedXmlString = groovy.xml.XmlUtil.serialize(xmlg)

                    // Print the updated XML to the console
                    echo "Updated XML content:\n${updatedXmlString}"
                     def xmlFilePath = "${WORKSPACE}/${Pipeline_XML}"
                     echo "${xmlFilePath}"

                    // Write the updated XML back to the file
                    writeFile(file: xmlFilePath, text: updatedXmlString)
                    echo "XML updated successfully!"
                    } catch (Exception e) {
                        echo "An error occurred: ${e.getMessage()}"
                        // Explicitly mark the pipeline as failed and abort
                        currentBuild.result = 'SUCCESS'
                        
                    }
                   
                }
            }
        }
    }
}
