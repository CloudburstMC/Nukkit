package cn.nukkit.level.format.updater;

import cn.nukkit.blockstate.BlockState;

@FunctionalInterface
interface Updater {
    boolean update(int offsetX, int offsetY, int offsetZ, int x, int y, int z, BlockState state);
}
