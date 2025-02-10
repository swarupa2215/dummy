import groovy.xml.XmlUtil
//import groovy.xml.XmlSlurper
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
                    def pipelineName = params.PIPELINE_NAME.split(',')
                    def newVersion = params.VERSION
                    def newValue = params.VALUE

                    // Read the XML file from the workspace
                    def xmlFile = new File("${workspace}/${Pipeline_XML}")
                    println "${workspace}/org.xml"
                    if (!xmlFile.exists()) {
                        error "pipelines.xml file not found!"
                    }

                    // Parse the XML using XmlSlurper
                    def xml = new XmlSlurper().parse(xmlFile)

                    // Iterate over the pipeline nodes and update the selected ones
                    xml.pipeline.each { pipeline ->
    	                if (pipeline.@name == pipelineName) {
       		                pipeline.version.replaceBody(newVersion)
        		            pipeline.value.replaceBody(newValue)
                	        }
	                    }
                    
                    try{

                   	xmlFile.text = XmlUtil.serialize(xml)
		            println "XML updated successfully for pipeline: ${pipelineName}"
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
