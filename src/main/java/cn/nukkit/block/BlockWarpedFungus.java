package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.object.tree.ObjectWarpedTree;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.level.Position;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.DyeColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockWarpedFungus extends BlockFungus {

    public BlockWarpedFungus() {
        // Does nothing
    }

    @Override
    public int getId() {
        return WARPED_FUNGUS;
    }

    @Override
    public String getName() {
        return "Warped Fungus";
    }

    @Override
    protected boolean canGrowOn(Block support) {
        return support.getId() == WARPED_NYLIUM;
    }

    @Override
    public boolean grow(Player cause) {
        // TODO:
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().isTransparent()) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
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

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == Item.DYE && item.getDamage() == DyeColor.WHITE.getDyeData()) {
            if (player != null && (player.gamemode & 0x01) == 0) {
                item.count--;
            }

            if (ThreadLocalRandom.current().nextFloat() < 0.4 && this.level.getBlockIdAt((int) this.x, (int) this.y - 1, (int) this.z) == WARPED_NYLIUM) {
                new ObjectWarpedTree().placeObject(this.level, (int) this.x, (int) this.y, (int) this.z, new NukkitRandom());
                this.level.setBlock(new Vector3((int) this.x, (int) this.y - 1, (int) this.z), Block.get(NETHERRACK), false, true);
            }

            this.level.addParticle(new BoneMealParticle(this));
            return true;
        }
        return false;
    }
}
