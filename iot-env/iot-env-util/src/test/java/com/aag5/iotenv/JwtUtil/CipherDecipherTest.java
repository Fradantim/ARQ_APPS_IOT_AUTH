package com.aag5.iotenv.JwtUtil;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.aag5.iotenv.model.Device;
import com.aag5.iotenv.util.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class CipherDecipherTest {
	
	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
		/*byte[] b1 = Base64.getDecoder().decode("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCWI7U6Jvw1YatAvYAyt9HkZlDCAXODCleY3byl0Jjj+pt2KzCG6M8so91+oly8cYq6G/zP8hflTUsEQigWdywTTVKawaRJl7t/HRWF71MTXh7qAnxy/30vYCcvDFrofZ6qwHRtolOFUZDfM8SaRgHEUMSsDb8jXKDD9lPIqepLL9yzcGI1KmfiUZg2u08SF+o2Wv6FsmH+AM8vWlg/wppYrQLkwgtZePBTl7DEK0zauJNm7lP1R/mY5r9oW3sk7RstquQ0PAwVBy07Cr8OpLzcVUqDksH6C2fWtpbD94lQN+RIcFwvawOWPlu8eYiEKkZlqa5KtY6y1zTip31/1PNFAgMBAAECggEBAIfxVQqOeharlLBN5WIG5tlzevPu9HRWTPJw83r/4S+rnk/k00+UROIajVc4E9fcxsu0w7hVcCWDUsVMUZfl19ayUHUnmGW6KV7zKqXXCNWkh0FDdwN5KhRXb6M+AKI5/AyHreQG8rDoJdCzwvQdJ0RmDW3sFIEkQbytsTckAqyICWK9p6pRZUh9NTZ4u07HcrGsXc1EswNd8ciG4aqO5QPFranqZIowYR6gzy91qhGPBHePiFPaW9zo3SawqlR5giaQtBKQBXMjltzsC3Wrbk2qbniG30o5y9b6jqFIbJIqDIbfOXKBa1cW7H/8eAxhyMNibiuT+Jb28oUnU0D7ioECgYEA1dj6bAA3JC4YuD7BGm9XcCpBXfk1rBOjABTUw1NeD79YPM/7Ujs5xdVoGDYcRN+RIXG2xbHRSWz8motlcFcbwbUDtKf8BeaDS0wAo8FrQ5h8WDxfi/5ePnIoVyuM9I0rAZQgkJIvXqOTWfXiLoF+bX0WAgToLDBe/hSCcTu+jgkCgYEAs7vwLdeE4F4Vk/jz3WOZNj3fMu2zDNGiPkbIJdZRu9H4+vTwE4olyOCS99yAx4Ohhqv7ANCTWjhaeY9VZav2E8WjowOhTIXUGW2sG+GIE72Jesx3UrFzmjZd8Mkirr0wZ7I1HY7LUFq7a2y3GnfnaWjHXd4mjGDj9QpTz61XCl0CgYEApdAYXKXOG7+iazo9gMHUiqQ5CySw+TxAp09/qfR6ertjL8QUmy5RHZTboepTARRY9BW4IpI+NTDMrQhpHzJr00cIGIwoXmreYwQLeu3+ver+f2xQqbwSV1ks7mpWYUkUj6TLm02+bSNondiKCIVgAoV7CxXC8ICRJ3G/sw0gNAECgYBxcsR0ux/1W421PCP57z8rPLyg8ebdxYjqVFVLhM9MhYnqJidPvhiBhjdeWoGy7cRMNEwYRI9uGDoLlsypg3StIEhcwhLTMXointZPNvlktHON3nfG65lzkrOTI/JPZ72hBpx+Tr7Itqysbw/YliysnWWSO4ILg7D/4AftA5X4RQKBgH92q7AaF+wEcaSNi+W3sLxHh9HbYgHT+aFDChh3F3dtYTDhy7YpPkz/plib4sVAWVbd4A32WYVTP1AqhvnIbk0eZ9DICFw87PVfXMkArYFKcoczg80YX0Af8+DDLebO5bB7Ehj8X5SIbmdvqoIajroi9Hl1lpEhPnUvQxghtp2c"); 
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b1); 
		KeyFactory kf = KeyFactory.getInstance(JwtUtil.ALGORITHM.getFamilyName()); 
		PrivateKey privateKey = kf.generatePrivate(spec);
		
        /*System.out.println("generating keys");
        Map<String, Object> rsaKeys = null;

        try {
            rsaKeys = getRSAKeys();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        PublicKey publicKey = (PublicKey) rsaKeys.get("public");
        PrivateKey privateKey = (PrivateKey) rsaKeys.get("private");

        System.out.println("PuK "+Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        System.out.println("PrK "+Base64.getEncoder().encodeToString(privateKey.getEncoded()));
        
        String publicKeyStringB64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        
        System.out.println("generated keys");

        Device d= new Device("IDAAA", "DeName");
        
        String token = JwtUtil.generateToken(d, privateKey);
        System.out.println("token: "+token);
        try {
			System.out.println(JwtUtil.validateToken(token, publicKeyStringB64));
			System.out.println(JwtUtil.validateToken(token+"0", publicKeyStringB64));
			System.out.println(JwtUtil.getDeviceId(token));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
       
        
        /*
        String token = generateToken(privateKey);
        System.out.println("Generated Token:\n" + token);

        verifyToken(token, publicKey);
        */
    }

    public static String generateToken(PrivateKey privateKey) {
        String token = null;
        try {
            Map<String, Object> claims = new HashMap<String, Object>();

            // put your information into claim
            claims.put("id", "xxx");
            claims.put("role", "user");
            claims.put("created", new Date());

            token = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.RS512, privateKey).compact();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }

    // verify and get claims using public key

    public static Claims verifyToken(String token, PublicKey publicKey) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();

            System.out.println(claims.get("id"));
            System.out.println(claims.get("role"));

        } catch (Exception e) {

            claims = null;
        }
        return claims;
    }

    // Get RSA keys. Uses key size of 2048.
    public static Map<String, Object> getRSAKeys() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put("private", privateKey);
        keys.put("public", publicKey);
        return keys;
    }
}
