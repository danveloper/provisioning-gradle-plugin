/*
 * Copyright 2014 Daniel Woods
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gradle.plugins.provisioning

import gradle.plugins.provisioning.aws.AwsDeployConfiguration
import gradle.plugins.provisioning.internal.dependencies.Packages
import gradle.plugins.provisioning.internal.disk.Partitioning
import gradle.plugins.provisioning.internal.network.Networking
import gradle.plugins.provisioning.tasks.VirtualBoxProvisioningTask
import gradle.plugins.provisioning.tasks.aws.AmazonProvisioningTask
import gradle.plugins.provisioning.virtualbox.VirtualBoxMachineDetails
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task

class ProvisioningProject {
    String rootpw
    String installImage
    String lang = "en_US.UTF-8"
    String keyboard = "us"
    String timezone = "Etc/GMT"
    String postInstall
    Boolean poweroff = Boolean.FALSE

    Partitioning partitioning
    Networking networking
    Packages packages

    Project project

    VirtualBoxMachineDetails serverDetails
    AwsDeployConfiguration awsDetails

    ProvisioningProject(Project project) {
        this.project = project
    }

    void partitioning(Action<Partitioning> action) {
        partitioning = new Partitioning()
        action.execute partitioning
    }

    void network(Action<Networking> action) {
        this.networking = new Networking()
        action.execute networking
    }

    void packages(Action<Packages> action) {
        packages = new Packages()
        action.execute packages
    }

    void vbox(Action<VirtualBoxMachineDetails> action) {
        serverDetails = new VirtualBoxMachineDetails()
        action.execute serverDetails
        def task = project.tasks.create('vboxProvision', VirtualBoxProvisioningTask).dependsOn('assembleInstallImage')
        replaceProvisionTask(task)
    }

    void aws(Closure clos) {
        def builder = new AwsDeployConfiguration.Builder()
        clos.delegate = builder
        clos.resolveStrategy = Closure.DELEGATE_FIRST
        clos.call()
        this.awsDetails = builder.build()
        poweroff = Boolean.TRUE
        def task = project.tasks.create('amiCreation', AmazonProvisioningTask).dependsOn('vboxProvision')
        replaceProvisionTask(task)
    }

    private void replaceProvisionTask(Task newLast) {
        project.tasks.replace('provision').dependsOn(newLast)
    }

    void postInstall(Closure clos) {
        this.postInstall = clos.call()
    }
}
