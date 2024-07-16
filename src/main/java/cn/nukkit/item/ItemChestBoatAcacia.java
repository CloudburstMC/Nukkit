package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.item.EntityChestBoat;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

public class ItemChestBoatAcacia extends Item {

    public ItemChestBoatAcacia() {
        this(0, 1);
    }

    public ItemChestBoatAcacia(Integer meta) {
        this(meta, 1);
    }

    public ItemChestBoatAcacia(Integer meta, int count) {
        super(ACACIA_CHEST_BOAT, meta, count, "Oak Boat with Chest");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (face != BlockFace.UP) return false;
        EntityChestBoat boat = new EntityChestBoat(
                level.getChunk(block.getChunkX(), block.getChunkZ()), new CompoundTag("")
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", block.getX() + 0.5))
                        .add(new DoubleTag("", block.getY() - (target instanceof BlockWater ? 0.1 : 0)))
                        .add(new DoubleTag("", block.getZ() + 0.5)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", (float) ((player.yaw + 90f) % 360)))
                        .add(new FloatTag("", 0)))
                .putInt("Variant", 4)
        );

        if (!player.isCreative()) {
            this.count--;
        }

        boat.spawnToAll();
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
