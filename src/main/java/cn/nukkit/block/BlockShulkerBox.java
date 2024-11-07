package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityShulkerBox;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockShulkerBox extends BlockTransparentMeta {

    public BlockShulkerBox() {
        this(0);
    }

    public BlockShulkerBox(int meta) {
        super(meta);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getId() {
        return SHULKER_BOX;
    }

    @Override
    public String getName() {
        return this.getDyeColor().getName() + " Shulker Box";
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item toItem() {
        ItemBlock item = new ItemBlock(this, this.getDamage(), 1);

        BlockEntity be = this.getLevel().getBlockEntity(this);

        if (be instanceof BlockEntityShulkerBox) {
            BlockEntityShulkerBox t = (BlockEntityShulkerBox) be;

            BaseInventory i = t.getInventory();

            if (!i.slots.isEmpty()) {

                CompoundTag nbt = item.getNamedTag();
                if (nbt == null)
                    nbt = new CompoundTag("");

                ListTag<CompoundTag> items = new ListTag<>();

                for (int it = 0; it < i.getSize(); it++) {
                    if (i.getItem(it).getId() != Item.AIR) {
                        CompoundTag d = NBTIO.putItemHelper(i.getItem(it), it);
                        items.add(d);
                    }
                }

                nbt.put("Items", items);

                item.setCompoundTag(nbt);
            }

            if (t.hasName()) {
                item.setCustomName(t.getName());
            }
        }

        return item;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.getLevel().setBlock(this, this, true, true);

        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.SHULKER_BOX)
                .putByte("facing", face.getIndex());

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        CompoundTag t = item.getNamedTag();

        if (t != null) {
            if (t.contains("Items")) {
                nbt.putList(t.getList("Items"));
            }
        }

        BlockEntity.createBlockEntity(BlockEntity.SHULKER_BOX, this.getChunk(), nbt);
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            BlockEntity t = this.getLevel().getBlockEntity(this);
            if (!(t instanceof BlockEntityShulkerBox)) {
                return false;
            }

            BlockEntityShulkerBox box = (BlockEntityShulkerBox) t;
            Block block = this.getSide(BlockFace.fromIndex(box.namedTag.getByte("facing")));
            if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockFlowable)) {
                return true;
            }

            player.addWindow(box.getInventory());
        }

        return true;
    }

    @Override
    public BlockColor getColor() {
        return this.getDyeColor().getColor();
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByWoolData(this.getDamage());
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }

    @Override
    public boolean breakWhenPushed() {
        return true;
    }

    @Override
    public boolean alwaysDropsOnExplosion() {
        return true;
    }
}