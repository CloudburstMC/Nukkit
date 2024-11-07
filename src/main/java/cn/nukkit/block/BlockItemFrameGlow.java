package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.LevelEventPacket;

public class BlockItemFrameGlow extends BlockItemFrame {

    public BlockItemFrameGlow() {
        this(0);
    }

    public BlockItemFrameGlow(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Glow Item Frame";
    }

    @Override
    public int getId() {
        return GLOW_FRAME;
    }

    @Override
    public Item toItem() {
        return Item.get(Item.GLOW_ITEM_FRAME);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (target.isSolid() && (!block.isSolid() || block.canBeReplaced())) {
            this.setDamage(FACING[face.getIndex()]);

            this.getLevel().setBlock(this, this, true, true);

            CompoundTag nbt = new CompoundTag()
                    .putString("id", BlockEntity.GLOW_ITEM_FRAME)
                    .putInt("x", (int) block.x)
                    .putInt("y", (int) block.y)
                    .putInt("z", (int) block.z)
                    .putByte("ItemRotation", 0)
                    .putFloat("ItemDropChance", 1.0f);
            if (item.hasCustomBlockData()) {
                for (Tag aTag : item.getCustomBlockData().getAllTags()) {
                    nbt.put(aTag.getName(), aTag);
                }
            }
            BlockEntity.createBlockEntity(BlockEntity.GLOW_ITEM_FRAME, this.getChunk(), nbt);
            this.getLevel().addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ITEM_FRAME_PLACED);
            return true;
        }
        return false;
    }
}

