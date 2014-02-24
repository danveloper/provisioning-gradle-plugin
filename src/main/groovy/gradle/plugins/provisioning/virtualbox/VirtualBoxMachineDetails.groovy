package gradle.plugins.provisioning.virtualbox

class VirtualBoxMachineDetails extends VirtualBoxDeployConfiguration {
    final DEFAULT_VM_NAME = "VM"
    final DEFAULT_MEMORY_SIZE = 1024
    final DEFAULT_DISK_SIZE = new Long(8) * 1024**3

    String name = DEFAULT_VM_NAME
    Long memory = DEFAULT_MEMORY_SIZE
    Long disk = DEFAULT_DISK_SIZE
    Boolean x64 = Boolean.TRUE
}