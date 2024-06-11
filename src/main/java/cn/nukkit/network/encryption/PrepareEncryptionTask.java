package cn.nukkit.network.encryption;

import cn.nukkit.Player;
import cn.nukkit.scheduler.AsyncTask;
import lombok.Getter;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.ECGenParameterSpec;

public class PrepareEncryptionTask extends AsyncTask {

    private final Player player;
    @Getter
    private String handshakeJwt;
    @Getter
    private SecretKey encryptionKey;
    @Getter
    private Cipher encryptionCipher;
    @Getter
    private Cipher decryptionCipher;

    public PrepareEncryptionTask(Player player) {
        this.player = player;
    }

    @Override
    public void onRun() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
            generator.initialize(new ECGenParameterSpec("secp384r1"));
            KeyPair serverKeyPair = generator.generateKeyPair();

            byte[] token = EncryptionUtils.generateRandomToken();
            this.encryptionKey = EncryptionUtils.getSecretKey(serverKeyPair.getPrivate(), EncryptionUtils.generateKey(player.getLoginChainData().getIdentityPublicKey()), token);
            this.handshakeJwt = EncryptionUtils.createHandshakeJwt(serverKeyPair, token).serialize();

            this.encryptionCipher = EncryptionUtils.createCipher(true, true, this.encryptionKey);
            this.decryptionCipher = EncryptionUtils.createCipher(true, false, this.encryptionKey);
        } catch (Exception ex) {
            player.getServer().getLogger().error("Exception in PrepareEncryptionTask", ex);
        }
    }
}
