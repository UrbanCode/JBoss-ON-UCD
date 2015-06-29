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
package com.urbancode.air.plugin.jon.helper

public class JONCmdHelper {
    
    def runJONCommand(def message, def cmdArgs) {
        if (message) {
            println (message)
        }
        
        def command = cmdArgs.join(' ')
        println("command : " + command)
        Process proc = Runtime.getRuntime().exec(command)
        proc.waitForProcessOutput(System.out, System.err)

        if (proc.exitValue()) {
            throw new Exception("Command failed with exit code: " + proc.exitValue())
        }
        
        return proc.exitValue()
    }
    
    def runStatusCheck(def message, def cmdArgs) {
        if (message) {
            println (message)
        }
        
        def command = cmdArgs.join(' ')
        println("command : " + command)
        Process proc = Runtime.getRuntime().exec(command)
        InputStream stdout = proc.getInputStream()
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout))
        def line
        while ((line = reader.readLine()) != null) {
            if (line.contains("running")) {
                println (line)
                break;
            }
            else if (line.contains("down")) {
                throw new Exception(line)
            }
        }
        proc.waitForProcessOutput(System.out, System.err)
        if (proc.exitValue()) {
            throw new Exception("Command failed with exit code: " + proc.exitValue())
        }
        
        return proc.exitValue()
    }

    def createTempFile(String filename) {
        def tmpFile = File.createTempFile("${filename}", ".js")
        return tmpFile
    }
}