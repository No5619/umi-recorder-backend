package cloudfront;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CloudFrontUtil.class)
class CloudFrontUtilTest {
    @Autowired
    private CloudFrontUtil cloudFrontUtil;
    @Test
    void test() throws NoSuchAlgorithmException, InvalidKeySpecException {
        System.out.println(
                cloudFrontUtil.getSignedUrl("https://d2rlmwlvrl17f4.cloudfront.net/vod_test.mp4")
        );
    }

}