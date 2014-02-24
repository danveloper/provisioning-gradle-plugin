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
package gradle.plugins.provisioning.aws

class AwsDeployConfiguration {
    final String apiUrl
    final String bucket
    final String accessKeyId
    final String accessSecret

    AwsDeployConfiguration(String apiUrl, String bucket, String accessKeyId, String accessSecret) {
        this.apiUrl = apiUrl
        this.bucket = bucket
        this.accessKeyId = accessKeyId
        this.accessSecret = accessSecret

        assert bucket, "AWS S3 Bucket Cannot be null. [env/sys: S3_BUCKET]"
        assert accessKeyId, "AWS Access Key ID Cannot be null."
        assert accessSecret, "AWS Access Secret cannot be null."
    }

    static class Builder {
        String apiUrl
        String bucket
        String accessKeyId
        String accessSecret

        Builder() {
        }

        void apiUrl(String apiUrl) {
            this.apiUrl = apiUrl
        }

        void bucket(String bucket) {
            this.bucket = bucket
        }

        void credentials(String file) {
            def props = new Properties()
            props.load(new FileInputStream(file))
            this.accessKeyId = props.AWSAccessKeyId
            this.accessSecret = props.AWSSecretKey
        }

        void accessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId
        }

        void accessSecret(String accessSecret) {
            this.accessSecret = accessSecret
        }

        AwsDeployConfiguration build() {
            new AwsDeployConfiguration(apiUrl, bucket, accessKeyId, accessSecret)
        }
    }
}
