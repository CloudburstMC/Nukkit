package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.inventory.LoomInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockLoom extends BlockSolidMeta {

    public BlockLoom() {
        this(0);
    }

    public BlockLoom(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Loom";
    }

    @Override
    public int getId() {
        return LOOM;
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
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            player.addWindow(new LoomInventory(player.getUIInventory(), this), Player.LOOM_WINDOW_ID);
        }
        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    private static final short[] FACES = {2, 3, 0, 1};

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.setDamage(FACES[player != null ? player.getDirection().getHorizontalIndex() : 0]);
        return this.getLevel().setBlock(this, this, true, true);
    }
}
