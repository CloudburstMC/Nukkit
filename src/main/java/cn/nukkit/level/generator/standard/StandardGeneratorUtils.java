package cn.nukkit.level.generator.standard;

import cn.nukkit.Nukkit;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.level.generator.standard.misc.filter.AnyOfBlockFilter;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.misc.filter.SingleBlockFilter;
import cn.nukkit.level.generator.standard.misc.layer.BlockLayer;
import cn.nukkit.level.generator.standard.misc.layer.ConstantSizeBlockLayer;
import cn.nukkit.level.generator.standard.misc.layer.VariableSizeBlockLayer;
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
import net.daporkchop.lib.random.impl.FastPRandom;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;

/**
 * Various helper methods used by the NukkitX standard generator.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class StandardGeneratorUtils {
    private final Cache<Matcher> BLOCK_CACHE      = ThreadCache.regex("^((?:[a-zA-Z0-9_]+:)?[a-zA-Z0-9_]+)(?:#([0-9]+))?$");
    private final Cache<Matcher> BLOCK_LIST_CACHE = ThreadCache.regex("^(?:(?:([0-9]+)|\\(([0-9]+)-([0-9]+)\\))\\*)?((?:[a-zA-Z0-9_]+:)?[a-zA-Z0-9_]+)(?:#([0-9]+))?$");

    /**
     * Gets a block encoded in the form {@code <identifier>[#meta]} from a named field in the given {@link ConfigSection}.
     *
     * @param config    the {@link ConfigSection} to load the block from
     * @param fieldName the name of the field that the block is stored as
     * @see #parseBlock(String)
     */
    public static Block getBlock(@NonNull ConfigSection config, @NonNull String fieldName) {
        String block = config.getString(fieldName, null);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(block), "%s must be set!", fieldName);
        return parseBlock(block);
    }

    /**
     * Gets a block encoded in the form {@code <identifier>[#meta]}.
     *
     * @param block the encoded block
     * @return a {@link Block} with the same identifier and metadata as the given input string
     */
    public static Block parseBlock(String block) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(block), "block must be set!");
        Matcher matcher = BLOCK_CACHE.get().reset(block);
        Preconditions.checkArgument(matcher.find(), block);

        Identifier id = Identifier.fromString(matcher.group(1));
        String meta = matcher.group(2);
        return BlockRegistry.get().getBlock(id, meta == null ? 0 : Integer.parseInt(meta));
    }

    public static BlockFilter parseBlockChecker(String block) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(block), "block must be set!");
        Collection<Block> blocks = new ArrayList<>();
        for (String s : block.split(",")) {
            blocks.add(parseBlock(s));
        }
        Preconditions.checkArgument(!blocks.isEmpty());
        int[] ids = blocks.stream()
                .mapToInt(BlockRegistry.get()::getRuntimeId)
                .distinct()
                .sorted()
                .toArray();
        return ids.length == 1 ? new SingleBlockFilter(ids[0]) : new AnyOfBlockFilter(ids);
    }

    public static Collection<Block> parseBlockList(String blockList) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(blockList), "blocks must be set!");
        Matcher matcher = BLOCK_LIST_CACHE.get();

        Collection<Block> blocks = new ArrayList<>();
        for (String s : blockList.split(",")) {
            Preconditions.checkArgument(matcher.reset(s).find(), "Invalid list entry: \"%s\"", s);

            String countS = Objects.requireNonNull(matcher.group(1), "size range not allowed!");
            Identifier id = Identifier.fromString(matcher.group(2));
            String meta = matcher.group(3);
            Block block = BlockRegistry.get().getBlock(id, meta == null ? 0 : Integer.parseInt(meta));
            for (int i = countS == null ? 0 : (Integer.parseUnsignedInt(countS) - 1); i >= 0; i--) {
                blocks.add(block);
            }
        }
        Preconditions.checkArgument(!blocks.isEmpty());
        return blocks;
    }

    public static Collection<BlockLayer> parseBlockLayers(String blockList) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(blockList), "blocks must be set!");
        Matcher matcher = BLOCK_LIST_CACHE.get();

        Collection<BlockLayer> blocks = new ArrayList<>();
        for (String s : blockList.split(",")) {
            Preconditions.checkArgument(matcher.reset(s).find(), "Invalid list entry: \"%s\"", s);

            Identifier id = Identifier.fromString(matcher.group(4));
            String meta = matcher.group(5);
            Block block = BlockRegistry.get().getBlock(id, meta == null ? 0 : Integer.parseInt(meta));
            String count = matcher.group(1);
            String min = matcher.group(2);
            String max = matcher.group(3);
            blocks.add(count == null && min != null
                    ? new VariableSizeBlockLayer(block, Integer.parseUnsignedInt(matcher.group(2)), Integer.parseUnsignedInt(matcher.group(3)) + 1)
                    : new ConstantSizeBlockLayer(block, count == null ? 1 : Integer.parseUnsignedInt(count)));
        }
        Preconditions.checkArgument(!blocks.isEmpty());
        return blocks;
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
        return new FastPRandom(computeSeed(levelSeed, category, config));
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

    public static Identifier getId(@NonNull ConfigSection config, @NonNull String fieldName) {
        String id = config.getString(fieldName);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "%s must be set!", fieldName);
        return Identifier.fromString(id);
    }
}
