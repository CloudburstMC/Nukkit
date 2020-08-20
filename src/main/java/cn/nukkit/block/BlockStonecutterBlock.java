package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.inventory.StonecutterInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.types.ContainerIds;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PowerNukkitOnly
public class BlockStonecutterBlock extends BlockTransparentMeta implements Faceable {

    @PowerNukkitOnly
    public BlockStonecutterBlock() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockStonecutterBlock(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STONECUTTER_BLOCK;
    }

    @Override
    public String getName() {
        return "Stonecutter";
    }

    @Override
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public void setBlockFace(BlockFace face) {
        int horizontalIndex = face.getHorizontalIndex();
        if (horizontalIndex > -1) {
            setDamage(horizontalIndex);
        }
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x7);
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        int[] faces = {2, 5, 3, 4};
        this.setDamage(faces[player != null ? player.getDirection().getHorizontalIndex() : 0]);
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, @Nullable Player player) {
        if (player != null) {
            player.addWindow(new StonecutterInventory(player.getUIInventory(), this), ContainerIds.NONE);
            player.craftingType = Player.CRAFTING_STONECUTTER;
        }
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    public double getHardness() {
        return 3.5;
    }

    @Override
    public double getResistance() {
        return 17.5;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[] {  toItem() };
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockStonecutterBlock());
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed the returned value")
    @Override
    public double getMaxY() {
        return y + 9/16.0;
    }
}
