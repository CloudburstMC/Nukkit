package cn.nukkit.block;

import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;

/**
 * @author Angelic47
 * Nukkit Project
 */
public class BlockFurnace extends BlockFurnaceBurning implements Faceable {

    public BlockFurnace() {
        this(0);
    }

    public BlockFurnace(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Furnace";
    }

    @Override
    public int getId() {
        return FURNACE;
    }

    @Override
    public int getLightLevel() {
        return 0;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x7);
    }
}