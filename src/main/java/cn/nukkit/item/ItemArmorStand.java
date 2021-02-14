package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityArmorStand;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.CompassRoseDirection;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

import static cn.nukkit.math.CompassRoseDirection.Precision.PRIMARY_INTER_CARDINAL;

public class ItemArmorStand extends Item {

    public ItemArmorStand() {
        this(0);
    }

    public ItemArmorStand(Integer meta) {
        this(meta, 1);
    }

    public ItemArmorStand(Integer meta, int count) {
        super(ARMOR_STAND, meta, count, "Armor Stand");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (player.isAdventure()) {
            return false;
        }
        
        FullChunk chunk = block.getChunk();
        if (chunk == null) {
            return false;
        }
        
        if (!block.canBeReplaced() || !block.up().canBeReplaced()) {
            return false;
        }
        
        for (Entity collidingEntity : level.getCollidingEntities(new SimpleAxisAlignedBB(block.x, block.y, block.z, block.x + 1, block.y + 1, block.z + 1))) {
            if (collidingEntity instanceof EntityArmorStand) {
                return false;
            }
        }

        CompassRoseDirection direction = CompassRoseDirection.getClosestFromYaw(player.yaw, PRIMARY_INTER_CARDINAL).getOppositeFace();
        CompoundTag nbt = Entity.getDefaultNBT(block.add(0.5, 0, 0.5), new Vector3(), direction.getYaw(), 0f);
        if (this.hasCustomName()) {
            nbt.putString("CustomName", this.getCustomName());
        }
        
        if (!removeForPlacement(block) || !removeForPlacement(block.up())) {
            return false;
        }

        Entity entity = Entity.createEntity(EntityArmorStand.NETWORK_ID, chunk, nbt);
        if (entity == null) {
            return false;
        }
        
        if (!player.isCreative()) {
            player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
        }
        
        entity.spawnToAll();
        player.getLevel().addSound(entity, Sound.MOB_ARMOR_STAND_PLACE);
        return true;
    }

    /**
     * @param block The block which is in the same space as the armor stand.
     * @return {@code true} if the armor stand entity can be placed
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected boolean removeForPlacement(Block block) {
        switch (block.getId()) {
            case AIR:
                return true;
            case SNOW_LAYER:
                return block.canBeReplaced();
            default:
        }
        
        return block.getLevel().setBlock(block, Block.get(BlockID.AIR));
    }
}
