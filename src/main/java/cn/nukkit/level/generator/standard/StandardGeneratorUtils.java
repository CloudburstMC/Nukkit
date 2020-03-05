package cn.nukkit.level.generator.standard;

import cn.nukkit.Nukkit;
import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.Identifier;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.misc.file.PFiles;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Various helper methods used by the NukkitX standard generator.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class StandardGeneratorUtils {
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
}
