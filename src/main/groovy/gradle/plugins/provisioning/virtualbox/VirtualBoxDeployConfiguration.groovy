package gradle.plugins.provisioning.virtualbox

import gradle.plugins.provisioning.tasks.VirtualBoxProvisioningTask

class VirtualBoxDeployConfiguration {
    String apiUrl
    String home
    String username
    String password

    transient VirtualBoxProvisioningTask.Deployer deployer
}