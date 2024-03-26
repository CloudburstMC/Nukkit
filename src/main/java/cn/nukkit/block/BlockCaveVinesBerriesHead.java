package cn.nukkit.block;

import cn.nukkit.level.Position;

public class BlockCaveVinesBerriesHead extends BlockCaveVines {

    public BlockCaveVinesBerriesHead() {
        this(0);
    }

    public BlockCaveVinesBerriesHead(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CAVE_VINES_HEAD_WITH_BERRIES;
    }

    @Override
    public BlockCaveVines getStateWithBerries(Position position) {
        return (BlockCaveVines) Block.get(CAVE_VINES_HEAD_WITH_BERRIES, 0, position);
    }

    @Override
    public BlockCaveVines getStateWithoutBerries(Position position) {
        return (BlockCaveVines) Block.get(CAVE_VINES, 0, position);
    }

    @Override
    public boolean hasBerries() {
        return true;
    }
}
