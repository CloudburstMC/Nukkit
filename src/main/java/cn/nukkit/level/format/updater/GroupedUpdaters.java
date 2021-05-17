package cn.nukkit.level.format.updater;

import cn.nukkit.blockstate.BlockState;

class GroupedUpdaters implements Updater {
    private final Updater[] updaters;

    public GroupedUpdaters(Updater... updaters) {
        this.updaters = updaters;
    }

    @Override
    public boolean update(int offsetX, int offsetY, int offsetZ, int x, int y, int z, BlockState state) {
        for (Updater updater : updaters) {
            if (updater != null && updater.update(offsetX, offsetY, offsetZ, x, y, z, state)) {
                return true;
            }
        }
        return false;
    }
}
