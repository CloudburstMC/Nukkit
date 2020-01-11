package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;

import java.util.ArrayList;
import java.util.List;

public class BlockEntityBell extends BlockEntitySpawnable {

    private boolean ringing;
    private int direction;
    private int ticks;
    public final List<Player> spawnExceptions = new ArrayList<>(2);

    public BlockEntityBell(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (!namedTag.contains("Ringing") || !(namedTag.get("Ringing") instanceof ByteTag)) {
            ringing = false;
        } else {
            ringing = namedTag.getBoolean("Ringing");
        }

        if (!namedTag.contains("Direction") || !(namedTag.get("Direction") instanceof IntTag)) {
            direction = 255;
        } else {
            direction = namedTag.getInt("Direction");
        }

        if (!namedTag.contains("Ticks") || !(namedTag.get("Ticks") instanceof IntTag)) {
            ticks = 0;
        } else {
            ticks = namedTag.getInt("Ticks");
        }

        super.initBlockEntity();
        scheduleUpdate();
    }

    @Override
    public void saveNBT() {
        namedTag.putBoolean("Ringing", ringing);
        namedTag.putInt("Direction", direction);
        namedTag.putInt("Ticks", ticks);
        super.saveNBT();
    }

    @Override
    public boolean onUpdate() {
        if (ringing) {
            if (ticks == 0) {
                level.addSound(this, Sound.BLOCK_BELL_HIT);
                spawnToAllWithExceptions();
                spawnExceptions.clear();
            } else if (ticks >= 50) {
                ringing = false;
                ticks = 0;
                spawnToAllWithExceptions();
                spawnExceptions.clear();
                return false;
            }
            //spawnToAll();
            ticks++;
            return true;
        } else if (ticks > 0) {
            ticks = 0;
            spawnToAllWithExceptions();
            spawnExceptions.clear();
        }

        return false;
    }

    private void spawnToAllWithExceptions() {
        if (this.closed) {
            return;
        }

        for (Player player : this.getLevel().getChunkPlayers(this.chunk.getX(), this.chunk.getZ()).values()) {
            if (player.spawned && !spawnExceptions.contains(player)) {
                this.spawnTo(player);
            }
        }
    }

    public boolean isRinging() {
        return ringing;
    }

    public void setRinging(boolean ringing) {
        if (this.level != null && this.ringing != ringing) {
            this.ringing = ringing;
            scheduleUpdate();
        }
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getTicks() {
        return ticks;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag tag = new CompoundTag()
                .putString("id", BlockEntity.BELL)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putBoolean("Ringing", this.ringing)
                .putInt("Direction", this.direction)
                .putInt("Ticks", this.ticks);
        return tag;
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock().getId() == Block.BELL;
    }
}
