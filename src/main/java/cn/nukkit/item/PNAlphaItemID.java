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
public interface PNAlphaItemID {
    @Since("1.3.2.0-PN") @PowerNukkitOnly int COD_BUCKET = 802;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int GHAST_SPAWN_EGG = 803;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int FLOWER_BANNER_PATTERN = 804;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int ZOGLIN_SPAWN_EGG = 805;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int BLUE_DYE = 806;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int SKULL_BANNER_PATTERN = 807;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int ENDERMITE_SPAWN_EGG = 808;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int POLAR_BEAR_SPAWN_EGG = 809;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int WHITE_DYE = 810;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int TROPICAL_FISH_BUCKET = 811;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int CYAN_DYE = 812;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int LIGHT_BLUE_DYE = 813;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int LIME_DYE = 814;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int ZOMBIE_VILLAGER_SPAWN_EGG = 815;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int STRAY_SPAWN_EGG = 816;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int GREEN_DYE = 817;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int EVOKER_SPAWN_EGG = 818;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int WITHER_SKELETON_SPAWN_EGG = 819;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int SALMON_BUCKET = 820;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int JUNGLE_BOAT = 821;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int BLACK_DYE = 822;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int MAGMA_CUBE_SPAWN_EGG = 823;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int TROPICAL_FISH_SPAWN_EGG = 824;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int VEX_SPAWN_EGG = 825;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int FIELD_MASONED_BANNER_PATTERN = 826;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int WANDERING_TRADER_SPAWN_EGG = 827;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int BROWN_DYE = 828;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int PANDA_SPAWN_EGG = 829;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int SILVERFISH_SPAWN_EGG = 830;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int OCELOT_SPAWN_EGG = 831;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int LAVA_BUCKET = 832;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int SKELETON_SPAWN_EGG = 833;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int VILLAGER_SPAWN_EGG = 834;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int ELDER_GUARDIAN_SPAWN_EGG = 835;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int ACACIA_BOAT = 836;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int OAK_BOAT = 837;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int PHANTOM_SPAWN_EGG = 838;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int HOGLIN_SPAWN_EGG = 839;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int DARK_OAK_BOAT = 840;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int HUSK_SPAWN_EGG = 841;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int BLAZE_SPAWN_EGG = 842;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int BORDURE_INDENTED_BANNER_PATTERN = 843;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int MULE_SPAWN_EGG = 844;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int CREEPER_BANNER_PATTERN = 845;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int ZOMBIE_HORSE_SPAWN_EGG = 846;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int BEE_SPAWN_EGG = 847;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int COD_SPAWN_EGG = 848;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int LLAMA_SPAWN_EGG = 849;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int FOX_SPAWN_EGG = 850;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int PIGLIN_BRUTE_SPAWN_EGG = 851;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int PIG_SPAWN_EGG = 852;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int COW_SPAWN_EGG = 853;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int NPC_SPAWN_EGG = 854;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int SQUID_SPAWN_EGG = 855;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int MAGENTA_DYE = 856;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int RED_DYE = 857;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int WITCH_SPAWN_EGG = 858;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int INK_SAC = 859;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int ORANGE_DYE = 860;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int PILLAGER_SPAWN_EGG = 861;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int CAVE_SPIDER_SPAWN_EGG = 862;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int BONE_MEAL = 863;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int PUFFERFISH_BUCKET = 864;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int BAT_SPAWN_EGG = 865;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int SPRUCE_BOAT = 866;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int SPIDER_SPAWN_EGG = 867;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int PIGLIN_BANNER_PATTERN = 868;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int RABBIT_SPAWN_EGG = 869;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int MOJANG_BANNER_PATTERN = 870;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int PIGLIN_SPAWN_EGG = 871;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int TURTLE_SPAWN_EGG = 872;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int MOOSHROOM_SPAWN_EGG = 873;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int PUFFERFISH_SPAWN_EGG = 874;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int PARROT_SPAWN_EGG = 875;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int ZOMBIE_SPAWN_EGG = 876;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int WOLF_SPAWN_EGG = 877;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int GRAY_DYE = 878;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int COCOA_BEANS = 879;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int SKELETON_HORSE_SPAWN_EGG = 880;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int SHEEP_SPAWN_EGG = 881;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int SLIME_SPAWN_EGG = 882;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int VINDICATOR_SPAWN_EGG = 883;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int DROWNED_SPAWN_EGG = 884;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int MILK_BUCKET = 885;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int DOLPHIN_SPAWN_EGG = 886;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int DONKEY_SPAWN_EGG = 887;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int PURPLE_DYE = 888;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int BIRCH_BOAT = 889;
    //@Since("1.3.2.0-PN") @PowerNukkitOnly int DEBUG_STICK = 890;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int ENDERMAN_SPAWN_EGG = 891;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int CHICKEN_SPAWN_EGG = 892;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int SHULKER_SPAWN_EGG = 893;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int STRIDER_SPAWN_EGG = 894;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int ZOMBIE_PIGMAN_SPAWN_EGG = 895;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int YELLOW_DYE = 896;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int CAT_SPAWN_EGG = 897;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int GUARDIAN_SPAWN_EGG = 898;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int PINK_DYE = 899;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int SALMON_SPAWN_EGG = 900;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int CREEPER_SPAWN_EGG = 901;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int HORSE_SPAWN_EGG = 902;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int LAPIS_LAZULI = 903;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int RAVAGER_SPAWN_EGG = 904;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int WATER_BUCKET = 905;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int LIGHT_GRAY_DYE = 906;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int CHARCOAL = 907;
    @Since("1.3.2.0-PN") @PowerNukkitOnly int AGENT_SPAWN_EGG = 908;
}
