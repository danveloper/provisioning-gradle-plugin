package gradle.plugins.provisioning.virtualbox

/**
 * User: danielwoods
 * Date: 12/9/13
 */
class VirtualBoxGuestDetail {
  String name
  String apiUrl
  Boolean x64 = Boolean.TRUE
  Long memory
  Long disk

}
