package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.event.entity.EntityPotionEffectEvent;
import cn.nukkit.inventory.BeaconInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.potion.Effect;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.Map;

/**
 * @author Rover656
 */
public class BlockEntityBeacon extends BlockEntitySpawnable {

    public BlockEntityBeacon(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (!namedTag.contains("Lock")) {
            namedTag.putString("Lock", "");
        }

        if (!namedTag.contains("Levels")) {
            namedTag.putInt("Levels", 0);
        }

        if (!namedTag.contains("Primary")) {
            namedTag.putInt("Primary", 0);
        }

        if (!namedTag.contains("Secondary")) {
            namedTag.putInt("Secondary", 0);
        }

        this.scheduleUpdate();

        super.initBlockEntity();
    }

    @Override
    public boolean isBlockEntityValid() {
        return level.getBlockIdAt(chunk, (int) x, (int) y, (int) z) == Block.BEACON;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return new CompoundTag()
                .putString("id", BlockEntity.BEACON)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putString("Lock", this.namedTag.getString("Lock"))
                .putInt("Levels", this.namedTag.getInt("Levels"))
                .putInt("Primary", this.namedTag.getInt("Primary"))
                .putInt("Secondary", this.namedTag.getInt("Secondary"));
    }

    @Override
    public boolean onUpdate() {
        if (this.closed) {
            return false;
        }

        //Only apply effects every 4 secs
        if (level.getCurrentTick() % 80 != 0) {
            return true;
        }

        //Get the power level based on the pyramid
        int power = this.calculatePowerLevel();
        if (power == -1) { //Couldn't calculate due to unloaded chunks
            return true;
        }

        int oldPowerLevel = this.getPowerLevel();
        this.setPowerLevel(power);

        //Skip beacons that do not have a pyramid or sky access
        if (this.getPowerLevel() < 1 || !hasSkyAccess()) {
            if (oldPowerLevel > 0) {
                this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BEACON_DEACTIVATE);
            }
            return true;
        } else if (oldPowerLevel < 1) {
            this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BEACON_ACTIVATE);
        } else {
            this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BEACON_AMBIENT);
        }

        int powerLevel = getPowerLevel();
        //In seconds
        int duration = (9 + (powerLevel << 1)) * 20;

        for (Map.Entry<Long, Player> entry : this.level.getPlayers().entrySet()) {
            Player p = entry.getValue();

            //If the player is in range
            if (p.distance(this) < 10 + powerLevel * 10) {
                Effect e;

                if (getPrimaryPower() != 0) {
                    //Apply the primary power
                    e = Effect.getEffect(getPrimaryPower());

                    //Set duration
                    e.setDuration(duration);

                    //If secondary is selected as the primary too, apply 2 amplification
                    if (getSecondaryPower() == getPrimaryPower()) {
                        e.setAmplifier(1);
                    } else {
                        e.setAmplifier(0);
                    }

                    //Hide particles
                    e.setVisible(false);

                    //Add the effect
                    p.addEffect(e, EntityPotionEffectEvent.Cause.BEACON);
                }

                //If we have a secondary power as regen, apply it
                if (getSecondaryPower() == Effect.REGENERATION) {
                    //Get the regen effect
                    e = Effect.getEffect(Effect.REGENERATION);

                    //Set duration
                    e.setDuration(duration);

                    //Regen I
                    e.setAmplifier(0);

                    //Hide particles
                    e.setVisible(false);

                    //Add effect
                    p.addEffect(e, EntityPotionEffectEvent.Cause.BEACON);
                }

                if (powerLevel >= POWER_LEVEL_MAX) {
                    p.awardAchievement("fullBeacon");
                }
            }
        }

        return true;
    }

    private static final int POWER_LEVEL_MAX = 4;

    private boolean hasSkyAccess() {
        int tileX = (int) this.x;
        int tileY = (int) this.y;
        int tileZ = (int) this.z;

        //Check every block from our y coord to the top of the world
        for (int y = tileY + 1; y <= this.level.getMaxBlockY(); y++) {
            int testBlockId = level.getBlockIdAt(chunk, tileX, y, tileZ);
            if (!Block.isBlockTransparentById(testBlockId)) {
                //There is no sky access
                return false;
            }
        }

        return true;
    }

    private int calculatePowerLevel() {
        int tileX = (int) this.x;
        int tileY = (int) this.y;
        int tileZ = (int) this.z;

        //The power level that we're testing for
        for (int powerLevel = 1; powerLevel <= POWER_LEVEL_MAX; powerLevel++) {
            int queryY = tileY - powerLevel; //Layer below the beacon block

            for (int queryX = tileX - powerLevel; queryX <= tileX + powerLevel; queryX++) {
                for (int queryZ = tileZ - powerLevel; queryZ <= tileZ + powerLevel; queryZ++) {

                    int testBlockId = getBlockIdIfLoaded(queryX, queryY, queryZ);
                    if (testBlockId == -1) {
                        return -1;
                    }

                    if (
                            testBlockId != Block.IRON_BLOCK &&
                                    testBlockId != Block.GOLD_BLOCK &&
                                    testBlockId != Block.EMERALD_BLOCK &&
                                    testBlockId != Block.DIAMOND_BLOCK &&
                                    testBlockId != Block.NETHERITE_BLOCK
                    ) {
                        return powerLevel - 1;
                    }
                }
            }
        }

        return POWER_LEVEL_MAX;
    }

    @Override
    public void setDirty() {
        super.setDirty();
        this.spawnToAll();
    }

    public int getPowerLevel() {
        return namedTag.getInt("Level");
    }

    public void setPowerLevel(int level) {
        int currentLevel = getPowerLevel();
        if (level != currentLevel) {
            namedTag.putInt("Level", level);
            setDirty();
        }
    }

    public int getPrimaryPower() {
        return namedTag.getInt("Primary");
    }

    public void setPrimaryPower(int power) {
        int currentPower = getPrimaryPower();
        if (power != currentPower) {
            namedTag.putInt("Primary", power);
            setDirty();
        }
    }

    public int getSecondaryPower() {
        return namedTag.getInt("Secondary");
    }

    public void setSecondaryPower(int power) {
        int currentPower = getSecondaryPower();
        if (power != currentPower) {
            namedTag.putInt("Secondary", power);
            setDirty();
        }
    }

    private static final IntSet ALLOWED_EFFECTS = new IntOpenHashSet(new int[]{0, Effect.SPEED, Effect.HASTE, Effect.DAMAGE_RESISTANCE, Effect.JUMP, Effect.STRENGTH, Effect.REGENERATION});

    @Override
    public boolean updateCompoundTag(CompoundTag nbt, Player player) {
        if (!nbt.getString("id").equals(BlockEntity.BEACON)) {
            return false;
        }

        Inventory inv = player.getWindowById(Player.BEACON_WINDOW_ID);
        if (inv != null) {
            if (!BeaconInventory.ITEMS.contains(inv.getItemFast(0).getId())) {
                Server.getInstance().getLogger().debug(player.getName() + " tried to set effect but there's no payment in beacon inventory");
                return false;
            }
            inv.setItem(0, Item.get(Item.AIR));
        } else {
            Server.getInstance().getLogger().debug(player.getName() + " tried to set effect but beacon inventory is null");
            return false;
        }

        int primary = nbt.getInt("primary");
        if (ALLOWED_EFFECTS.contains(primary)) {
            this.setPrimaryPower(primary);
        } else {
            Server.getInstance().getLogger().debug(player.getName() + " tried to set an invalid primary effect to a beacon: " + primary);
        }

        int secondary = nbt.getInt("secondary");
        if (ALLOWED_EFFECTS.contains(secondary)) {
            this.setSecondaryPower(secondary);
        } else {
            Server.getInstance().getLogger().debug(player.getName() + " tried to set an invalid secondary effect to a beacon: " + secondary);
        }

        this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BEACON_POWER);
        return true;
    }
}
