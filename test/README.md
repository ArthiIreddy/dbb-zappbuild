# zAppBuild/test
Test folder is designed to help test samples like the Mortgage Application against ZAppBuild.

## Repository Legend
Folder/File | Description | Documentation Link
--- | --- | ---
samples/MortgageApplication | This folder contains modified language scripts used to execute impact build by replacing these modified files with the original language files | [MortgageApplication/README.md](samples/MortgageApplication/README.md)
test-conf | This folder contains global configuration properties used by test.groovy | [test-conf/README.md](test-conf/README.md)   
test.groovy  | This is the main build script that is called to start the test process | [test.groovy](/test/README.md#testing-applications-with-zappbuild)
initialization.groovy | This script that is called by test.groovy to clean “automation” test branch created for testing purposes from the feature branch that‘s to be tested and hlq from the previous run | [initialization.groovy](/test/README.md#initializationgroovy)
fullBuild.groovy | This script is called by test.groovy to run a full build by creating an “automation” branch from the feature branch | [fullBuild.groovy](/test/README.md#fullBuildgroovy)
impactBuild.groovy | This script that is called by test.groovy to run an impact build against the program file provided via command line arguments | [impactBuild.groovy](/test/README.md#impactBuildgroovy)

# Testing Applications with zAppBuild
The main script for testing applications against zAppBuild is `test.groovy`. It takes most of its input from the command line to run full and impact builds. `test.groovy` once executed from the command line calls `initialization.groovy`, `fullBuild.groovy` and `impactBuild.groovy` scripts to perform an end to end test on the given feature branch with the program specified for impact build. 

test.groovy script has one optional argument that can be present during each invocation:
* --zRepoPath <arg> - Optional path to ZAppBuild Repo

test.groovy script has nine required arguments that must be present during each invocation:
* --branchName <arg> - Feature branch that needs to be tested
* --app <arg> - Application that is being tested (example: MortgageApplication)
* --serverURL <arg> - Server URL 
* --hlq <arg> - hlq to delete segments from (example: IBMDBB.ZAPP.BUILD)
* --userName <arg> - User for server
* --password <arg> - Password for server
* --fullFiles <arg> - Full build files for verification
* --impactFiles <arg> - Impact build files for verification
* --programFile <arg> - Folder of the program to edit (example: /bms/epsmort.bms)


Examples of running an end to end test:

With zRepoPath passed in as an argument
```
$DBB_HOME/bin/groovyz ${repoPath}/test/test.groovy -z ${repoPath} -b AutomationTest -a MortgageApplication -q IBMDBB.ZAPPB.BUILD -s https://dbbdev.rtp.raleigh.ibm.com:19443/dbb/ -u ADMIN -p ADMIN -f epsmort.bms,epsmlis.bms,epsnbrvl.cbl,epscsmrt.cbl,epsmlist.cbl,epsmpmt.cbl,epscmort.cbl,epscsmrd.cbl,epsmlist.lnk -c /bms/epsmort.bms -i epsmort.bms,epscmort.cbl
``` 
Without zRepoPath passed in as an argument
```
$DBB_HOME/bin/groovyz ${repoPath}/test/test.groovy -b AutomationTest -a MortgageApplication -q IBMDBB.ZAPPB.BUILD -s https://dbbdev.rtp.raleigh.ibm.com:19443/dbb/ -u ADMIN -p ADMIN -f epsmo
```

## Command Line Options Summary
```
optional arguments:
-z --zRepoPath <arg>   Path to the cloned/forked zAppBuild repository

required arguments:
 -b --branchName <arg> Feature Branch that needs to be tested 
 -a --app <arg> Application that is being tested (example: MortgageApplication)
 -s --serverURL <arg> Server URL
 -q --hlq <arg> hlq to delete segments from (example: IBMDBB.ZAPP.BUILD)
 -u --userName <arg> User for server
 -p --password <arg> Password for server
 -f --fullFiles <arg> Full build files for verification
 -i --impactFiles <arg> Impact build files for verification
 -c --programFile <arg> Folder of the program to edit (example: /bms/epsmort.bms)

utility arguments:
 -h ,--help           Shows usage information, like above
 ```

## initialization.groovy
This script that is called by test.groovy to clean “automation” test branch created for testing purposes from the feature branch that‘s to be tested and hlq from the previous run.

```
Optional arguments:
 -z --zRepoPath <arg>   Path to the cloned/forked zAppBuild repository

Required arguments that must be present during each invocation of `test.groovy`
 -b --branchName <arg> Feature Branch that needs to be tested 
 -q --hlq <arg> hlq to delete segments from (example: IBMDBB.ZAPP.BUILD)
```

## fullBuild.groovy
This script is called by test.groovy to run a full build by creating a new “automation” branch from the feature branch specified in the command line argument. It verifies the below requirments
- Full build ran clean
- Number of expected build files (is calculated from the 'fullFiles' argument passed from the command line) matches the number of files build during the full build   in the console.
- Build files expected (passsed via command line argument 'fullFiles') matches the build files during the full build in the console.

```
Optional arguments:
 -z --zRepoPath <arg>   Path to the cloned/forked zAppBuild repository

Required arguments that must be present during each invocation of `test.groovy`
 -b --branchName <arg> Feature Branch that needs to be tested 
 -a --app <arg> Application that is being tested (example: MortgageApplication)
 -s --serverURL <arg> Server URL
 -q --hlq <arg> hlq to delete segments from (example: IBMDBB.ZAPP.BUILD)
 -u --userName <arg> User for server
 -p --password <arg> Password for server
 -f --fullFiles <arg> Full build files for verification
```

## impactBuild.groovy
This script that is called by test.groovy to run an impact build against the program file specified in the command line argument. It verifies the below requirments
- Impact build ran clean
- Number of expected build files(is calculated from the 'impactFiles' argument passed from the command line) matches the number of files build during the impact 
  build in the console.
- Build files expected(passsed via command line argument 'impactFiles') matches the build files during the impact build in the console.

```
Optional arguments:
 -z --zRepoPath <arg>   Path to the cloned/forked zAppBuild repository

Required arguments that must be present during each invocation of `test.groovy`
 -b --branchName <arg> Feature Branch that needs to be tested 
 -a --app <arg> Application that is being tested (example: MortgageApplication)
 -s --serverURL <arg> Server URL
 -q --hlq <arg> hlq to delete segments from (example: IBMDBB.ZAPP.BUILD)
 -u --userName <arg> User for server
 -p --password <arg> Password for server
 -i --impactFiles <arg> Impact build files for verification
 -c --programFile <arg> Folder of the program to edit (example: /bms/epsmort.bms)
```

