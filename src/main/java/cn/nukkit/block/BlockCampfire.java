package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlockCampfire extends BlockSolid implements Faceable {

    private static final int CAMPFIRE_LIT_MASK = 0x04; // Bit is 1 when fire is extinguished
    private static final int CAMPFIRE_FACING_MASK = 0x03;

    public BlockCampfire(Identifier id) {
        super(id);
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
        return (this.getDamage() & CAMPFIRE_LIT_MASK) == 0;
    }

    public void toggleFire() {
        this.setDamage(this.getDamage() ^ CAMPFIRE_LIT_MASK);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (!block.canBeReplaced()) return false;
        this.setDamage(player.getHorizontalFacing().getHorizontalIndex() & CAMPFIRE_FACING_MASK);
        if (getLevel().setBlock(block, this, true, true)) {
            CompoundTag tag = new CompoundTag()
                    .putString("id", BlockEntity.CAMPFIRE)
                    .putInt("x", block.x)
                    .putInt("y", block.y)
                    .putInt("z", block.z);
            BlockEntity campfire = BlockEntity.createBlockEntity(BlockEntity.CAMPFIRE, this.getChunk(), tag);
            return campfire != null;
        }
        return false;
    }

    @Override
    public int getLightLevel() {
        return isLit() ? 15 : 0;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item) {
        return this.onActivate(item, null);
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == ItemIds.FLINT_AND_STEEL) {
            if (!(this.isLit())) {
                this.toggleFire();
            }
            return true;
        } else if (item.isShovel()) {
            if (this.isLit()) {
                this.toggleFire();
            }
            return true;
        }
        return false;
    }
}
