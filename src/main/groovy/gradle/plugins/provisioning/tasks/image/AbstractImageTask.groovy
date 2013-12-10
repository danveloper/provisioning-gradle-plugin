package gradle.plugins.provisioning.tasks.image

import org.gradle.api.DefaultTask

/**
 * User: danielwoods
 * Date: 12/8/13
 */
abstract class AbstractImageTask extends DefaultTask {

  def getCacheDir() {
    "$project.buildDir${File.separatorChar}provisioning-cache"
  }
}
