package cn.nukkit.item;

import cn.nukkit.Server;
import cn.nukkit.api.Since;
import cn.nukkit.utils.BinaryStream;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Since("1.3.2.0-PN")
@UtilityClass
public class RuntimeItems {

    private static final Gson GSON = new Gson();
    private static final Type ENTRY_TYPE = new TypeToken<ArrayList<Entry>>(){}.getType();

    @Since("1.3.2.0-PN") public static final byte[] ITEM_DATA_PALETTE;

    private static final Int2IntMap LEGACY_NETWORK_MAP = new Int2IntOpenHashMap();
    private static final Int2IntMap NETWORK_LEGACY_MAP = new Int2IntOpenHashMap();

    static {
        List<Entry> entries;
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_item_ids.json");
             InputStreamReader reader = new InputStreamReader(stream)) {
            entries = GSON.fromJson(reader, ENTRY_TYPE);
        } catch (NullPointerException | IOException e) {
            throw new AssertionError("Unable to load runtime_item_ids.json", e);
        }

        BinaryStream paletteBuffer = new BinaryStream();
        paletteBuffer.putUnsignedVarInt(entries.size());

        LEGACY_NETWORK_MAP.defaultReturnValue(-1);
        NETWORK_LEGACY_MAP.defaultReturnValue(-1);

        for (Entry entry : entries) {

            paletteBuffer.putString(entry.name);
            paletteBuffer.putLShort(entry.id);
            paletteBuffer.putBoolean(false); // Component item
        }

        ITEM_DATA_PALETTE = paletteBuffer.getBuffer();
    }

    @ToString
    @RequiredArgsConstructor
    static class Entry {
        String name;
        int id;
    }
}
