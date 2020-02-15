package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.data.SoundEvent;
import com.nukkitx.protocol.bedrock.packet.LevelSoundEvent2Packet;

import static cn.nukkit.block.BlockIds.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockDoubleSlab extends BlockSolid {

    public BlockDoubleSlab(Identifier id) {
        super(id);
    }

    @Override
    public float getResistance() {
        return this.getId() == DOUBLE_WOODEN_SLAB ? 15 : 30;
    }

    @Override
    public int getToolType() {
        return this.getId() == DOUBLE_WOODEN_SLAB ? ItemTool.TYPE_AXE : ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return this.getId() == DOUBLE_WOODEN_SLAB;
    }

    @Override
    public int getBurnChance() {
        return this.getId() == DOUBLE_WOODEN_SLAB ? 5 : 0;
    }

    @Override
    public int getBurnAbility() {
        return this.getId() == DOUBLE_WOODEN_SLAB ? 20 : 0;
    }

    @Override
    public Item[] getDrops(Item item) {
        if ((item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN)
                || (this.getId() == STONE_SLAB && (this.getMeta() & 0x07) == 2)
                || this.getId() == WOODEN_SLAB) {
            return new Item[]{
                    Item.get(getSlab(), this.getMeta() & 0x07, 2)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockSlab.colorMap.get(getSlab())[this.getMeta() & 0x07];
    }

    public Identifier getSlab() {
        if (this.getId() == BlockIds.DOUBLE_STONE_SLAB) {
            return BlockIds.STONE_SLAB;
        } else if (this.getId() == BlockIds.DOUBLE_STONE_SLAB2) {
            return BlockIds.STONE_SLAB2;
        } else if (this.getId() == BlockIds.DOUBLE_STONE_SLAB3) {
            return BlockIds.STONE_SLAB3;
        } else if (this.getId() == BlockIds.DOUBLE_STONE_SLAB4) {
            return BlockIds.STONE_SLAB4;
        } else if (this.getId() == BlockIds.DOUBLE_WOODEN_SLAB) {
            return BlockIds.WOODEN_SLAB;
        }
        return BlockIds.AIR;

    }

    protected void playPlaceSound() {
        LevelSoundEvent2Packet pk = new LevelSoundEvent2Packet();
        pk.setSound(SoundEvent.ITEM_USE_ON);
        pk.setExtraData(725); // Who knows what this means?
        pk.setPosition(Vector3f.from(this.getX() + 0.5f, this.getY() + 0.5f, this.getZ() + 0.5f));
        pk.setIdentifier("");
        pk.setBabySound(false);
        pk.setRelativeVolumeDisabled(false);


        this.getLevel().addChunkPacket(this.getChunk().getX(), this.getChunk().getZ(), pk);
    }
}
