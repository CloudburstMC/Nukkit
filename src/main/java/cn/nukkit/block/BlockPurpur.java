package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import static cn.nukkit.block.BlockIds.PURPUR_BLOCK;

public class BlockPurpur extends BlockSolid {

    public static final int PURPUR_NORMAL = 0;
    public static final int PURPUR_PILLAR = 2;

    public BlockPurpur(Identifier id) {
        super(id);
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public float getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (this.getMeta() != PURPUR_NORMAL) {
            short[] faces = new short[]{
                    0,
                    0,
                    0b1000,
                    0b1000,
                    0b0100,
                    0b0100
            };

            this.setMeta(((this.getMeta() & 0x03) | faces[face.getIndex()]));
        }
        this.getLevel().setBlock(block.getPosition(), this, true, true);

        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public Item toItem() {
        return Item.get(PURPUR_BLOCK, this.getMeta() & 0x03, 1);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.MAGENTA_BLOCK_COLOR;
    }
}
