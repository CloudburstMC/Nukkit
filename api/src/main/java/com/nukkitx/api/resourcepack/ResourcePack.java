package com.nukkitx.api.resourcepack;

import com.nukkitx.api.util.SemVer;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ResourcePack {

    @Nonnull
    UUID getId();

    int getFormatVersion();

    @Nonnull
    Optional<SemVer> getMinimumSupportedMinecraftVersion();

    @Nonnull
    SemVer getVersion();

    @Nonnull
    String getName();

    @Nonnull
    String getDescription();

    @Nonnull
    Collection<MinecraftPackManifest.Module> getModules();

    Collection<MinecraftPackManifest.Dependency> getDependencies();

    long getPackSize();

    int getCompressedSize();

    byte[] getSha256();

    byte[] getPackChunk(int chunkIndex);

    int getChunkCount();
}
