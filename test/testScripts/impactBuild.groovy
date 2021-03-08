@groovy.transform.BaseScript com.ibm.dbb.groovy.ScriptLoader baseScript
import groovy.transform.*
import com.ibm.dbb.*
import com.ibm.dbb.build.*
import com.ibm.jzos.ZFile

@Field BuildProperties props = BuildProperties.getInstance()
println "\n** Executing test script impactBuild.groovy"

// Get the DBB_HOME location
def dbbHome = EnvVars.getHome()
if (props.verbose) println "** DBB_HOME = ${dbbHome}"

// create impact build command
def impactBuildCommand = []
impactBuildCommand << "${dbbHome}/bin/groovyz"
impactBuildCommand << "${props.zAppBuildDir}/build.groovy"
impactBuildCommand << "--workspace ${props.workspace}"
impactBuildCommand << "--application ${props.app}"
impactBuildCommand << "--outDir ${props.zAppBuildDir}/out"
impactBuildCommand << "--hlq ${props.hlq}"
impactBuildCommand << "--logEncoding UTF-8"
impactBuildCommand << "--url ${props.url}"
impactBuildCommand << "--id ${props.id}"
impactBuildCommand << (props.pw ? "--pw ${props.pw}" : "--pwFile ${props.pwFile}")
// impactBuildCommand << (props.verbose ? "--verbose" : "")
impactBuildCommand << "--impactBuild"

// iterate through change files to test impact build
PropertyMappings filesBuiltMappings = new PropertyMappings('impactBuild_expectedFilesBuilt')
def changedFiles = props.impactBuild_changedFiles.split(',')
println("** Processing changed files from impactBuild_changedFiles property : ${props.impactBuild_changedFiles}")
try {
	changedFiles.each { changedFile ->
		// update changed file in Git repo test branch
		copyAndCommit(changedFile)
		
		// run impact build
		println "** Executing ${impactBuildCommand.join(" ")}"
		def outputStream = new StringBuffer()
		def process = ['bash', '-c', impactBuildCommand.join(" ")].execute()
		process.waitForProcessOutput(outputStream, System.err)
		
		// validate build results
		validateImpactBuild(changedFile, filesBuiltMappings)
	}
}
finally {
	cleanUpDatasets()
}


// script end  


//*************************************************************
// Method Definitions
//*************************************************************

def copyAndCommit(String changedFile) {
	println "** Copying and committing $changedFile to test application repo"
	def commands = """
    cp ${props.zAppBuildDir}/test/applications/${props.app}/${changedFile} ${props.appLocation}/${changedFile}
    cd ${props.appLocation}/
    git add .
    git commit . -m "edited program file"
"""
	def task = ['bash', '-c', commands].execute()
	def outputStream = new StringBuffer();
	task.waitForProcessOutput(outputStream, System.err)
}

def validateImpactBuild(String changedFile, PropertyMappings filesBuiltMappings) {

	println "** Validating impact build results"
	def expectedFilesBuiltList = filesBuiltMappings.getValue(changedFile).split(',')
	
	// Validate clean build
	assert outputStream.contains("Build State : CLEAN") : "*! IMPACT BUILD FAILED\nOUTPUT STREAM:\n$outputStream\n"

	// Validate expected number of files built
	def numImpactFiles = expectedFilesBuiltList.size()
	assert outputStream.contains("Total files processed : ${numImpactFiles}") : "*! TOTAL FILES PROCESSED ARE NOT EQUAL TO ${numImpactFiles}\nOUTPUT STREAM:\n$outputStream\n"

	// Validate expected built files in output stream
	assert expectedFilesBuiltList.count{ i-> outputStream.contains(i) } == expectedFilesBuiltList.size() : "*! FILES PROCESSED IN THE IMPACT BUILD DOES NOT CONTAIN THE LIST OF FILES EXPECTED ${expectedFilesBuiltList}\nOUTPUT STREAM:\n$outputStream\n"
	
	println "**"
	println "** IMPACT BUILD TEST : PASSED **"
	println "**"
}

def cleanUpDatasets() {
	def segments = props.impactBuild_datasetsToCleanUp.split(',')
	
	println "Deleting impact build PDSEs ${segments}"
	segments.each { segment ->
	    def pds = "'${props.hlq}.${segment}'"
	    if (ZFile.dsExists(pds)) {
	       if (props.verbose) println "** Deleting ${pds}"
	       ZFile.remove("//$pds")
	    }
	}
}