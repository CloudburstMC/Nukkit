package org.powernukkit.tools;

import cn.nukkit.Server;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import lombok.Data;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RuntimeItemIdUpdater {
    public static void main(String[] args) throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        List<RuntimeItem> runtimeItems;
        try(InputStream resourceAsStream = Server.class.getClassLoader().getResourceAsStream("runtime_item_ids.json");
            Reader reader = new InputStreamReader(Objects.requireNonNull(resourceAsStream), StandardCharsets.UTF_8);
            JsonReader jsonReader = new JsonReader(reader);
        ) {
            runtimeItems = gson.fromJson(jsonReader, LIST);
        }


        Map<String, RuntimeItem> itemNameToNukkitRegistry = new LinkedHashMap<>();
        for (RuntimeItem runtimeItem : runtimeItems) {
            itemNameToNukkitRegistry.put(runtimeItem.name, runtimeItem);
        }

        JsonObject requiredItems;
        try(InputStream resourceAsStream = Server.class.getClassLoader().getResourceAsStream("required_item_list.json");
            Reader reader = new InputStreamReader(Objects.requireNonNull(resourceAsStream), StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(reader);
        ) {
            requiredItems = gson.fromJson(bufferedReader, JsonObject.class);
        }

        for (Map.Entry<String, JsonElement> entry : requiredItems.entrySet()) {
            String name = entry.getKey();
            RuntimeItem runtimeItem = itemNameToNukkitRegistry.get(name);
            if (runtimeItem == null) {
                continue;
            }
            runtimeItem.id =
                    entry.getValue().getAsJsonObject().getAsJsonPrimitive("runtime_id").getAsInt();
        }

        try (FileWriter writer = new FileWriter("src/main/resources/runtime_item_ids.json");
            BufferedWriter bufferedWriter = new BufferedWriter(writer)
        ) {
            gson.toJson(runtimeItems, LIST, bufferedWriter);
        }
    }

    private static Type LIST = new TypeToken<List<RuntimeItem>>(){}.getType();

    @Data class RuntimeItem {
        private String name;
        private Integer id;
        private Integer oldId;
        private Integer oldData;
        private Boolean deprecated;
    }
}
