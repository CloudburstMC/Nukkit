package cn.nukkit.server.block;

import cn.nukkit.server.level.NukkitLevel;

/**
 * @author Pub4Game
 */
public class BlockRedstoneLampLit extends BlockRedstoneLamp {

    public BlockRedstoneLampLit(int meta) {
        super(meta);
    }

    public BlockRedstoneLampLit() {
        this(0);
    }

    @Override
    public String getName() {
        return "Lit Redstone Lamp";
    }

    @Override
    public int getId() {
        return LIT_REDSTONE_LAMP;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public int onUpdate(int type) {
        if ((type == NukkitLevel.BLOCK_UPDATE_NORMAL || type == NukkitLevel.BLOCK_UPDATE_REDSTONE) && !this.level.isBlockPowered(this)) {
            this.level.scheduleUpdate(this, 4);
            return 1;
        }

        if (type == NukkitLevel.BLOCK_UPDATE_SCHEDULED && !this.level.isBlockPowered(this)) {
            this.level.setBlock(this, new BlockRedstoneLamp(), false, false);
        }
        return 0;
    }
}
