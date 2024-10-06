package cn.nukkit.block;

import cn.nukkit.level.Level;
import cn.nukkit.utils.BlockColor;

public class BlockSoulFire extends BlockFire {

    public BlockSoulFire() {
        this(0);
    }

    public BlockSoulFire(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SOUL_FIRE;
    }

    @Override
    public String getName() {
        return "Soul Fire";
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            int downId = down().getId();
            if (downId != Block.SOUL_SAND && downId != Block.SOUL_SOIL) {
                this.getLevel().setBlock(this, Block.get(Block.FIRE, this.getDamage()));
            }
            return type;
        }
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LIGHT_BLUE_BLOCK_COLOR;
    }
}
