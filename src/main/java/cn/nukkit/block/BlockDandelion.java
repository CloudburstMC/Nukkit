package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockDandelion extends BlockFlower {
    public BlockDandelion() {
        this(0);
    }

    public BlockDandelion(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Dandelion";
    }

    @Override
    public int getId() {
        return DANDELION;
    }

    @Override
    protected Block getUncommonFlower() {
        return get(POPPY);
    }
}
