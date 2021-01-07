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

package org.powernukkit.tools;

import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockStateRegistry;
import com.google.common.base.Preconditions;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class BlockIdListGenerator {
    public static void main(String[] args) throws IOException {
        TreeMap<Integer, String> ids = new TreeMap<>();

        File file = new File("src/main/resources/block_ids.csv");
        int count = 0;
        try (FileReader fr = new FileReader(file); BufferedReader reader = new BufferedReader(fr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                count++;
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                Preconditions.checkArgument(parts.length == 2 || parts[0].matches("^[0-9]+$"));
                if (parts.length > 1 && parts[1].startsWith("minecraft:")) {
                    ids.put(Integer.parseInt(parts[0]), parts[1]);
                }
            }
        } catch (Exception e) {
            throw new IOException("Error reading the line "+count, e);
        }
        
        for (int id = 0; id < Block.MAX_BLOCK_ID; id++) {
            String persistenceName = BlockStateRegistry.getPersistenceName(id);
            if (persistenceName != null) {
                ids.put(id, persistenceName);
            } else {
                ids.putIfAbsent(id, "");
            }
        }
        
        try (FileWriter fw = new FileWriter(file); BufferedWriter writer = new BufferedWriter(fw)) {
            for (Map.Entry<Integer, String> entry : ids.entrySet()) {
                writer.write(entry.getKey().toString());
                writer.write(',');
                writer.write(entry.getValue());
                writer.newLine();
            }
        }
    }
}
