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

import com.github.stephenc.javaisotools.eltorito.impl.ElToritoConfig
import com.github.stephenc.javaisotools.iso9660.ISO9660RootDirectory
import com.github.stephenc.javaisotools.iso9660.impl.CreateISO
import com.github.stephenc.javaisotools.iso9660.impl.ISO9660Config
import com.github.stephenc.javaisotools.iso9660.impl.ISOImageFileHandler
import com.github.stephenc.javaisotools.joliet.impl.JolietConfig
import com.github.stephenc.javaisotools.loopfs.iso9660.Iso9660FileEntry
import com.github.stephenc.javaisotools.loopfs.iso9660.Iso9660FileSystem
import com.github.stephenc.javaisotools.rockridge.impl.RockRidgeConfig
import org.gradle.api.tasks.TaskAction

class ImageAssemblyTask extends AbstractImageTask {

    @TaskAction
    def assemble() {
        def installIso = new File("$project.buildDir${File.separatorChar}provisioning-cache${File.separatorChar}install.iso")
        if (!installIso.exists()) {
            throw new RuntimeException("Could not retrieve install iso from provisioning-cache!")
        }
        def image = new Iso9660FileSystem(installIso, true)
        def tmpDir = new File(System.properties['java.io.tmpdir'] + "${File.separatorChar}provisioning-${System.currentTimeMillis()}")
        tmpDir.mkdirs()
        for (Iso9660FileEntry entry : image) {
            if (entry.isDirectory()) {
                new File(tmpDir, entry.path).mkdirs()
            } else {
                new File(tmpDir, entry.path) << image.getInputStream(entry)
            }
        }
        def isolinuxPath = findIsolinuxPath(image)
        def isolinuxDir = new File(tmpDir, isolinuxPath).parentFile

        def ksDir = new File(tmpDir, "ks")
        if (!ksDir.exists()) {
            ksDir.mkdirs()
        }
        def output = new File(project.buildDir, "${project.name}-${project.version}.iso")

        ISO9660RootDirectory.MOVED_DIRECTORIES_STORE_NAME = "rr_moved"
        ISO9660RootDirectory root = new ISO9660RootDirectory()

        def ksTxt = new File(project.buildDir, "kickstart${File.separatorChar}ks.cfg").text
        def ksFile = new File(ksDir, "ks.cfg")
        ksFile.delete()
        ksFile << ksTxt

        def isolinuxCfg = new File(isolinuxDir, "isolinux.cfg")
        isolinuxCfg.delete()
        isolinuxCfg << getClass().getResourceAsStream("${File.separatorChar}boot.cfg").text

        root.addContentsRecursively(tmpDir)

        def handler = new ISOImageFileHandler(output)
        CreateISO iso = new CreateISO(handler, root)
        def iso9660config = new ISO9660Config().with {
            allowASCII false
            restrictDirDepthTo8 true
            volumeID = "Bootable Install Image"
            forceDotDelimiter false
            interchangeLevel = 3
            it
        }
        def rrConfig = new RockRidgeConfig().with {
            mkisofsCompatibility = false
            hideMovedDirectoriesStore true
            forcePortableFilenameCharacterSet true
            it
        }
        def jolietConfig = new JolietConfig().with {
            volumeID = "INSTALL"
            forceDotDelimiter false
            it
        }
        def elToritoConfig = new ElToritoConfig(
                new File(isolinuxDir, "isolinux.bin"),
                ElToritoConfig.BOOT_MEDIA_TYPE_NO_EMU,
                ElToritoConfig.PLATFORM_ID_X86, "isoTest", 4,
                ElToritoConfig.LOAD_SEGMENT_7C0)
        elToritoConfig.genBootInfoTable = true

        iso.process(iso9660config, rrConfig, jolietConfig, elToritoConfig)
        tmpDir.delete()
    }

    private static String findIsolinuxPath(Iso9660FileSystem image) {
        def path = null
        for (Iso9660FileEntry entry : image) {
            if (entry.name == "isolinux.bin") {
                path = entry.path
                break
            }
        }
        if (!path) {
            throw new RuntimeException("Could not locate isolinux.bin within image!")
        }
        path
    }

}
