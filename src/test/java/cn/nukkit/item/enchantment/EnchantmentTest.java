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
import com.google.gson.Gson;
import lombok.Data;
import lombok.Getter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opentest4j.AssertionFailedError;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static cn.nukkit.item.ItemID.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author joserobjr
 * @since 2021-02-15
 */
@ExtendWith(PowerNukkitExtension.class)
class EnchantmentTest {
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
    }
    
    enum ItemType {
        helmet(LEATHER_CAP, IRON_HELMET, GOLD_HELMET, CHAIN_HELMET, DIAMOND_HELMET, NETHERITE_HELMET, SHULKER_SHELL),
        chestplate(LEATHER_TUNIC, IRON_CHESTPLATE, GOLD_CHESTPLATE, CHAIN_CHESTPLATE, DIAMOND_CHESTPLATE, NETHERITE_CHESTPLATE),
        leggings(LEATHER_PANTS, IRON_LEGGINGS, GOLD_LEGGINGS, CHAIN_LEGGINGS, DIAMOND_LEGGINGS, NETHERITE_LEGGINGS),
        boots(LEATHER_BOOTS, IRON_BOOTS, GOLD_BOOTS, CHAIN_BOOTS, DIAMOND_BOOTS, NETHERITE_BOOTS),
        elytra(ELYTRA),
        pumpkin(BlockID.PUMPKIN, 255 - BlockID.CARVED_PUMPKIN),
        skull(SKULL),
        sword(WOODEN_SWORD, GOLD_SWORD, IRON_SWORD, DIAMOND_SWORD, NETHERITE_SWORD),
        axe(WOODEN_AXE, GOLD_AXE, IRON_AXE, DIAMOND_AXE, NETHERITE_AXE),
        hoe(WOODEN_HOE, GOLD_HOE, IRON_HOE, DIAMOND_HOE, NETHERITE_HOE),
        shovel(WOODEN_SHOVEL, GOLD_SHOVEL, DIAMOND_SHOVEL, NETHERITE_SHOVEL),
        pickaxe(WOODEN_PICKAXE, GOLD_PICKAXE, DIAMOND_PICKAXE, NETHERITE_PICKAXE),
        shears(SHEARS),
        fishing_rod(FISHING_ROD),
        carrot_on_stick(CARROT_ON_A_STICK),
        warped_fungus_on_stick(WARPED_FUNGUS_ON_A_STICK),
        bow(BOW),
        crossbow(CROSSBOW),
        trident(TRIDENT),
        flint_and_steel(FLINT_AND_STEEL),
        shield(SHIELD),
        any,
        ;
        private int[] itemIds;
        ItemType(int... ids) {
            this.itemIds = ids;
        }
    }
    
    static class EnchantmentTestList extends ArrayList<EnchantmentData> {
    }
}
