package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;

import static cn.nukkit.block.BlockIds.*;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockFarmland extends BlockTransparent {

    public BlockFarmland(Identifier id) {
        super(id);
    }

    @Override
    public float getResistance() {
        return 3;
    }

    @Override
    public float getHardness() {
        return 0.6f;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public float getMaxY() {
        return this.getY() + 0.9375f;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            Vector3f v = Vector3f.ZERO;

            Vector3i upPos = this.getPosition().up();
            if (this.level.getBlock(upPos) instanceof BlockCrops) {
                return 0;
            }

            if (this.level.getBlock(upPos).isSolid()) {
                this.level.setBlock(this.getPosition(), Block.get(DIRT), false, true);

                return Level.BLOCK_UPDATE_RANDOM;
            }

            boolean found = false;

            if (this.level.isRaining()) {
                found = true;
            } else {
                for (int x = this.getX() - 4; x <= this.getX() + 4; x++) {
                    for (int z = this.getZ() - 4; z <= this.getZ() + 4; z++) {
                        for (int y = this.getY(); y <= this.getY() + 1; y++) {
                            if (z == this.getZ() && x == this.getX() && y == this.getY()) {
                                continue;
                            }

                            Identifier block = this.level.getBlockId(x, y, z);

                            if (block == FLOWING_WATER || block == WATER) {
                                found = true;
                                break;
                            }
                        }
                    }
                }
            }

            Block block = this.level.getBlock(this.getPosition().down());
            if (found || block instanceof BlockWater) {
                if (this.getMeta() < 7) {
                    this.setMeta(7);
                    this.level.setBlock(this.getPosition(), this, false, false);
                }
                return Level.BLOCK_UPDATE_RANDOM;
            }

            if (this.getMeta() > 0) {
                this.setMeta(this.getMeta() - 1);
                this.level.setBlock(this.getPosition(), this, false, false);
            } else {
                this.level.setBlock(this.getPosition(), Block.get(DIRT), false, true);
            }

            return Level.BLOCK_UPDATE_RANDOM;
        }

        return 0;
    }

    @Override
    public Item toItem() {
        return Item.get(DIRT);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
