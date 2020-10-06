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

import cn.nukkit.Server;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import com.google.common.io.ByteStreams;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.LinkedHashMap;
import java.util.Map;

public class BlockStates {
    public static void main(String[] args) {
        InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_block_states.dat");
        if (stream == null) {
            throw new AssertionError("Unable to locate block state nbt");
        }
        ListTag<CompoundTag> overrides = new ListTag<>("Overrides");
        ListTag<CompoundTag> tag;
        try {
            //noinspection UnstableApiUsage
            byte[] bytes = ByteStreams.toByteArray(stream);
            //noinspection unchecked
            tag = (ListTag<CompoundTag>) NBTIO.readTag(new ByteArrayInputStream(bytes), ByteOrder.LITTLE_ENDIAN, false);
            
            Map<CompoundTag, CompoundTag> fullMap = new LinkedHashMap<>();
            
            for (int i = 0; i < tag.size(); i++) {
                CompoundTag compoundTag = tag.get(i);
                fullMap.put(compoundTag.getCompound("block"), compoundTag);
            }
            
            for (int i = 0; i < tag.size(); i++) {
                CompoundTag compoundTag = tag.get(i);
                String name = compoundTag.getCompound("block").getString("name");
                if (name.equals("minecraft:light_block")) {
                    int lightLevel = compoundTag.getCompound("block").getCompound("states").getInt("block_light_level");
                    CompoundTag override = new CompoundTag()
                            .putCompound("block", compoundTag.getCompound("block").remove("version"))
                            .putIntArray("meta", new int[]{lightLevel});
                    overrides.add(override);
                    //overrides.add(compoundTag.getCompound("block"));
                    //compoundTag.putIntArray("meta", new int[]{lightLevel});
                }
                /*if (name.equals("minecraft:lectern")) {
                    System.out.println(compoundTag);
                }*/
                if (name.equals("minecraft:kelp")) {
                    int age = compoundTag.getCompound("block").getCompound("states").getInt("kelp_age");
                    if (age > 15) {
                        CompoundTag override = new CompoundTag()
                                .putCompound("block", compoundTag.getCompound("block").remove("version"))
                                .putIntArray("meta", new int[]{age});
                        overrides.add(override);
                    }
                }
                switch (name) {
                    case "minecraft:honeycomb_block":
                    case "minecraft:honey_block":
                    case "minecraft:wither_rose":
                        //compoundTag.putIntArray("meta", new int[]{0});
                        CompoundTag override = new CompoundTag()
                                .putCompound("block", compoundTag.getCompound("block").remove("version"))
                                .putIntArray("meta", new int[]{0});
                        overrides.add(override);
                        break;
                }
                if (name.equalsIgnoreCase("minecraft:pistonArmCollision")) {
                    CompoundTag override = new CompoundTag()
                            .putCompound("block", compoundTag.getCompound("block").remove("version").putString("name", "minecraft:stickyPistonArmCollision"))
                            .putIntArray("meta", compoundTag.getIntArray("meta"));
                    overrides.add(override);
                }
                if (name.equals("minecraft:coral")) {
                    int dead = compoundTag.getCompound("block").getCompound("states").getByte("dead_bit");
                    if (dead == 0) {
                        continue;
                    }
                    CompoundTag scan = compoundTag.getCompound("block").copy();
                    scan.getCompound("states").putByte("dead_bit", 0);
                    CompoundTag alive = fullMap.get(scan);
                    int meta = alive.getIntArray("meta")[0] | 0x8;
                    CompoundTag override = new CompoundTag()
                            .putCompound("block", compoundTag.getCompound("block").remove("version"))
                            .putIntArray("meta", new int[]{meta});
                    overrides.add(override);
                }
                if (name.equals("minecraft:wood")) {
                    String axis = compoundTag.getCompound("block").getCompound("states").getString("pillar_axis");
                    String typeString = compoundTag.getCompound("block").getCompound("states").getString("wood_type");
                    //if (!axis.equals("y") || typeString.equals()) {
                    int type;
                    switch (typeString) {
                        case "oak": type = 0; break;
                        case "spruce": type = 1; break;
                        case "birch": type = 2; break;
                        case "jungle": type = 3; break;
                        case "acacia": type = 4; break;
                        case "dark_oak": type = 5; break;
                        default: continue;
                    }
                    int axisInt;
                    switch (axis) {
                        case "y": axisInt = 0; break;
                        case "x": axisInt = 1; break;
                        case "z": axisInt = 2; break;
                        default: continue;
                    }
                    int strippedBit = compoundTag.getCompound("block").getCompound("states").getInt("stripped_bit");
                    int meta = axisInt << 4 | strippedBit << 3 | type;
                    CompoundTag override = new CompoundTag()
                            .putCompound("block", compoundTag.getCompound("block").remove("version"))
                            .putIntArray("meta", new int[]{meta});
                    overrides.add(override);
                    //}
                }
                if (name.equals("minecraft:beehive") || name.equals("minecraft:bee_nest")) {
                    int facing = compoundTag.getCompound("block").getCompound("states").getInt("facing_direction");
                    int honey = compoundTag.getCompound("block").getCompound("states").getInt("honey_level");
                    //if (honey == 0 || honey % 2 == 1) {
                        /*BlockFace face = BlockFace.fromIndex(facing);
                        int faceBits = face.getHorizontalIndex();
                        if (faceBits >= 0) {*/
                            /*int honeyBits;
                            switch (honey) {
                                case 0:
                                    honeyBits = 0;
                                    break;
                                case 1:
                                case 2:
                                    honeyBits = 1;
                                    break;
                                case 3:
                                case 4:
                                    honeyBits = 2;
                                    break;
                                case 5:
                                    honeyBits = 3;
                                    break;
                                default:
                                    continue;
                            }
                            int meta = honeyBits << 2 | faceBits;*/
                            //int meta = honey << 2 | faceBits;
                            int meta = honey << 3 | facing;
                            //compoundTag.putIntArray("meta", new int[]{meta});
                            CompoundTag override = new CompoundTag()
                                    .putCompound("block", compoundTag.getCompound("block").remove("version"))
                                    .putIntArray("meta", new int[]{meta});
                            overrides.add(override);
                        //}
                    //}
                }
            }
            //bytes = NBTIO.writeNetwork(tag);
            bytes = NBTIO.write(new CompoundTag().putList(overrides));
            try(FileOutputStream fos = new FileOutputStream("runtime_block_states_overrides.dat")) {
                fos.write(bytes);
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
