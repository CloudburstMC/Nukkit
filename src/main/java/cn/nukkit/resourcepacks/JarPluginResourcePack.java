package cn.nukkit.resourcepacks;

import cn.nukkit.Server;
import com.google.gson.JsonParser;
import org.jline.utils.InputStreamReader;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class JarPluginResourcePack extends AbstractResourcePack {

    public static final String RESOURCE_PACK_PATH = "assets/resource_pack/";

    protected File jarPluginFile;
    protected ByteBuffer zippedByteBuffer;
    protected byte[] sha256;
    protected String encryptionKey = "";

    public static boolean hasResourcePack(File jarPluginFile) {
        try {
            return findManifestInJar(new ZipFile(jarPluginFile)) != null;
        } catch (IOException e) {
            return false;
        }
    }

    @Nullable
    protected static ZipEntry findManifestInJar(ZipFile jar) {
        ZipEntry manifest = jar.getEntry(RESOURCE_PACK_PATH + "manifest.json");
        if (manifest == null) {
            manifest = jar.stream()
                    .filter(e -> e.getName().toLowerCase(Locale.ROOT).endsWith("manifest.json") && !e.isDirectory())
                    .filter(e -> {
                        File fe = new File(e.getName());
                        if (!fe.getName().equalsIgnoreCase("manifest.json")) {
                            return false;
                        }
                        return fe.getParent() == null || fe.getParentFile().getParent() == null;
                    })
                    .findFirst()
                    .orElse(null);
        }
        return manifest;
    }

    public JarPluginResourcePack(File jarPluginFile) {
        if (!jarPluginFile.exists()) {
            throw new IllegalArgumentException(Server.getInstance().getLanguage()
                    .translateString("nukkit.resources.zip.not-found", jarPluginFile.getName()));
        }

        this.jarPluginFile = jarPluginFile;


        try {
            ZipFile jar = new ZipFile(jarPluginFile);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
            ZipEntry manifest = findManifestInJar(jar);
            if (manifest == null)
                throw new IllegalArgumentException(
                        Server.getInstance().getLanguage().translateString("nukkit.resources.zip.no-manifest"));

            this.manifest = JsonParser
                    .parseReader(new InputStreamReader(jar.getInputStream(manifest), StandardCharsets.UTF_8))
                    .getAsJsonObject();

            ZipEntry encryptionKeyEntry = jar.getEntry(RESOURCE_PACK_PATH + "encryption.key");
            if (encryptionKeyEntry != null) {
                this.encryptionKey = new String(readAllBytes(jar, encryptionKeyEntry),StandardCharsets.UTF_8);
                Server.getInstance().getLogger().debug(this.encryptionKey);
            }

            jar.stream().forEach(entry -> {
                if (entry.getName().startsWith(RESOURCE_PACK_PATH) && !entry.isDirectory() && !entry.getName().equals(RESOURCE_PACK_PATH + "encryption.key")) {
                    try {
                        zipOutputStream.putNextEntry(new ZipEntry(entry.getName().substring(RESOURCE_PACK_PATH.length())));
                        zipOutputStream.write(readAllBytes(jar, entry));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            jar.close();
            zipOutputStream.close();
            byteArrayOutputStream.close();

            zippedByteBuffer = ByteBuffer.allocateDirect(byteArrayOutputStream.size());
            byte[] bytes = byteArrayOutputStream.toByteArray();
            zippedByteBuffer.put(bytes);
            zippedByteBuffer.flip();

            try {
                this.sha256 = MessageDigest.getInstance("SHA-256").digest(bytes);
            } catch (Exception e) {
                Server.getInstance().getLogger().error("Failed to parse the SHA-256 of the resource pack inside of jar plugin " + jarPluginFile.getName(), e);
            }
        } catch (IOException e) {
            Server.getInstance().getLogger().error("An error occurred while loading the resource pack inside of a jar plugin " + jarPluginFile, e);
        }

        if (!this.verifyManifest()) {
            throw new IllegalArgumentException(Server.getInstance().getLanguage()
                    .translateString("nukkit.resources.zip.invalid-manifest"));
        }
    }

    @Override
    public int getPackSize() {
        return this.zippedByteBuffer.limit();
    }

    @Override
    public byte[] getSha256() {
        return this.sha256;
    }

    @Override
    public String getEncryptionKey() {
        return encryptionKey;
    }

    @Override
    public byte[] getPackChunk(int off, int len) {
        byte[] chunk;
        if (this.getPackSize() - off > len) {
            chunk = new byte[len];
        } else {
            chunk = new byte[this.getPackSize() - off];
        }

        try{
            zippedByteBuffer.position(off);
            zippedByteBuffer.get(chunk);
        } catch (Exception e) {
            Server.getInstance().getLogger().error("An error occurred while processing the resource pack " + getPackName() + " at offset:" + off + " and length: " + len, e);
        }

        return chunk;
    }

    private static byte[] readAllBytes(ZipFile jar, ZipEntry encryptionKeyEntry) {
        byte[] data = null;
        try (InputStream is = jar.getInputStream(encryptionKeyEntry)) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] temp = new byte[2048];
            while ((nRead = is.read(temp, 0, temp.length)) != -1) {
                buffer.write(temp, 0, nRead);
            }
            data = buffer.toByteArray();
            buffer.close();
        } catch (IOException e) {
            Server.getInstance().getLogger().error("An error occurred while reading ZipFile", e);
        }
        return data;
    }
}
