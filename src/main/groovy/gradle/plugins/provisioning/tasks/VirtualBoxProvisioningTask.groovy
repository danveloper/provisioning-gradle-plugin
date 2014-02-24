package gradle.plugins.provisioning.tasks

import gradle.plugins.provisioning.virtualbox.VirtualBoxMachineDetails
import java.nio.file.Path
import java.nio.file.Paths
import org.virtualbox_4_1.AccessMode
import org.virtualbox_4_1.DeviceType
import org.virtualbox_4_1.IMachine
import org.virtualbox_4_1.IMedium
import org.virtualbox_4_1.ISession
import org.virtualbox_4_1.IVirtualBox
import org.virtualbox_4_1.LockType
import org.virtualbox_4_1.NetworkAttachmentType
import org.virtualbox_4_1.SessionState
import org.virtualbox_4_1.StorageBus
import org.virtualbox_4_1.VirtualBoxManager

class VirtualBoxProvisioningTask extends AbstractProvisioningTask {

    void provision() {
        if (provisioning.serverDetails) {
            def deployer = new Deployer()
            deployer.run()
        }
    }

    String getIsoPath() {
        new File(project.buildDir, "${project.name}-${project.version}.iso").absolutePath
    }

    class Deployer implements Runnable {
        private final IVirtualBox box
        private final Path workingDir
        private final VirtualBoxManager manager
        private final ISession session
        private final VirtualBoxMachineDetails machineDetails

        private IMachine machine

        Deployer() {
            this.machineDetails = provisioning.serverDetails

            this.manager = VirtualBoxManager.createInstance(machineDetails.home)
            manager.connect machineDetails.apiUrl, machineDetails.username, machineDetails.password
            this.box = this.manager.VBox
            this.session = manager.sessionObject
            this.workingDir = Paths.get(box.settingsFilePath).normalize().parent
        }

        private void withLockAndSave(Closure closure) {
            machine.lockMachine session, LockType.Write
            closure.call()
            session.machine.saveSettings()
            session.unlockMachine()
        }

        void server() {
            final descriptor = "${machineDetails.name}.vbox"
            this.machine = box.createMachine(descriptor, machineDetails.name,
                    machineDetails.x64 ? 'Linux_64' : 'Linux', null, true).with {
                memorySize = machineDetails.memory
                addStorageController "IDE", StorageBus.IDE
                addStorageController "SATA", StorageBus.SATA
                box.registerMachine it
                it
            }
        }

        void harddisk() {
            def vdi = getHardDiskFileName("vdi")
            IMedium disk = box.createHardDisk(null, vdi)
            disk.createBaseStorage(machineDetails.disk, 0).waitForCompletion(-1)
            IMedium opened = box.openMedium(vdi, DeviceType.HardDisk, AccessMode.ReadWrite, true)

            withLockAndSave {
                IMachine mutable = session.machine
                mutable.attachDevice "SATA", 0, 0, DeviceType.HardDisk, opened
                mutable.saveSettings()
            }
        }

        String getHardDiskFileName(String format) {
            workingDir.resolve("${machineDetails.name}.$format").toString()
        }

        void cloneHardDiskToRaw() {
            "VBoxManage clonehd ${getHardDiskFileName('vdi')} ${getHardDiskFileName('raw')} --format RAW"
                    .execute()
                    .waitFor()
        }

        void network() {
            withLockAndSave {
                def adapter = session.machine.getNetworkAdapter(0)
                adapter.attachmentType = NetworkAttachmentType.NAT
                adapter.bridgedInterface = box.host.networkInterfaces.get(0).name
            }
        }

        void attachIso() {
            withLockAndSave {
                IMedium dvd = box.openMedium(isoPath, DeviceType.DVD, AccessMode.ReadOnly, true)
                session.machine.attachDevice "IDE", 0, 0, DeviceType.DVD, dvd
            }
        }

        void boot() {
            machine
                    .launchVMProcess(session, "gui", null)
                    .waitForCompletion(-1)
        }

        boolean isComplete() {
            session.state == SessionState.Unlocked
        }

        void run() throws Exception {
            server()
            harddisk()
            network()
            attachIso()
            boot()
            machineDetails.deployer = this
        }
    }

}
