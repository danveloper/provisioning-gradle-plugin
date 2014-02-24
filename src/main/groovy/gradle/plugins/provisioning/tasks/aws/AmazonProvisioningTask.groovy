package gradle.plugins.provisioning.tasks.aws

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.CreateImageRequest
import com.amazonaws.services.ec2.model.DescribeConversionTasksRequest
import com.amazonaws.services.ec2.model.DescribeImagesRequest
import gradle.plugins.provisioning.aws.AwsDeployConfiguration
import gradle.plugins.provisioning.tasks.AbstractProvisioningTask
import gradle.plugins.provisioning.tasks.aws.processes.AwsImportInstanceProcess
import gradle.plugins.provisioning.virtualbox.VirtualBoxMachineDetails

class AmazonProvisioningTask extends AbstractProvisioningTask {

    void provision() {
        def deployer = getProvisioning().serverDetails.deployer

        println "Pooling for installation completion..."
        Thread.start {
            while (true) {
                if (deployer.complete) {
                    break
                } else {
                    sleep 1000
                }
            }
        }.join()

        deployer.cloneHardDiskToRaw()

        def amiCreator = new AmiCreator(deployer.getHardDiskFileName('raw'), getProvisioning().awsDetails)
        try {
            amiCreator.run()
        } catch (e) {
            deployer.release()
        }
    }

    VirtualBoxMachineDetails getMachineDetails() {
        getProvisioning().serverDetails
    }

    class AmiCreator implements Runnable {
        final String vboxDiskFile
        final AmazonEC2Client client
        final AwsDeployConfiguration config

        private String instanceId

        AmiCreator(String vboxDiskFile, AwsDeployConfiguration config) {
            this.vboxDiskFile = vboxDiskFile
            this.config = config

            def credentials = new BasicAWSCredentials(config.accessKeyId, config.accessSecret)
            this.client = new AmazonEC2Client(credentials)
        }

        void importInstance() {
            println "Importing instance."
            def process = new AwsImportInstanceProcess(config.bucket, config.accessKeyId, config.accessSecret,
                    vboxDiskFile, getMachineDetails().x64)
            process.exec()
            String taskId = (process.stdout =~ /TaskId(.*)ExpirationTime/)[0][1].trim()
            String instanceId = (process.stdout =~ /InstanceID(.*)\n/)[0][1].trim()

            println "Polling for instance ($instanceId) import completion"
            Thread.start {
                while (true) {
                    def conversionResult = client
                            .describeConversionTasks(new DescribeConversionTasksRequest().withConversionTaskIds(taskId))
                    def status = conversionResult.conversionTasks[0].state
                    if (status.toLowerCase() != 'active') {
                        break
                    } else {
                        sleep 1000
                    }
                }
            }.join()

            println "Instance imported!"

            this.instanceId = instanceId
        }

        void createAmi() {
            println "Creating AMI."
            def name = getMachineDetails().name
            if (name.length() < 3) {
                (3 - name.length()).times { name += "0" }
            }
            def createImageRequest = new CreateImageRequest(instanceId, name)
            def createImageResult = client.createImage(createImageRequest)

            println "Polling for AMI ($createImageResult.imageId) availability..."

            Thread.start {
                while (true) {
                    def req = new DescribeImagesRequest().withImageIds(createImageResult.imageId)
                    def img = client.describeImages(req).images[0]
                    if (img.state == "available") {
                        break
                    } else {
                        sleep 1000
                    }
                }
            }.join()

            println "AMI is available!"
        }

        void run() {
            importInstance()
            createAmi()
        }
    }
}
