import xml.etree.ElementTree as ET
import sys

# Get parameters from Jenkins
pipeline_name = sys.argv[1]  # Pipeline name to update (e.g., "da" or "de")
new_version = sys.argv[2]    # New version value (e.g., "2.0")
new_value = sys.argv[3]      # New value (e.g., "99")

# Load XML file
xml_file = "org.xml"  # Ensure this file exists in the workspace
tree = ET.parse(xml_file)
root = tree.getroot()

# Find the correct <pipeline> node by name and update its values
for pipeline in root.findall("pipeline"):
    if pipeline.get("name") == pipeline_name:
        pipeline.find("version").text = new_version
        pipeline.find("value").text = new_value
        print(f"Updated pipeline: {pipeline_name}, version: {new_version}, value: {new_value}")

# Save updated XML back to file
tree.write(xml_file)

print("XML update complete!")
