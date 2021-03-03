# samples
Here we can add addtionally sample applications that can be used to test ZAppBuild. 

Prerequisites: The application needs to be setup under the [ZAppBuild](/samples/) and have the dataset.properties set.

To add a new sample to test against follow the steps below
1) Create a folder with the name of the test application (example: MortgageApplication)
2) Create a folders under the application folder for each of the programming language you would like to test(example: cobol,bms)
3) Add modified versions of the files for each of these programs. These modified files will be used by the impact build when end to end test is ran. For example we add a    
   modified version of epsmlist.cbl under the yourtestapplicationfolder/cobol/epsmlist.cbl

Refer [README.md](/test/README.md) for additional information about testing an application using zAppBuild.
