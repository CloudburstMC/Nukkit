package cn.nukkit.pack;

import cn.nukkit.pack.loader.PackLoader;
import cn.nukkit.utils.SemVersion;
import com.nukkitx.protocol.bedrock.packet.ResourcePackDataInfoPacket;
import lombok.ToString;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.UUID;

@ToString(exclude = {"hash"})
public abstract class Pack implements Closeable {

    private final PackLoader loader;
    private final PackManifest manifest;
    private final PackManifest.Module module;
    private byte[] hash;

    public Pack(PackLoader loader, PackManifest manifest, PackManifest.Module module) {
        this.loader = loader;
        this.manifest = manifest;
        this.module = module;
    }

    protected PackLoader getLoader() {
        return loader;
    }

    public PackManifest getManifest() {
        return manifest;
    }

    public String getName() {
        return manifest.getHeader().getName();
    }

    public SemVersion getVersion() {
        return manifest.getHeader().getVersion();
    }

    public UUID getId() {
        return manifest.getHeader().getUuid();
    }

    public long getSize() {
        try {
            return Files.size(this.loader.getNetworkPreparedFile().join());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to get size of pack", e);
        }
    }

    public byte[] getHash() {
        if (hash == null) {
            try {
                this.hash = MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(this.loader.getNetworkPreparedFile().join()));
            } catch (Exception e) {
                throw new IllegalStateException("Unable to get hash of pack", e);
            }
        }
        return hash;
    }

    public byte[] getChunk(int off, int len) {
        byte[] chunk;
        if (this.getSize() - off > len) {
            chunk = new byte[len];
        } else {
            chunk = new byte[(int) (this.getSize() - off)];
        }

        try (InputStream is = Files.newInputStream(this.loader.getNetworkPreparedFile().join())) {
            is.skip(off);
            is.read(chunk);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to read pack chunk");
        }

        return chunk;
    }

    @Override
    public void close() throws IOException {
        this.loader.close();
    }

    public abstract ResourcePackDataInfoPacket.Type getType();

    @FunctionalInterface
    public interface Factory {
        Pack create(PackLoader loader, PackManifest manifest, PackManifest.Module module);
    }
}
