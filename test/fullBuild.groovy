@groovy.transform.BaseScript com.ibm.dbb.groovy.ScriptLoader baseScript

import groovy.transform.*
import org.apache.commons.cli.Option
import com.ibm.dbb.*
import com.ibm.dbb.build.*

@Field BuildProperties properties = BuildProperties.getInstance()
println "/////********EXECUTING FULL BUILD USING THESE BUILD PROPERTIES\nzRepoPath: Optional path to ZAppBuild Repo\n
\nbranchName: Feature branch to create a test(automation) branch against\n
\napp: Application that is being tested (example: MortgageApplication)\n
\nhlq: hlq to delete segments from (example: IBMDBB.ZAPP.BUILD)\n
\nserverURL: Server URL example(https://dbbdev.rtp.raleigh.ibm.com:19443/dbb/)\n
\nuserName: User for server\n
\npassword: Password for server\n
\nfullFiles: Build files for verification\n"
/****************************************************************************************
1. Creates an automation branch from ${branchName} 
2. Sets the values up for datasets in the datasets.properties
3. Cleans up test PDSEs
4. Runs a full build using mortgage application
@param zRepoPath             Optional path to ZAppBuild Repo
@param branchName            Feature branch to create a test(automation) branch against
@param app                   Application that is being tested (example: MortgageApplication)
@param hlq                   hlq to delete segments from (example: IBMDBB.ZAPP.BUILD)
@param serverURL             Server URL example(https://dbbdev.rtp.raleigh.ibm.com:19443/dbb/)
@param userName              User for server
@param password              Password for server
@param fullFiles             Build files for verification
******************************************************************************************/
def dbbHome = EnvVars.getHome()
println "***This is dbb home****" + dbbHome

if (properties.z || properties.zRepoPath){
 def runFullBuild = """
    cd ${properties.zRepoPath}
    git checkout ${properties.branchName}
    git checkout -b automation ${properties.branchName}
    mv ${properties.zRepoPath}/test/samples/${properties.app}/datasets.properties ${properties.zRepoPath}/build-conf/datasets.properties
    ${dbbHome}/bin/groovyz ${properties.zRepoPath}/build.groovy --workspace ${properties.zRepoPath}/samples --application ${properties.app} --outDir ${properties.zRepoPath}/out --hlq ${properties.hlq} --logEncoding UTF-8 --url ${properties.serverURL} --id ${properties.userName} --pw ${properties.password} --fullBuild
"""
} else{
    def zAppBuildDirTest = getScriptDir()
    def zAppBuildDir = zAppBuildDirTest.replace("/test","")
    println "***This is zAppBuildDir home****:" + zAppBuildDir
    def runFullBuild = """
    cd ${zAppBuildDir}
    git checkout ${properties.branchName}
    git checkout -b automation ${properties.branchName}
    mv ${zAppBuildDir}/test/samples/${properties.app}/datasets.properties ${zAppBuildDir}/build-conf/datasets.properties
    ${dbbHome}/bin/groovyz ${zAppBuildDir}/build.groovy --workspace ${zAppBuildDir}/samples --application ${properties.app} --outDir ${zAppBuildDir}/out --hlq ${properties.hlq} --logEncoding UTF-8 --url ${properties.serverURL} --id ${properties.userName} --pw ${properties.password} --fullBuild
"""
}

def process = ['bash', '-c', runFullBuild].execute()
def outputStream = new StringBuffer();
process.waitForProcessOutput(outputStream, System.err)

def list = properties.fullFiles
def listNew = list.split(',')
def numFullFiles = listNew.size()
assert outputStream.contains("Build State : CLEAN") && outputStream.contains("Total files processed : ${numFullFiles}") : "///***EITHER THE FULLBUILD FAILED OR TOTAL FILES PROCESSED ARE NOT EQUAL TO ${numFullFiles}.\n HERE IS THE OUTPUT FROM FULLBUILD \n$outputStream\n"

def files = properties.fullFiles
List<String> fileList = []
if (files) {
  fileList.addAll(files.trim().split(',')) 
  assert fileList.count{ i-> outputStream.contains(i) } == fileList.size() : "///***FILES PROCESSED IN THE FULLBUILD DOES NOT CONTAIN THE LIST OF FILES PASSED ${fileList}.\n HERE IS THE OUTPUT FROM FULLBUILD \n$outputStream\n"
}
