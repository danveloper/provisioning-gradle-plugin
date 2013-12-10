package gradle.plugins.provisioning

import gradle.plugins.provisioning.internal.dependencies.Packages
import gradle.plugins.provisioning.internal.disk.Partitioning
import gradle.plugins.provisioning.internal.network.Networking
import gradle.plugins.provisioning.virtualbox.VirtualBoxGuestDetail
import org.gradle.api.Project

/**
 * User: danielwoods
 * Date: 12/8/13
 */
class ProvisioningProject {
  String rootpw
  String installImage
  String lang = "en_US.UTF-8"
  String keyboard = "us"
  String timezone = "Etc/GMT"
  String postInstall

  Partitioning partitioning
  Networking networking
  Packages packages

  Project project

  def serverDetails

  ProvisioningProject(Project project) {
    this.project = project
  }

  void partitioning(Closure clos) {
    def partitioning = new Partitioning()
    clos.delegate = partitioning
    clos.resolveStrategy = Closure.DELEGATE_ONLY
    clos.call()
    this.partitioning = partitioning
  }

  void network(Closure clos) {
    def networking = new Networking()
    clos.delegate = networking
    clos.resolveStrategy = Closure.DELEGATE_FIRST
    clos.call()
    this.networking = networking
  }

  void packages(Closure clos) {
    def packages = new Packages()
    clos.delegate = packages
    clos.resolveStrategy = Closure.DELEGATE_FIRST
    clos.call()
    this.packages = packages
  }

  void postInstall(Closure clos) {
    this.postInstall = clos.call()
  }

  void vbox(Closure clos) {
    def details = new VirtualBoxGuestDetail()
    clos.delegate = details
    clos.resolveStrategy = Closure.DELEGATE_FIRST
    clos.call()
    this.serverDetails = details
  }
}
