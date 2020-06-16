package com.aag5.iotenv.util;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.aag5.iotenv.model.Device;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtil {

    private static final Integer DEFAULT_JWT_LIFETIME = 1; //jwt's lifetime expressed in minutes, currently 1 minute
    public static final SignatureAlgorithm ALGORITHM = SignatureAlgorithm.RS512;
    
    public static String generateToken(Device device, PrivateKey privateKey) {
    	return generateToken(device, privateKey, DEFAULT_JWT_LIFETIME);
    }
    
    public static String generateToken(Device device, PrivateKey privateKey, Integer lifetimeMinutes) {
        String token = null;
        try {
    
            //token = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.RS512, privateKey).compact();
            token = Jwts.builder()
            		.setClaims(deviceToClaimsMap(device))
            		.setSubject(device.getId())
            		.setIssuedAt(new Date(System.currentTimeMillis()))
            		.setExpiration(new Date(System.currentTimeMillis() + lifetimeMinutes * 60 * 1000))
            		.signWith(ALGORITHM, privateKey).compact();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }
    
    public static Map<String, Object> deviceToClaimsMap(Device d) {
    	Map<String, Object> claims = new HashMap<String, Object>();
    	claims.put("deviceId", d.getId());
    	return claims;
    }
    
    public static Boolean validateToken(String token, String publicKeyB64) throws InvalidKeySpecException, NoSuchAlgorithmException {
    	return validateToken(token, generatePublicKey(publicKeyB64));
    }
    
    public static PublicKey generatePublicKey(String publicKeyB64) throws NoSuchAlgorithmException, InvalidKeySpecException {
    	byte[] b1 = Base64.getDecoder().decode(publicKeyB64); 
    	X509EncodedKeySpec spec = new X509EncodedKeySpec(b1); 
    	KeyFactory kf = KeyFactory.getInstance(ALGORITHM.getFamilyName()); 
    	return kf.generatePublic(spec);
    }
    
    public static PrivateKey generatePrivateKey(String privateKeyB64) throws NoSuchAlgorithmException, InvalidKeySpecException {
    	byte[] b1 = Base64.getDecoder().decode(privateKeyB64); 
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b1); 
		KeyFactory kf = KeyFactory.getInstance(ALGORITHM.getFamilyName()); 
		return kf.generatePrivate(spec);
    }
    
    // verify and get claims using public key
    public static Boolean validateToken(String token, PublicKey publicKey) {
        if(token != null && publicKey != null) {
        	try {
        		return !isTokenExpired(token, publicKey);
            } catch (Exception e) { }
        } else {
        	return false;
        }
        return false;
    }
    
    public static Date extractExpiration(String token, PublicKey publicKey) {
        return extractClaim(token, publicKey, Claims::getExpiration);
    }

    public static <T> T extractClaim(String token, PublicKey publicKey, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token, publicKey);
        return claimsResolver.apply(claims);
    }
    private static Claims extractAllClaims(String token, PublicKey publicKey) {
        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();
    }

    private static Boolean isTokenExpired(String token, PublicKey publicKey) {
        return extractExpiration(token, publicKey).before(new Date());
    }
    
    public static String getDeviceId(String token) {
    	return Jwts.parser().parseClaimsJwt(token.substring(0,token.lastIndexOf(".")+1)).getBody().getSubject();
    }
}