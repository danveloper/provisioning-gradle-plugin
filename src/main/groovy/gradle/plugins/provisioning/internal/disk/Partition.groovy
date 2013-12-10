package gradle.plugins.provisioning.internal.disk

/**
 * User: danielwoods
 * Date: 12/8/13
 */
class Partition {
  String mntpoint
  String fstype       = "ext4"
  Integer size        = 1
  Boolean grow        = Boolean.FALSE
  Boolean recommended = Boolean.FALSE
}
