package cn.nukkit.customblock.util;

import cn.nukkit.block.*;
import cn.nukkit.customblock.properties.*;
import cn.nukkit.customblock.properties.exception.InvalidBlockPropertyMetaException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class BlockPropertyDumper {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final BlockProperties PROPERTIES = null; // new BlockProperties(BlockDripleafBig.TILT_PROPERTY, BlockDripleafBig.HEAD_PROPERTY, VanillaProperties.DIRECTION);

    public static void main(String[] args) {
        JsonArray blockStates = new JsonArray();
        long maxStates = getBlockStatesCount(PROPERTIES);


        for (int meta = 0; meta < (1 << Block.DATA_BITS); meta++) {
            if (meta >= maxStates) {
                log.info("Max states reached: {}", maxStates);
                break;
            }

            try {
                blockStates.add(createJsonBlockState(PROPERTIES, meta));
            } catch (InvalidBlockPropertyMetaException e) {
                break; // Nukkit has more states than our block
            }
        }

        JsonObject blockDefinition = new JsonObject();
        blockDefinition.add("minecraft:block", blockStates);


        System.out.println(GSON.toJson(blockDefinition));
        log.info("Created {} block states", blockStates.size());
    }

    private static JsonObject createJsonBlockState(BlockProperties properties, int meta) {
        JsonObject json = new JsonObject();
        if (properties != null) {
            for (String propertyName : properties.getNames()) {
                BlockProperty<?> property = properties.getBlockProperty(propertyName);
                if (property instanceof EnumBlockProperty) {
                    json.add(property.getPersistenceName(), GSON.toJsonTree(properties.getPersistenceValue(meta, propertyName)));
                } else if (property instanceof BooleanBlockProperty) {
                    json.addProperty(property.getPersistenceName(), properties.getBooleanValue(meta, propertyName));
                } else {
                    json.add(property.getPersistenceName(), GSON.toJsonTree(properties.getValue(meta, propertyName)));
                }
            }
        }
        return json;
    }

    private static long getBlockStatesCount(BlockProperties properties) {
        long maxStates = 1;
        for (String propertyName : properties.getNames()) {
            BlockProperty<?> property = properties.getBlockProperty(propertyName);
            if (property instanceof BooleanBlockProperty) {
                maxStates *= 2;
            } else if (property instanceof IntBlockProperty) {
                int values = ((IntBlockProperty) property).getMaxValue() + 1 - ((IntBlockProperty) property).getMinValue();
                maxStates *= values;
            } else if (property instanceof UnsignedIntBlockProperty) {
                long values = ((UnsignedIntBlockProperty) property).getMaxValue() + 1 - ((UnsignedIntBlockProperty) property).getMinValue();
                maxStates *= values;
            } else if (property instanceof EnumBlockProperty) {
                maxStates *= ((EnumBlockProperty<?>) property).getValues().length;
            }
        }
        return maxStates;
    }
}