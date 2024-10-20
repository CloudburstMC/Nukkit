package cn.nukkit.network.encryption;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256 {

    private final MessageDigest digest;

    public Sha256() {
        try {
            this.digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new AssertionError(ex);
        }
    }

    public void update(byte[] input, int offset, int len) {
        this.digest.update(input, offset, len);
    }

    public void update(ByteBuffer buffer) {
        this.digest.update(buffer);
    }

    public byte[] digest() {
        return this.digest.digest();
    }

    public void reset() {
        this.digest.reset();
    }
}
