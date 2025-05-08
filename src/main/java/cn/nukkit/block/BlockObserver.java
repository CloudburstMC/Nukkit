package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Faceable;

public class BlockObserver extends BlockSolidMeta implements Faceable {

    /**
     * Where the block update happens. Used to check whether this observer should detect it.
     */
    private Vector3 updatePos;

    public BlockObserver() {
        this(0);
    }

    public BlockObserver(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return OBSERVER;
    }

    @Override
    public String getName() {
        return "Observer";
    }

    @Override
    public double getHardness() {
        return 3.5;
    }

    @Override
    public double getResistance() {
        return 17.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe()) {
            return new Item[]{
                    Item.get(Item.OBSERVER, 0, 1)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (player != null) {
            if (Math.abs(player.x - this.x) < 2 && Math.abs(player.z - this.z) < 2) {
                double y = player.y + player.getEyeHeight();

                if (y - this.y > 2) {
                    this.setDamage(BlockFace.DOWN.getIndex());
                } else if (this.y - y > 0) {
                    this.setDamage(BlockFace.UP.getIndex());
                } else {
                    this.setDamage(player.getHorizontalFacing().getIndex());
                }
            } else {
                this.setDamage(player.getHorizontalFacing().getIndex());
            }
        }

        return this.getLevel().setBlock(this, this, true, true);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(this.getDamage() & 0x07);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL && this.getSideVec(this.getBlockFace()).equals(this.updatePos) && !this.isPowered()) {
            // Make sure the block still exists to prevent item duplication
            if (this.level.getBlockIdAt((int) this.x, (int) this.y, (int) this.z) != this.getId()) {
                return 0;
            }
            RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
            this.level.getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return 0;
            }
            this.setPowered(true);
            this.level.setBlock(this, this, false, false);
            this.level.updateAroundRedstone(this, this.getBlockFace());
            level.scheduleUpdate(this, 4);
            return Level.BLOCK_UPDATE_NORMAL;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED && this.isPowered()) {
            // Make sure the block still exists to prevent item duplication
            if (this.level.getBlockIdAt((int) this.x, (int) this.y, (int) this.z) != this.getId()) {
                return 0;
            }
            RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
            this.level.getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return 0;
            }
            this.setPowered(false);
            this.level.setBlock(this, this, false, false);
            this.level.updateAroundRedstone(this, this.getBlockFace());
        }
        return type;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(Block.OBSERVER));
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return this.isPowered() && side == this.getBlockFace() ? 15 : 0;
    }

    @Override
    public int getWeakPower(BlockFace face) {
        return this.getStrongPower(face);
    }

    public boolean isPowered() {
        return (this.getDamage() & 0x8) == 0x8;
    }

    public void setPowered(boolean powered) {
        this.setDamage((this.getDamage() & 0x7) | (powered ? 0x8 : 0x0));
    }

    @Override
    public Block setUpdatePos(Vector3 pos) {
        this.updatePos = pos;
        return this;
    }
}
