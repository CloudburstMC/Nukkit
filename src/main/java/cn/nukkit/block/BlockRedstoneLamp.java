package cn.nukkit.block;

import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import static cn.nukkit.block.BlockIds.LIT_REDSTONE_LAMP;
import static cn.nukkit.block.BlockIds.REDSTONE_LAMP;

/**
 * @author Nukkit Project Team
 */
public class BlockRedstoneLamp extends BlockSolid {

    public BlockRedstoneLamp(Identifier id) {
        super(id);
    }

    @Override
    public float getHardness() {
        return 0.3f;
    }

    @Override
    public float getResistance() {
        return 1.5f;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (this.level.isBlockPowered(this.getPosition())) {
            this.level.setBlock(this.getPosition(), Block.get(LIT_REDSTONE_LAMP), false, true);
        } else {
            this.level.setBlock(this.getPosition(), this, false, true);
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
            // Redstone event
            RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
            getLevel().getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return 0;
            }
            if (this.level.isBlockPowered(this.getPosition())) {
                this.level.setBlock(this.getPosition(), Block.get(LIT_REDSTONE_LAMP), false, false);
                return 1;
            }
        }

        return 0;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                Item.get(REDSTONE_LAMP)
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }
}
