/*
 * Licensed Materials - Property of IBM Corp.
 * IBM UrbanCode Build
 * IBM UrbanCode Deploy
 * IBM UrbanCode Release
 * IBM AnthillPro
 * (c) Copyright IBM Corporation 2002, 2015. All Rights Reserved.
 *
 * U.S. Government Users Restricted Rights - Use, duplication or disclosure restricted by
 * GSA ADP Schedule Contract with IBM Corp.
 */

import com.urbancode.air.AirPluginTool
import com.urbancode.air.plugin.jon.helper.JONCmdHelper
 
def apTool = new AirPluginTool(this.args[0], this.args[1])
def props = apTool.getStepProperties()

final def isWindows = apTool.isWindows

def username = props['username'] != null ? props['username'] : ""
def password = props['password'] != null ? props['password'] : ""
def hostname = props['hostname']
def port = props['serverPort']
def cliPath = new File(props['cliPath'])
def cli = isWindows ? "rhq-cli.bat" : "rhq-cli.sh"
def cliFile = new File(cliPath, cli)
 
if (!cliFile.isFile()) {
    throw new Exception("Could not find file in directory: ${cliFile.absolutePath}")
}
 
def helper = new JONCmdHelper()
 
def cmdArgs = [cliFile.absolutePath]
cmdArgs << "-u"
cmdArgs << "${username}"
cmdArgs << "-p"
cmdArgs << "${password}"
cmdArgs << "-s"
cmdArgs << "${hostname}"
cmdArgs << "-t"
cmdArgs << "${port}"

def scriptFile = helper.createTempFile("config-resources")
def resourceType = props['resourceType'] != null ? props['resourceType'] : ""
def agentName = props['agents'] != null ? props ['agentName'] : ""
def resourceId = props['resourceId'] != null ? props['resourceId'] : ""
def resourceProperties = props['resourceProperties'].tokenize("\n") as String[]

scriptFile.withWriter { out ->
    out << "var criteria = new ResourceCriteria()\n"
    if (resourceType) {
        out << "criteria.addFilterResourceTypeName('${resourceType}')\n"
    }
    if (agentName) {
        out << "criteria.addFilterAgentName('${agentName}')\n"
    }
    if (resourceId) {
    out << "criteria.addFilterId(${resourceId})\n"
    }

    out << "var resources = ResourceManager.findResourcesByCriteria(criteria)\n"
    out << "for (i = 0; i < resources.length; i++) {\n"
    out << "\tvar config = ConfigurationManager.getResourceConfiguration(resources.get(i).id)\n"
    for (resourceProperty in resourceProperties) {
        def splitProperties = resourceProperty.split("=")
        def property = splitProperties[0].trim()
        def value = splitProperties[1].trim()
        out << "\tconfig.setSimpleValue(\"${property}\",\"${value}\")\n"
    }
    out << "}"
}

cmdArgs << "-f ${scriptFile.absolutePath}"
def ch = new JONCmdHelper()
ch.runJONCommand("Configuring Resources", cmdArgs)