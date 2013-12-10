provisioning-gradle-plugin
==========================

Gradle Plugin for Driving Server Provisioning Through Configuration

```groovy
import static gradle.plugins.provisioning.types.BootProto.*

apply plugin: 'provisioning'

version = "1.0.RELEASE"

buildscript {
  repositories {
    mavenLocal()
    mavenCentral()
  }
  dependencies {
    classpath group: 'com.danveloper', name: 'provisioning-gradle-plugin', version: '0.1-SNAPSHOT'
  }
}

provisioning {
  installImage = "http://www.gtlib.gatech.edu/pub/centos/6.4/isos/x86_64/CentOS-6.4-x86_64-netinstall.iso"
  
  // generated with "grub-crypt"
  rootpw       = '$6$M2N0GvDMV.hro4Nj$6/4W1SmGuWs8fscbdNLfp4fGFpEt93Y7kCNi8jnjN5JIkPy8YJGkkjCwImyXtCiheMyAkUR24IPgcrfeIliB7/'

  vbox {
    apiUrl = "http://localhost:18083"
    name   = "web"
    x64    = true
    memory = 1024
    disk   = 8589934592
  }

  network {
    device("eth0") {
      bootproto = DHCP
      onboot    = true
      ipv6      = false
    }
  }

  partitioning {
    clear(init: true)

    part {
      mntpoint = "/"
      fstype   = "ext4"
      size     = 1
      grow     = true
    }

    part {
      mntpoint    = "swap"
      recommended = true
    }
  }

  packages {
    // or, perhaps more preferrably, a network-local repo
    url "http://www.gtlib.gatech.edu/pub/centos/6.4/os/x86_64/"

    // some other repo
    repo("extra") {
      "http://192.168.0.106/project-repo"
    }

    // kickstart package groups
    group "base"
    group "core"
    group "console-internet"
    group "server-platform"

    // These packages come from the "extra" repo
    pkg   "jdk"
    pkg   "apache-tomcat"
    pkg   "hello-webapp"
  }

  postInstall {
    '''\
      |echo "export JAVA_HOME=/usr/java/latest" >> /etc/profile.d/java.sh
      |rm /usr/bin/java && ln -s /usr/java/latest/bin/java /usr/bin/java
    '''.stripMargin()
  }
}

```
