package cn.nukkit.pack;

import cn.nukkit.Server;
import cn.nukkit.pack.loader.DirectoryPackLoader;
import cn.nukkit.pack.loader.PackLoader;
import cn.nukkit.pack.loader.ZipPackLoader;
import com.google.common.base.Preconditions;
import com.nukkitx.protocol.bedrock.packet.ResourcePackDataInfoPacket;
import com.nukkitx.protocol.bedrock.packet.ResourcePackStackPacket;
import com.nukkitx.protocol.bedrock.packet.ResourcePacksInfoPacket;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Log4j2
public class PackManager implements Closeable {
    private static final Path MANIFEST_PATH = Paths.get("manifest.json");

    private final Map<Class<? extends PackLoader>, PackLoader.Factory> packLoaders = new HashMap<>();
    private final EnumMap<PackType, Pack.Factory> packFactories = new EnumMap<>(PackType.class);
    private final Map<String, Pack> packs = new HashMap<>();
    private final Map<UUID, Pack> packsById = new HashMap<>();

    private final ResourcePacksInfoPacket packsInfos = new ResourcePacksInfoPacket();
    private final ResourcePackStackPacket packStack = new ResourcePackStackPacket();
    private volatile boolean closed;

    public PackManager() {
        this.registerLoader(DirectoryPackLoader.class, DirectoryPackLoader.FACTORY);
        this.registerLoader(ZipPackLoader.class, ZipPackLoader.FACTORY);

        this.packFactories.put(PackType.RESOURCES, ResourcePack.FACTORY);
    }

    @Nullable
    private static PackManifest getManifest(PackLoader loader) {
        if (!loader.hasAsset(MANIFEST_PATH)) {
            return null;
        }
        try {
            return PackManifest.load(loader.getAsset(MANIFEST_PATH));
        } catch (IllegalStateException | IOException e) {
            log.error("Failed to load " + loader.getLocation(), e);
            return null;
        }
    }

    public void registerLoader(Class<? extends PackLoader> clazz, PackLoader.Factory factory) {
        Preconditions.checkNotNull(clazz, "clazz");
        Preconditions.checkNotNull(factory, "factory");

        if (this.packLoaders.putIfAbsent(clazz, factory) != null) {
            throw new IllegalArgumentException("loader already registered");
        }
    }

