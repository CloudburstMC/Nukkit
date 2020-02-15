package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacketV2;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

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
    public double getResistance() {
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
                || (this.getId() == STONE_SLAB && (this.getDamage() & 0x07) == 2)
                || this.getId() == WOODEN_SLAB) {
            return new Item[]{
                    Item.get(getSlab(), this.getDamage() & 0x07, 2)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockSlab.colorMap.get(getSlab())[this.getDamage() & 0x07];
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
        LevelSoundEventPacketV2 pk = new LevelSoundEventPacketV2();
        pk.sound = LevelSoundEventPacket.SOUND_ITEM_USE_ON;
        pk.extraData = 725;
        pk.x = this.x + 0.5f;
        pk.y = this.y + 0.5f;
        pk.z = this.z + 0.5f;
        pk.entityIdentifier = "";
        pk.isBabyMob = false;
        pk.isGlobal = false;

        this.getLevel().addChunkPacket(this.getChunkX(), this.getChunkZ(), pk);
    }
}
