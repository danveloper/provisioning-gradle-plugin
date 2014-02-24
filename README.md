Provisioning Gradle Plugin
==========================

Gradle Plugin for Driving Server Provisioning Through Configuration

Tasks
---

The plugin provides a few tasks dedicated to the assembly of installation media, as well as the automation of server provisioning. The plugin exposes a DSL extension to the Gradle build script, which will be used as a descriptor when generating the installation's kickstart configuration.

Given a configuration like the sample below, the `gradle provision` command will produce an installation ISO that is configured for unattended installation.


Sample Build Script
---

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

Server Deployment
---

The plugin also provides tasks for automating the server's deployment to VirtualBox or Amazon AWS.

### VirtualBox

In order to trigger deployment to VirtualBox, you will need to add the `vbox` configuration block to the `provisioning` section of your build script. 

```groovy
provisioning {
  ...
  
  vbox {
    apiUrl   = "http://localhost:18083" // required
    name     = "web" // required
    home     = "/opt/vbox" // optional
    username = "user" // optional
    password = "pass" // optional
  }
  
  ...
}
```

To facilitate deployment to VirtualBox, the VirtualBox binaries will need to be in the build user's PATH, and as well the VirtualBox Web API server will need to be running. After VirtualBox is installed, use the `vboxwebsrv` command to start the Web API. Please read the [VirtualBox SDK](http://download.virtualbox.org/virtualbox/SDKRef.pdf) manual for a full discussion on VirtualBox authentication. As a quick-start option, you can disable authenitcation with the following command: `VBoxManage setproperty websrvauthlibrary null`.

### Amazon AWS

The plugin's AWS integration will provide the necessary steps to deploy a server from the installation media, upload that instance to AWS, and create an Amazon Machine Image (AMI) from the produced instance. In order to trigger deployment to AWS, you will need to have the `aws` *and* `vbox` configuration blocks in the `provisioning` section of your build script.

```groovy
provisioning {
  ...
  
  vbox {
    apiUrl   = "http://localhost:18083" // required
    name     = "web" // required
    home     = "/opt/vbox" // optional
    username = "user" // optional
    password = "pass" // optional
  }
  
  aws {
    apiUrl       = "ec2.us-east-1.amazonaws.com" // optional. This is default as defined by the AWS SDK.
    bucket       = "s3-bucket" // required. This is where the instance's hard disk will be stored during conversion.
    credentials  = "/home/user/.aws-credentials-master" // conditionally optional. This is a properties file w/ the AWSAccessKeyID and AWSSecretKey key/value pairs.
    accessKeyId  = "AKI...." // conditionally optional. If no "credentials" directive is applied, this is required!
    accessSecret = "wUQ...." // conditionally optional. If no "credentials" directive is applied, this is required!
  }
  
  ...
}
```

The `vbox` block is necessary because before we can ship the server instance to AWS, we need to have a machine to ship. In order to bridge the gap from the produced installation media to a fully-available server deployment, the installation is deployed within VirtualBox. The plugin recognizes when the installation has completed, and proceeds to conver the VirtualBox disk and ship it to AWS.

In order to facilitate the bridge, both the VirtualBox binaries and the AWS EC2 Tools binaries will need to be on the build user's PATH. This is necessary due to a short-coming in both the VirtualBox and AWS SDKs. In the case of the former, the mechanism to initiate a hard disk conversion isn't support, and in the latter the mecanism deliver the image is dependent on external commands from the Tools binaries.

The build user will also need the `EC2_HOME`, `EC2_CERT`, and `EC2_PRIVATE_KEY` environment variables exported at build time. These are used in obscure ways by both the AWS SDK and the Tools binaries. Please refer to the _Getting Started_ section of the [AWS SDK Documentation](http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/java-dg-setup.html) for instructions on getting the necessary credentials for the plugin to success.

License
---

This project and all its contents are licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

Author
---

Dan Woods; t:[@danveloper](http://twitter.com/danveloper)

Date
---
10 Dec 2013
