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

package cn.nukkit.item.enchantment;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import com.google.gson.Gson;
import io.netty.util.internal.EmptyArrays;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opentest4j.AssertionFailedError;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static cn.nukkit.item.ItemID.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author joserobjr
 * @since 2021-02-15
 */
@ExtendWith(PowerNukkitExtension.class)
class EnchantmentTest {
    static int[] allIds = Stream.of(getAllItemIds(), 
            IntStream.of(255 - BlockID.CARVED_PUMPKIN, BlockID.PUMPKIN, BlockID.JACK_O_LANTERN)
    ).flatMapToInt(Function.identity()).toArray();
    
    @Getter
    static List<EnchantmentData> enchantmentDataList;

    @ParameterizedTest
    @MethodSource("getEnchantmentData")
    void testInternationalizationNames(EnchantmentData data, Enchantment enchantment) {
        assertEquals("%" + data.getI18n(), enchantment.getName());
    }

    @SuppressWarnings("deprecation")
    @ParameterizedTest
    @MethodSource("getEnchantmentData")
    void testWeights(EnchantmentData data, Enchantment enchantment) {
        assertEquals(data.getWeight(), enchantment.getRarity().getWeight());
        assertEquals(data.getWeight(), enchantment.getWeight());
    }

    @ParameterizedTest
    @MethodSource("getEnchantmentData")
    void testMaxLevels(EnchantmentData data, Enchantment enchantment) {
        assertEquals(data.getLevels().length, enchantment.getMaxLevel());
    }

    @ParameterizedTest
    @MethodSource("getEnchantmentData")
    void testMinLevels(EnchantmentData data, Enchantment enchantment) {
        assertNotNull(data);
        assertEquals(1, enchantment.getMinLevel());
    }

    @ParameterizedTest
    @MethodSource("getEnchantmentDataWithLevels")
    void testMinEnchantability(EnchantmentData data, Enchantment enchantment, int level) {
        int minEnchantAbility = enchantment.getMinEnchantAbility(level);
        int[] levelData = data.getLevelData(level);
        assertEquals(levelData[0], minEnchantAbility);
    }

    @ParameterizedTest
    @MethodSource("getEnchantmentDataWithLevels")
    void testMaxEnchantability(EnchantmentData data, Enchantment enchantment, int level) {
        int maxEnchantAbility = enchantment.getMaxEnchantAbility(level);
        int[] levelData = data.getLevelData(level);
        assertEquals(levelData[1], maxEnchantAbility);
    }

    @ParameterizedTest
    @MethodSource("getEnchantmentDataWithPrimaryItems")
    void testPrimaryItem(Enchantment enchantment, Item item) {
        assertTrue(enchantment.canEnchant(item));
    }

    @SuppressWarnings("deprecation")
    @ParameterizedTest
    @MethodSource("getEnchantmentDataWithSecondaryItems")
    void testSecondaryItem(Enchantment enchantment, Item item) {
        assertTrue(enchantment.canEnchant(item));
        assertTrue(enchantment.isItemAcceptable(item));
    }

    @SuppressWarnings("deprecation")
    @ParameterizedTest
    @MethodSource("getNotAcceptedItems")
    @Execution(ExecutionMode.CONCURRENT)
    void testNotAcceptedItems(Enchantment enchantment, Item item) {
        assertFalse(enchantment.canEnchant(item));
        assertFalse(enchantment.isItemAcceptable(item));
    }
    
    @SneakyThrows
    public static int unchecked(CheckedIntSupplier supplier) {
        return supplier.getAsInt();
    }
    
    static IntStream getAllItemIds() {
        return Arrays.stream(ItemID.class.getDeclaredFields())
                .filter(field -> field.getType().equals(int.class))
                .filter(field -> field.getModifiers() == (Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL))
                .mapToInt(field -> unchecked(()-> field.getInt(null)));
    }
    
    static Stream<Arguments> getNotAcceptedItems() {
        return enchantmentDataList.parallelStream().flatMap(data -> {
            IntSet acceptedItems = new IntOpenHashSet();
            IntSet notAccepted = new IntOpenHashSet(0);
            Stream.of(data.primary.stream(), data.secondary.stream()).flatMap(Function.identity()).distinct()
                    .forEachOrdered(type -> {
                        acceptedItems.addAll(new IntArrayList(type.itemIds));
                        if (type.notAccepted.length > 0) {
                            notAccepted.addAll(new IntArrayList(type.notAccepted));
                        }
                    });
            Enchantment enchantment = Enchantment.getEnchantment(data.nid);
            IntStream allIdsStream = IntStream.of(allIds);
            IntStream idStream = notAccepted.isEmpty()? allIdsStream : 
                    Stream.of(IntStream.of(allIds), IntStream.of(notAccepted.toIntArray()))
                            .flatMapToInt(Function.identity()); 
            return idStream.parallel()
                    .filter(id-> !acceptedItems.contains(id))
                    .mapToObj(id-> Arguments.of(enchantment, Item.get(id)));
        });
    }

