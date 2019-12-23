package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityLectern;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockLectern extends BlockTransparentMeta {
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
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 12.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public double getMaxY() {
        return y + 0.89999;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        int[] faces = {0, 1, 2, 3};
        this.setDamage(faces[player != null ? player.getDirection().getOpposite().getHorizontalIndex() : 0]);
        this.getLevel().setBlock(block, this, true, true);
        CompoundTag nbt = new CompoundTag()
                .putString("id", BlockEntity.LECTERN)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

        new BlockEntityLectern(this.getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);

        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            BlockEntity t = this.getLevel().getBlockEntity(this);
            BlockEntityLectern lectern;
            if (t instanceof BlockEntityLectern) {
                lectern = (BlockEntityLectern) t;
            } else {
                CompoundTag nbt = new CompoundTag()
                        .putString("id", BlockEntity.LECTERN)
                        .putInt("x", (int) this.x)
                        .putInt("y", (int) this.y)
                        .putInt("z", (int) this.z);

                lectern = new BlockEntityLectern(this.getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);
            }

            Item currentBook = lectern.getBook();
            if (currentBook.getId() == Item.AIR) {
                if (item.getId() == Item.WRITTEN_BOOK || item.getId() == Item.BOOK_AND_QUILL) {
                    Item newBook = item.clone();
                    if (player.isSurvival()) {
                        newBook.setCount(newBook.getCount() - 1);
                        player.getInventory().setItemInHand(newBook);
                    }
                    newBook.setCount(1);
                    lectern.setBook(newBook);
                    lectern.spawnToAll();
                    //TODO Couldn't find out the exact sound name that vanilla does
                    this.getLevel().addSound(this, Sound.BLOCK_ITEMFRAME_ADD_ITEM);
                }
            }
        }

        return true;
    }

    @Override
    public boolean isBreakable(Item item) {
        return super.isBreakable(item);
    }

    public void dropBook() {
        BlockEntity t = this.getLevel().getBlockEntity(this);
        if (t instanceof BlockEntityLectern) {
            BlockEntityLectern lectern = (BlockEntityLectern) t;
            Item book = lectern.getBook();
            if (book.getId() != Item.AIR) {
                lectern.setBook(Item.get(Item.AIR));
                lectern.spawnToAll();
                this.level.dropItem(lectern.add(0.5f, 0.6f, 0.5f), book);
            }
        }
    }
}
