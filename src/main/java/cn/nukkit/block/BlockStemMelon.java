package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemID;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;

/**
 * @author Pub4Game
 * @since 15.01.2016
 *
 * @apiNote Implements {@link Faceable} only on PowerNukkit since 1.3.0.0-PN
 * and extends {@link BlockCropsStem} instead of {@link BlockCrops} only in PowerNukkit since 1.4.0.0-PN
 */
@PowerNukkitDifference(since = "1.3.0.0-PN", info = "Implements Faceable only in PowerNukkit")
@PowerNukkitDifference(since = "1.3.0.0-PN", info = "Will bind to the melon by the server-side")
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Extends BlockCropsStem instead of BlockCrops only in PowerNukkit")
public class BlockStemMelon extends BlockCropsStem implements Faceable {

    public BlockStemMelon() {
        this(0);
    }

    public BlockStemMelon(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MELON_STEM;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getFruitId() {
        return MELON_BLOCK;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getSeedsId() {
        return ItemID.MELON_SEEDS;
    }

    @Override
    public String getName() {
        return "Melon Stem";
    }

    @PowerNukkitOnly("Implements Faceable only on PowerNukkit since 1.3.0.0-PN")
    @Since("1.3.0.0-PN")
    @Override
    public BlockFace getBlockFace() {
        return super.getBlockFace();
    }
    
    @Override
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public void setBlockFace(BlockFace face) {
        super.setBlockFace(face);
    }
}
