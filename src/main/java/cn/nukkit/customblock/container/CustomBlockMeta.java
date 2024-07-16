package cn.nukkit.customblock.container;

import cn.nukkit.block.BlockMeta;
import cn.nukkit.customblock.properties.BlockProperties;

public class CustomBlockMeta extends BlockMeta implements BlockStorageContainer {

    private final String blockName;
    private final int blockId;
    private final BlockProperties properties;

    public CustomBlockMeta(String blockName, int blockId, BlockProperties properties) {
        this(blockName, blockId, properties, 0);
    }

    public CustomBlockMeta(String blockName, int blockId, BlockProperties properties, int meta) {
        super(meta);
        this.blockName = blockName;
        this.blockId = blockId;
        this.properties = properties;
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

    @Override
    public int getStorage() {
        return this.getDamage();
    }

    @Override
    public void setStorage(int damage) {
        this.setDamage(damage);
    }

    @Override
    public void setDamage(int meta) {
        super.setDamage(meta);
    }

    @Override
    public BlockProperties getBlockProperties() {
        return this.properties;
    }
}
