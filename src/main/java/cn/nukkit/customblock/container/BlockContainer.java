package cn.nukkit.customblock.container;

import cn.nukkit.level.GlobalBlockPalette;

public interface BlockContainer {

    int getNukkitId();

    default int getNukkitDamage() {
        return 0;
    }

    default int getRuntimeId() {
        return GlobalBlockPalette.getOrCreateRuntimeId(this.getNukkitId(), this.getNukkitDamage());
    }
}
