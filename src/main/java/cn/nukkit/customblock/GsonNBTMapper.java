package cn.nukkit.customblock;

import com.google.gson.*;
import com.google.gson.internal.LazilyParsedNumber;
import org.cloudburstmc.nbt.NbtList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.lang.reflect.Type;
import java.util.List;

public class GsonNBTMapper {

    public static final Gson GSON;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(NbtMap.class, new NbtMapDeserializer());
        builder.registerTypeAdapter(NbtList.class, new NbtArrayDeserializer());
        GSON = builder.create();
    }

    public static NbtMap objectToNbtMap(Object object) {
        JsonObject json = GSON.toJsonTree(object).getAsJsonObject();
        return GSON.fromJson(json, NbtMap.class);
    }

    public static class NbtMapDeserializer implements JsonDeserializer<NbtMap> {
        @Override
        public NbtMap deserialize(JsonElement json, Type typeOf, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject()) {
                throw new IllegalStateException("Expected JsonObject but got: " + json.getClass().getSimpleName());
            }

            JsonObject jsonObject = json.getAsJsonObject();
            NbtMapBuilder builder = NbtMap.builder();

            for (String key : jsonObject.keySet()) {
                JsonElement element = jsonObject.get(key);
                if (element.isJsonObject()) {
                    builder.putCompound(key, context.deserialize(element, NbtMap.class));
                } else if (element.isJsonArray()) {
                    NbtList list = context.deserialize(element, NbtList.class);
                    builder.putList(key, list.getType(), list);
                } else if (element.isJsonPrimitive()) {
                    builder.put(key, getPrimitiveObject(element));
                } else if (element.isJsonNull()) {
                    builder.putCompound(key, NbtMap.builder().build());
                }
            }
            return builder.build();
        }
    }

    public static class NbtArrayDeserializer implements JsonDeserializer<NbtList<?>> {
        @Override
        public NbtList<?> deserialize(JsonElement json, Type typeOf, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonArray()) {
                throw new IllegalStateException("Expected JsonObject but got: " + json.getClass().getSimpleName());
            }

            JsonArray jsonArray = json.getAsJsonArray();
            NbtType type = getListType(jsonArray);
            return new NbtList<>(type, getList(jsonArray, type, context));
        }
    }

    public static NbtType<?> getListType(JsonArray array) {
        NbtType<?> type = null;
        for (JsonElement element : array) {
            NbtType<?> elementType;
            if (element.isJsonObject()) {
                elementType = NbtType.COMPOUND;
            } else if (element.isJsonArray()) {
                elementType = NbtType.LIST;
            } else {
                elementType = getPrimitiveType(element);
            }

            if (type != null && elementType != type) {
                throw new IllegalArgumentException("Can not create array of mixed types");
            } else {
                type = elementType;
            }
        }
        return type;
    }

    public static <T> List<T> getList(JsonArray array, NbtType<T> type, JsonDeserializationContext context) {
        List<T> list = new ObjectArrayList<>();
        for (JsonElement element : array) {
            if (type == NbtType.COMPOUND) {
                list.add(context.deserialize(element, NbtMap.class));
            } else if (type == NbtType.LIST) {
                list.add(context.deserialize(element, NbtList.class));
            } else {
                list.add((T) getPrimitiveObject(element));
            }
        }
        return list;
    }

    public static Object getPrimitiveObject(JsonElement element) {
        if (element.getAsJsonPrimitive().isBoolean()) {
            return element.getAsBoolean();
        } else if (element.getAsJsonPrimitive().isNumber()) {
            Number number = element.getAsNumber();
            if (number instanceof Byte) {
                return number.byteValue();
            } else if (number instanceof Short) {
                return number.shortValue();
            } else if (number instanceof Integer) {
                return number.intValue();
            } else if (number instanceof Long) {
                return number.longValue();
            } else if (number instanceof Float) {
                return number.floatValue();
            } else if (number instanceof Double) {
                return number.doubleValue();
            } else {
                String str = number.toString();
                try {
                    return Integer.parseInt(str);
                } catch(NumberFormatException e) {
                    try {
                        return Long.parseLong(str);
                    } catch(NumberFormatException e1) {
                        try {
                            return Float.parseFloat(str);
                        } catch(NumberFormatException e2) {
                            return Double.parseDouble(str);
                        }
                    }
                }
            }
        } else if (element.getAsJsonPrimitive().isString()) {
            return element.getAsString();
        }
        throw new IllegalArgumentException("Unknown type of primitive: " + element);
    }

    public static NbtType<?> getPrimitiveType(JsonElement element) {
        if (element.getAsJsonPrimitive().isBoolean()) {
            return NbtType.BYTE;
        } else if (element.getAsJsonPrimitive().isNumber()) {
            Number number = element.getAsNumber();
            if (number instanceof Byte) {
                return NbtType.BYTE;
            } else if (number instanceof Short) {
                return NbtType.SHORT;
            } else if (number instanceof Integer) {
                return NbtType.INT;
            } else if (number instanceof Long) {
                return NbtType.LONG;
            } else if (number instanceof Float) {
                return NbtType.FLOAT;
            } else if (number instanceof Double) {
                return NbtType.DOUBLE;
            } else if (number instanceof LazilyParsedNumber) {
                String str = number.toString();
                try {
                    Integer.parseInt(str);
                    return NbtType.INT;
                } catch(NumberFormatException e) {
                    try {
                        Long.parseLong(str);
                        return NbtType.LONG;
                    } catch(NumberFormatException e1) {
                        try {
                            Float.parseFloat(str);
                            return NbtType.FLOAT;
                        } catch(NumberFormatException e2) {
                            Double.parseDouble(str);
                            return NbtType.DOUBLE;
                        }
                    }
                }
            }
        } else if (element.getAsJsonPrimitive().isString()) {
            return NbtType.STRING;
        }
        throw new IllegalArgumentException("Unknown type of primitive: " + element);
    }
}
