//******************************************************************************************************
// JWTService.java
//
// JWT Service for account according to redfish.
//
//Copyright (C) 2022, PICMG.
//
//        This program is free software: you can redistribute it and/or modify
//        it under the terms of the GNU General Public License as published by
//        the Free Software Foundation, either version 3 of the License, or
//        (at your option) any later version.
//
//        This program is distributed in the hope that it will be useful,
//        but WITHOUT ANY WARRANTY; without even the implied warranty of
//        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//        GNU General Public License for more details.
//
//        You should have received a copy of the GNU General Public License
//        along with this program.  If not, see <https://www.gnu.org/licenses/>.
//*******************************************************************************************************


package com.redfishserver.Redfish_Server.services.jwt;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.SessionService_SessionService;
import com.redfishserver.Redfish_Server.RFmodels.AllModels.Session_Session;
import com.redfishserver.Redfish_Server.RFmodels.AllModels.ManagerAccount_ManagerAccount;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.tomcat.util.codec.binary.Base64;

import java.io.*;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
@Service
public class JWTService {

    @Autowired
    com.redfishserver.Redfish_Server.services.SessionService sessionService;
    public static final long JWT_TOKEN_EXPIRY_TIME = 5 * 60 * 60; // in seconds

    public String extractJWTUsername(String jwt) throws IOException, NoSuchAlgorithmException {
        Key publicKey = readPublicKey(new FileInputStream("src//main//java//com//redfishserver//Redfish_Server//config//certs//publickey.pem"));

        String userName = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(jwt).getBody().getSubject();
        return userName;
    }

    public Map<String, Object> extractJWTClaims(String jwt) throws IOException, NoSuchAlgorithmException {
        Key publicKey = readPublicKey(new FileInputStream("src//main//java//com//redfishserver//Redfish_Server//config//certs//publickey.pem"));
        Map<String, Object> claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(jwt).getBody();
        return claims;
    }
    public String generateToken(ManagerAccount_ManagerAccount account, String sessionId) throws IOException, NoSuchAlgorithmException {
        Map<String, Object> claims = new HashMap<>();
        claims.put("scope","Redfish.Role." + account.getRoleId());
        claims.put("jti","");
        claims.put("SessionId",sessionId);
        return buildJWT(claims, account.getUserName());
    }

    private String buildJWT(Map<String, Object> claims, String subject) throws IOException, NoSuchAlgorithmException {

        Key privateKey = readPrivateKey(new FileInputStream("src//main//java//com//redfishserver//Redfish_Server//config//certs//pkcs8.key"));

        return Jwts.builder().setClaims(claims).setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setIssuer("RedfishServer")
                .setNotBefore(new Date(System.currentTimeMillis()))
                .setAudience("")
                //.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_EXPIRY_TIME * 1000))
                .signWith(SignatureAlgorithm.RS256, privateKey).compact();
    }

    private static Key loadKey(InputStream in, Function<byte[], Key> keyParser) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String file_line;
            StringBuilder content = new StringBuilder();
            while ((file_line = reader.readLine()) != null) {
                if (!(file_line.contains("BEGIN") || file_line.contains("END"))) {
                    content.append(file_line).append('\n');
                }
            }
            byte[] encodedContent = Base64.decodeBase64(content.toString());
            return keyParser.apply(encodedContent);
        }
    }


    public static Key readPrivateKey(FileInputStream in) throws IOException, NoSuchAlgorithmException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return loadKey(in, bytes -> {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
            try {
                RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
                return privateKey;
            } catch (InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static Key readPublicKey(FileInputStream in) throws IOException, NoSuchAlgorithmException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return loadKey(in, bytes -> {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
            try {
                X509EncodedKeySpec spec =
                        new X509EncodedKeySpec(bytes);
                return keyFactory.generatePublic(spec);
            } catch (InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Boolean validateToken(String token, UserDetails userDetails) throws Exception{
        final String username = extractJWTUsername(token);
        return (username.equals(userDetails.getUsername()));
    }

    public Boolean isTokenValid(String token) throws IOException, NoSuchAlgorithmException {
        try{
            final Map<String, Object> claims = extractJWTClaims(token);
            Session_Session session = sessionService.getSessionById((String) claims.get("SessionId"));
            OffsetDateTime createdAt = session.getCreatedTime().get();
            List<SessionService_SessionService> sessionService1 = this.sessionService.getSessionService();
            long sessionTimeOut = sessionService1.get(0).getSessionTimeout();
            LocalDateTime dateTime = createdAt.toLocalDateTime();
            if (LocalDateTime.now().isAfter(dateTime.plusMinutes(sessionTimeOut))){
                sessionService.deleteMemberFromSessionCollection(session.getId());
                sessionService.deleteSession(session.getId());

                return false;
            }
            session.setCreatedTime(JsonNullable.of(OffsetDateTime.now()));
            sessionService.updateSession(session);
            return true;
        }
        catch (NullPointerException e){
            return false;
        }
    }
}
