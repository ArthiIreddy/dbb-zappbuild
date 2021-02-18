import groovy.transform.*
import org.apache.commons.cli.Option
import com.ibm.dbb.*
import com.ibm.dbb.build.*

@Field BuildProperties properties = BuildProperties.getInstance()
println "********Executing impact build using these build properties =\n ${properties.list()} \n"
/******************************************************************************************
1. Edits the file for incremental build 
2. Runs a incremental/impact build based on file changed
@param dbbHome          Path to DBB to access groovyz
@param serverURL        Server URL example(https://dbbdev.rtp.raleigh.ibm.com:19443/dbb/)
@param repoPath         Path to ZAppBuild Repo
@param programFile      Path to the program folder for the file to be edited
@param app              Application that is being tested (example: MortgageApplication)
@param hlq              hlq to delete segments from (example: IBMDBB.ZAPP.BUILD)
@param userName         User for server
@param password         Password for server
@param impactFiles      Impact build files for verification
@param numImpactFiles   Number of files expected for impact build verification
*******************************************************************************************/
String dbbHome = EnvVars.getHome();

def runImpactBuild = """
    mv ${properties.repoPath}/test/samples/${properties.app}${properties.programFile} ${properties.repoPath}/samples/${properties.app}${properties.programFile}
    cd ${properties.repoPath}/samples/${properties.app}/
    git add .
    git commit . -m "edited program file"
    ${dbbHome}/bin/groovyz ${properties.repoPath}/build.groovy --workspace ${properties.repoPath}/samples --application ${properties.app} --outDir ${properties.repoPath}/out --hlq ${properties.hlq} --logEncoding UTF-8 --url ${properties.serverURL} --id ${properties.userName} --pw ${properties.password} --impactBuild
"""
def task = ['bash', '-c', runImpactBuild].execute()
def outputStream = new StringBuffer();
task.waitForProcessOutput(outputStream, System.err)

assert outputStream.contains("Build State : CLEAN") &&
      !outputStream.contains("Total files processed : 0") &&
       outputStream.contains("Total files processed : ${properties.numImpactFiles}") : "///***IMPACT BUILD FAILED OR TOTAL FILES PROCESSED IS NOT EQUAL TO ${properties.numImpactFiles}.\n HERE IS THE OUTPUT FROM IMPACT BUILD FOR ${properties.programFile} \n$outputStream\n"

def files = properties.impactFiles
List<String> fileList = []
if (files) {
    fileList.addAll(files.trim().split(','))
    assert fileList.count{ i-> outputStream.contains(i) } == fileList.size() : "///***FILES PROCESSED IN THE IMPACT BUILD FOR ${properties.programFile} DOES NOT CONTAIN THE LIST OF FILES PASSED ${fileList}.\n HERE IS THE OUTPUT FROM IMPACT BUILD \n$outputStream\n"
}
