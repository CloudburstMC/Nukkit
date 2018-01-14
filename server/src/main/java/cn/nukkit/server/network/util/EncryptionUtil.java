package cn.nukkit.server.network.util;

import cn.nukkit.server.network.minecraft.packet.ServerToClientHandshakePacket;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import javax.crypto.KeyAgreement;
import java.net.URI;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.util.Base64;

@Log4j2
@UtilityClass
public class EncryptionUtil {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static byte[] getServerKey(KeyPair serverPair, PublicKey key, byte[] token) throws InvalidKeyException {
        byte[] sharedSecret = getSharedSecret(serverPair, key);

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }

        digest.update(token);
        digest.update(sharedSecret);
        return digest.digest();
    }

    private static byte[] getSharedSecret(KeyPair serverPair, PublicKey clientKey) throws InvalidKeyException {
        KeyAgreement agreement;
        try {
            agreement = KeyAgreement.getInstance("ECDH");
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }

        agreement.init(serverPair.getPrivate());
        agreement.doPhase(clientKey, true);
        return agreement.generateSecret();
    }

    public static ServerToClientHandshakePacket createHandshakePacket(KeyPair pair, byte[] token) {
        ECPrivateKey privKey = (ECPrivateKey) pair.getPrivate();
        URI x5u = URI.create(Base64.getEncoder().encodeToString(pair.getPublic().getEncoded()));
        ServerToClientHandshakePacket packet = new ServerToClientHandshakePacket();

        JWSObject jwt = new JWSObject(
                new JWSHeader.Builder(JWSAlgorithm.ES384).x509CertURL(x5u).build(),
                new Payload("{salt:\"" + Base64.getEncoder().encodeToString(token) + "\"")
        );

        try {
            JWSSigner signer = new ECDSASigner(privKey);
            jwt.sign(signer);
        } catch (JOSEException e) {
            throw new RuntimeException("Unable to sign JWT", e);
        }

        packet.setJwt(jwt.serialize());
        return packet;
    }

    public static byte[] generateRandomToken() {
        byte[] token = new byte[16];
        secureRandom.nextBytes(token);
        return token;
    }

    public static long generateServerId() {
        return secureRandom.nextLong();
    }
}
