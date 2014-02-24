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
package gradle.plugins.provisioning.tasks.aws.processes

class AwsImportInstanceProcess {

    private final String command

    String stdout
    String stderr

    AwsImportInstanceProcess(String bucket, String accessKeyId, String accessSecret, String diskFile, boolean x64) {
        def bin = "ec2-import-instance"
        def args = [
                "$diskFile",
                '-f RAW',
                '-t m3.xlarge',
                (x64 ? '-a x86_64' : '-a i386'),
                "--bucket $bucket",
                "-o $accessKeyId",
                "-w $accessSecret",
                '-p Linux'
        ]
        this.command = "$bin ${args.join(' ')}"
    }

    void exec() {
        def process = this.command.execute()
        process.waitFor()
        this.stdout = process.inputStream.text
        this.stderr = process.errorStream.text
    }

}