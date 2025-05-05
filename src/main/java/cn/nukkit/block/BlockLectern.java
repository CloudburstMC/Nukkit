package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityLectern;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

public class BlockLectern extends BlockTransparentMeta implements Faceable {

    public BlockLectern() {
        this(0);
    }

    public BlockLectern(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Lectern";
    }

    @Override
    public int getId() {
        return LECTERN;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    public double getHardness() {
        return 2.5;
    }

    public double getResistance() {
        return 2.5;
    }

    @Override
    public int getBurnChance() {
        return 30;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        int horizontalIndex = (player != null ? player.getDirection().getOpposite() : BlockFace.SOUTH).getHorizontalIndex();
        if (horizontalIndex >= 0) {
            this.setDamage(getDamage() & (15 ^ 0b11) | (horizontalIndex & 0b11));
        }
        CompoundTag nbt = new CompoundTag()
                .putString("id", BlockEntity.LECTERN)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);
        BlockEntityLectern lectern = (BlockEntityLectern) BlockEntity.createBlockEntity(BlockEntity.LECTERN, this.getChunk(), nbt);
        if (lectern == null) {
            return false;
        }
        this.getLevel().setBlock(this, this, true, true);
        return true;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getDamage() & 0b11);
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    public boolean isActivated() {
        return (this.getDamage() & 0x04) == 0x04;
    }

    public void setActivated(boolean activated) {
        if (activated) {
            setDamage(getDamage() | 0x04);
        } else {
            setDamage(getDamage() ^ 0x04);
        }
    }

    @Override
    public int getWeakPower(BlockFace face) {
        return isActivated() ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return 0;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            BlockEntity t = this.getLevel().getBlockEntity(this);
            if (!(t instanceof BlockEntityLectern)) {
                return false;
            }

            BlockEntityLectern lectern = (BlockEntityLectern) t;;
            Item currentBook = lectern.getBook();
            if (currentBook.getId() == BlockID.AIR) {
                if (item.getId() == ItemID.WRITTEN_BOOK || item.getId() == ItemID.BOOK_AND_QUILL) {
                    Item newBook = item.clone();
                    if (!player.isCreative()) {
                        item.count--;
                    }
                    newBook.setCount(1);
                    lectern.setBook(newBook);
                    lectern.spawnToAll();
                    this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ITEM_BOOK_PUT);
                }
            } else {
                ContainerOpenPacket pk = new ContainerOpenPacket();
                pk.windowId = -1;
                pk.type = InventoryType.LECTERN.getNetworkType();
                pk.x = (int) x;
                pk.y = (int) y;
                pk.z = (int) z;
                pk.entityId = player.getId();
                player.dataPacket(pk);
            }
        }

        return true;
    }

    @Override
    public boolean canBePushed() {
        return false; // prevent item loss issue with pistons until a working implementation
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        int power = 0;
        BlockEntity lectern = level.getBlockEntityIfLoaded(this);
        if (lectern instanceof BlockEntityLectern && ((BlockEntityLectern) lectern).hasBook()) {
            int currentPage = ((BlockEntityLectern) lectern).getLeftPage();
            int totalPages = ((BlockEntityLectern) lectern).getTotalPages();
            power = NukkitMath.floorDouble(1 + ((double) (currentPage - 1) / (totalPages - 1)) * 14);
        }
        return power;
    }

    public void onPageChange(boolean active) {
        if (isActivated() != active) {
            setActivated(active);
            level.setBlock((int) this.x, (int) this.y, (int) this.z, BlockLayer.NORMAL, this, false, false, false); // No need to send this to client
            level.updateAroundRedstone(this, null);
            if (active) {
                level.scheduleUpdate(this, 1);
            }
        }
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED || type == Level.BLOCK_UPDATE_NORMAL) {
            onPageChange(false);
        }

        return 0;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }
}
