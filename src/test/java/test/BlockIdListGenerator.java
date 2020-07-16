package test;

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
                Preconditions.checkArgument(parts.length == 2);
                ids.put(Integer.parseInt(parts[0]), parts[1]);
                Preconditions.checkArgument(parts[1].isEmpty() || parts[1].startsWith("minecraft:"));
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
