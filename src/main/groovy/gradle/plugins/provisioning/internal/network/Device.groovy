package gradle.plugins.provisioning.internal.network

import gradle.plugins.provisioning.types.BootProto

/**
 * User: danielwoods
 * Date: 12/8/13
 */
class Device {
  String name
  BootProto bootproto
  Boolean onboot = Boolean.TRUE
  Boolean ipv6 = Boolean.FALSE

  String ip
  String netmask
  String gateway
  String nameserver
  String hostname

  Device(String name) {
    this.name = name
  }
}
