package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.level.StructureGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.ListChunkManager;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.object.tree.ObjectNetherTree;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.DyeColor;

import java.util.concurrent.ThreadLocalRandom;

public abstract class BlockFungus extends BlockFlowable {

    protected BlockFungus() {
        super(0);
    }

    protected abstract ObjectNetherTree getTree();

    protected abstract int getGround();

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL && !canPlaceOn(this.down(), this)) {
            this.level.useBreakOn(this);
            return type;
        }
        return 0;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == Item.DYE && item.getDamage() == DyeColor.WHITE.getDyeData()) {
            if (player != null && (player.gamemode & 0x01) == 0) {
                item.count--;
            }

            if (ThreadLocalRandom.current().nextFloat() < 0.4 && this.level.getBlockIdAt((int) this.x, (int) this.y - 1, (int) this.z) == this.getGround()) {
                growTreeHere();
            }

            this.level.addParticle(new BoneMealParticle(this));
            return true;
        }
        return false;
    }

    @Override
    public boolean canPlaceOn(Block floor, Position pos) {
        switch (floor.getId()) {
            case BlockID.GRASS:
            case BlockID.DIRT:
            case BlockID.PODZOL:
            case BlockID.FARMLAND:
            case BlockID.CRIMSON_NYLIUM:
            case BlockID.WARPED_NYLIUM:
            case BlockID.MYCELIUM:
            case BlockID.SOUL_SOIL:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    private boolean growTreeHere() {
        NukkitRandom random = new NukkitRandom();
        ObjectNetherTree tree = this.getTree();
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
