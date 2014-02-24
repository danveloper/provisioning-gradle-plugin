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
                "--bucket $awsApiConfiguration.bucket",
                "-o $awsApiConfiguration.accessKeyId",
                "-w $awsApiConfiguration.accessSecret",
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