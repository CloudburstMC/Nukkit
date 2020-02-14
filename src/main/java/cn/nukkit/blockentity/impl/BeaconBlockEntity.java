package cn.nukkit.blockentity.impl;

import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.Beacon;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.inventory.BeaconInventory;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.player.Player;
import cn.nukkit.potion.Effect;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.protocol.bedrock.data.SoundEvent;

import java.util.Map;

import static cn.nukkit.block.BlockIds.*;

/**
 * author: Rover656
 */
public class BeaconBlockEntity extends BaseBlockEntity implements Beacon {

    private int primaryEffect;
    private int secondaryEffect;
    private int powerLevel;

    public BeaconBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    protected void initBlockEntity() {
        scheduleUpdate();

        super.initBlockEntity();
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForInt("primary", this::setPrimaryEffect);
        tag.listenForInt("secondary", this::setSecondaryEffect);
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        tag.intTag("primary", this.getPrimaryEffect());
        tag.intTag("secondary", this.getSecondaryEffect());
    }

    @Override
    public boolean isValid() {
        return getBlock().getId() == BlockIds.BEACON;
    }

    private long currentTick = 0;

    @Override
    public boolean onUpdate() {
        //Only apply effects every 4 secs
        if (currentTick++ % 80 != 0) {
            return true;
        }

        int oldPowerLevel = this.powerLevel;
        //Get the power level based on the pyramid
        this.powerLevel = calculatePowerLevel();
        int newPowerLevel = this.powerLevel;

        //Skip beacons that do not have a pyramid or sky access
        if (newPowerLevel < 1 || !hasSkyAccess()) {
            if (oldPowerLevel > 0) {
                this.getLevel().addLevelSoundEvent(this.getPosition(), SoundEvent.BEACON_DEACTIVATE);
            }
            return true;
        } else if (oldPowerLevel < 1) {
            this.getLevel().addLevelSoundEvent(this.getPosition(), SoundEvent.BEACON_ACTIVATE);
        } else {
            this.getLevel().addLevelSoundEvent(this.getPosition(), SoundEvent.BEACON_AMBIENT);
        }

        //Get all players in game
        Map<Long, Player> players = this.getLevel().getPlayers();

        //Calculate vars for beacon power
        int range = 10 + this.powerLevel * 10;
        int duration = 9 + this.powerLevel * 2;

        for (Map.Entry<Long, Player> entry : players.entrySet()) {
            Player p = entry.getValue();

            //If the player is in range
            if (p.getPosition().distance(this.getPosition().toFloat()) < range) {
                Effect e;

                if (getPrimaryEffect() != 0) {
                    //Apply the primary power
                    e = Effect.getEffect(getPrimaryEffect());

                    //Set duration
                    e.setDuration(duration * 20);

                    //If secondary is selected as the primary too, apply 2 amplification
                    if (getSecondaryEffect() == getPrimaryEffect()) {
                        e.setAmplifier(2);
                    } else {
                        e.setAmplifier(1);
                    }

                    //Hide particles
                    e.setVisible(false);

                    //Add the effect
                    p.addEffect(e);
                }

                //If we have a secondary power as regen, apply it
                if (getSecondaryEffect() == Effect.REGENERATION) {
                    //Get the regen effect
                    e = Effect.getEffect(Effect.REGENERATION);

                    //Set duration
                    e.setDuration(duration * 20);

                    //Regen I
                    e.setAmplifier(1);

                    //Hide particles
                    e.setVisible(false);

                    //Add effect
                    p.addEffect(e);
                }
            }
        }

        return true;
    }

    private static final int POWER_LEVEL_MAX = 4;

    private boolean hasSkyAccess() {
        //Check every block from our y coord to the top of the world
        for (int y = getPosition().getY() + 1; y <= 255; y++) {
            Identifier testBlockId = getLevel().getBlockId(getPosition().getX(), y, getPosition().getZ());
            if (!BlockRegistry.get().getBlock(testBlockId, 0).isTransparent()) {
                //There is no sky access
                return false;
            }
        }

        return true;
    }

    private int calculatePowerLevel() {
        int tileX = this.getPosition().getZ();
        int tileY = this.getPosition().getY();
        int tileZ = this.getPosition().getZ();

        //The power level that we're testing for
        for (int powerLevel = 1; powerLevel <= POWER_LEVEL_MAX; powerLevel++) {
            int queryY = tileY - powerLevel; //Layer below the beacon block

            for (int queryX = tileX - powerLevel; queryX <= tileX + powerLevel; queryX++) {
                for (int queryZ = tileZ - powerLevel; queryZ <= tileZ + powerLevel; queryZ++) {

                    Identifier testBlockId = getLevel().getBlockId(queryX, queryY, queryZ);
                    if (testBlockId != IRON_BLOCK && testBlockId != GOLD_BLOCK && testBlockId != EMERALD_BLOCK &&
                            testBlockId != DIAMOND_BLOCK) {
                        return powerLevel - 1;
                    }
                }
            }
        }

        return POWER_LEVEL_MAX;
    }

    public int getPrimaryEffect() {
        return primaryEffect;
    }

    public void setPrimaryEffect(int primaryEfect) {
        if (primaryEfect != this.primaryEffect) {
            this.primaryEffect = primaryEfect;
            setDirty();
            this.spawnToAll();
        }
    }

    public int getSecondaryEffect() {
        return secondaryEffect;
    }

    public void setSecondaryEffect(int secondaryEffect) {
        if (secondaryEffect != this.secondaryEffect) {
            this.secondaryEffect = secondaryEffect;
            setDirty();
            this.spawnToAll();
        }
    }

    @Override
    public boolean updateCompoundTag(CompoundTag nbt, Player player) {
        this.setPrimaryEffect(nbt.getInt("primary"));
        this.setSecondaryEffect(nbt.getInt("secondary"));

        this.getLevel().addLevelSoundEvent(this.getPosition(), SoundEvent.BEACON_POWER);

        BeaconInventory inv = (BeaconInventory) player.getWindowById(Player.BEACON_WINDOW_ID);

        inv.clear(0);
        return true;
    }
}
