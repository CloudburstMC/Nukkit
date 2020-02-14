package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.math.MathHelper.clamp;

public class BlockKelp extends FloodableBlock {

    public BlockKelp(Identifier id) {
        super(id);
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }

    @Override
    public boolean canWaterlogFlowing() {
        return true;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemIds.KELP);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        Block down = down();
        Block layerOneBlock = getBlockAtLayer(1);
        int waterDamage;
        if ((down.getId() == BlockIds.KELP || down.isSolid()) && down.getId() != BlockIds.MAGMA && down.getId() != BlockIds.ICE &&
                down.getId() != BlockIds.SOUL_SAND && (layerOneBlock instanceof BlockWater && ((waterDamage = (block.getDamage())) == 0
                || waterDamage == 8))) {
            if (waterDamage == 8) {
                this.level.setBlock(this, new BlockWater(BlockIds.WATER), true, false);
            }
            if (down.getId() == BlockIds.KELP && down.getDamage() != 24) {
                down.setDamage(24);
                this.level.setBlock(this, this, true, true);
            }
            setDamage(ThreadLocalRandom.current().nextInt(25));
            this.level.setBlock(this, this, true, true);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block down = down();
            Block layerOneBlock = getBlockAtLayer(1);
            int waterDamage;
            if (!(layerOneBlock.getId().equals(BlockIds.FROSTED_ICE)) &&
                    (!(layerOneBlock instanceof BlockWater) || ((waterDamage = layerOneBlock.getDamage()) != 0 && waterDamage != 8))
            ) {
                this.getLevel().useBreakOn(this);
                return type;
            }
            if (down instanceof BlockWater) {
                this.getLevel().useBreakOn(this);
                return type;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (ThreadLocalRandom.current().nextInt(100) <= 14) {
                grow();
            }
            return type;
        }
        return super.onUpdate(type);
    }

    private boolean grow() {
        int age = clamp(getDamage(), 0, 25);
        if (age < 25) {
            Block up = up();
            if (up instanceof BlockWater && up.getDamage() == 0 || up.getDamage() == 8) {
                BlockKelp grown = new BlockKelp(BlockIds.KELP);
                grown.setDamage(age + 1);
                BlockGrowEvent ev = new BlockGrowEvent(this, grown);
                Server.getInstance().getPluginManager().callEvent(ev);
                if (!ev.isCancelled()) {
                    this.setDamage(25);
                    this.getLevel().setBlock(this, this, true, true);
                    this.getLevel().setBlock(up.layer(1), new BlockWater(BlockIds.WATER), true, false);
                    this.getLevel().setBlock(up, ev.getNewState(), true, true);
                    return true;
                }
            }
        }
        return false;
    }
}
