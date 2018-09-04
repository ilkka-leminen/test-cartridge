
import pluggable.scm.*;

//SCMProvider scmProvider = SCMProviderHandler.getScmProvider("${SCM_PROVIDER_ID}", binding.variables)

// Folders
def workspaceFolderName = "${WORKSPACE_NAME}"
def projectFolderName = "${PROJECT_NAME}"
def projectScmNamespace = "DEMO"

// Variables
// **The git repo variables will be changed to the users' git repositories manually in the Jenkins jobs**
def demoGitRepo = "https://github.com/contentful/the-example-app.nodejs.git"
//def apiRepo = "api"
//def regressionTestGitRepo = "YOUR_REGRESSION_TEST_REPO"

// ** The logrotator variables should be changed to meet your build archive requirements
def logRotatorDaysToKeep = 7
def logRotatorBuildNumToKeep = 7
def logRotatorArtifactsNumDaysToKeep = 7
def logRotatorArtifactsNumToKeep = 7

// Jobs
def validateAPIJob = freeStyleJob(projectFolderName + "/validate")

// Views
def pipelineView = buildPipelineView(projectFolderName + "/DEMO")

pipelineView.with{
    title('DEMO Build Pipeline')
    displayedBuilds(5)
    selectedJob(projectFolderName + "/validate")
    showPipelineParameters()
    showPipelineDefinitionHeader()
    refreshFrequency(5)
}

// All jobs are tied to build on the Jenkins slave
// The functional build steps for each job have been left empty
// A default set of wrappers have been used for each job
// New jobs can be introduced into the pipeline as required

validateAPIJob.with{
  description("Validation job")
  logRotator {
    daysToKeep(logRotatorDaysToKeep)
    numToKeep(logRotatorBuildNumToKeep)
    artifactDaysToKeep(logRotatorArtifactsNumDaysToKeep)
    artifactNumToKeep(logRotatorArtifactsNumToKeep)
  }
  //scm scmProvider.get(projectScmNamespace, apiRepo, "*/master", "adop-jenkins-master", null)

  environmentVariables {
      env('WORKSPACE_NAME',workspaceFolderName)
      env('PROJECT_NAME',projectFolderName)
  }
  label("docker")
  tools {
      nodejs 'NodeJS 10.9.0'
  }
  wrappers {
    //nodejs('NodeJS 10.9.0')
    preBuildCleanup()
    injectPasswords()
    maskPasswords()
    sshAgent("adop-jenkins-master")
  }
  scm {
        git {
            remote {
                url(demoGitRepo)
                //credentials("adop-jenkins-master")
            }
            branch("master")
        }
    }
  //triggers scmProvider.trigger(projectScmNamespace, apiGitRepo, "master")
  steps {
    sh 'npm -version'
    shell('''## YOUR BUILD STEPS GO HERE'''.stripMargin())

  
    //nodejsCommand('console.log("Hello World!")', 'NodeJS 10.9.0')
  }
  publishers{
  }
}
