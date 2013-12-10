package gradle.plugins.provisioning

import gradle.plugins.provisioning.tasks.VirtualBoxProvisioningTask
import gradle.plugins.provisioning.tasks.image.GenerateKickstartTask
import gradle.plugins.provisioning.tasks.image.DownloadImageTask
import gradle.plugins.provisioning.tasks.image.ImageAssemblyTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * User: danielwoods
 * Date: 12/8/13
 */
class ProvisioningPlugin implements Plugin<Project> {
  @Override
  void apply(Project project) {
    project.extensions.create "provisioning", ProvisioningProject, project

    project.tasks.create 'generateKickstart', GenerateKickstartTask

    project.tasks.create 'downloadInstallImage', DownloadImageTask

    project.tasks.create('assembleInstallImage', ImageAssemblyTask)
      .dependsOn('generateKickstart', 'downloadInstallImage')

    project.tasks.create('provisionServer', VirtualBoxProvisioningTask)
      .dependsOn('assembleInstallImage')

    project.tasks.create('provision')
      .dependsOn('provisionServer')
  }
}
