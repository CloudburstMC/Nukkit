package cn.nukkit.block;

import cn.nukkit.event.level.StructureGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.ListChunkManager;
import cn.nukkit.level.generator.object.tree.ObjectMangroveTree;
import cn.nukkit.math.NukkitRandom;

public class BlockMangrovePropagule extends BlockSapling {

    public BlockMangrovePropagule() {
        this(0);
    }

    public BlockMangrovePropagule(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MANGROVE_PROPAGULE;
    }

    @Override
    public String getName() {
        return "Mangrove Propagule";
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.FLOW_INTO_BLOCK;
    }

    @Override
    protected boolean growTreeHere() {
        NukkitRandom random = new NukkitRandom();
        ObjectMangroveTree tree = new ObjectMangroveTree();
        ListChunkManager chunkManager = new ListChunkManager(this.level);
        tree.placeObject(chunkManager, (int) this.x, (int) this.y, (int) this.z, random);

        StructureGrowEvent ev = new StructureGrowEvent(this, chunkManager.getBlocks());
        this.level.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }

        for (Block block : ev.getBlockList()) {
            this.level.setBlock(block, block);
        }
        return true;
    }
}
