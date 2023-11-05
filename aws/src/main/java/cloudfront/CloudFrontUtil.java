package cloudfront;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;
import software.amazon.awssdk.services.cloudfront.url.SignedUrl;

import javax.annotation.PostConstruct;

@Component
public class CloudFrontUtil {
    //注意PRIVATE_KEY_STR的格式，java只認得PKCS8的格式。
    //PKCS#1:
    //-----BEGIN RSA PRIVATE KEY-----
    //...
    //-----END RSA PRIVATE KEY-----
    //
    //PKCS#8:
    //-----BEGIN PRIVATE KEY-----
    //...
    //-----END PRIVATE KEY-----
    //
    //SEC1:
    //-----BEGIN EC PRIVATE KEY-----
    //...
    //-----END EC PRIVATE KEY-----
    @Value(("${private-key}"))
    private String privateKey;
    @Value(("${public-key}"))
    private String publicKey;
    @Value(("${expiration-time}"))
    private int expirationTime;

    private Instant expirationInstant;
    @PostConstruct
    public void postConstruct() {
        expirationInstant = Instant.now().plusSeconds(expirationTime);
    }

    private PrivateKey getPrivateKey(String rsa2048Str) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Decode the RSA 2048 string from Base64.
        byte[] privateKeyBytes = Base64.getDecoder().decode(
                rsa2048Str.replaceAll("-----BEGIN PRIVATE KEY-----", "")
                        .replaceAll("-----END PRIVATE KEY-----", "")
                        .replaceAll("\n", "")
                        .replaceAll("\r", "")
                        .getBytes()
        );

        // Create a PKCS8EncodedKeySpec object from the decoded bytes.
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

        // Get a KeyFactory instance for the RSA algorithm.
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // Generate a PrivateKey object from the PKCS8EncodedKeySpec object.
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        return privateKey;
    }

    //srcUrl = https://<cloudfront-domain-name>/<s3-ObjectKey>
    private CannedSignerRequest getCannedRequest(String srcUrl) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return CannedSignerRequest.builder()
                .resourceUrl(srcUrl)
                .privateKey(getPrivateKey(privateKey))
                .keyPairId(publicKey)
                .expirationDate(expirationInstant)
                .build();
    }

    public String getSignedUrl(String srcUrl) throws NoSuchAlgorithmException, InvalidKeySpecException {
        CloudFrontUtilities cloudFrontUtilities = CloudFrontUtilities.create();
        SignedUrl signedUrl = cloudFrontUtilities.getSignedUrlWithCannedPolicy(
                getCannedRequest(srcUrl)
        );
        return signedUrl.url();
    }
}
