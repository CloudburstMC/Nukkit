package cn.nukkit.network.encryption;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.JSONValue;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nukkitx.natives.aes.AesFactory;
import com.nukkitx.natives.util.Natives;
import com.nukkitx.network.util.Preconditions;
import lombok.experimental.UtilityClass;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;

/**
 * <a href="https://github.com/CloudburstMC/Protocol/blob/6b48673067d5c0e60f5e0a5a7e889dbf2aafa1a1/bedrock/bedrock-common/src/main/java/com/nukkitx/protocol/bedrock/util/EncryptionUtils.java">...</a>
 */
@UtilityClass
public class EncryptionUtils {

    private static final AesFactory AES_FACTORY;
    public static final ECPublicKey MOJANG_PUBLIC_KEY;
    public static final ECPublicKey OLD_MOJANG_PUBLIC_KEY;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String MOJANG_PUBLIC_KEY_BASE64 =
            "MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAECRXueJeTDqNRRgJi/vlRufByu/2G0i2Ebt6YMar5QX/R0DIIyrJMcUpruK4QveTfJSTp3Shlq4Gk34cD/4GUWwkv0DVuzeuB+tXija7HBxii03NHDbPAD0AKnLr2wdAp";
    private static final String OLD_MOJANG_PUBLIC_KEY_BASE64 =
            "MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAE8ELkixyLcwlZryUQcu1TvPOmI2B7vX83ndnWRUaXm74wFfa5f/lwQNTfrLVHa2PmenpGI6JhIMUJaWZrjmMj90NoKNFSNBuKdm8rYiXsfaz3K36x/1U26HpG0ZxK/V1V";
    private static final KeyPairGenerator KEY_PAIR_GEN;

    static {
        // DO NOT REMOVE THIS
        // Since Java 8u231, secp384r1 is deprecated and will throw an exception.
        String namedGroups = System.getProperty("jdk.tls.namedGroups");
        System.setProperty("jdk.tls.namedGroups", namedGroups == null || namedGroups.isEmpty() ? "secp384r1" : ", secp384r1");

        AesFactory aesFactory;
        try {
            aesFactory = Natives.AES_CFB8.get();
        } catch (NullPointerException | IllegalStateException e) {
            aesFactory = null;
        }
        AES_FACTORY = aesFactory;

        try {
            KEY_PAIR_GEN = KeyPairGenerator.getInstance("EC");
            KEY_PAIR_GEN.initialize(new ECGenParameterSpec("secp384r1"));
            MOJANG_PUBLIC_KEY = generateKey(MOJANG_PUBLIC_KEY_BASE64);
            OLD_MOJANG_PUBLIC_KEY = generateKey(OLD_MOJANG_PUBLIC_KEY_BASE64);
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeySpecException e) {
            throw new AssertionError("Unable to initialize required encryption", e);
        }
    }

