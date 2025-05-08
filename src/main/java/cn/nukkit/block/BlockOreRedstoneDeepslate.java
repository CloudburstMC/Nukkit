package cn.nukkit.block;

import cn.nukkit.level.Level;
import cn.nukkit.utils.BlockColor;

public class BlockOreRedstoneDeepslate extends BlockOreRedstone {

    public BlockOreRedstoneDeepslate() {
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_TOUCH) {
            this.getLevel().setBlock(this, Block.get(LIT_DEEPSLATE_REDSTONE_ORE), false, false);
            this.getLevel().scheduleUpdate(this, 600);

            return Level.BLOCK_UPDATE_WEAK;
        }
        return 0;
    }

    @Override
    public int getId() {
        return DEEPSLATE_REDSTONE_ORE;
    }

    @Override
    public double getHardness() {
        return 4.5;
    }

    @Override
    public String getName() {
        return "Deepslate Redstone Ore";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_GRAY_BLOCK_COLOR;
    }
}
