import groovy.transform.BaseScript
import groovy.transform.*
import org.apache.commons.cli.Option
import com.ibm.dbb.build.*
import com.ibm.jzos.ZFile
 
@Field BuildProperties properties = BuildProperties.getInstance()
println "/////********Executing initialization script using these build properties\n${properties.list()}\n"
/***************************************************************************************
1. Discards the changes on the automation test branch
2. Checkout's out the feature branch ${branchName}
3. Deletes the automation test branch
@param repoPath         Path to ZAppBuild Repo
@param branchName       Feature branch to create a test(automation) branch against
****************************************************************************************/
def deleteTestBranch = """
    cd ${properties.repoPath}
    git reset --hard automation
    git checkout ${properties.branchName}
    git branch -D automation
"""
def job = ['bash', '-c', deleteTestBranch].execute()
job.waitFor()
def deleteBranch = job.in.text
println "Output:  $deleteBranch"

if(job.exitValue()!=0)
println "Output:  $deleteBranch"
println "Exit code: " + job.exitValue()

/********************************************************************************************
  Clean up hlq
  @param hlq   hlq to delete segments from (example: IBMDBB.ZAPP.BUILD)
*********************************************************************************************/
def project_hlq = ["${properties.hlq}"]
def project_segments = ["BMS","COBOL","COPYBOOK","DBRM","LINK","LOAD","MFS","OBJ","TFORMAT"]

println "Deleting test PDSEs . . ."
 project_hlq.each { hlq ->
  project_segments.each { segment ->
    def pds = "'${hlq}.${segment}'"
    if (ZFile.dsExists(pds)) {
       println "** deleting ${pds}"
       ZFile.remove("//$pds")
    }
  }
}
