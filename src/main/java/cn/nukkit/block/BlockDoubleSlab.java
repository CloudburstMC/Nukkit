package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.data.SoundEvent;
import com.nukkitx.protocol.bedrock.packet.LevelSoundEvent2Packet;

import java.util.Arrays;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockDoubleSlab extends BlockSolid {

    private final Identifier slabId;
    private final BlockColor[] colors;

    protected BlockDoubleSlab(Identifier id, Identifier slabId, BlockColor[] colors) {
        super(id);
        this.slabId = slabId;
        this.colors = colors;
    }

    public static BlockFactory factory(Identifier slabId, BlockColor... colors) {
        return id -> new BlockDoubleSlab(id, slabId, Arrays.copyOf(colors, 8));
    }

    public Identifier getSlabId() {
        return this.slabId;
    }

    @Override
    public float getResistance() {
        return 30;
    }

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    Item.get(this.slabId, this.getMeta() & 0x07, 2)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public BlockColor getColor() {
        return colors[this.getMeta() & 0x7];
    }
    protected void playPlaceSound() {
        LevelSoundEvent2Packet pk = new LevelSoundEvent2Packet();
        pk.setSound(SoundEvent.ITEM_USE_ON);
        pk.setExtraData(725); // Who knows what this means? It's what is sent per ProxyPass
        pk.setPosition(Vector3f.from(this.getX() + 0.5f, this.getY() + 0.5f, this.getZ() + 0.5f));
        pk.setIdentifier("");
        pk.setBabySound(false);
        pk.setRelativeVolumeDisabled(false);


        this.getLevel().addChunkPacket(this.getChunk().getX(), this.getChunk().getZ(), pk);
    }
}
