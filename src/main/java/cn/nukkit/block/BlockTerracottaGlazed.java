package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockTerracottaGlazed extends BlockSolid implements Faceable {

    public BlockTerracottaGlazed(Identifier id) {
        super(id);
    }

    @Override
    public float getResistance() {
        return 7;
    }

    @Override
    public float getHardness() {
        return 1.4f;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        return item.getTier() >= ItemTool.TIER_WOODEN ? new Item[]{this.toItem()} : new Item[0];
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        int[] faces = {2, 5, 3, 4};
        this.setDamage(faces[player != null ? player.getDirection().getHorizontalIndex() : 0]);
        return this.getLevel().setBlock(block.getPosition(), this, true, true);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getMeta() & 0x07);
    }
}