    static Stream<Arguments> getEnchantmentDataWithSecondaryItems() {
        return enchantmentDataList.stream()
                .flatMap(data ->
                        data.secondary.stream().flatMap(type->
                                Arrays.stream(type.itemIds).mapToObj(Item::get)
                                        .map(item -> Arguments.of(Enchantment.getEnchantment(data.nid), item))));
    }

    static Stream<Arguments> getEnchantmentDataWithPrimaryItems() {
        return enchantmentDataList.stream()
                .flatMap(data -> 
                        data.primary.stream().flatMap(type->
                                Arrays.stream(type.itemIds).mapToObj(Item::get)
                                        .map(item -> Arguments.of(Enchantment.getEnchantment(data.nid), item))));
    }
    
    static Stream<Arguments> getEnchantmentDataWithLevels() {
        return enchantmentDataList.stream()
                .flatMap(data -> IntStream.range(0, data.levels.length)
                        .mapToObj(index -> Arguments.of(data, Enchantment.getEnchantment(data.nid), index + 1)));
    }
    
    static Stream<Arguments> getEnchantmentData() {
        return enchantmentDataList.stream().map( data ->
                Arguments.of(data, Enchantment.getEnchantment(data.getNid()))
        );
    }
    
    @BeforeAll
    static void beforeAll() throws IOException {
        try (InputStream is = EnchantmentTest.class.getResourceAsStream("enchantments.json");
             Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {

            enchantmentDataList = new Gson().fromJson(reader, EnchantmentTestList.class);
            for (EnchantmentData enchantmentData : enchantmentDataList) {
                try {
                    assertTrue(enchantmentData.primary.stream().noneMatch(Objects::isNull));
                    assertTrue(enchantmentData.secondary.stream().noneMatch(Objects::isNull));
                } catch (AssertionFailedError e) {
                    throw new AssertionFailedError(
                            "One of the primary or secondary items in enchantment data of the " + 
                                    enchantmentData.getId() + " enchantment was null", e);
                }
            }
        }
    }
    
    static @Data class EnchantmentData {
        String id;
        int nid;
        String i18n;
        List<String> incompatible;
        int weight;
        List<ItemType> primary;
        List<ItemType> secondary;
        int[][] levels;
        
        int[] getLevelData(int level) {
            return levels[level - 1];
        }
    }
    
    enum ItemType {
        helmet(LEATHER_CAP, IRON_HELMET, GOLD_HELMET, CHAIN_HELMET, DIAMOND_HELMET, NETHERITE_HELMET, TURTLE_SHELL),
        chestplate(LEATHER_TUNIC, IRON_CHESTPLATE, GOLD_CHESTPLATE, CHAIN_CHESTPLATE, DIAMOND_CHESTPLATE, NETHERITE_CHESTPLATE),
        leggings(LEATHER_PANTS, IRON_LEGGINGS, GOLD_LEGGINGS, CHAIN_LEGGINGS, DIAMOND_LEGGINGS, NETHERITE_LEGGINGS),
        boots(LEATHER_BOOTS, IRON_BOOTS, GOLD_BOOTS, CHAIN_BOOTS, DIAMOND_BOOTS, NETHERITE_BOOTS),
        elytra(ELYTRA),
        pumpkin(new int[]{BlockID.PUMPKIN}, new int[]{255 - BlockID.CARVED_PUMPKIN}),
        skull(SKULL),
        sword(WOODEN_SWORD, STONE_SWORD, GOLD_SWORD, IRON_SWORD, DIAMOND_SWORD, NETHERITE_SWORD),
        axe(WOODEN_AXE, STONE_AXE, GOLD_AXE, IRON_AXE, DIAMOND_AXE, NETHERITE_AXE),
        hoe(WOODEN_HOE, STONE_HOE, GOLD_HOE, IRON_HOE, DIAMOND_HOE, NETHERITE_HOE),
        shovel(WOODEN_SHOVEL, STONE_SHOVEL, IRON_SHOVEL, GOLD_SHOVEL, DIAMOND_SHOVEL, NETHERITE_SHOVEL),
        pickaxe(WOODEN_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, GOLD_PICKAXE, DIAMOND_PICKAXE, NETHERITE_PICKAXE),
        shears(SHEARS),
        fishing_rod(FISHING_ROD),
        carrot_on_stick(CARROT_ON_A_STICK),
        warped_fungus_on_stick(WARPED_FUNGUS_ON_A_STICK),
        bow(BOW),
        crossbow(CROSSBOW),
        trident(TRIDENT),
        flint_and_steel(FLINT_AND_STEEL),
        shield(SHIELD),
        compass(COMPASS)
        ;
        private final int[] itemIds;
        private final int[] notAccepted; 
        ItemType(int... ids) {
            this.itemIds = ids;
            this.notAccepted = EmptyArrays.EMPTY_INTS;
        }
        ItemType(int[] notAccepted, int[] ids) {
            this.itemIds = ids;
            this.notAccepted = notAccepted;
        }
    }
    
    static class EnchantmentTestList extends ArrayList<EnchantmentData> {}
    
    @FunctionalInterface interface CheckedIntSupplier {
        int getAsInt() throws Exception;
    }
}
