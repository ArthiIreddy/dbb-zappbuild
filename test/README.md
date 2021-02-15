zAppBuild/test
Test folder is designed to help test samples like the Mortgage Application against ZAppBuild. 

IMPORTANT : The datasets.properties must be configured for your build machine before executing a build! See build-conf/README.md for more information.

Repository Legend

Folder/File	                         Description	                 Documentation Link
samples/MortgageApplication	This folder contains modified language scripts used to execute impact build by replacing these modified  files with the original language files	samples/MortgageApplication/README.md
test-conf	This folder contains global configuration properties used by test.groovy	test-conf/README.md


test.groovy	This is the main build script that is called to start the test process.

initialization.groovy
	  This script that is called by test.groovy to
clean “automation” test branch created for testing purposes from the feature branch that‘s to be tested and hlq from the previous run. 


fullBuild.groovy
	This script is called by test.groovy to run a full build by creating an “automation” branch from the feature branch
impactBuild.groovy
	This script that is called by test.groovy to run an impact build against the program file provided via command line arguments.  


