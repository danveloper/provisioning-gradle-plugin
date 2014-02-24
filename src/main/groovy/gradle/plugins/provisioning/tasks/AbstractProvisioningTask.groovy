package gradle.plugins.provisioning.tasks

import gradle.plugins.provisioning.ProvisioningProject
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class AbstractProvisioningTask extends DefaultTask {

    @TaskAction
    abstract void provision()

    ProvisioningProject getProvisioning() {
        (ProvisioningProject) project.provisioning
    }
}
