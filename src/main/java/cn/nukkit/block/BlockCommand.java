package cn.nukkit.block;

import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCommand;
import cn.nukkit.blockentity.BlockEntityCommandFormWindow;
import cn.nukkit.blockentity.BlockEntityComparator;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.event.server.ServerCommandEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.Faceable;
import co.aikar.timings.Timings;

/**
 * @author nmaster
 */
public abstract class BlockCommand extends BlockSolidMeta implements Faceable {

    public BlockCommand() {
        this(0);
    }

    public BlockCommand(int meta) {
        super(meta);
    }

    @Override
    public double getHardness() {
        return 10.0D;
    }

    @Override
    public double getResistance() {
        return 4.0D;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean isPowerSource() {
        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        if (getEntity() != null) {
            return getEntity().getOutputSignal();
        } else {
            return 0;
        }
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {

            BlockEntity t = getLevel().getBlockEntity(this);
            BlockEntityCommand command;
            if (t instanceof BlockEntityCommand) {
                command = (BlockEntityCommand) t;
            } else {
                CompoundTag nbt = new CompoundTag()
                		.putString("id", BlockEntity.COMMAND_BLOCK)
                		.putInt("x", (int) this.x)
                		.putInt("y", (int) this.y)
                		.putInt("z", (int) this.z)
                		.putString("Command", "")
                		.putInt("type", 0)
                		.putInt("redstone", 0)
                		.putInt("conditional", 0);

                command = new BlockEntityCommand(this.getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);
            }

            BlockEntityCommandFormWindow form = command.getFormWindow();

            form.setCommand(command.getCommand());
            form.setType(command.getType());
            form.setRedstone(command.getRedstone());
            form.setConditional(command.getConditional());

            player.showFormWindow(form);
        }

        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {

        int damage = 0;

        if (player != null) {
            damage = player.getDirection().getOpposite().getIndex();
            this.setDamage(damage);
        }

        this.level.setBlock(block, this, false, true);

        CompoundTag nbt = new CompoundTag("")
        		.putString("id", BlockEntity.COMMAND_BLOCK)
        		.putInt("x", (int) this.x)
        		.putInt("y", (int) this.y)
        		.putInt("z", (int) this.z);

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        new BlockEntityCommand(this.getLevel().getChunk((int) this.x >> 4, (int) this.z >> 4), nbt);

        return true;
    }

    @Override
    public BlockFace getBlockFace() {
        int index = (this.getDamage() & 0x7);
        return BlockFace.fromIndex(index);
    }

    public BlockEntityCommand getEntity() {
        BlockEntityCommand entity = (BlockEntityCommand) this.getLevel().getBlockEntity(this);
        return entity;
    }

    public void reactOnPeak() {
        
    }

    public void reactOnDip() {
        
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

            BlockEntityCommand commandBlockEntity = getEntity();

            if (commandBlockEntity != null) {
                if (!commandBlockEntity.isPowered() && this.level.isBlockPowered(this.getLocation())) {
                    commandBlockEntity.setPowered(true);
                    reactOnPeak();
                } else if (commandBlockEntity.isPowered() && !this.level.isBlockPowered(this.getLocation())) {
                    commandBlockEntity.setPowered(false);
                    reactOnDip();
                }
            }

        }
        return 0;
    }

}
