package cn.nukkit.server.resourcepacks;

import cn.nukkit.server.NukkitServer;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Log4j2
public class ZippedResourcePack extends AbstractResourcePack {
    private Path file;
    private byte[] sha256 = null;

    public ZippedResourcePack(Path file) {
        if (Files.notExists(file)) {
            throw new IllegalArgumentException(NukkitServer.getInstance().getLanguage()
                    .translateString("nukkit.resources.zip.not-found", file.getFileName().toString()));
        }

        this.file = file;

        try (ZipFile zip = new ZipFile(file.toFile())) {
            ZipEntry entry = zip.getEntry("manifest.json");
            if (entry == null) {
                throw new IllegalArgumentException(NukkitServer.getInstance().getLanguage()
                        .translateString("nukkit.resources.zip.no-manifest"));
            } else {
                manifest = NukkitServer.JSON_MAPPER.readValue(Files.newInputStream(file), NukkitResourcePackPackManifest.class);
            }
        } catch (IOException e) {
            log.throwing(e);
        }

        if (!manifest.verify()) {
            throw new IllegalArgumentException(NukkitServer.getInstance().getLanguage()
                    .translateString("nukkit.resources.zip.invalid-manifest"));
        }
    }

    @Override
    public long getPackSize() {
        try {
            return Files.size(file);
        } catch (IOException e) {
            log.throwing(e);
            return 0L;
        }
    }

    @Override
    public byte[] getSha256() {
        if (this.sha256 == null) {
            try {
                this.sha256 = MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(file));
            } catch (Exception e) {
                log.throwing(e);
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
            chunk = new byte[(int)(getPackSize() - off)];
        }

        try (InputStream fis = Files.newInputStream(file)) {
            fis.skip(off);
            fis.read(chunk);
        } catch (Exception e) {
            log.throwing(e);
        }

        return chunk;
    }
}
