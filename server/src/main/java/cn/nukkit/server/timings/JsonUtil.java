package cn.nukkit.server.timings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.*;
import java.util.function.Function;

/**
 * @author Tee7even
 *         <p>
 *         Various methods for more compact JSON object constructing
 */
@SuppressWarnings("unchecked")
public class JsonUtil {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static JsonArray toArray(Object... objects) {
        List array = new ArrayList();
        Collections.addAll(array, objects);
        return GSON.toJsonTree(array).getAsJsonArray();
    }

    public static JsonObject toObject(Object object) {
        return GSON.toJsonTree(object).getAsJsonObject();
    }

    public static <E> JsonObject mapToObject(Iterable<E> collection, Function<E, JSONPair> mapper) {
        Map object = new LinkedHashMap();
        for (E e : collection) {
            JSONPair pair = mapper.apply(e);
            if (pair != null) {
                object.put(pair.key, pair.value);
            }
        }
        return GSON.toJsonTree(object).getAsJsonObject();
    }

    public static <E> JsonArray mapToArray(E[] elements, Function<E, Object> mapper) {
        ArrayList array = new ArrayList();
        Collections.addAll(array, elements);
        return mapToArray(array, mapper);
    }

    public static <E> JsonArray mapToArray(Iterable<E> collection, Function<E, Object> mapper) {
        List array = new ArrayList();
        for (E e : collection) {
            Object obj = mapper.apply(e);
            if (obj != null) {
                array.add(obj);
            }
        }
        return GSON.toJsonTree(array).getAsJsonArray();
    }

    public static class JSONPair {
        public final String key;
        public final Object value;

        public JSONPair(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        public JSONPair(int key, Object value) {
            this.key = String.valueOf(key);
            this.value = value;
        }
    }
}
