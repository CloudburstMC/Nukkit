package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityShulkerBox;
import cn.nukkit.inventory.ShulkerBoxInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import java.util.Map;

/**
 * Created by PetteriM1
 */
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
        return "Shulker Box";
    }

    @Override
    public double getHardness() {
        return 6;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }
    
    @Override
    public Item toItem() {
        ItemBlock item = new ItemBlock(this, this.getDamage(), 1);
                        
        BlockEntityShulkerBox t = (BlockEntityShulkerBox)this.getLevel().getBlockEntity(this);
        
        if (t != null)
        {
            ShulkerBoxInventory i = t.getRealInventory();
            
            CompoundTag nbt = item.getNamedTag();
            if (nbt == null)
                nbt = new CompoundTag("");
            
            ListTag<CompoundTag> items = new ListTag<>();
            
            for (int it = 0; it < i.getSize(); it++)
            {
                if (i.getItem(it).getId() != Item.AIR)
                {
                    CompoundTag d = NBTIO.putItemHelper(i.getItem(it), it);
                    items.add(d);
                }
            }
            
            nbt.put("Items", items);
                
            item.setCompoundTag(nbt);
            
            if (t.hasName())
                item.setCustomName(t.getName());
        }
        
        return item;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        BlockEntityShulkerBox box = null;

        for (int side = 2; side <= 5; ++side) {
            if ((this.getDamage() == 4 || this.getDamage() == 5) && (side == 4 || side == 5)) {
                continue;
            } else if ((this.getDamage() == 3 || this.getDamage() == 2) && (side == 2 || side == 3)) {
                continue;
            }
            Block c = this.getSide(BlockFace.fromIndex(side));
            if (c instanceof BlockShulkerBox && c.getDamage() == this.getDamage()) {
                BlockEntity blockEntity = this.getLevel().getBlockEntity(c);
                if (blockEntity instanceof BlockEntityShulkerBox && !((BlockEntityShulkerBox) blockEntity).isPaired()) {
                    box = (BlockEntityShulkerBox) blockEntity;
                    break;
                }
            }
        }

        this.getLevel().setBlock(block, this, true, true);
        CompoundTag nbt = new CompoundTag("")
                .putList(new ListTag<>("Items"))
                .putString("id", BlockEntity.SHULKER_BOX)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        CompoundTag t = item.getNamedTag();
            
        if (t != null)
        {
            if (t.contains("Items"))
            {
                nbt.putList(t.getList("Items"));
            }
        }

        BlockEntity blockEntity = new BlockEntityShulkerBox(this.getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);

        if (box != null) {
            box.pairWith(((BlockEntityShulkerBox) blockEntity));
            ((BlockEntityShulkerBox) blockEntity).pairWith(box);
        }

        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        BlockEntity t = this.getLevel().getBlockEntity(this);
        if (t instanceof BlockEntityShulkerBox) {
            ((BlockEntityShulkerBox) t).unpair();
        }
        this.getLevel().setBlock(this, new BlockAir(), true, true);

        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            Block top = up();
            if (!top.isTransparent()) {
                return true;
            }

            BlockEntity t = this.getLevel().getBlockEntity(this);
            BlockEntityShulkerBox box;
            if (t instanceof BlockEntityShulkerBox) {
                box = (BlockEntityShulkerBox) t;
            } else {
                CompoundTag nbt = new CompoundTag("")
                        .putList(new ListTag<>("Items"))
                        .putString("id", BlockEntity.SHULKER_BOX)
                        .putInt("x", (int) this.x)
                        .putInt("y", (int) this.y)
                        .putInt("z", (int) this.z);
                box = new BlockEntityShulkerBox(this.getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);
            }

            player.addWindow(box.getInventory());
        }

        return true;
    }
}