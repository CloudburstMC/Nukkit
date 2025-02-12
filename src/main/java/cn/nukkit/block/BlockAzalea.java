package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.level.StructureGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.level.ListChunkManager;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.object.tree.ObjectAzaleaTree;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitRandom;

import java.util.concurrent.ThreadLocalRandom;

public class BlockAzalea extends BlockTransparent {

    public BlockAzalea() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Azalea";
    }

    @Override
    public int getId() {
        return AZALEA;
    }

    @Override
    public boolean canPlaceOn(Block floor, Position pos) {
        // Azaleas can be placed on grass blocks, dirt, coarse dirt, rooted dirt, podzol, moss blocks, farmland, mud, muddy mangrove roots and clay.
        switch (floor.getId()) {
            case GRASS:
            case DIRT:
            case ROOTED_DIRT:
            case PODZOL:
            case MOSS_BLOCK:
            case FARMLAND:
            case MUD:
            case CLAY_BLOCK:
                return true;
        }
        return false;
    }

    @Override
    public boolean canBeClimbed() {
        return true;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    public boolean breakWhenPushed() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == Item.DYE && item.getDamage() == ItemDye.BONE_MEAL) {
            if (player != null && !player.isCreative()) {
                item.count--;
            }

            this.level.addParticle(new BoneMealParticle(this));
            if (ThreadLocalRandom.current().nextFloat() >= 0.45) {
                return true;
            }

            return growTreeHere();
        }
        return false;
    }

    private boolean growTreeHere() {
        NukkitRandom random = new NukkitRandom();
        ObjectAzaleaTree tree = new ObjectAzaleaTree();
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
