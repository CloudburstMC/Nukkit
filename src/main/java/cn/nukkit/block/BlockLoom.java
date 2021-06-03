package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

@PowerNukkitOnly
public class BlockLoom extends BlockSolidMeta {

    @PowerNukkitOnly
    public BlockLoom() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockLoom(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LOOM;
    }

    @Override
    public String getName() {
        return "Loom";
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockLoom());
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public double getResistance() {
        return 12.5;
    }

    @Override
    public double getHardness() {
        return 2.5;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        //TODO Loom's inventory
        return false;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        if (player != null) {
            setDamage(player.getDirection().getOpposite().getHorizontalIndex());
        }
        this.level.setBlock(this, this, true, true);
        return true;
    }
}
