package gradle.plugins.provisioning

import gradle.plugins.provisioning.tasks.VirtualBoxProvisioningTask
import gradle.plugins.provisioning.tasks.aws.AmazonProvisioningTask
import gradle.plugins.provisioning.tasks.image.DownloadImageTask
import gradle.plugins.provisioning.tasks.image.GenerateKickstartTask
import gradle.plugins.provisioning.tasks.image.ImageAssemblyTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class ProvisioningPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def provisioning = project.extensions.create("provisioning", ProvisioningProject, project)

        // Base functionality!
        def generateKickstartTask = project.tasks.create('generateKickstart', GenerateKickstartTask)
        def downloadInstallImageTask = project.tasks.create('downloadInstallImage', DownloadImageTask)
        def isoAssemblyTask = project.tasks.create('assembleInstallImage', ImageAssemblyTask)
                .dependsOn(generateKickstartTask, downloadInstallImageTask)

        def lastCollectiveTask = isoAssemblyTask

        if (provisioning.serverDetails) {
            def vboxProvisionTask = project.tasks.create('vboxProvision', VirtualBoxProvisioningTask)
                    .dependsOn('assembleInstallImage')
            lastCollectiveTask = vboxProvisionTask

            if (provisioning.awsDetails) {
                def awsProvisionTask = project.tasks.create('amiCreation', AmazonProvisioningTask)
                        .dependsOn(vboxProvisionTask)
                lastCollectiveTask = awsProvisionTask
            }
        }

        project.tasks.create('provision').dependsOn(lastCollectiveTask)
    }
}
