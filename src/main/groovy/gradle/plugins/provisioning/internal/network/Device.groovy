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
package gradle.plugins.provisioning.internal.network

import gradle.plugins.provisioning.types.BootProto

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
