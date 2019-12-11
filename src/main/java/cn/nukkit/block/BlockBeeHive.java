package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;

public class BlockBeeHive extends BlockMeta implements Faceable {

    public final static int HONEY_LEVEL_BITMASK = 0x1C;

    public BlockBeeHive() {
        this(0);
    }
    protected BlockBeeHive(int meta) {
        super(meta);
    }

    public int getHoneyLevel() {
        return this.getDamage() & HONEY_LEVEL_BITMASK;
    }

    public void setHoneyLevel(int level) {
        if(level < 0) level = 0;
        if(level > 5) level = 5;
        this.setDamage(level << 2 + (this.getDamage() & 0x03));
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 3.0;
    }

    @Override
    public String getName() {
        return "Beehive";
    }

    @Override
    public int getId() {
        return BEEHIVE;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x03);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        int[] faces = {2, 5, 3, 4};
        this.setDamage(faces[player != null ? player.getDirection().getHorizontalIndex() : 0]);
        return super.place(item, block, target, face, fx, fy, fz, player);
    }
}
