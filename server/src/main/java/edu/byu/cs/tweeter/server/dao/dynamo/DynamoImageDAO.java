package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.server.dao.abstractDAO.ImageDAO;

public class DynamoImageDAO implements ImageDAO {
    @Override
    public String storeImage(String alias, String image_string) {
        AmazonS3 s3 = AmazonS3ClientBuilder
                .standard()
                .withRegion("us-east-2")
                .build();

        byte[] byteArray = Base64.getDecoder().decode(image_string);

        ObjectMetadata data = new ObjectMetadata();

        data.setContentLength(byteArray.length);

        data.setContentType("image/jpeg");

        PutObjectRequest request = new PutObjectRequest("340-tweeter-bucket", alias, new ByteArrayInputStream(byteArray), data)
                .withCannedAcl(CannedAccessControlList.PublicRead);

        s3.putObject(request);

        return "https://340-tweeter-bucket.s3.us-east-2.amazonaws.com/" + alias;
    }
}
