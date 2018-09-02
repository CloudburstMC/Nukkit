package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.inventory.BeaconInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;

import java.util.Map;

public class BlockEntityBeacon extends BlockEntitySpawnable implements InventoryHolder {

    protected final BeaconInventory inventory;

    public BlockEntityBeacon(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.inventory = new BeaconInventory(this);
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

        scheduleUpdate();

        super.initBlockEntity();
    }

    @Override
    public boolean isBlockEntityValid() {
        int blockID = getBlock().getId();
        return blockID == Block.BEACON;
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

    private long currentTick = 0;

    @Override
    public boolean onUpdate() {
        //Only check every 2 secs
        if (currentTick++ % 40 != 0) {
            return true;
        }

        //Check power level every 10 secs
        if (currentTick++ % 200 != 0) {
            setPowerLevel(calculatePowerLevel());
        }

        Map<Long, Player> players = this.level.getPlayers();

        Integer range = 0;
        Integer duration = 0;

        switch(getPowerLevel()) {
            case 1:
                range = 20;
                duration = 11;
                break;
            case 2:
                range = 30;
                duration = 13;
                break;
            case 3:
                range = 40;
                duration = 15;
                break;
            case 4:
                range = 50;
                duration = 17;
                break;
        }

        for(Map.Entry<Long, Player> entry : players.entrySet()) {
            Player p = entry.getValue();
            if (p.distance(this) < range) {
                //For now, default to haste
                Effect e = Effect.getEffect(Effect.HASTE);
                e.setDuration(duration * 20);
                if (getPowerLevel() == 4) {
                    e.setAmplifier(2);
                } else {
                    e.setAmplifier(1);
                }
                e.setVisible(false);
                p.addEffect(e);

                if (getPowerLevel() == 4) {
                    e = Effect.getEffect(Effect.REGENERATION);
                    e.setDuration(duration * 20);
                    e.setAmplifier(1);
                    e.setVisible(false);
                    p.addEffect(e);
                }
            }
        }

        return true;
    }

    private static final int POWER_LEVEL_MAX = 4;

    private int calculatePowerLevel() {
        int tileX = getFloorX();
        int tileY = getFloorY();
        int tileZ = getFloorZ();

        //The power level that we're testing for
        for (int powerLevel = 1; powerLevel <= POWER_LEVEL_MAX; powerLevel++) {
            int queryY = tileY - powerLevel; //Layer below the beacon block

            for (int queryX = tileX - powerLevel; queryX <= tileX + powerLevel; queryX++) {
                for (int queryZ = tileZ - powerLevel; queryZ <= tileZ + powerLevel; queryZ++) {

                    int testBlockId = level.getBlockIdAt(queryX, queryY, queryZ);
                    if (
                            testBlockId != Block.IRON_BLOCK &&
                                    testBlockId != Block.GOLD_BLOCK &&
                                    testBlockId != Block.EMERALD_BLOCK &&
                                    testBlockId != Block.DIAMOND_BLOCK
                            ) {
                        return powerLevel - 1;
                    }

                }
            }
        }

        return POWER_LEVEL_MAX;
    }

    public int getPowerLevel() {
        return namedTag.getInt("Level");
    }

    public void setPowerLevel(int level) {
        int currentLevel = getPowerLevel();
        if (level != currentLevel) {
            namedTag.putInt("Level", level);
            chunk.setChanged();
            this.spawnToAll();
        }
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }
}
