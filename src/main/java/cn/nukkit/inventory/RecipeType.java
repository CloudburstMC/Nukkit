package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

public enum RecipeType {
    SHAPELESS(0),
    SHAPED(1),
    FURNACE(2),
    FURNACE_DATA(3),
    MULTI(4),
    SHULKER_BOX(5),
    SHAPELESS_CHEMISTRY(6),
    SHAPED_CHEMISTRY(7),
    @PowerNukkitOnly BLAST_FURNACE(2),
    @PowerNukkitOnly BLAST_FURNACE_DATA(3),
    @PowerNukkitOnly SMOKER(2),
    @PowerNukkitOnly SMOKER_DATA(3),
    @PowerNukkitOnly CAMPFIRE(2),
    @PowerNukkitOnly CAMPFIRE_DATA(3),
    @PowerNukkitOnly STONECUTTER(0),
    @PowerNukkitOnly CARTOGRAPHY(0),
    @PowerNukkitOnly REPAIR(-1),
    @PowerNukkitOnly @Since("1.4.0.0-PN") SMITHING(-1)
    ;

    @PowerNukkitOnly public final int networkType;

    RecipeType(int networkType) {
        this.networkType = networkType;
    }
}
