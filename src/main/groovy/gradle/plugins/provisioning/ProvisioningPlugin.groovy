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
