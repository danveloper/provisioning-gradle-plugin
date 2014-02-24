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

        assert apiUrl, "AWS API URL Cannot be null. [env/sys: AWS_API_URL]"
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
