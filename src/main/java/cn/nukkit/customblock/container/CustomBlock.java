package cn.nukkit.customblock.container;

import cn.nukkit.block.Block;

public class CustomBlock extends Block implements BlockContainer {

    private final String blockName;
    private final int blockId;

    public CustomBlock(String blockName, int blockId) {
        this.blockName = blockName;
        this.blockId = blockId;
    }

    @Override
    public int getId() {
        return this.blockId;
    }

    @Override
    public int getNukkitId() {
        return this.blockId;
    }

    @Override
    public String getName() {
        return this.blockName;
    }
}
