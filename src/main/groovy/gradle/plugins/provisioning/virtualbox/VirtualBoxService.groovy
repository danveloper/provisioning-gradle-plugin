package gradle.plugins.provisioning.virtualbox

import org.virtualbox_4_1.AccessMode
import org.virtualbox_4_1.DeviceType
import org.virtualbox_4_1.IMachine
import org.virtualbox_4_1.IMedium
import org.virtualbox_4_1.INetworkAdapter
import org.virtualbox_4_1.IProgress
import org.virtualbox_4_1.ISession
import org.virtualbox_4_1.IVirtualBox
import org.virtualbox_4_1.LockType
import org.virtualbox_4_1.NetworkAttachmentType
import org.virtualbox_4_1.StorageBus
import org.virtualbox_4_1.VirtualBoxManager


/**
 * User: danielwoods
 * Date: 12/9/13
 */
class VirtualBoxService {
  private final IVirtualBox box
  private final VirtualBoxManager manager
  private final ISession session

  private IMachine machine

  VirtualBoxService(String url, String home=null, String user=null, String pass=null) {
    manager = VirtualBoxManager.createInstance(home)
    manager.connect(url, user, pass)
    this.box = manager.VBox
    this.session = manager.sessionObject
  }

  void newServer(String name, Long memorySize, Long diskSize, Boolean x64 = Boolean.TRUE) {
    provisionServer name, memorySize, x64
    this.machine.lockMachine session, LockType.Write
    IMachine mutable = session.machine
    mutable.attachDevice("SATA", 0, 0, DeviceType.HardDisk, createHardDisk(name, diskSize))
    mutable.saveSettings()
    session.unlockMachine()
  }

  void provisionServer(String name, Long memorySize, Boolean x64) {
    this.machine = box.createMachine("${name}.vbox", "$name", x64 ? "Linux_64" : "Linux", null, true)
    this.machine.memorySize = memorySize
    this.machine.addStorageController("IDE", StorageBus.IDE)
    this.machine.addStorageController("SATA", StorageBus.SATA)
    box.registerMachine(this.machine)
  }

  IMedium createHardDisk(String name, Long diskSize) {
    def path = new File(box.getSettingsFilePath()).parentFile.absolutePath
    IMedium disk = box.createHardDisk(null, "${path}${File.separatorChar}${name}.vdi")
    def progress = disk.createBaseStorage(diskSize, 0)
    progress.waitForCompletion(-1)
    box.openMedium("${path}${File.separatorChar}${name}.vdi", DeviceType.HardDisk, AccessMode.ReadWrite, true)
  }

  void bridgeNetwork() {
    this.machine.lockMachine session, LockType.Write
    INetworkAdapter networkAdapter = session.machine.getNetworkAdapter(0)
    networkAdapter.attachmentType = NetworkAttachmentType.Bridged
    networkAdapter.bridgedInterface = box.host.networkInterfaces.get(0).name
    session.machine.saveSettings()
    session.unlockMachine()
  }

  void boot() {
    IProgress progress = machine.launchVMProcess(session, "gui", null)
    progress.waitForCompletion(-1)
  }

  void attachISO(String path) {
    this.machine.lockMachine session, LockType.Write
    IMachine mutable = session.machine
    IMedium dvd = box.openMedium(path, DeviceType.DVD, AccessMode.ReadOnly, true)
    mutable.attachDevice "IDE", 0, 0, DeviceType.DVD, dvd
    mutable.saveSettings()
    session.unlockMachine()
  }

  void release() {
    session.unlockMachine()
  }

}
