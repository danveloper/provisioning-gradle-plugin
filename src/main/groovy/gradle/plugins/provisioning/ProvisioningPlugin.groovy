package gradle.plugins.provisioning

import gradle.plugins.provisioning.tasks.image.DownloadImageTask
import gradle.plugins.provisioning.tasks.image.GenerateKickstartTask
import gradle.plugins.provisioning.tasks.image.ImageAssemblyTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class ProvisioningPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create("provisioning", ProvisioningProject, project)

        // Base functionality!
        def generateKickstartTask = project.tasks.create('generateKickstart', GenerateKickstartTask)
        def downloadInstallImageTask = project.tasks.create('downloadInstallImage', DownloadImageTask)
        def isoAssemblyTask = project.tasks.create('assembleInstallImage', ImageAssemblyTask)
                .dependsOn(generateKickstartTask, downloadInstallImageTask)

        // May be overridden in ProvisioningProject
        project.tasks.create('provision').dependsOn(isoAssemblyTask)
    }
}
