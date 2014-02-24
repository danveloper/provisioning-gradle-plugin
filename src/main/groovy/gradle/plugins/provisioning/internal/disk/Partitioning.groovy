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
package gradle.plugins.provisioning.internal.disk

class Partitioning {
    private static final String PARTITION_CONFIG_NAME = "part"
    private static final String PARTITION_CLEAR_NAME = "clear"

    private Boolean clear = Boolean.FALSE
    private Boolean init = Boolean.FALSE

    List<Partition> details = []

    def invokeMethod(String name, args) {
        if (name == PARTITION_CONFIG_NAME) {
            def detail = new Partition()
            Closure clos = args[0]
            clos.delegate = detail
            clos.resolveStrategy = Closure.DELEGATE_FIRST
            clos.call()
            details << detail
        }
        if (name == PARTITION_CLEAR_NAME) {
            clear = Boolean.TRUE
            if (args.size() && args[0] instanceof Map) {
                init = args[0].init ?: Boolean.FALSE
            }
        }
    }
}
