package gradle.plugins.provisioning.tasks

import gradle.plugins.provisioning.ProvisioningProject
import gradle.plugins.provisioning.virtualbox.VirtualBoxGuestDetail
import gradle.plugins.provisioning.virtualbox.VirtualBoxService
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * User: danielwoods
 * Date: 12/9/13
 */
class VirtualBoxProvisioningTask extends DefaultTask {

  @TaskAction
  void provision() {
    VirtualBoxGuestDetail details = (VirtualBoxGuestDetail)((ProvisioningProject)project.provisioning).serverDetails

    def vboxService = new VirtualBoxService(details.apiUrl)
    try {
      vboxService.newServer(details.name, details.memory, details.disk, details.x64)
      vboxService.bridgeNetwork()
      vboxService.attachISO(new File(project.buildDir, "${project.name}-${project.version}.iso").absolutePath)
      vboxService.boot()
    } finally {
      vboxService.release()
    }
  }

}
