package cn.nukkit.resourcepacks;

import cn.nukkit.Server;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZippedResourcePack extends AbstractResourcePack {

    protected final File file;
    protected ByteBuffer byteBuffer;
    protected byte[] sha256;
    protected String encryptionKey = "";

    public ZippedResourcePack(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException(Server.getInstance().getLanguage()
                    .translateString("nukkit.resources.zip.not-found", file.getName()));
        }

        this.file = file;

        try {
            this.sha256 = MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(this.file.toPath()));
        } catch (Exception e) {
            Server.getInstance().getLogger().error("Failed to parse the SHA-256 of the resource pack " + file, e);
        }

        try (ZipFile zip = new ZipFile(file)) {
            loadZip(zip);
        } catch (java.util.zip.ZipException exception) {
            if (exception.getMessage().equals("invalid CEN header (bad entry name)")) {
                try (ZipFile zip = new ZipFile(file, Charset.forName("GBK"))) {
                    loadZip(zip);
                } catch (IOException e) {
                    Server.getInstance().getLogger().error("An error occurred while loading the zipped resource pack " + file, e);
                }
            } else {
                Server.getInstance().getLogger().error("An error occurred while loading the zipped resource pack " + file, exception);
            }
        } catch (IOException e) {
            Server.getInstance().getLogger().error("An error occurred while loading the zipped resource pack " + file, e);
        }

        if (!this.verifyManifest()) {
            throw new IllegalArgumentException(Server.getInstance().getLanguage()
                    .translateString("nukkit.resources.zip.invalid-manifest"));
        }
    }

    private void loadZip(ZipFile zip) throws IOException {
        ZipEntry entry = zip.getEntry("manifest.json");
        if (entry == null) {
            entry = zip.stream()
                    .filter(e -> e.getName().toLowerCase(Locale.ROOT).endsWith("manifest.json") && !e.isDirectory())
                    .filter(e -> {
                        File fe = new File(e.getName());
                        if (!fe.getName().equalsIgnoreCase("manifest.json")) {
                            return false;
                        }
                        return fe.getParent() == null || fe.getParentFile().getParent() == null;
                    })
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            Server.getInstance().getLanguage().translateString("nukkit.resources.zip.no-manifest")));
        }

        this.manifest = JsonParser
                .parseReader(new InputStreamReader(zip.getInputStream(entry), StandardCharsets.UTF_8))
                .getAsJsonObject();
        File parentFolder = this.file.getParentFile();
        if (parentFolder == null || !parentFolder.isDirectory()) {
            throw new IOException("Invalid resource pack path");
        }
        File keyFile = new File(parentFolder, this.file.getName() + ".key");
        if (keyFile.exists()) {
            this.encryptionKey = new String(Files.readAllBytes(keyFile.toPath()), StandardCharsets.UTF_8);
            Server.getInstance().getLogger().debug(this.encryptionKey);
        }

        byte[] bytes = Files.readAllBytes(file.toPath());
        byteBuffer = ByteBuffer.allocateDirect(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.flip();
    }

    @Override
    public byte[] getPackChunk(int off, int len) {
        byte[] chunk;
        if (this.getPackSize() - off > len) {
            chunk = new byte[len];
        } else {
            chunk = new byte[this.getPackSize() - off];
        }

        try {
            byteBuffer.position(off);
            byteBuffer.get(chunk);
        } catch (Exception e) {
            Server.getInstance().getLogger().error("An error occurred while processing the resource pack " + getPackName() + " at offset:" + off + " and length: " + len, e);
        }

        return chunk;
    }

    @Override
    public int getPackSize() {
        return (int) this.file.length();
    }

    @Override
    public byte[] getSha256() {
        return this.sha256;
    }

    @Override
    public String getEncryptionKey() {
        return this.encryptionKey;
    }
}