    /**
     * Generate EC public key from base 64 encoded string
     *
     * @param b64 base 64 encoded key
     * @return key generated
     * @throws NoSuchAlgorithmException runtime does not support the EC key spec
     * @throws InvalidKeySpecException  input does not conform with EC key spec
     */
    public static ECPublicKey generateKey(String b64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return (ECPublicKey) KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(b64)));
    }

    /**
     * Create EC key pair to be used for handshake and encryption
     *
     * @return EC KeyPair
     */
    public static KeyPair createKeyPair() {
        return KEY_PAIR_GEN.generateKeyPair();
    }

    /**
     * Sign JWS object with a given private key.
     *
     * @param jws object to be signed
     * @param key key to sign object with
     * @throws JOSEException invalid key provided
     */
    public static void signJwt(JWSObject jws, ECPrivateKey key) throws JOSEException {
        jws.sign(new ECDSASigner(key, Curve.P_384));
    }

    /**
     * Check whether a JWS object is valid for a given public key.
     *
     * @param jws object to be verified
     * @param key key to verify object with
     * @return true if the JWS object is valid
     * @throws JOSEException invalid key provided
     */
    public static boolean verifyJwt(JWSObject jws, ECPublicKey key) throws JOSEException {
        return jws.verify(new ECDSAVerifier(key));
    }

    /**
     * Verify the validity of the login chain data from the LoginPacket
     *
     * @param chain array of JWS objects
     * @return chain validity
     * @throws JOSEException            invalid JWS algorithm used
     * @throws ParseException           invalid JWS object
     * @throws InvalidKeySpecException  invalid EC key provided
     * @throws NoSuchAlgorithmException runtime does not support EC spec
     */
    public static boolean verifyChain(JSONArray chain) throws JOSEException, ParseException, InvalidKeySpecException, NoSuchAlgorithmException {
        ECPublicKey lastKey = null;
        boolean validChain = false;
        Iterator<Object> iterator = chain.iterator();
        while (iterator.hasNext()) {
            Object node = iterator.next();
            Preconditions.checkArgument(node instanceof String, "Chain node is not a string");
            JWSObject jwt = JWSObject.parse((String) node);

            // x509 cert is expected in every claim
            URI x5u = jwt.getHeader().getX509CertURL();
            if (x5u == null) {
                return false;
            }

            ECPublicKey expectedKey = EncryptionUtils.generateKey(jwt.getHeader().getX509CertURL().toString());
            // First key is self-signed
            if (lastKey == null) {
                lastKey = expectedKey;
            } else if (!lastKey.equals(expectedKey)) {
                return false;
            }

            if (!verifyJwt(jwt, lastKey)) {
                return false;
            }

            if (validChain) {
                return !iterator.hasNext();
            }

            if (lastKey.equals(EncryptionUtils.MOJANG_PUBLIC_KEY) || lastKey.equals(EncryptionUtils.OLD_MOJANG_PUBLIC_KEY)) {
                validChain = true;
            }

            Object payload = JSONValue.parse(jwt.getPayload().toString());
            Preconditions.checkArgument(payload instanceof JSONObject, "Payload is not a object");

            Object identityPublicKey = ((JSONObject) payload).get("identityPublicKey");
            Preconditions.checkArgument(identityPublicKey instanceof String, "identityPublicKey node is missing in chain");
            lastKey = generateKey((String) identityPublicKey);
        }
        return validChain;
    }

    /**
     * Generate the secret key used to encrypt the connection
     *
     * @param localPrivateKey local private key
     * @param remotePublicKey remote public key
     * @param token           token generated or received from the server
     * @return secret key used to encrypt connection
     * @throws InvalidKeyException keys provided are not EC spec
     */
    public static SecretKey getSecretKey(PrivateKey localPrivateKey, PublicKey remotePublicKey, byte[] token) throws InvalidKeyException {
        byte[] sharedSecret = getEcdhSecret(localPrivateKey, remotePublicKey);

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }

        digest.update(token);
        digest.update(sharedSecret);
        byte[] secretKeyBytes = digest.digest();
        return new SecretKeySpec(secretKeyBytes, "AES");
    }

    private static byte[] getEcdhSecret(PrivateKey localPrivateKey, PublicKey remotePublicKey) throws InvalidKeyException {
        KeyAgreement agreement;
        try {
            agreement = KeyAgreement.getInstance("ECDH");
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }

        agreement.init(localPrivateKey);
        agreement.doPhase(remotePublicKey, true);
        return agreement.generateSecret();
    }

    /**
     * Create handshake JWS used in the ServerToClientHandshakePacket
     * which completes the encryption handshake.
     *
     * @param serverKeyPair used to sign the JWT
     * @param token         salt for the encryption handshake
     * @return signed JWS object
     * @throws JOSEException invalid key pair provided
     */
    public static JWSObject createHandshakeJwt(KeyPair serverKeyPair, byte[] token) throws JOSEException {
        URI x5u = URI.create(Base64.getEncoder().encodeToString(serverKeyPair.getPublic().getEncoded()));

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().claim("salt", Base64.getEncoder().encodeToString(token)).build();
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.ES384).x509CertURL(x5u).build(), claimsSet);

        signJwt(jwt, (ECPrivateKey) serverKeyPair.getPrivate());

        return jwt;
    }

    /**
     * Generate 16 bytes of random data for the handshake token using a {@link SecureRandom}
     *
     * @return 16 byte token
     */
    public static byte[] generateRandomToken() {
        byte[] token = new byte[16];
        SECURE_RANDOM.nextBytes(token);
        return token;
    }

    /**
     * Check whether java supports the encryption required to authenticate sessions.
     *
     * @return can use encryption
     */
    public static boolean canUseEncryption() {
        return AES_FACTORY != null;
    }

    /**
     * Mojang's public key used to verify the JWT during login.
     *
     * @return Mojang's public EC key
     */
    public static ECPublicKey getMojangPublicKey() {
        return MOJANG_PUBLIC_KEY;
    }

    public static Cipher createCipher(boolean gcm, boolean encrypt, SecretKey key) {
        try {
            byte[] iv;
            String transformation;
            if (gcm) {
                iv = new byte[16];
                System.arraycopy(key.getEncoded(), 0, iv, 0, 12);
                iv[15] = 2;
                transformation = "AES/CTR/NoPadding";
            } else {
                iv = Arrays.copyOf(key.getEncoded(), 16);
                transformation = "AES/CFB8/NoPadding";
            }
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new AssertionError("Unable to initialize required encryption", e);
        }
    }
}
