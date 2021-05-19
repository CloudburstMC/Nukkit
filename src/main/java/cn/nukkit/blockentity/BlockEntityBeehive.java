package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBeehive;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.EntityBee;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.*;

@PowerNukkitOnly
public class BlockEntityBeehive extends BlockEntity {

    private static final Random RANDOM = new Random();

    private List<Occupant> occupants;

    @PowerNukkitOnly
    public BlockEntityBeehive(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        this.occupants = new ArrayList<>(4);
        if (!this.namedTag.contains("ShouldSpawnBees")) {
            this.namedTag.putByte("ShouldSpawnBees", 0);
        }

        if(!this.namedTag.contains("Occupants")) {
            this.namedTag.putList(new ListTag<>("Occupants"));
        } else {
            ListTag<CompoundTag> occupantsTag = namedTag.getList("Occupants", CompoundTag.class);
            for (int i = 0; i < occupantsTag.size(); i++) {
                this.occupants.add(new Occupant(occupantsTag.get(i)));
            }
        }
        
        // Backward compatibility
        if (this.namedTag.contains("HoneyLevel")) {
            int faceHorizontalIndex = 0;
            Block block = getBlock();
            if (block instanceof BlockBeehive) {
                faceHorizontalIndex = block.getDamage() & 0b11;
                int honeyLevel = this.namedTag.getByte("HoneyLevel");
                BlockBeehive beehive = (BlockBeehive) block;
                beehive.setBlockFace(BlockFace.fromHorizontalIndex(faceHorizontalIndex));
                beehive.setHoneyLevel(honeyLevel);
                beehive.getLevel().setBlock(beehive, beehive, true, true);
            }
            this.namedTag.remove("HoneyLevel");
        }

        if (!isEmpty()) {
            scheduleUpdate();
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        ListTag<CompoundTag> occupantsTag = new ListTag<>("Occupants");
        for (Occupant occupant : occupants) {
            occupantsTag.add(occupant.saveNBT());
        }
        this.namedTag.putList(occupantsTag);
    }

    @PowerNukkitOnly
    public int getHoneyLevel() {
        Block block = getBlock();
        if (block instanceof BlockBeehive) {
            return ((BlockBeehive) block).getHoneyLevel();
        } else {
            return 0;
        }
    }

    @PowerNukkitOnly
    public void setHoneyLevel(int honeyLevel) {
        Block block = getBlock();
        if (block instanceof BlockBeehive) {
            ((BlockBeehive) block).setHoneyLevel(honeyLevel);
            block.getLevel().setBlock(block, block, true, true);
        }
    }

    @PowerNukkitOnly
    public boolean addOccupant(Occupant occupant) {
        occupants.add(occupant);
        ListTag<CompoundTag> occupants = this.namedTag.getList("Occupants", CompoundTag.class);
        occupants.add(occupant.saveNBT());
        this.namedTag.putList(occupants);
        scheduleUpdate();
        return true;
    }

    @PowerNukkitOnly
    public Occupant addOccupant(Entity entity) {
        if (entity instanceof EntityBee) {
            EntityBee bee = (EntityBee) entity;
            boolean hasNectar = bee.getHasNectar();
            return addOccupant(bee, hasNectar ? 2400 : 600, hasNectar, true);
        } else {
            return addOccupant(entity, 600, false, true);
        }
    }
    
    @PowerNukkitOnly
    public Occupant addOccupant(Entity entity, int ticksLeftToStay) {
        return addOccupant(entity, ticksLeftToStay, false, true);
    }
    
    @PowerNukkitOnly
    public Occupant addOccupant(Entity entity, int ticksLeftToStay, boolean hasNectar) {
        return addOccupant(entity, ticksLeftToStay, hasNectar, true);
    }

    @PowerNukkitOnly
    public Occupant addOccupant(Entity entity, int ticksLeftToStay, boolean hasNectar, boolean playSound) {
        entity.saveNBT();
        Occupant occupant = new Occupant(ticksLeftToStay, entity.getSaveId(), entity.namedTag.clone());
        if(!addOccupant(occupant)) {
            return null;
        }

        entity.close();
        if (playSound) {
            entity.level.addSound(this, Sound.BLOCK_BEEHIVE_ENTER);
            if (entity.level != null && (entity.level != level || distanceSquared(this) >= 4)) {
                entity.level.addSound(entity, Sound.BLOCK_BEEHIVE_ENTER);
            }
        }
        return occupant;
    }

    @PowerNukkitOnly
    public Occupant[] getOccupants() {
        return occupants.toArray(Occupant.EMPTY_ARRAY);
    }

    @PowerNukkitOnly
    public boolean removeOccupant(Occupant occupant) {
        return occupants.remove(occupant);
    }

    @PowerNukkitOnly
    public boolean isHoneyEmpty() {
        return getHoneyLevel() == BlockBeehive.HONEY_LEVEL.getMinValue();
    }

    @PowerNukkitOnly
    public boolean isHoneyFull() {
        return getHoneyLevel() == BlockBeehive.HONEY_LEVEL.getMaxValue();
    }

    @PowerNukkitOnly
    public boolean isEmpty() {
        return occupants.isEmpty();
    }

    @PowerNukkitOnly
    public int getOccupantsCount() {
        return occupants.size();
    }
    
    @PowerNukkitOnly
    public boolean isSpawnFaceValid(BlockFace face) {
        Block side = getSide(face).getLevelBlock();
        return side.canPassThrough() && !(side instanceof BlockLiquid);
    }
    
    @PowerNukkitOnly
    public List<BlockFace> scanValidSpawnFaces() {
        return scanValidSpawnFaces(false);
    }

    @PowerNukkitOnly
    public List<BlockFace> scanValidSpawnFaces(boolean preferFront) {
        if (preferFront) {
            Block block = getBlock();
            if (block instanceof BlockBeehive) {
                BlockFace beehiveFace = ((BlockBeehive) block).getBlockFace();
                if (isSpawnFaceValid(beehiveFace)) {
                    return Collections.singletonList(beehiveFace);
                }
            }
        }
        
        List<BlockFace> validFaces = new ArrayList<>(4);
        for (int faceIndex = 0; faceIndex < 4; faceIndex++) {
            BlockFace face = BlockFace.fromHorizontalIndex(faceIndex);
            if (isSpawnFaceValid(face)) {
                validFaces.add(face);
            }
        }
        
        return validFaces;
    }

    @PowerNukkitOnly
    public Entity spawnOccupant(Occupant occupant, List<BlockFace> validFaces) {
        if (validFaces != null && validFaces.isEmpty()) {
            return null;
        }
        
        CompoundTag saveData = occupant.saveData.clone();
    
        Position lookAt;
        Position spawnPosition;
        if (validFaces != null) {
            BlockFace face = validFaces.get(RANDOM.nextInt(validFaces.size()));
            spawnPosition = add(
                    face.getXOffset() * 0.25 - face.getZOffset() * 0.5,
                    face.getYOffset() + (face.getYOffset() < 0 ? -0.4 : 0.2),
                    face.getZOffset() * 0.25 - face.getXOffset() * 0.5
            );
    
            saveData.putList(new ListTag<DoubleTag>("Pos")
                    .add(new DoubleTag("0", spawnPosition.x))
                    .add(new DoubleTag("1", spawnPosition.y))
                    .add(new DoubleTag("2", spawnPosition.z))
            );
    
            saveData.putList(new ListTag<DoubleTag>("Motion")
                    .add(new DoubleTag("0", 0))
                    .add(new DoubleTag("1", 0))
                    .add(new DoubleTag("2", 0))
            );
    
            lookAt = getSide(face, 2);
        } else {
            spawnPosition = add(RANDOM.nextDouble(), 0.2, RANDOM.nextDouble());
            lookAt = spawnPosition.add(RANDOM.nextDouble(), 0, RANDOM.nextDouble());
        }
    
        double dx = lookAt.getX() - spawnPosition.getX();
        double dz = lookAt.getZ() - spawnPosition.getZ();
        float yaw = 0;

        if (dx != 0) {
            if (dx < 0) {
                yaw = (float) (1.5 * Math.PI);
            } else {
                yaw = (float) (0.5 * Math.PI);
            }
            yaw = yaw - (float) Math.atan(dz / dx);
        } else if (dz < 0) {
            yaw = (float) Math.PI;
        }

        yaw = -yaw * 180f / (float) Math.PI;

        saveData.putList(new ListTag<FloatTag>("Rotation")
                .add(new FloatTag("0", yaw))
                .add(new FloatTag("1", 0))
        );
        
        Entity entity = Entity.createEntity(occupant.actorIdentifier, spawnPosition.getChunk(), saveData);
        if (entity != null) {
            removeOccupant(occupant);
            level.addSound(this, Sound.BLOCK_BEEHIVE_EXIT);
        }

        EntityBee bee = entity instanceof EntityBee? (EntityBee) entity : null;
        
        if (occupant.getHasNectar() && occupant.getTicksLeftToStay() <= 0) {
            if (!isHoneyFull()) {
                setHoneyLevel(getHoneyLevel() + 1);
            }
            if (bee != null) {
                bee.nectarDelivered(this);
            }
        } else {
            if (bee != null) {
                bee.leftBeehive(this);
            }
        }
        
        if (entity != null) {
            entity.spawnToAll();
        }

        return entity;
    }
    
    @Override
    public void onBreak() {
        if (!isEmpty()) {
            for (BlockEntityBeehive.Occupant occupant : getOccupants()) {
                Entity entity = spawnOccupant(occupant, null);
                if (level == null || level.getBlock(down()).getId() != BlockID.CAMPFIRE_BLOCK) {
                    if (entity instanceof EntityBee) {
                        ((EntityBee) entity).setAngry(true);
                    } else {
                        // TODO attack nearest player
                    }
                }
            }
        }
    }
    
    @Override
    public void onBreak(boolean isSilkTouch) {
        if (!isSilkTouch) {
            onBreak();
        }
    }
    
    @PowerNukkitOnly
    public void angerBees(Player player) {
        if (!isEmpty()) {
            List<BlockFace> validFaces = scanValidSpawnFaces();
            if (isSpawnFaceValid(BlockFace.UP)) {
                validFaces.add(BlockFace.UP);
            }
            if (isSpawnFaceValid(BlockFace.DOWN)) {
                validFaces.add(BlockFace.DOWN);
            }
            for (BlockEntityBeehive.Occupant occupant : getOccupants()) {
                Entity entity = spawnOccupant(occupant, validFaces);
                if (entity instanceof EntityBee) {
                    EntityBee bee = (EntityBee) entity;
                    if (player != null) {
                        bee.setAngry(player);
                    } else {
                        bee.setAngry(true);
                    }
                } else {
                    // TODO attack player
                }
            }
        }
    }
    
    @Override
    public boolean onUpdate() {
        if (this.closed || isEmpty()) {
            return false;
        }
        
        this.timing.startTiming();

        List<BlockFace> validSpawnFaces = null;

        // getOccupants will avoid ConcurrentModificationException if plugins changes the contents while iterating
        for (Occupant occupant: getOccupants()) {
            if (--occupant.ticksLeftToStay <= 0) {
                if (validSpawnFaces == null) {
                    validSpawnFaces = scanValidSpawnFaces(true);
                }

                if (spawnOccupant(occupant, validSpawnFaces) == null) {
                    occupant.ticksLeftToStay = 600;
                }
            } else if (!occupant.isMuted() && RANDOM.nextDouble() < 0.005) {
                level.addSound(add(0.5, 0, 0.5), occupant.workSound, 1f, occupant.workSoundPitch);
            }
        }

        this.timing.stopTiming();

        return true;
    }
    
    @Override
    public boolean isBlockEntityValid() {
        int id = this.getBlock().getId();
        return id == Block.BEEHIVE || id == Block.BEE_NEST;
    }
    
    @PowerNukkitOnly
    public static final class Occupant implements Cloneable {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        public static final Occupant[] EMPTY_ARRAY = new Occupant[0];
        
        private int ticksLeftToStay;
        private String actorIdentifier;
        private CompoundTag saveData;
        private Sound workSound = Sound.BLOCK_BEEHIVE_WORK;
        private float workSoundPitch = 1;
        private boolean hasNectar;
        private boolean muted;

        @PowerNukkitOnly
        public Occupant(int ticksLeftToStay, String actorIdentifier, CompoundTag saveData) {
            this.ticksLeftToStay = ticksLeftToStay;
            this.actorIdentifier = actorIdentifier;
            this.saveData = saveData;
        }

        @PowerNukkitOnly
        private Occupant(CompoundTag saved) {
            this.ticksLeftToStay = saved.getInt("TicksLeftToStay");
            this.actorIdentifier = saved.getString("ActorIdentifier");
            this.saveData = saved.getCompound("SaveData").clone();
            if (saved.contains("WorkSound")) {
                try {
                    this.workSound = Sound.valueOf(saved.getString("WorkSound"));
                } catch (IllegalArgumentException ignored) {

                }
            }
            if (saved.contains("WorkSoundPitch")) {
                this.workSoundPitch = saved.getFloat("WorkSoundPitch");
            }
            this.hasNectar = saved.getBoolean("HasNectar");
            this.muted = saved.getBoolean("Muted");
        }

        @PowerNukkitOnly
        public CompoundTag saveNBT() {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("ActorIdentifier", actorIdentifier)
                    .putInt("TicksLeftToStay", ticksLeftToStay)
                    .putCompound("SaveData", saveData)
                    .putString("WorkSound", workSound.name())
                    .putFloat("WorkSoundPitch", workSoundPitch)
                    .putBoolean("HasNectar", hasNectar)
                    .putBoolean("Muted", muted);
            return compoundTag;
        }

        @PowerNukkitOnly
        public boolean getHasNectar() {
            return hasNectar;
        }

        @PowerNukkitOnly
        public void setHasNectar(boolean hasNectar) {
            this.hasNectar = hasNectar;
        }

        @PowerNukkitOnly
        public int getTicksLeftToStay() {
            return ticksLeftToStay;
        }

        @PowerNukkitOnly
        public void setTicksLeftToStay(int ticksLeftToStay) {
            this.ticksLeftToStay = ticksLeftToStay;
        }

        @PowerNukkitOnly
        public String getActorIdentifier() {
            return actorIdentifier;
        }

        @PowerNukkitOnly
        public void setActorIdentifier(String actorIdentifier) {
            this.actorIdentifier = actorIdentifier;
        }

        @PowerNukkitOnly
        public CompoundTag getSaveData() {
            return saveData.clone();
        }

        @PowerNukkitOnly
        public void setSaveData(CompoundTag saveData) {
            this.saveData = saveData.clone();
        }

        @PowerNukkitOnly
        public Sound getWorkSound() {
            return workSound;
        }

        @PowerNukkitOnly
        public void setWorkSound(Sound workSound) {
            this.workSound = workSound;
        }

        @PowerNukkitOnly
        public float getWorkSoundPitch() {
            return workSoundPitch;
        }

        @PowerNukkitOnly
        public void setWorkSoundPitch(float workSoundPitch) {
            this.workSoundPitch = workSoundPitch;
        }

        @PowerNukkitOnly
        public boolean isMuted() {
            return muted;
        }

        @PowerNukkitOnly
        public void setMuted(boolean muted) {
            this.muted = muted;
        }

        @Override
        public String toString() {
            return "Occupant{" +
                    "ticksLeftToStay=" + ticksLeftToStay +
                    ", actorIdentifier='" + actorIdentifier + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Occupant occupant = (Occupant) o;
            return ticksLeftToStay == occupant.ticksLeftToStay &&
                    Objects.equals(actorIdentifier, occupant.actorIdentifier) &&
                    Objects.equals(saveData, occupant.saveData);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ticksLeftToStay, actorIdentifier, saveData);
        }

        @Override
        protected Occupant clone() {
            try {
                Occupant occupant = (Occupant) super.clone();
                occupant.saveData = this.saveData.clone();
                return occupant;
            } catch (CloneNotSupportedException e) {
                throw new InternalError("Unexpected exception", e);
            }
        }
    }
}
