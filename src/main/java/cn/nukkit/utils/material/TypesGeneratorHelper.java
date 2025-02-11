package cn.nukkit.utils.material;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.utils.material.tags.MaterialTags;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

@Log4j2
public class TypesGeneratorHelper {

    private static final String ITEMS_FILE = "item_types.txt";
    private static final String BLOCKS_FILE = "block_types.txt";
    private static final String TAGS_FILE = "material_tags.txt";

    public static void main(String[] args) throws Exception {
        RuntimeItems.init();

        generateItems();
        generateBlocks();
        generateTags();
    }

    private static void generateItems() throws Exception {
        Map<Integer, List<String>> types = new TreeMap<>();

        for (Field field : ItemID.class.getDeclaredFields()) {
            field.setAccessible(true);

            String name = field.getName();

            int legacyId = field.getInt(null);

            types.computeIfAbsent(field.getInt(null), id -> new ArrayList<>())
                    .add("public static final ItemType " + name + " = register(\"" + getIdentifierFromId(legacyId) + "\", ItemID." + name + ");");
        }

        log.info("Saving {} item types to {}", types.size(), ITEMS_FILE);
        saveFile(toString(types), ITEMS_FILE);
    }

    private static void generateBlocks() throws Exception {
        Map<Integer, List<String>> types = new TreeMap<>();

        for (Field field : BlockID.class.getDeclaredFields()) {
            field.setAccessible(true);

            String name = field.getName();
            int legacyId = field.getInt(null);


            if (legacyId > 255) {
                legacyId = 255 - legacyId;
            }

            types.computeIfAbsent(field.getInt(null), id -> new ArrayList<>())
                    .add("public static final BlockType " + name + " = register(\"" + getIdentifierFromId(legacyId) + "\", BlockID." + name + ");");
        }

        log.info("Saving {} block types to {}", types.size(), BLOCKS_FILE);
        saveFile(toString(types), BLOCKS_FILE);
    }

    private static void generateTags() throws Exception {
        StringJoiner joiner = new StringJoiner("\n");

        try (InputStream stream = MaterialTags.class.getClassLoader().getResourceAsStream("item_tags.json")) {
            if (stream == null) {
                throw new IllegalStateException("Resource file item_tags.json is missing");
            }
            try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                for (String tag : json.keySet()) {
                    String name = tag.split(":")[1].toUpperCase(Locale.ROOT);
                    joiner.add("public static final MaterialTag " + name + " = register(\"" + tag + "\", new LazilyInitializedMaterialTag(\"" + tag + "\"));");
                }
            }
        }

        log.info("Saving tags to {}", TAGS_FILE);
        saveFile(joiner.toString(), TAGS_FILE);
    }

    private static String toString(Map<Integer, List<String>> types) {
        StringJoiner joiner = new StringJoiner("\n");
        for (List<String> value : types.values()) {
            for (String type : value) {
                joiner.add(type);
            }
        }
        return joiner.toString();
    }

    private static void saveFile(String buffer, String path)  {
        try {
            Files.write(Paths.get(path), buffer.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            log.error("Unable to save file " + path, e);
        }
    }

    private static String getIdentifierFromId(int legacyId) {
        // yes, we still use legacy identifiers internally
        String identifier = RuntimeItems.getLegacyStringFromLegacyId(legacyId);
        if (legacyId == 0) {
            identifier = "minecraft:air";
        }
        return identifier;
    }
}
