package cn.nukkit.timings;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;
import java.util.function.Function;

/**
 * @author Tee7even
 *         <p>
 *         Various methods for more compact JSON object constructing
 */
@SuppressWarnings("unchecked")
public class JsonUtil {
    private static final JsonMapper MAPPER = new JsonMapper();

    public static ArrayNode toArray(Object... objects) {
        ArrayNode array = MAPPER.createArrayNode();
        for (Object object : objects) {
            array.addPOJO(object);
        }
        return array;
    }

    public static JsonNode toObject(Object object) {
        return MAPPER.valueToTree(object);
    }

    public static <E> JsonNode mapToObject(Iterable<E> collection, Function<E, JSONPair> mapper) {
        Map object = new LinkedHashMap();
        for (E e : collection) {
            JSONPair pair = mapper.apply(e);
            if (pair != null) {
                object.put(pair.key, pair.value);
            }
        }
        return MAPPER.valueToTree(object);
    }

    public static <E> ArrayNode mapToArray(E[] elements, Function<E, Object> mapper) {
        ArrayList array = new ArrayList();
        Collections.addAll(array, elements);
        return mapToArray(array, mapper);
    }

    public static <E> ArrayNode mapToArray(Iterable<E> collection, Function<E, Object> mapper) {
        ArrayNode node = MAPPER.createArrayNode();
        for (E e : collection) {
            Object obj = mapper.apply(e);
            if (obj != null) {
                node.addPOJO(obj);
            }
        }
        return node;
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
