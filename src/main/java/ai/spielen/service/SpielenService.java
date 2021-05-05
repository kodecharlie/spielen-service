package ai.spielen.service;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@RestController
@SpringBootApplication
@EnableEurekaServer
public class SpielenService {
   public static void main(String[] args) {
      SpringApplication.run(SpielenService.class, args);
   }

   @GetMapping("/test-endpoint")
   public String testEndpoint(@RequestParam(value = "name", defaultValue = "World") String name)
   {
      Region region = Region.US_WEST_2;
  
      ProfileCredentialsProvider pcp = ProfileCredentialsProvider.create("AWS_CREDENTIALS_PROFILE_GOES_HERE");
      S3Client s3 = S3Client.builder().credentialsProvider(pcp).region(region).build();
  
      String bucket = "bucket" + System.currentTimeMillis();
      String key = "key";
  
      tutorialSetup(s3, bucket, region);
  
      System.out.println("Uploading object...");
      s3.putObject(PutObjectRequest.builder().bucket(bucket).key(key).build(),
          RequestBody.fromString("Testing with the AWS SDK for Java"));
  
      System.out.println("Upload complete");
      System.out.printf("%n");
  
      cleanUp(s3, bucket, key);
  
      System.out.println("Closing the connection to Amazon S3");
      s3.close();
      System.out.println("Connection closed");
      System.out.println("Exiting...");
  
      return String.format("Done testing %s!", name);
   }

   private void tutorialSetup(S3Client s3Client, String bucketName, Region region) {
      try {
        s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName)
            .createBucketConfiguration(CreateBucketConfiguration.builder().locationConstraint(region.id()).build())
            .build());
        System.out.println("Creating bucket: " + bucketName);
        s3Client.waiter().waitUntilBucketExists(HeadBucketRequest.builder().bucket(bucketName).build());
        System.out.println(bucketName + " is ready.");
        System.out.printf("%n");
      } catch (S3Exception e) {
        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
      }
    }

    private void cleanUp(S3Client s3Client, String bucketName, String keyName) {
      System.out.println("Cleaning up...");
      try {
        System.out.println("Deleting object: " + keyName);
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(bucketName).key(keyName).build();
        s3Client.deleteObject(deleteObjectRequest);
        System.out.println(keyName + " has been deleted.");
        System.out.println("Deleting bucket: " + bucketName);
        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(bucketName).build();
        s3Client.deleteBucket(deleteBucketRequest);
        System.out.println(bucketName + " has been deleted.");
        System.out.printf("%n");
      } catch (S3Exception e) {
        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
      }

      System.out.println("Cleanup complete");
      System.out.printf("%n");
    }
}
