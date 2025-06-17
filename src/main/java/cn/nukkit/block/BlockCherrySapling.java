package cn.nukkit.block;

import cn.nukkit.event.level.StructureGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.ListChunkManager;
import cn.nukkit.level.generator.object.tree.ObjectCherryTree;
import cn.nukkit.math.NukkitRandom;

public class BlockCherrySapling extends BlockSapling {

    public BlockCherrySapling() {
        this(0);
    }

    public BlockCherrySapling(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CHERRY_SAPLING;
    }

    @Override
    public String getName() {
        return "Cherry Sapling";
    }

    @Override
    protected boolean growTreeHere() {
        NukkitRandom random = new NukkitRandom();
        ObjectCherryTree tree = new ObjectCherryTree();
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

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }
}
