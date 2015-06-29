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

def service = props['service']
def startPath = new File(props['startPath'])
def start = "rhqctl"
def startFile = new File(startPath, start)

if (!startFile.isFile()) {
    throw new Exception("Could not find file in directory ${startFile.absolutePath}")
}

def cmdArgs = [startFile.absolutePath]
cmdArgs << "--${service} status"

def ch = new JONCmdHelper()
ch.runStatusCheck("Checking ${service} status", cmdArgs)
