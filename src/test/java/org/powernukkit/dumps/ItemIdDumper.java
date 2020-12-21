/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.powernukkit.dumps;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import org.powernukkit.HumanStringComparator;

import java.io.*;
import java.util.Map;
import java.util.OptionalInt;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author joserobjr
 * @since 2020-11-20
 */
@PowerNukkitOnly
@Since("1.3.2.0-PN")
@Log4j2
public class ItemIdDumper {
    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();
        JsonArray array;
        try(InputStream is = Server.class.getResourceAsStream("/runtime_item_ids.json");
            Reader reader = new InputStreamReader(is, Charsets.UTF_8)
        ) {
            array = gson.fromJson(reader, JsonArray.class);
        }

        SortedMap<String, String> itemIds = new TreeMap<>(new HumanStringComparator());
        SortedMap<String, String> blockIds = new TreeMap<>(new HumanStringComparator());
        
        for (JsonElement element : array) {
            JsonObject object = (JsonObject) element;
            int itemId;
            if (!object.has("oldId")) {
                itemId = object.get("id").getAsInt();
                log.warn(object.get("name").getAsString()+" don't have oldId!");
                if (itemId > 255) {
                    log.warn("Skipping id "+itemId);
                    continue;
                }
            } else {
                itemId = object.get("oldId").getAsInt();
            }
            OptionalInt oldData = object.has("oldData")? OptionalInt.of(object.get("oldData").getAsInt()) : OptionalInt.empty();
            String name = object.get("name").getAsString();
            itemIds.put(itemId + (oldData.isPresent()? "_"+oldData.getAsInt() : ""), name);
            
            if (itemId <= 255) {
                int blockId = itemId;
                if (blockId < 0) {
                    blockId = 255 - blockId;
                }
                
                blockIds.put(blockId + (oldData.isPresent()? "_"+oldData.getAsInt() : ""), name);
            }
        }
        
        try (FileWriter writer = new FileWriter("item-id-dump.properties")) {
            for (Map.Entry<String, String> entry : itemIds.entrySet()) {
                writer.write(entry.getKey());
                writer.write('=');
                writer.write(entry.getValue());
                writer.write(System.lineSeparator());
            }
        }

        try (FileWriter writer = new FileWriter("block-id-dump-from-items.properties")) {
            for (Map.Entry<String, String> entry : blockIds.entrySet()) {
                writer.write(entry.getKey());
                writer.write('=');
                writer.write(entry.getValue());
                writer.write(System.lineSeparator());
            }
        }
    }
}
