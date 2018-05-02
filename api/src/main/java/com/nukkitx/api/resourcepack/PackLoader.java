package com.nukkitx.api.resourcepack;

import java.nio.file.Path;
import java.util.Optional;

public interface PackLoader {

    Optional<String[]> getPluginFileExtension();

    MinecraftPackManifest loadManifest(Path path);

    ResourcePack createResourcePack(MinecraftPackManifest manifest);

    BehaviorPack createBehaviorPack(MinecraftPackManifest manifest);
}
