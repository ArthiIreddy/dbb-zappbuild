@groovy.transform.BaseScript com.ibm.dbb.groovy.ScriptLoader baseScript

import groovy.transform.*
import org.apache.commons.cli.Option
import com.ibm.dbb.*
import com.ibm.dbb.build.*

@Field BuildProperties properties = BuildProperties.getInstance()
println "\n/////********EXECUTING IMPACT BUILD USING THESE BUILD PROPERTIES\nserverURL: Server URL example(https://dbbdev.rtp.raleigh.ibm.com:19443/dbb/)\nzRepoPath: Optional path to ZAppBuild Repo\nprogramFile: Path to the program folder for the file to be edited\napp: Application that is being tested (example: MortgageApplication)\nhlq: hlq to delete segments from (example: IBMDBB.ZAPP.BUILD)\nuserName: User for server\npassword: Password for server\nimpactFiles: Impact build files for verification\n"
/******************************************************************************************
1. Edits the file for incremental build 
2. Runs a incremental/impact build based on file changed
@param serverURL        Server URL example(https://dbbdev.rtp.raleigh.ibm.com:19443/dbb/)
@param zRepoPath        Optional path to ZAppBuild Repo
@param programFile      Path to the program folder for the file to be edited
@param app              Application that is being tested (example: MortgageApplication)
@param hlq              hlq to delete segments from (example: IBMDBB.ZAPP.BUILD)
@param userName         User for server
@param password         Password for server
@param impactFiles      Impact build files for verification
*******************************************************************************************/
def dbbHome = EnvVars.getHome()
def runImpactBuild

if (properties.z || properties.zRepoPath){
 runImpactBuild = """
    mv ${properties.zRepoPath}/test/samples/${properties.app}${properties.programFile} ${properties.zRepoPath}/samples/${properties.app}${properties.programFile}
    cd ${properties.zRepoPath}/samples/${properties.app}/
    git add .
    git commit . -m "edited program file"
    ${dbbHome}/bin/groovyz ${properties.zRepoPath}/build.groovy --workspace ${properties.zRepoPath}/samples --application ${properties.app} --outDir ${properties.zRepoPath}/out --hlq ${properties.hlq} --logEncoding UTF-8 --url ${properties.serverURL} --id ${properties.userName} --pw ${properties.password} --impactBuild
"""
} else{ 
    def zAppBuildDirTest = getScriptDir()
    def zAppBuildDir = zAppBuildDirTest.replace("/test","")
    println "***This is zAppBuildDir home****:" + zAppBuildDir  
 runImpactBuild = """
    mv ${zAppBuildDir}/test/samples/${properties.app}${properties.programFile} ${zAppBuildDir}/samples/${properties.app}${properties.programFile}
    cd ${zAppBuildDir}/samples/${properties.app}/
    git add .
    git commit . -m "edited program file"
    ${dbbHome}/bin/groovyz ${zAppBuildDir}/build.groovy --workspace ${zAppBuildDir}/samples --application ${properties.app} --outDir ${zAppBuildDir}/out --hlq ${properties.hlq} --logEncoding UTF-8 --url ${properties.serverURL} --id ${properties.userName} --pw ${properties.password} --impactBuild
"""
}

def task = ['bash', '-c', runImpactBuild].execute()
def outputStream = new StringBuffer();
task.waitForProcessOutput(outputStream, System.err)

def list = properties.impactFiles
def listNew = list.split(',')
def numImpactFiles = listNew.size()
assert outputStream.contains("Build State : CLEAN") &&
      !outputStream.contains("Total files processed : 0") &&
       outputStream.contains("Total files processed : ${numImpactFiles}") : "///***IMPACT BUILD FAILED OR TOTAL FILES PROCESSED IS NOT EQUAL TO ${numImpactFiles}.\n HERE IS THE OUTPUT FROM IMPACT BUILD FOR ${properties.programFile} \n$outputStream\n"

def files = properties.impactFiles
List<String> fileList = []
if (files) {
    fileList.addAll(files.trim().split(','))
    assert fileList.count{ i-> outputStream.contains(i) } == fileList.size() : "///***FILES PROCESSED IN THE IMPACT BUILD FOR ${properties.programFile} DOES NOT CONTAIN THE LIST OF FILES PASSED ${fileList}.\n HERE IS THE OUTPUT FROM IMPACT BUILD \n$outputStream\n"
}