    public void loadPacks(Path directoryPath) {
        checkRegistrationClosed();
        Preconditions.checkNotNull(directoryPath, "directoryPath");
        Preconditions.checkArgument(Files.isDirectory(directoryPath), "%s is not a directory", directoryPath);

        // Get all loaders
        List<PackLoader> loaders = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath)) {
            for (Path entry : stream) {
                try {
                    PackLoader loader = getLoader(entry);
                    if (loader == null) {
                        continue;
                    }
                    loaders.add(loader);
                } catch (IOException e) {
                    log.throwing(e);
                }
            }
        } catch (IOException e) {
            log.throwing(e);
        }

        // Load manifests
        Map<UUID, PackManifest> manifestMap = new HashMap<>();
        Map<UUID, PackLoader> loaderMap = new HashMap<>();

        for (PackLoader loader : loaders) {
            PackManifest manifest = getManifest(loader);
            if (manifest == null || !manifest.isValid()) {
                continue;
            }
            UUID uuid = manifest.getHeader().getUuid();
            manifestMap.put(uuid, manifest);
            loaderMap.put(uuid, loader);
        }
        loaders.clear();


        // Check dependencies
        List<PackManifest> missingDependencies = new ArrayList<>();

        boolean missingDependency;
        do {
            missingDependency = false;
            Iterator<PackManifest> iterator = manifestMap.values().iterator();
            while (iterator.hasNext()) {
                PackManifest manifest = iterator.next();
                for (PackManifest.Dependency dependency : manifest.getDependencies()) {
                    UUID uuid = dependency.getUuid();
                    if (!manifestMap.containsKey(uuid) && !packsById.containsKey(uuid)) {
                        // TODO: 23/12/2019 check version
                        iterator.remove();
                        missingDependencies.add(manifest);
                        missingDependency = true;
                        break;
                    }
                }
            }
        } while (missingDependency);

        if (!missingDependencies.isEmpty()) {
            StringJoiner joiner = new StringJoiner(", ");
            for (PackManifest pack : missingDependencies) {
                joiner.add(pack.getHeader().getName() + ":" + pack.getHeader().getVersion());
            }
            log.error("Could not load packs due to missing dependencies {}", joiner.toString());
        }

        for (PackManifest manifest : manifestMap.values()) {
            PackLoader loader = loaderMap.get(manifest.getHeader().getUuid());

            PackManifest.Module module = manifest.getModules().get(0);
            Pack.Factory factory = this.packFactories.get(module.getType());
            if (factory == null) {
                log.warn("Unsupported pack type {}", module.getType());
                continue;
            }
            UUID uuid = manifest.getHeader().getUuid();
            Pack pack = factory.create(loader, manifest, module);
            this.packs.put(uuid + "_" + manifest.getHeader().getVersion(), pack);
            this.packsById.put(uuid, pack);

            // prepare for network
            loader.getNetworkPreparedFile();
        }

        log.info(Server.getInstance().getLanguage()
                .translateString("nukkit.resources.success", String.valueOf(manifestMap.size())));
    }

    public void loadPack(Path packPath) throws IOException {
        checkRegistrationClosed();
        PackLoader loader = getLoader(packPath);
        if (loader == null) {
            throw new IOException("No suitable loader found for pack");
        }

        PackManifest manifest = getManifest(loader);
        if (manifest == null) {
            throw new IOException("No manifest found");
        }

        PackManifest.Module module = manifest.getModules().get(0);
        Pack.Factory factory = this.packFactories.get(module.getType());
        if (factory == null) {
            throw new IOException("Unsupported pack type " + module.getType());
        }
        UUID uuid = manifest.getHeader().getUuid();
        Pack pack = factory.create(loader, manifest, module);
        this.packs.put(uuid + "_" + manifest.getHeader().getVersion(), pack);
        this.packsById.put(uuid, pack);

        // prepare for network
        loader.getNetworkPreparedFile();
    }

    @Nullable
    private PackLoader getLoader(Path path) throws IOException {
        PackLoader loader = null;
        for (PackLoader.Factory factory : this.packLoaders.values()) {
            if (factory.canLoad(path)) {
                loader = factory.create(path);
                break;
            }
        }
        if (loader == null) {
            log.warn(Server.getInstance().getLanguage().translateString("nukkit.resources.unknown-format", path));
        }
        return loader;
    }

    public synchronized void closeRegistration() {
        checkRegistrationClosed();

        boolean mustAccept = Server.getInstance().getForceResources();
        packsInfos.setForcedToAccept(mustAccept);
        packStack.setForcedToAccept(mustAccept);

        packsInfos.getBehaviorPackInfos().clear();
        packsInfos.getResourcePackInfos().clear();
        packStack.getBehaviorPacks().clear();
        packStack.getResourcePacks().clear();
        packStack.setExperimental(true); // Needed for custom blocks, items and entities
        packStack.setGameVersion("*");
        for (Pack pack : packs.values()) {
            if (pack.getType() != ResourcePackDataInfoPacket.Type.BEHAVIOR) {
                packsInfos.getResourcePackInfos().add(new ResourcePacksInfoPacket.Entry(pack.getId().toString(),
                        pack.getVersion().toString(), pack.getSize(), "", "", "", false));
                packStack.getResourcePacks().add(new ResourcePackStackPacket.Entry(pack.getId().toString(),
                        pack.getVersion().toString(), ""));
            }
        }
    }

    private void checkRegistrationClosed() {
        if (closed) {
            throw new IllegalStateException("PackManager registration is closed");
        }
    }

    public ResourcePacksInfoPacket getPacksInfos() {
        return packsInfos;
    }

    public ResourcePackStackPacket getPackStack() {
        return packStack;
    }

    public Pack getPackByIdVersion(String id) {
        return this.packs.get(id);
    }

    public Pack getPackById(UUID uuid) {
        return this.packsById.get(uuid);
    }

    @Override
    public void close() throws IOException {
        for (Pack pack : this.packs.values()) {
            pack.close();
        }
    }
}
