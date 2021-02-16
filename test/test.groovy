@groovy.transform.BaseScript com.ibm.dbb.groovy.ScriptLoader baseScript

import groovy.util.*
import groovy.transform.*
import groovy.time.*
import groovy.xml.*
import com.ibm.dbb.build.*

@Field BuildProperties properties = BuildProperties.getInstance()
/*
 * parseArgs - parses test.groovy input options and arguments
 */
def cli = new CliBuilder(
   usage: 'initialization,fullBuild,impactBuild groovy files, execute arguments',
   header: '\nAvailable options (use -h for help):\n',
   footer: '\nInformation provided via above options is used to execute build against ZAppBuild.\n')

cli.with
{
   h(longOpt: 'help', 'Show usage information')
   r(longOpt: 'repoPath', 'Repo Path', args: 1, required: true)
   b(longOpt: 'branchName', 'Feature Branch', args: 1, required: true)
   a(longOpt: 'app', 'Application that is being tested (example: MortgageApplication)', args: 1, required: true)
   s(longOpt: 'serverURL', 'Server URL', args: 1, required: true)
   q(longOpt: 'hlq', 'hlq to delete segments from (example: IBMDBB.ZAPP.BUILD)', args: 1, required: true)
   u(longOpt: 'userName', 'User for server', args: 1, required: true)
   p(longOpt: 'password', 'Password for server', args: 1, required: true)
   f(longOpt: 'fullFiles', 'Full build files for verification', args:1, required: true)
   i(longOpt: 'impactFiles', 'Impact build files for verification', args:1, required: true)
   n(longOpt: 'numFullFiles', 'Number of files expected for full build verification', args:1, required: true)
   m(longOpt: 'numImpactFiles', 'Number of files expected for impact build verification', args:1, required: true)
   c(longOpt: 'programFile', 'Folder of the program to edit (example: /bms/epsmort.bms)', args: 2, required: true)
}

def options = cli.parse(args)
    if (!options) {
        return
    }
    // Show usage text when -h or --help option is used.
    if (options.h || options.help) {
        cli.usage() 
        return
    }

// set command line arguments
if (options.r) properties.repoPath = options.r
if (options.b) properties.branchName = options.b
if (options.a) properties.app = options.a
if (options.s) properties.serverURL = options.s
if (options.q) properties.hlq = options.q
if (options.u) properties.userName = options.u
if (options.p) properties.password = options.p
if (options.f) properties.fullFiles = options.f
if (options.i) properties.impactFiles = options.i
if (options.n) properties.numFullFiles = options.n
if (options.m) properties.numImpactFiles = options.m
if (options.c) properties.programFile = options.c

// Load test.properties
properties.load(new File("${getScriptDir()}/test-conf/test.properties"))

if (properties.testList.size() == 0){
	println("*! No files in test list.  Nothing to do.")
}
else {
println("** Invoking test scripts according to test list order: ${properties.testList}")
String[] testOrderList = properties.testList.split(',')
   testOrderList.each { script ->
   runScript(new File("${script}"), [:])
 }
}

// /u/dbbAutomation/workspace/Automation_Jobs/DBB_All_BuildS/DBBZtoolkitTar/bin/groovyz /u/dbbAutomation/workspace/Automation_Jobs/ZAppBuildTest/ZAppBuild/dbb-zappbuild/test/test.groovy -r /u/dbbAutomation/workspace/Automation_Jobs/ZAppBuildTest/ZAppBuild/dbb-zappbuild -b AutomationTest -a MortgageApplication -q IBMDBB.ZAPPB.BUILD -s https://dbbdev.rtp.raleigh.ibm.com:19443/dbb/ -u ADMIN -p ADMIN -n 9 -f epsmort.bms,epsmlis.bms,epsnbrvl.cbl,epscsmrt.cbl,epsmlist.cbl,epsmpmt.cbl,epscmort.cbl,epscsmrd.cbl,epsmlist.lnk -i epsmort.bms,epscmort.cbl -m 2 -c /bms/epsmort.bms
