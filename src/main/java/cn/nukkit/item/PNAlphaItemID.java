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
    @Since("1.3.2.0-PN") @PowerNukkitOnly COD_BUCKET(802, MinecraftItemID.COD_BUCKET),
    @Since("1.3.2.0-PN") @PowerNukkitOnly GHAST_SPAWN_EGG(803, MinecraftItemID.GHAST_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly FLOWER_BANNER_PATTERN(804, MinecraftItemID.FLOWER_BANNER_PATTERN),
    @Since("1.3.2.0-PN") @PowerNukkitOnly ZOGLIN_SPAWN_EGG(805, MinecraftItemID.ZOGLIN_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly BLUE_DYE(806, MinecraftItemID.BLUE_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly SKULL_BANNER_PATTERN(807, MinecraftItemID.SKULL_BANNER_PATTERN),
    @Since("1.3.2.0-PN") @PowerNukkitOnly ENDERMITE_SPAWN_EGG(808, MinecraftItemID.ENDERMITE_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly POLAR_BEAR_SPAWN_EGG(809, MinecraftItemID.POLAR_BEAR_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly WHITE_DYE(810, MinecraftItemID.WHITE_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly TROPICAL_FISH_BUCKET(811, MinecraftItemID.TROPICAL_FISH_BUCKET),
    @Since("1.3.2.0-PN") @PowerNukkitOnly CYAN_DYE(812, MinecraftItemID.CYAN_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly LIGHT_BLUE_DYE(813, MinecraftItemID.LIGHT_BLUE_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly LIME_DYE(814, MinecraftItemID.LIME_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly ZOMBIE_VILLAGER_SPAWN_EGG(815, MinecraftItemID.ZOMBIE_VILLAGER_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly STRAY_SPAWN_EGG(816, MinecraftItemID.STRAY_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly GREEN_DYE(817, MinecraftItemID.GREEN_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly EVOKER_SPAWN_EGG(818, MinecraftItemID.EVOKER_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly WITHER_SKELETON_SPAWN_EGG(819, MinecraftItemID.WITHER_SKELETON_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly SALMON_BUCKET(820, MinecraftItemID.SALMON_BUCKET),
    @Since("1.3.2.0-PN") @PowerNukkitOnly JUNGLE_BOAT(821, MinecraftItemID.JUNGLE_BOAT),
    @Since("1.3.2.0-PN") @PowerNukkitOnly BLACK_DYE(822, MinecraftItemID.BLACK_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly MAGMA_CUBE_SPAWN_EGG(823, MinecraftItemID.MAGMA_CUBE_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly TROPICAL_FISH_SPAWN_EGG(824, MinecraftItemID.TROPICAL_FISH_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly VEX_SPAWN_EGG(825, MinecraftItemID.VEX_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly FIELD_MASONED_BANNER_PATTERN(826, MinecraftItemID.FIELD_MASONED_BANNER_PATTERN),
    @Since("1.3.2.0-PN") @PowerNukkitOnly WANDERING_TRADER_SPAWN_EGG(827, MinecraftItemID.WANDERING_TRADER_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly BROWN_DYE(828, MinecraftItemID.BROWN_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly PANDA_SPAWN_EGG(829, MinecraftItemID.PANDA_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly SILVERFISH_SPAWN_EGG(830, MinecraftItemID.SILVERFISH_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly OCELOT_SPAWN_EGG(831, MinecraftItemID.OCELOT_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly LAVA_BUCKET(832, MinecraftItemID.LAVA_BUCKET),
    @Since("1.3.2.0-PN") @PowerNukkitOnly SKELETON_SPAWN_EGG(833, MinecraftItemID.SKELETON_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly VILLAGER_SPAWN_EGG(834, MinecraftItemID.VILLAGER_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly ELDER_GUARDIAN_SPAWN_EGG(835, MinecraftItemID.ELDER_GUARDIAN_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly ACACIA_BOAT(836, MinecraftItemID.ACACIA_BOAT),
    @Since("1.3.2.0-PN") @PowerNukkitOnly OAK_BOAT(837, MinecraftItemID.OAK_BOAT),
    @Since("1.3.2.0-PN") @PowerNukkitOnly PHANTOM_SPAWN_EGG(838, MinecraftItemID.PHANTOM_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly HOGLIN_SPAWN_EGG(839, MinecraftItemID.HOGLIN_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly DARK_OAK_BOAT(840, MinecraftItemID.DARK_OAK_BOAT),
    @Since("1.3.2.0-PN") @PowerNukkitOnly HUSK_SPAWN_EGG(841, MinecraftItemID.HUSK_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly BLAZE_SPAWN_EGG(842, MinecraftItemID.BLAZE_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly BORDURE_INDENTED_BANNER_PATTERN(843, MinecraftItemID.BORDURE_INDENTED_BANNER_PATTERN),
    @Since("1.3.2.0-PN") @PowerNukkitOnly MULE_SPAWN_EGG(844, MinecraftItemID.MULE_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly CREEPER_BANNER_PATTERN(845, MinecraftItemID.CREEPER_BANNER_PATTERN),
    @Since("1.3.2.0-PN") @PowerNukkitOnly ZOMBIE_HORSE_SPAWN_EGG(846, MinecraftItemID.ZOMBIE_HORSE_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly BEE_SPAWN_EGG(847, MinecraftItemID.BEE_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly COD_SPAWN_EGG(848, MinecraftItemID.COD_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly LLAMA_SPAWN_EGG(849, MinecraftItemID.LLAMA_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly FOX_SPAWN_EGG(850, MinecraftItemID.FOX_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly PIGLIN_BRUTE_SPAWN_EGG(851, MinecraftItemID.PIGLIN_BRUTE_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly PIG_SPAWN_EGG(852, MinecraftItemID.PIG_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly COW_SPAWN_EGG(853, MinecraftItemID.COW_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly NPC_SPAWN_EGG(854, MinecraftItemID.NPC_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly SQUID_SPAWN_EGG(855, MinecraftItemID.SQUID_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly MAGENTA_DYE(856, MinecraftItemID.MAGENTA_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly RED_DYE(857, MinecraftItemID.RED_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly WITCH_SPAWN_EGG(858, MinecraftItemID.WITCH_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly INK_SAC(859, MinecraftItemID.INK_SAC),
    @Since("1.3.2.0-PN") @PowerNukkitOnly ORANGE_DYE(860, MinecraftItemID.ORANGE_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly PILLAGER_SPAWN_EGG(861, MinecraftItemID.PILLAGER_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly CAVE_SPIDER_SPAWN_EGG(862, MinecraftItemID.CAVE_SPIDER_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly BONE_MEAL(863, MinecraftItemID.BONE_MEAL),
    @Since("1.3.2.0-PN") @PowerNukkitOnly PUFFERFISH_BUCKET(864, MinecraftItemID.PUFFERFISH_BUCKET),
    @Since("1.3.2.0-PN") @PowerNukkitOnly BAT_SPAWN_EGG(865, MinecraftItemID.BAT_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly SPRUCE_BOAT(866, MinecraftItemID.SPRUCE_BOAT),
    @Since("1.3.2.0-PN") @PowerNukkitOnly SPIDER_SPAWN_EGG(867, MinecraftItemID.SPIDER_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly PIGLIN_BANNER_PATTERN(868, MinecraftItemID.PIGLIN_BANNER_PATTERN),
    @Since("1.3.2.0-PN") @PowerNukkitOnly RABBIT_SPAWN_EGG(869, MinecraftItemID.RABBIT_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly MOJANG_BANNER_PATTERN(870, MinecraftItemID.MOJANG_BANNER_PATTERN),
    @Since("1.3.2.0-PN") @PowerNukkitOnly PIGLIN_SPAWN_EGG(871, MinecraftItemID.PIGLIN_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly TURTLE_SPAWN_EGG(872, MinecraftItemID.TURTLE_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly MOOSHROOM_SPAWN_EGG(873, MinecraftItemID.MOOSHROOM_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly PUFFERFISH_SPAWN_EGG(874, MinecraftItemID.PUFFERFISH_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly PARROT_SPAWN_EGG(875, MinecraftItemID.PARROT_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly ZOMBIE_SPAWN_EGG(876, MinecraftItemID.ZOMBIE_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly WOLF_SPAWN_EGG(877, MinecraftItemID.WOLF_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly GRAY_DYE(878, MinecraftItemID.GRAY_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly COCOA_BEANS(879, MinecraftItemID.COCOA_BEANS),
    @Since("1.3.2.0-PN") @PowerNukkitOnly SKELETON_HORSE_SPAWN_EGG(880, MinecraftItemID.SKELETON_HORSE_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly SHEEP_SPAWN_EGG(881, MinecraftItemID.SHEEP_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly SLIME_SPAWN_EGG(882, MinecraftItemID.SLIME_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly VINDICATOR_SPAWN_EGG(883, MinecraftItemID.VINDICATOR_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly DROWNED_SPAWN_EGG(884, MinecraftItemID.DROWNED_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly MILK_BUCKET(885, MinecraftItemID.MILK_BUCKET),
    @Since("1.3.2.0-PN") @PowerNukkitOnly DOLPHIN_SPAWN_EGG(886, MinecraftItemID.DOLPHIN_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly DONKEY_SPAWN_EGG(887, MinecraftItemID.DONKEY_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly PURPLE_DYE(888, MinecraftItemID.PURPLE_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly BIRCH_BOAT(889, MinecraftItemID.BIRCH_BOAT),
    @Since("1.3.2.0-PN") @PowerNukkitOnly ENDERMAN_SPAWN_EGG(891, MinecraftItemID.ENDERMAN_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly CHICKEN_SPAWN_EGG(892, MinecraftItemID.CHICKEN_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly SHULKER_SPAWN_EGG(893, MinecraftItemID.SHULKER_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly STRIDER_SPAWN_EGG(894, MinecraftItemID.STRIDER_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly ZOMBIE_PIGMAN_SPAWN_EGG(895, MinecraftItemID.ZOMBIE_PIGMAN_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly YELLOW_DYE(896, MinecraftItemID.YELLOW_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly CAT_SPAWN_EGG(897, MinecraftItemID.CAT_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly GUARDIAN_SPAWN_EGG(898, MinecraftItemID.GUARDIAN_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly PINK_DYE(899, MinecraftItemID.PINK_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly SALMON_SPAWN_EGG(900, MinecraftItemID.SALMON_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly CREEPER_SPAWN_EGG(901, MinecraftItemID.CREEPER_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly HORSE_SPAWN_EGG(902, MinecraftItemID.HORSE_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly LAPIS_LAZULI(903, MinecraftItemID.LAPIS_LAZULI),
    @Since("1.3.2.0-PN") @PowerNukkitOnly RAVAGER_SPAWN_EGG(904, MinecraftItemID.RAVAGER_SPAWN_EGG),
    @Since("1.3.2.0-PN") @PowerNukkitOnly WATER_BUCKET(905, MinecraftItemID.WATER_BUCKET),
    @Since("1.3.2.0-PN") @PowerNukkitOnly LIGHT_GRAY_DYE(906, MinecraftItemID.LIGHT_GRAY_DYE),
    @Since("1.3.2.0-PN") @PowerNukkitOnly CHARCOAL(907, MinecraftItemID.CHARCOAL),
    @Since("1.3.2.0-PN") @PowerNukkitOnly AGENT_SPAWN_EGG(908, MinecraftItemID.AGENT_SPAWN_EGG)
    ;
    private final int badItemId;
    private final MinecraftItemID minecraftItemId;
    
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
