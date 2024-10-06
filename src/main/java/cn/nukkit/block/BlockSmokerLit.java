package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySmoker;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;

import java.util.Map;

public class BlockSmokerLit extends BlockFurnaceBurning {

    public BlockSmokerLit() {
        this(0);
    }

    public BlockSmokerLit(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LIT_SMOKER;
    }

    @Override
    public String getName() {
        return "Lit Smoker";
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(SMOKER));
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.setDamage(Block.FACES2534[player != null ? player.getDirection().getHorizontalIndex() : 0]);
        this.getLevel().setBlock(block, this, true, true);
        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<>("Items"))
                .putString("id", BlockEntity.SMOKER)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        BlockEntitySmoker smoker = (BlockEntitySmoker) BlockEntity.createBlockEntity(BlockEntity.SMOKER, this.getChunk(), nbt);
        return smoker != null;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            BlockEntity t = this.getLevel().getBlockEntity(this);
            if (!(t instanceof BlockEntitySmoker)) {
                return false;
            }

            BlockEntitySmoker smoker = (BlockEntitySmoker) t;
            if (smoker.namedTag.contains("Lock") && smoker.namedTag.get("Lock") instanceof StringTag) {
                if (!smoker.namedTag.getString("Lock").equals(item.getCustomName())) {
                    return true;
                }
            }

            player.addWindow(smoker.getInventory());
        }

        return true;
    }
}
