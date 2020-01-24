package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;

public class BlockCampfire extends BlockSolid implements Faceable {

    private static final int CAMPFIRE_LIT_MASK = 0x08;
    private static final int CAMPFIRE_FACING_MASK = 0x07;

    public BlockCampfire(Identifier id) {
        super(id);
    }

    public BlockCampfire(Identifier id, int meta) {
        super(id);
        this.setDamage(meta);
    }

    @Override
    public double getHardness() {
        return 2.0;
    }

    @Override
    public double getResistance() {
        return 10.0;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & CAMPFIRE_FACING_MASK);
    }

    public boolean isLit() {
        return (this.getDamage() & CAMPFIRE_LIT_MASK) > 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (target.isTransparent() || face != BlockFace.UP) return false;
        if (super.place(item, block, target, face, clickPos, player)) {
            CompoundTag tag = new CompoundTag()
                    .putString("id", BlockEntity.CAMPFIRE)
                    .putInt("x", this.x)
                    .putInt("y", this.y)
                    .putInt("z", this.z);
            BlockEntity campfire = BlockEntity.createBlockEntity(BlockEntity.CAMPFIRE, this.getChunk(), tag);
            return campfire != null;
        }
        return false;
    }
}
