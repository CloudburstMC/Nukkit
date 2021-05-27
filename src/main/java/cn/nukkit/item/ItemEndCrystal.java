package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBedrock;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockObsidian;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.Random;

public class ItemEndCrystal extends Item {

    public ItemEndCrystal() {
        this(0, 1);
    }

    public ItemEndCrystal(Integer meta) {
        this(meta, 1);
    }

    public ItemEndCrystal(Integer meta, int count) {
        super(END_CRYSTAL, meta, count, "End Crystal");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (!(target instanceof BlockBedrock) && !(target instanceof BlockObsidian)) return false;
        FullChunk chunk = level.getChunk((int) block.getX() >> 4, (int) block.getZ() >> 4);
        Entity[] entities = level.getNearbyEntities(new SimpleAxisAlignedBB(target.x, target.y, target.z, target.x + 1, target.y + 2, target.z + 1));
        Block up = target.up();

        if (chunk == null || up.getId() != BlockID.AIR || up.up().getId() != BlockID.AIR || entities.length != 0) {
            return false;
        }

        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", target.x + 0.5))
                        .add(new DoubleTag("", up.y))
                        .add(new DoubleTag("", target.z + 0.5)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", new Random().nextFloat() * 360))
                        .add(new FloatTag("", 0)));

        if (this.hasCustomName()) {
            nbt.putString("CustomName", this.getCustomName());
        }

        Entity entity = Entity.createEntity("EndCrystal", chunk, nbt);

        if (entity != null) {
            if (player.isAdventure() || player.isSurvival()) {
                Item item = player.getInventory().getItemInHand();
                item.setCount(item.getCount() - 1);
                player.getInventory().setItemInHand(item);
            }
            entity.spawnToAll();
            return true;
        }
        return false;
    }
}
