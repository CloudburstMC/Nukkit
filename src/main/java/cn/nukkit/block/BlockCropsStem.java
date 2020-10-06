/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.blockproperty.CommonBlockProperties.FACING_DIRECTION;

/**
 * @author joserobjr
 * @since 2020-09-15
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public abstract class BlockCropsStem extends BlockCrops implements Faceable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(GROWTH, FACING_DIRECTION);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected BlockCropsStem(int meta) {
        super(meta);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public abstract int getFruitId();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public abstract int getSeedsId();

    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(FACING_DIRECTION);
    }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(FACING_DIRECTION, face);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getGrowth() {
        return getIntValue(GROWTH);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setGrowth(int growth) {
        setIntValue(GROWTH, growth);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() != FARMLAND) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
            BlockFace blockFace = getBlockFace();
            if (blockFace.getAxis().isHorizontal() && getSide(blockFace).getId() != getFruitId()) {
                setBlockFace(null);
                getLevel().setBlock(this, this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
            return 0;
        }
        
        if (type != Level.BLOCK_UPDATE_RANDOM) {
            return 0;
        }
        
        if (ThreadLocalRandom.current().nextInt(1, 3) != 1 
                || getLevel().getFullLight(this) < MINIMUM_LIGHT_LEVEL) {
            return Level.BLOCK_UPDATE_RANDOM;
        }
        
        int growth = getGrowth();
        if (growth < GROWTH.getMaxValue()) {
            BlockCropsStem block = this.clone();
            block.setGrowth(growth + 1);
            BlockGrowEvent ev = new BlockGrowEvent(this, block);
            Server.getInstance().getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                this.getLevel().setBlock(this, ev.getNewState(), true);
            }
            return Level.BLOCK_UPDATE_RANDOM;
        }
        
        growFruit();
        return Level.BLOCK_UPDATE_RANDOM;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean growFruit() {
        int fruitId = getFruitId();
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            Block b = this.getSide(face);
            if (b.getId() == fruitId) {
                return false;
            }
        }
        
        BlockFace sideFace = BlockFace.Plane.HORIZONTAL.random();
        Block side = this.getSide(sideFace);
        Block d = side.down();
        if (side.getId() == AIR && (d.getId() == FARMLAND || d.getId() == GRASS || d.getId() == DIRT)) {
            BlockGrowEvent ev = new BlockGrowEvent(side, Block.get(fruitId));
            Server.getInstance().getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                this.getLevel().setBlock(side, ev.getNewState(), true);
                setBlockFace(sideFace);
                this.getLevel().setBlock(this, this, true);
            }
        }
        return true;
    }

    @Override
    public Item toItem() {
        return Item.get(getSeedsId());
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                Item.get(getSeedsId(), 0, ThreadLocalRandom.current().nextInt(4))
        };
    }

    @Override
    public BlockCropsStem clone() {
        return (BlockCropsStem) super.clone();
    }
}
