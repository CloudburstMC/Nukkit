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
import com.google.gson.*;

import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.TreeMap;

/**
 * @author joserobjr
 * @since 2020-12-21
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class RuntimeItemIdReorder {
    public static void main(String[] args) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonArray array;
        try(InputStream is = Server.class.getResourceAsStream("/runtime_item_ids.json");
            Reader reader = new InputStreamReader(is, Charsets.UTF_8)
        ) {
            array = gson.fromJson(reader, JsonArray.class);
        }

        TreeMap<Integer, JsonObject> entries = new TreeMap<>();
        for (JsonElement element : array) {
            JsonObject obj = element.getAsJsonObject();
            entries.put(obj.get("id").getAsInt(), obj);
        }

        JsonArray sorted = new JsonArray();
        entries.forEach((k,v)-> sorted.add(v));
        try (FileWriter out = new FileWriter("src/main/resources/runtime_item_ids.json")) {
            gson.toJson(sorted, gson.newJsonWriter(out));
        }
    }
}
