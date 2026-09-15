package cn.nukkit.utils;

import com.google.gson.*;
import org.cloudburstmc.nbt.NbtList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NbtMapDeserializer implements JsonDeserializer<NbtMap> {

    private static NbtMap toNbtMap(JsonElement node) {
        Map<String, Object> map = new LinkedHashMap<>();

        for (Map.Entry<String, JsonElement> entry : node.getAsJsonObject().entrySet()) {
            JsonElement value = entry.getValue();

            if (value.isJsonNull()) {
                continue;
            }

            map.put(entry.getKey(), toValue(value));
        }

        return NbtMap.fromMap(map);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static NbtList<?> toNbtList(JsonElement node) {
        List<Object> elements = new ArrayList<>();

        for (JsonElement element : node.getAsJsonArray()) {
            if (!element.isJsonNull()) {
                elements.add(toValue(element));
            }
        }

        if (elements.isEmpty()) {
            return NbtList.EMPTY;
        }

        NbtType type = NbtType.byClass(elements.get(0).getClass());
        return new NbtList(type, elements);
    }

    private static Object toValue(JsonElement node) {
        if (node.isJsonObject()) {
            return toNbtMap(node);
        }

        if (node.isJsonArray()) {
            return toNbtList(node);
        }

        if (node.isJsonPrimitive()) {
            JsonPrimitive primitive = node.getAsJsonPrimitive();

            if (primitive.isNumber()) {
                return toNumber(primitive);
            }

            if (primitive.isBoolean()) {
                return (byte) (primitive.getAsBoolean() ? 1 : 0);
            }

            return primitive.getAsString();
        }

        return node.getAsString();
    }

    private static Number toNumber(JsonPrimitive primitive) {
        String value = primitive.getAsString();

        if (!value.contains(".") && !value.contains("e") && !value.contains("E")) {
            try {
                return Integer.valueOf(value);
            } catch (NumberFormatException ignored) {
                return Long.valueOf(value);
            }
        }

        return Double.valueOf(value);
    }

    @Override
    public NbtMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return toNbtMap(json);
    }
}
