package gradle.plugins.provisioning.tasks.image

import groovy.text.SimpleTemplateEngine
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GenerateKickstartTask extends DefaultTask {

    @TaskAction
    def generate() {
        def tmpl = getClass().getResourceAsStream('/KickstartTemplate.cfg').text
        String ks = new SimpleTemplateEngine().createTemplate(tmpl).make([proj: project.provisioning])
        project.file("$project.buildDir${File.separatorChar}kickstart").mkdirs()
        project.file("$project.buildDir${File.separatorChar}kickstart${File.separatorChar}ks.cfg").write(ks)
    }
}
