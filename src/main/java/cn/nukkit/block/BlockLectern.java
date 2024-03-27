package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityLectern;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

public class BlockLectern extends BlockSolidMeta implements Faceable {

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

    public boolean dropBook()  {
        BlockEntity blockEntity = this.getLevel().getBlockEntity(this);
        if (blockEntity instanceof BlockEntityLectern) {
            BlockEntityLectern lectern = (BlockEntityLectern) blockEntity;
            Item book = lectern.getBook();
            if (book.getId() != BlockID.AIR) {
                lectern.setBook(Item.get(BlockID.AIR));
                lectern.spawnToAll();
                this.level.dropItem(lectern.add(0.5f, 1, 0.5f), book);
                return true;
            }
        }
        return false;
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
                    if (player.isSurvival()) {
                        newBook.setCount(newBook.getCount() - 1);
                        player.getInventory().setItemInHand(newBook);
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
}
