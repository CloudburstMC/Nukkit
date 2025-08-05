package cn.nukkit.network.protocol;

import cn.nukkit.utils.Utils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.ToString;
import lombok.Value;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.zip.Deflater;

@ToString
public class BiomeDefinitionListPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.BIOME_DEFINITION_LIST_PACKET;

    private static final BatchPacket CACHED_PACKET;

    private LinkedHashMap<String, BiomeDefinitionData> biomeDefinitions;

    static {
        try {
            BiomeDefinitionListPacket pk = new BiomeDefinitionListPacket();
            pk.biomeDefinitions = new GsonBuilder().registerTypeAdapter(Color.class, new ColorTypeAdapter()).create().fromJson(Utils.loadJsonResource("stripped_biome_definitions.json"), new TypeToken<LinkedHashMap<String, BiomeDefinitionData>>() {
            }.getType());
            pk.tryEncode();
            CACHED_PACKET = pk.compress(Deflater.BEST_COMPRESSION);
        } catch (Exception e) {
            throw new AssertionError("Error whilst loading biome definitions", e);
        }
    }

    public static BatchPacket getCachedPacket() {
        return CACHED_PACKET;
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        if (this.biomeDefinitions == null) {
            throw new RuntimeException("biomeDefinitions == null, use getCachedPacket!");
        }

        this.reset();
        SequencedHashSet<String> strings = new SequencedHashSet<>();

        this.putUnsignedVarInt(this.biomeDefinitions.size());
        for (Map.Entry<String, BiomeDefinitionData> entry : this.biomeDefinitions.entrySet()) {
            String name = "minecraft:" + entry.getKey(); // TODO: Update mappings
            this.putLShort(strings.addAndGetIndex(name));

            BiomeDefinitionData definition = entry.getValue();
            this.putLShort(-1); // Vanilla biomes don't contain ID field
            this.putLFloat(definition.getTemperature());
            this.putLFloat(definition.getDownfall());
            this.putLFloat(definition.getRedSporeDensity());
            this.putLFloat(definition.getBlueSporeDensity());
            this.putLFloat(definition.getAshDensity());
            this.putLFloat(definition.getWhiteAshDensity());
            this.putLFloat(definition.getDepth());
            this.putLFloat(definition.getScale());
            this.putLInt(definition.getMapWaterColor().getRGB());
            this.putBoolean(definition.isRain());
            this.putBoolean(false); // Optional Tags
            this.putBoolean(false); // Optional ChunkGenData
        }

        this.putUnsignedVarInt(strings.size());
        for (String str : strings) {
            this.putString(str);
        }
    }


    @Value
    private static class BiomeDefinitionData {

        public float temperature;
        public float downfall;
        public float redSporeDensity;
        public float blueSporeDensity;
        public float ashDensity;
        public float whiteAshDensity;
        public float depth;
        public float scale;
        public Color mapWaterColor;
        public boolean rain;

        @JsonCreator
        public BiomeDefinitionData(float temperature, float downfall, float redSporeDensity,
                                   float blueSporeDensity, float ashDensity, float whiteAshDensity, float depth,
                                   float scale, Color mapWaterColor, boolean rain) {
            this.temperature = temperature;
            this.downfall = downfall;
            this.redSporeDensity = redSporeDensity;
            this.blueSporeDensity = blueSporeDensity;
            this.ashDensity = ashDensity;
            this.whiteAshDensity = whiteAshDensity;
            this.depth = depth;
            this.scale = scale;
            this.mapWaterColor = mapWaterColor;
            this.rain = rain;
        }
    }

    @SuppressWarnings({"NullableProblems", "SuspiciousMethodCalls"})
    private static class SequencedHashSet<E> implements java.util.List<E> {

        private final Object2IntMap<E> map = new Object2IntLinkedOpenHashMap<>();
        private final Int2ObjectMap<E> inverse = new Int2ObjectLinkedOpenHashMap<>();
        private int index = 0;

        @Override
        public int indexOf(Object o) {
            return map.getInt(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return map.getInt(o);
        }

        @Override
        public ListIterator<E> listIterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public boolean isEmpty() {
            return map.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return map.containsKey(o);
        }

        @Override
        public Iterator<E> iterator() {
            return map.keySet().iterator();
        }

        @Override
        public Object[] toArray() {
            return map.keySet().toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return map.keySet().toArray(a);
        }

        @Override
        public boolean add(E e) {
            if (!this.map.containsKey(e)) {
                int index = this.index++;
                this.map.put(e, index);
                this.inverse.put(index, e);
                return true;
            }
            return false;
        }

        public int addAndGetIndex(E e) {
            if (!this.map.containsKey(e)) {
                int index = this.index++;
                this.map.put(e, index);
                this.inverse.put(index, e);
                return index;
            }
            return this.map.getInt(e);
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return map.keySet().containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            for (E e : c) {
                this.add(e);
            }
            return true;
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        public E get(int index) {
            return this.inverse.get(index);
        }

        @Override
        public E set(int index, E element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, E element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public E remove(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return map.keySet().toString();
        }
    }

    private static class ColorTypeAdapter extends TypeAdapter<Color> {

        @Override
        public void write(JsonWriter out, Color color) {
        }

        @Override
        public Color read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }

            int r = 0, g = 0, b = 0, a = 255;
            in.beginObject();
            while (in.hasNext()) {
                switch (in.nextName()) {
                    case "r": r = in.nextInt(); break;
                    case "g": g = in.nextInt(); break;
                    case "b": b = in.nextInt(); break;
                    case "a": a = in.nextInt(); break;
                    default: in.skipValue(); break;
                }
            }
            in.endObject();
            return new Color(r, g, b, a);
        }
    }
}
