# zAppBuild/test
Test folder is designed to help test samples like the Mortgage Application against ZAppBuild.

## Repository Legend
Folder/File | Description | Documentation Link
--- | --- | ---
samples/MortgageApplication | This folder contains modified language scripts used to execute impact build by replacing these modified files with the original language files | [/test/samples/MortgageApplication/README.md](test/samples/MortgageApplication/README.md)
test-conf | This folder contains global configuration properties used by test.groovy | [test-conf/README.md](test-conf/README.md)   
test.groovy  | This is the main build script that is called to start the test process |
initialization.groovy | This script that is called by test.groovy to clean “automation” test branch created for testing purposes from the feature branch that‘s to be tested and hlq from the previous run | 
fullBuild.groovy | This script is called by test.groovy to run a full build by creating an “automation” branch from the feature branch |
impactBuild.groovy | This script that is called by test.groovy to run an impact build against the program file provided via command line arguments |

# Testing Applications with zAppBuild
The main script for testing applications against zAppBuild is `test.groovy`. It takes most of its input from the command line to run full and impact builds. `test.groovy` once executed from the command line calls `initialization.groovy`, `fullBuild.groovy` and `impactBuild.groovy` scripts to perform an end to end test on the given feature branch with the program specified for impact build. 

test.groovy script has tweleve required arguments that must be present during each invocation:
* --repoPath <arg> - Path to the cloned/forked zAppBuild repository
* --branchName <arg> - Feature branch that needs to be tested
* --app <arg> - Application that is being tested (example: MortgageApplication)
* --serverURL <arg> - Server URL 
* --hlq <arg> - hlq to delete segments from (example: IBMDBB.ZAPP.BUILD)
* --userName <arg> - User for server
* --password <arg> - Password for server
* --fullFiles <arg> - Full build files for verification
* --impactFiles <arg> - Impact build files for verification
* --numFullFiles <arg> - Number of files expected for full build verification
* --numImpactFiles <arg> - Number of files expected for impact build verification
* --programFile <arg> - Folder of the program to edit (example: /bms/epsmort.bms)


Example of running an end to end test:
```
$DBB_HOME/bin/groovyz ${repoPath}/test/test.groovy -r ${repoPath} -b AutomationTest -a MortgageApplication -q IBMDBB.ZAPPB.BUILD -s https://dbbdev.rtp.raleigh.ibm.com:19443/dbb/ -u ADMIN -p ADMIN -n 9 -f epsmort.bms,epsmlis.bms,epsnbrvl.cbl,epscsmrt.cbl,epsmlist.cbl,epsmpmt.cbl,epscmort.cbl,epscsmrd.cbl,epsmlist.lnk -c /bms/epsmort.bms -m 2 -i epsmort.bms,epscmort.cbl
``` 

## Command Line Options Summary
```
required options:
 -r --repoPath <arg>   Path to the cloned/forked zAppBuild repository
 -b --branchName <arg> Feature Branch that needs to be tested 
 -a --app <arg> Application that is being tested (example: MortgageApplication)
 -s --serverURL <arg> Server URL
 -q --hlq <arg> hlq to delete segments from (example: IBMDBB.ZAPP.BUILD)
 -u --userName <arg> User for server
 -p --password <arg> Password for server
 -f --fullFiles <arg> Full build files for verification
 -i --impactFiles <arg> Impact build files for verification
 -n --numFullFiles <arg> Number of files expected for full build verification
 -m --numImpactFiles <arg> Number of files expected for impact build verification
 -c --programFile <arg> Folder of the program to edit (example: /bms/epsmort.bms)

utility options
 -h ,--help           Shows usage information, like above
 ```

## initialization.groovy
This script that is called by test.groovy to clean “automation” test branch created for testing purposes from the feature branch that‘s to be tested and hlq from the previous run.

```
Required arguments that must be present during each invocation of `initialization.groovy` :
 -r --repoPath <arg>   Path to the cloned/forked zAppBuild repository
 -b --branchName <arg> Feature Branch that needs to be tested 
 -q --hlq <arg> hlq to delete segments from (example: IBMDBB.ZAPP.BUILD)
```

## fullBuild.groovy
This script is called by test.groovy to run a full build by creating a new “automation” branch from the feature branch specified in the command line argument 

```
Required arguments that must be present during each invocation of `fullBuild.groovy` :
 -r --repoPath <arg>   Path to the cloned/forked zAppBuild repository
 -b --branchName <arg> Feature Branch that needs to be tested 
 -a --app <arg> Application that is being tested (example: MortgageApplication)
 -s --serverURL <arg> Server URL
 -q --hlq <arg> hlq to delete segments from (example: IBMDBB.ZAPP.BUILD)
 -u --userName <arg> User for server
 -p --password <arg> Password for server
 -f --fullFiles <arg> Full build files for verification
 -n --numFullFiles <arg> Number of files expected for full build verification
```

## impactBuild.groovy
This script that is called by test.groovy to run an impact build against the program file specified in the command line argument

```
Required arguments that must be present during each invocation of `impactBuild.groovy` :
 -r --repoPath <arg>   Path to the cloned/forked zAppBuild repository
 -b --branchName <arg> Feature Branch that needs to be tested 
 -a --app <arg> Application that is being tested (example: MortgageApplication)
 -s --serverURL <arg> Server URL
 -q --hlq <arg> hlq to delete segments from (example: IBMDBB.ZAPP.BUILD)
 -u --userName <arg> User for server
 -p --password <arg> Password for server
 -i --impactFiles <arg> Impact build files for verification
 -m --numImpactFiles <arg> Number of files expected for impact build verification
 -c --programFile <arg> Folder of the program to edit (example: /bms/epsmort.bms)
```

