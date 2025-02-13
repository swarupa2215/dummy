
                    import groovy.xml.XmlSlurper
                    import groovy.xml.XmlUtil

                    def pipelineName = 'da'
                    def newVersion = '2.0'  // New version you want to set
                    def newValue = '99'  // New value you want to set

                    def xmlFile = new File("C:/Users/LENOVO/.jenkins/workspace/update/old.xml")  // Path to your XML file in the current workspace
                    if (!xmlFile.exists()) {
                        throw new FileNotFoundException("XML file not found: " + xmlFile.path)
                    }

                    // Parse XML
                    def xml = new XmlSlurper().parse(xmlFile)

                    // Ensure we have the correct root element
                    assert xml.name() == 'pipelines' : "Root element is not 'pipelines'"

                    // Iterate and update the correct pipeline based on the name
                    xml.pipeline.each { pipeline ->
                        if (pipeline.@name == pipelineName) {
                            pipeline.version.replaceBody(newVersion)
                            pipeline.value.replaceBody(newValue)
                        }
                    }

                    // Write back the updated XML to the same file (old.xml)
                    xmlFile.text = XmlUtil.serialize(xml)
                    println "XML updated successfully for pipeline: da"
                    println XmlUtil.serialize(xml)  // Print updated XML for verification
                    