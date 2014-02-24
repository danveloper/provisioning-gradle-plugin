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
package gradle.plugins.provisioning.tasks.image

import org.gradle.api.tasks.TaskAction

class DownloadImageTask extends AbstractImageTask {
    @TaskAction
    def download() {
        if (new File("$project.buildDir${File.separatorChar}provisioning-cache${File.separatorChar}install.iso")
                .exists()) {
            return true
        }
        String url = project.provisioning.installImage
        def file = project.file(cacheDir)
        file.mkdirs()
        def out = new FileOutputStream(new File("$file.absolutePath${File.separatorChar}install.iso"))
        out << url.toURL().openStream()
        out.close()
    }

}
