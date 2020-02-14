package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import com.nukkitx.protocol.bedrock.data.SoundEvent;

import static cn.nukkit.item.ItemIds.ENDER_EYE;

/**
 * Created by Pub4Game on 26.12.2015.
 */
public class BlockEndPortalFrame extends BlockTransparent implements Faceable {

    public BlockEndPortalFrame(Identifier id) {
        super(id);
    }

    @Override
    public float getResistance() {
        return 18000000;
    }

    @Override
    public float getHardness() {
        return -1;
    }

    @Override
    public int getLightLevel() {
        return 1;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public float getMaxY() {
        return this.getY() + ((this.getMeta() & 0x04) > 0 ? 1 : 0.8125f);
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride() {
        return (getMeta() & 4) != 0 ? 15 : 0;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if ((this.getMeta() & 0x04) == 0 && player != null && item.getId() == ENDER_EYE) {
            this.setDamage(this.getMeta() + 4);
            this.getLevel().setBlock(this.getPosition(), this, true, true);
            this.getLevel().addLevelSoundEvent(this.getPosition(), SoundEvent.BLOCK_END_PORTAL_FRAME_FILL);
            //TODO: create portal
            return true;
        }
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item toItem() {
        return Item.get(id, 0);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getMeta() & 0x07);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GREEN_BLOCK_COLOR;
    }
}
