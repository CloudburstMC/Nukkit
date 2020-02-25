package cn.nukkit.level.generator.standard;

import cn.nukkit.Nukkit;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.Identifier;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.cache.Cache;
import net.daporkchop.lib.common.cache.ThreadCache;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.random.impl.FastJavaPRandom;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Various helper methods used by the NukkitX standard generator.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class StandardGeneratorUtils {
    private final Pattern        BLOCK_PATTERN       = Pattern.compile("^((?:[a-z0-9_]+:)?[a-z0-9_]+)(?:#([0-9]+))?$", Pattern.CASE_INSENSITIVE);
    private final Cache<Matcher> BLOCK_MATCHER_CACHE = ThreadCache.soft(() -> BLOCK_PATTERN.matcher(""));

    /**
     * Gets a block encoded in the form {@code <identifier>[#meta]}.
     *
     * @param block the encoded block
     * @return a {@link Block} with the same identifier and metadata as the given input string
     */
    public static Block parseBlock(String block) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(block), "block must be set!");
        Matcher matcher = BLOCK_MATCHER_CACHE.get().reset(block);
        Preconditions.checkArgument(matcher.find(), block);

        Identifier id = Identifier.fromString(matcher.group(1));
        String meta = matcher.group(2);
        return BlockRegistry.get().getBlock(id, meta == null ? 0 : Integer.parseInt(meta));
    }

    public static Config loadUnchecked(@NonNull String category, @NonNull Identifier id) {
        try {
            return load(category, id);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Unable to load %s \"%s\"", category, id), e);
        }
    }

    public static Config load(@NonNull String category, @NonNull Identifier id) throws IOException {
        Config config = new Config(Config.YAML);
        try (InputStream in = read(category, id)) {
            Preconditions.checkState(config.load(in));
        }
        return config;
    }

    public static InputStream read(@NonNull String category, @NonNull Identifier id) throws IOException {
        String name = String.format("generator/%s/%s/%s.yml", category, id.getNamespace(), id.getName());

        File file = new File(name);
        if (PFiles.checkFileExists(file)) {
            return new BufferedInputStream(new FileInputStream(file));
        }

        InputStream in = null;
        switch (id.getNamespace()) {
            case "minecraft":
            case "nukkitx":
                in = Nukkit.class.getClassLoader().getResourceAsStream(name);
                break;
            default:
                Plugin plugin = Server.getInstance().getPluginManager().getPlugin(id.getNamespace());
                if (plugin != null) {
                    in = plugin.getResource(name);
                }
        }
        if (in == null) {
            throw new FileNotFoundException(name);
        } else {
            return in;
        }
    }

    /**
     * Creates the {@link PRandom} instance to be used for initializing a generation component.
     *
     * @see #computeSeed(long, String, ConfigSection)
     */
    public static PRandom computeRandom(long levelSeed, @NonNull String category, @NonNull ConfigSection config) {
        return new FastJavaPRandom(computeSeed(levelSeed, category, config));
    }

    /**
     * Calculates the seed to be used for initializing a generation component.
     * <p>
     * This is important so that the RNG state for each component remains the same even if other ones are added/removed/modified.
     *
     * @param levelSeed the global level seed
     * @param category  the category that the component belongs to
     * @param config    the component's configuration
     * @return the seed to be used for the given component
     */
    public static long computeSeed(long levelSeed, @NonNull String category, @NonNull ConfigSection config) {
        if (config.containsKey("seed")) {
            //allow users to manually specify a seed that will be XOR-ed with the level seed
            return levelSeed ^ config.getLong("seed");
        }
        UUID uuid = UUID.nameUUIDFromBytes((category + '|' + config.getString("id")).getBytes(StandardCharsets.UTF_8));
        return levelSeed ^ uuid.getLeastSignificantBits() ^ uuid.getMostSignificantBits();
    }

    public static Identifier getId(@NonNull ConfigSection config, @NonNull String fieldName)    {
        String id = config.getString(fieldName);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "%s must be set!", fieldName);
        return Identifier.fromString(id);
    }
}
