package gradle.plugins.provisioning.tasks.image

import org.gradle.api.DefaultTask

abstract class AbstractImageTask extends DefaultTask {

    def getCacheDir() {
        "$project.buildDir${File.separatorChar}provisioning-cache"
    }
}
