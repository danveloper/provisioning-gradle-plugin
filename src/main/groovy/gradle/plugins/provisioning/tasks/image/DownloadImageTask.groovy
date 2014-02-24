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
