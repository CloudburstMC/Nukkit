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

package cn.nukkit.item;

import cn.nukkit.api.API;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author joserobjr
 * @since 2020-12-21
 */
@PowerNukkitOnly
@Since("1.3.2.0-PN")
@API(definition = API.Definition.INTERNAL, usage = API.Usage.DEPRECATED)
@Deprecated
@DeprecationDetails(since = "1.3.2.0-PN", reason = "" +
        "This interface was created to map item ids which were used in v1.3.2.0-PN-ALPHA.1, v1.3.2.0-PN-ALPHA.2 and v1.4.0.0-PN-ALPHA.1 " +
        "and will no longer be used because Nukkit took an other way and we will follow it to keep plugin compatibility in future.")
@RequiredArgsConstructor
@Getter
public enum PNAlphaItemID {
    @Since("1.3.2.0-PN") @PowerNukkitOnly COD_BUCKET(802, VanillaItemID.COD_BUCKET),
    @Since("1.3.2.0-PN") @PowerNukkitOnly GHAST_SPAWN_EGG(803, VanillaItemID.GHAST_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly FLOWER_BANNER_PATTERN(804, VanillaItemID.FLOWER_BANNER_PATTERN),
    @Since("1.3.2.0-PN") @PowerNukkitOnly ZOGLIN_SPAWN_EGG(805, VanillaItemID.ZOGLIN_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly BLUE_DYE(806, VanillaItemID.BLUE_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly SKULL_BANNER_PATTERN(807, VanillaItemID.SKULL_BANNER_PATTERN),
    @Since("1.3.2.0-PN") @PowerNukkitOnly ENDERMITE_SPAWN_EGG(808, VanillaItemID.ENDERMITE_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly POLAR_BEAR_SPAWN_EGG(809, VanillaItemID.POLAR_BEAR_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly WHITE_DYE(810, VanillaItemID.WHITE_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly TROPICAL_FISH_BUCKET(811, VanillaItemID.TROPICAL_FISH_BUCKET),
    @Since("1.3.2.0-PN") @PowerNukkitOnly CYAN_DYE(812, VanillaItemID.CYAN_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly LIGHT_BLUE_DYE(813, VanillaItemID.LIGHT_BLUE_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly LIME_DYE(814, VanillaItemID.LIME_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly ZOMBIE_VILLAGER_SPAWN_EGG(815, VanillaItemID.ZOMBIE_VILLAGER_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly STRAY_SPAWN_EGG(816, VanillaItemID.STRAY_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly GREEN_DYE(817, VanillaItemID.GREEN_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly EVOKER_SPAWN_EGG(818, VanillaItemID.EVOKER_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly WITHER_SKELETON_SPAWN_EGG(819, VanillaItemID.WITHER_SKELETON_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly SALMON_BUCKET(820, VanillaItemID.SALMON_BUCKET),
    @Since("1.3.2.0-PN") @PowerNukkitOnly JUNGLE_BOAT(821, VanillaItemID.JUNGLE_BOAT),
    @Since("1.3.2.0-PN") @PowerNukkitOnly BLACK_DYE(822, VanillaItemID.BLACK_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly MAGMA_CUBE_SPAWN_EGG(823, VanillaItemID.MAGMA_CUBE_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly TROPICAL_FISH_SPAWN_EGG(824, VanillaItemID.TROPICAL_FISH_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly VEX_SPAWN_EGG(825, VanillaItemID.VEX_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly FIELD_MASONED_BANNER_PATTERN(826, VanillaItemID.FIELD_MASONED_BANNER_PATTERN),
    @Since("1.3.2.0-PN") @PowerNukkitOnly WANDERING_TRADER_SPAWN_EGG(827, VanillaItemID.WANDERING_TRADER_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly BROWN_DYE(828, VanillaItemID.BROWN_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly PANDA_SPAWN_EGG(829, VanillaItemID.PANDA_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly SILVERFISH_SPAWN_EGG(830, VanillaItemID.SILVERFISH_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly OCELOT_SPAWN_EGG(831, VanillaItemID.OCELOT_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly LAVA_BUCKET(832, VanillaItemID.LAVA_BUCKET),
    @Since("1.3.2.0-PN") @PowerNukkitOnly SKELETON_SPAWN_EGG(833, VanillaItemID.SKELETON_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly VILLAGER_SPAWN_EGG(834, VanillaItemID.VILLAGER_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly ELDER_GUARDIAN_SPAWN_EGG(835, VanillaItemID.ELDER_GUARDIAN_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly ACACIA_BOAT(836, VanillaItemID.ACACIA_BOAT),
    @Since("1.3.2.0-PN") @PowerNukkitOnly OAK_BOAT(837, VanillaItemID.OAK_BOAT),
    @Since("1.3.2.0-PN") @PowerNukkitOnly PHANTOM_SPAWN_EGG(838, VanillaItemID.PHANTOM_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly HOGLIN_SPAWN_EGG(839, VanillaItemID.HOGLIN_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly DARK_OAK_BOAT(840, VanillaItemID.DARK_OAK_BOAT),
    @Since("1.3.2.0-PN") @PowerNukkitOnly HUSK_SPAWN_EGG(841, VanillaItemID.HUSK_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly BLAZE_SPAWN_EGG(842, VanillaItemID.BLAZE_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly BORDURE_INDENTED_BANNER_PATTERN(843, VanillaItemID.BORDURE_INDENTED_BANNER_PATTERN),
    @Since("1.3.2.0-PN") @PowerNukkitOnly MULE_SPAWN_EGG(844, VanillaItemID.MULE_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly CREEPER_BANNER_PATTERN(845, VanillaItemID.CREEPER_BANNER_PATTERN),
    @Since("1.3.2.0-PN") @PowerNukkitOnly ZOMBIE_HORSE_SPAWN_EGG(846, VanillaItemID.ZOMBIE_HORSE_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly BEE_SPAWN_EGG(847, VanillaItemID.BEE_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly COD_SPAWN_EGG(848, VanillaItemID.COD_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly LLAMA_SPAWN_EGG(849, VanillaItemID.LLAMA_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly FOX_SPAWN_EGG(850, VanillaItemID.FOX_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly PIGLIN_BRUTE_SPAWN_EGG(851, VanillaItemID.PIGLIN_BRUTE_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly PIG_SPAWN_EGG(852, VanillaItemID.PIG_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly COW_SPAWN_EGG(853, VanillaItemID.COW_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly NPC_SPAWN_EGG(854, VanillaItemID.NPC_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly SQUID_SPAWN_EGG(855, VanillaItemID.SQUID_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly MAGENTA_DYE(856, VanillaItemID.MAGENTA_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly RED_DYE(857, VanillaItemID.RED_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly WITCH_SPAWN_EGG(858, VanillaItemID.WITCH_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly INK_SAC(859, VanillaItemID.INK_SAC),
    @Since("1.3.2.0-PN") @PowerNukkitOnly ORANGE_DYE(860, VanillaItemID.ORANGE_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly PILLAGER_SPAWN_EGG(861, VanillaItemID.PILLAGER_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly CAVE_SPIDER_SPAWN_EGG(862, VanillaItemID.CAVE_SPIDER_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly BONE_MEAL(863, VanillaItemID.BONE_MEAL),
    @Since("1.3.2.0-PN") @PowerNukkitOnly PUFFERFISH_BUCKET(864, VanillaItemID.PUFFERFISH_BUCKET),
    @Since("1.3.2.0-PN") @PowerNukkitOnly BAT_SPAWN_EGG(865, VanillaItemID.BAT_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly SPRUCE_BOAT(866, VanillaItemID.SPRUCE_BOAT),
    @Since("1.3.2.0-PN") @PowerNukkitOnly SPIDER_SPAWN_EGG(867, VanillaItemID.SPIDER_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly PIGLIN_BANNER_PATTERN(868, VanillaItemID.PIGLIN_BANNER_PATTERN),
    @Since("1.3.2.0-PN") @PowerNukkitOnly RABBIT_SPAWN_EGG(869, VanillaItemID.RABBIT_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly MOJANG_BANNER_PATTERN(870, VanillaItemID.MOJANG_BANNER_PATTERN),
    @Since("1.3.2.0-PN") @PowerNukkitOnly PIGLIN_SPAWN_EGG(871, VanillaItemID.PIGLIN_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly TURTLE_SPAWN_EGG(872, VanillaItemID.TURTLE_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly MOOSHROOM_SPAWN_EGG(873, VanillaItemID.MOOSHROOM_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly PUFFERFISH_SPAWN_EGG(874, VanillaItemID.PUFFERFISH_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly PARROT_SPAWN_EGG(875, VanillaItemID.PARROT_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly ZOMBIE_SPAWN_EGG(876, VanillaItemID.ZOMBIE_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly WOLF_SPAWN_EGG(877, VanillaItemID.WOLF_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly GRAY_DYE(878, VanillaItemID.GRAY_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly COCOA_BEANS(879, VanillaItemID.COCOA_BEANS),
    @Since("1.3.2.0-PN") @PowerNukkitOnly SKELETON_HORSE_SPAWN_EGG(880, VanillaItemID.SKELETON_HORSE_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly SHEEP_SPAWN_EGG(881, VanillaItemID.SHEEP_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly SLIME_SPAWN_EGG(882, VanillaItemID.SLIME_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly VINDICATOR_SPAWN_EGG(883, VanillaItemID.VINDICATOR_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly DROWNED_SPAWN_EGG(884, VanillaItemID.DROWNED_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly MILK_BUCKET(885, VanillaItemID.MILK_BUCKET),
    @Since("1.3.2.0-PN") @PowerNukkitOnly DOLPHIN_SPAWN_EGG(886, VanillaItemID.DOLPHIN_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly DONKEY_SPAWN_EGG(887, VanillaItemID.DONKEY_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly PURPLE_DYE(888, VanillaItemID.PURPLE_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly BIRCH_BOAT(889, VanillaItemID.BIRCH_BOAT),
    @Since("1.3.2.0-PN") @PowerNukkitOnly ENDERMAN_SPAWN_EGG(891, VanillaItemID.ENDERMAN_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly CHICKEN_SPAWN_EGG(892, VanillaItemID.CHICKEN_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly SHULKER_SPAWN_EGG(893, VanillaItemID.SHULKER_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly STRIDER_SPAWN_EGG(894, VanillaItemID.STRIDER_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly ZOMBIE_PIGMAN_SPAWN_EGG(895, VanillaItemID.ZOMBIE_PIGMAN_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly YELLOW_DYE(896, VanillaItemID.YELLOW_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly CAT_SPAWN_EGG(897, VanillaItemID.CAT_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly GUARDIAN_SPAWN_EGG(898, VanillaItemID.GUARDIAN_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly PINK_DYE(899, VanillaItemID.PINK_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly SALMON_SPAWN_EGG(900, VanillaItemID.SALMON_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly CREEPER_SPAWN_EGG(901, VanillaItemID.CREEPER_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly HORSE_SPAWN_EGG(902, VanillaItemID.HORSE_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly LAPIS_LAZULI(903, VanillaItemID.LAPIS_LAZULI),
    @Since("1.3.2.0-PN") @PowerNukkitOnly RAVAGER_SPAWN_EGG(904, VanillaItemID.RAVAGER_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly WATER_BUCKET(905, VanillaItemID.WATER_BUCKET),
    @Since("1.3.2.0-PN") @PowerNukkitOnly LIGHT_GRAY_DYE(906, VanillaItemID.LIGHT_GRAY_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly CHARCOAL(907, VanillaItemID.CHARCOAL),
    @Since("1.3.2.0-PN") @PowerNukkitOnly AGENT_SPAWN_EGG(908, VanillaItemID.AGENT_SPAWN_EGG)
    ;
    private final int badItemId;
    private final VanillaItemID vanillaItemId;
    
    private final static Int2ObjectMap<PNAlphaItemID> byId = new Int2ObjectOpenHashMap<>(values().length);
    static {
        for (PNAlphaItemID value : values()) {
            byId.put(value.badItemId, value);
        }
    }
    
    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    public static PNAlphaItemID getBadAlphaId(int id) {
        return byId.get(id);
    }
}
