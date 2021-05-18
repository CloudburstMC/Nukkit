package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.world.StructureGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.world.World;
import cn.nukkit.world.ListChunkManager;
import cn.nukkit.world.generator.object.mushroom.BigMushroom;
import cn.nukkit.world.particle.BoneMealParticle;

import java.util.concurrent.ThreadLocalRandom;

public abstract class BlockMushroom extends BlockFlowable {

    public BlockMushroom() {
        this(0);
    }

    public BlockMushroom(int meta) {
        super(0);
    }

    @Override
    public int onUpdate(int type) {
        if (type == World.BLOCK_UPDATE_NORMAL) {
            if (!canStay()) {
                getWorld().useBreakOn(this);

                return World.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (canStay()) {
            getWorld().setBlock(block, this, true, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == Item.DYE && item.getDamage() == DyeColor.WHITE.getDyeData()) {
            if (player != null && (player.gamemode & 0x01) == 0) {
                item.count--;
            }

            if (ThreadLocalRandom.current().nextFloat() < 0.4) {
                this.grow();
            }

            this.world.addParticle(new BoneMealParticle(this));
            return true;
        }
        return false;
    }

    public boolean grow() {
        this.world.setBlock(this, Block.get(BlockID.AIR), true, false);

        BigMushroom generator = new BigMushroom(getType());

        ListChunkManager chunkManager = new ListChunkManager(this.world);
        if (generator.generate(chunkManager, new NukkitRandom(), this)) {
            StructureGrowEvent ev = new StructureGrowEvent(this, chunkManager.getBlocks());
            this.world.getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }
            for(Block block : ev.getBlockList()) {
                this.world.setBlockAt(block.getFloorX(), block.getFloorY(), block.getFloorZ(), block.getId(), block.getDamage());
            }
            return true;
        } else {
            this.world.setBlock(this, this, true, false);
            return false;
        }
    }

    public boolean canStay() {
        Block block = this.down();
        return block.getId() == MYCELIUM || block.getId() == PODZOL || (!block.isTransparent() && this.world.getFullLight(this) < 13);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    protected abstract int getType();
}
