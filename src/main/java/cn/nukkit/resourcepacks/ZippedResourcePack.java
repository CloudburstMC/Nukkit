package cn.nukkit.resourcepacks;

import cn.nukkit.Server;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZippedResourcePack extends AbstractResourcePack {
    private File file;
    private byte[] sha256 = null;

    private String encryptionKey = "";

    public ZippedResourcePack(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException(Server.getInstance().getLanguage()
                    .translateString("nukkit.resources.zip.not-found", file.getName()));
        }

        this.file = file;

        try (ZipFile zip = new ZipFile(file)) {
            ZipEntry entry = zip.getEntry("manifest.json");
            if (entry == null) {
                throw new IllegalArgumentException(Server.getInstance().getLanguage()
                        .translateString("nukkit.resources.zip.no-manifest"));
            } else {
                this.manifest = new JsonParser()
                        .parse(new InputStreamReader(zip.getInputStream(entry), StandardCharsets.UTF_8))
                        .getAsJsonObject();
            }
            File parentFolder = this.file.getParentFile();
            if (parentFolder == null || !parentFolder.isDirectory()) {
                throw new IOException("Invalid resource pack path");
            }
            File keyFile = new File(parentFolder, this.file.getName() + ".key");
            if (keyFile.exists()) {
                this.encryptionKey = new String(Files.readAllBytes(keyFile.toPath()), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            Server.getInstance().getLogger().logException(e);
        }

        if (!this.verifyManifest()) {
            throw new IllegalArgumentException(Server.getInstance().getLanguage()
                    .translateString("nukkit.resources.zip.invalid-manifest"));
        }
    }

    @Override
    public int getPackSize() {
        return (int) this.file.length();
    }

    @Override
    public byte[] getSha256() {
        if (this.sha256 == null) {
            try {
                this.sha256 = MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(this.file.toPath()));
            } catch (Exception e) {
                Server.getInstance().getLogger().logException(e);
            }
        }

        return this.sha256;
    }

    @Override
    public byte[] getPackChunk(int off, int len) {
        byte[] chunk;
        if (this.getPackSize() - off > len) {
            chunk = new byte[len];
        } else {
            chunk = new byte[this.getPackSize() - off];
        }

        try (FileInputStream fis = new FileInputStream(this.file)) {
            fis.skip(off);
            fis.read(chunk);
        } catch (Exception e) {
            Server.getInstance().getLogger().logException(e);
        }

        return chunk;
    }

    @Override
    public String getEncryptionKey() {
        return this.encryptionKey;
    }
}
