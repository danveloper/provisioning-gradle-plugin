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
package gradle.plugins.provisioning.internal.dependencies

class Packages {
    List<PackageDefinition> details = []
    List<Repository> repositories = []
    String url

    void group(String name) {
        details << new PackageDefinition(isGroup: true, name: name)
    }

    void pkg(String name) {
        details << new PackageDefinition(name: name)
    }

    void repo(String name, Closure clos) {
        def repo = new Repository(name: name)
        clos.delegate = repo
        clos.resolveStrategy = Closure.DELEGATE_FIRST
        repo.url = clos.call()
        repositories << repo
    }

    void url(String url) {
        this.url = url
    }
}
