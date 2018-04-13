package cn.nukkit.server.entity;

import cn.nukkit.api.block.BlockTypes;
import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.entity.component.Ageable;
import cn.nukkit.api.entity.component.EntityComponent;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.level.Level;
import cn.nukkit.api.level.chunk.Chunk;
import cn.nukkit.api.util.BoundingBox;
import cn.nukkit.api.util.Rotation;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.data.MetadataConstants;
import cn.nukkit.server.network.minecraft.packet.AddEntityPacket;
import cn.nukkit.server.network.minecraft.packet.SetEntityDataPacket;
import cn.nukkit.server.network.minecraft.util.MetadataDictionary;
import cn.nukkit.server.util.bitset.BitSet;
import cn.nukkit.server.util.bitset.SyncLongBitSet;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import java.util.*;

import static cn.nukkit.server.network.minecraft.data.MetadataConstants.*;
import static cn.nukkit.server.network.minecraft.data.MetadataConstants.Flag.*;

@Log4j2
public class BaseEntity implements Entity {
    private final NukkitServer server;
    private final EntityType entityType;
    private final Map<Class<? extends EntityComponent>, EntityComponent> componentMap = new HashMap<>();
    // Flags
    private long tickCreated;
    private long entityId;
    private NukkitLevel level;
    private Vector3f position;
    private Vector3f motion;
    private Rotation rotation;
    private boolean teleported = false;
    private boolean removed = false;
    private BoundingBox boundingBox;
    private boolean movementStale;
    private boolean metadataStale;
    protected final BitSet metadataFlags = new SyncLongBitSet();

    public BaseEntity(EntityType entityType, Vector3f position, NukkitLevel level, NukkitServer server) {
        this.level = level;
        this.server = server;
        this.entityType = entityType;
        this.position = position;
        this.rotation = Rotation.ZERO;
        this.motion = Vector3f.ZERO;
        this.entityId = level.getEntityManager().allocateEntityId();
        this.level.getEntityManager().registerEntity(this);
        this.tickCreated = level.getCurrentTick();

        setFlag(HAS_COLLISION, true, false);
        setFlag(AFFECTED_BY_GRAVITY, true, false);
        setFlag(CAN_SHOW_NAMETAG, true, false);

        refreshBoundingBox();
    }

    public MinecraftPacket createAddEntityPacket() {
        AddEntityPacket packet = new AddEntityPacket();
        packet.setUniqueEntityId(entityId);
        packet.setRuntimeEntityId(entityId);
        packet.setEntityType(entityType.getType());
        packet.setPosition(position);
        packet.setMotion(motion);
        packet.setRotation(rotation);
        packet.getMetadata().putAll(getMetadata());
        return packet;
    }

    @Nonnull
    @Override
    public Rotation getRotation() {
        return rotation;
    }

    public void setRotation(@Nonnull Rotation rotation) {
        Preconditions.checkNotNull(rotation, "rotation");
        checkIfAlive();

        if (this.rotation.equals(rotation)) {
            this.rotation = rotation;
            movementStale = true;
        }
    }

    @Nonnull
    @Override
    public Vector3f getMotion() {
        return motion;
    }

    @Override
    public UUID getUniqueId() {
        return null;
    }

    @Nonnull
    @Override
    public Vector3f getPosition() {
        return position;
    }

    @Override
    public void setPosition(@Nonnull Vector3f position) {
        Preconditions.checkNotNull(position, "position");
        checkIfAlive();

        if (!this.position.equals(position)) {
            this.position = position;
            movementStale = true;

            refreshBoundingBox();
        }
    }

    public void setPositionFromSystem(@Nonnull Vector3f position) {
        Preconditions.checkState(level.getEntityManager().isTicking(), "entities in level are not being ticked");
        setPosition(position);
    }

    @Nonnull
    @Override
    public Vector3f getGamePosition() {
        return position.add(0, entityType.getOffset(), 0);
    }

    @Nonnull
    @Override
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    @Override
    public void setMotion(Vector3f motion) {
        Preconditions.checkNotNull(motion, "vector");
        checkIfAlive();

        if (!this.motion.equals(motion)) {
            this.motion = motion;
            movementStale = true;
        }
    }

    @Override
    public float getHeight() {
        float height = entityType.getHeight();
        if (get(Ageable.class).map(Ageable::isBaby).orElse(false)) {
            height /= 2;
        }
        return height;
    }

    @Override
    public float getWidth() {
        float width = entityType.getWidth();
        if (get(Ageable.class).map(Ageable::isBaby).orElse(false)) {
            width /= 2;
        }
        return width;
    }

    public float getLength() {
        float length = entityType.getLength();
        if (get(Ageable.class).map(Ageable::isBaby).orElse(false)) {
            length /= 2;
        }
        return length;
    }

    @Override
    public float getOffset() {
        float offset = entityType.getOffset();
        if (get(Ageable.class).map(Ageable::isBaby).orElse(false)) {
            offset /= 2;
        }
        return offset;
    }

    @Override
    public boolean isOnGround() {
        Vector3i blockPosition = getPosition().sub(0f, 0.1f, 0f).toInt();

        if (blockPosition.getY() < 0 || blockPosition.getY() > 255) {
            return false;
        }

        int chunkX = blockPosition.getX() >> 4;
        int chunkZ = blockPosition.getZ() >> 4;
        int chunkInX = blockPosition.getX() & 0x0f;
        int chunkInZ = blockPosition.getZ() & 0x0f;

        Optional<Chunk> chunkOptional = level.getChunkIfLoaded(chunkX, chunkZ);
        return chunkOptional.isPresent() && chunkOptional.get().getBlock(chunkInX, blockPosition.getY(), chunkInZ).getBlockState().getBlockType() != BlockTypes.AIR;
    }

    @Override
    public NukkitLevel getLevel() {
        return level;
    }

    @Override
    public boolean teleport(Vector3f position) {
        return false;
    }

    @Override
    public boolean teleport(Vector3f location, Level level) {
        return false;
    }

    @Override
    public boolean teleport(Entity destination) {
        return false;
    }

    @Override
    public long getEntityId() {
        return entityId;
    }

    @Override
    public void remove() {
        checkIfAlive();
        removed = true;
    }

    @Override
    public boolean isAlive() {
        return false;
    }

    public final void checkIfAlive() {
        Preconditions.checkState(!removed, "Entity has been removed.");
    }

    @Nonnull
    @Override
    public NukkitServer getServer() {
        return server;
    }

    @Override
    public long getTickCreated() {
        return tickCreated;
    }

    @Override
    public void setTickCreated(long tickCreated) {
        this.tickCreated = tickCreated;
    }

    @Override
    public long getTicksLived() {
        return level.getCurrentTick() - tickCreated;
    }

    @Override
    public void setTicksLived(long ticks) {
        this.tickCreated = level.getCurrentTick() - ticks;
    }

    @Override
    public boolean isInsideVehicle() {
        return false;
    }

    @Override
    public boolean leaveVehicle() {
        return false;
    }

    @Override
    public Optional<Entity> getVehicle() {
        return Optional.empty();
    }

    @Override
    public boolean isCustomNameVisible() {
        return false;
    }

    @Override
    public void setCustomNameVisible(boolean flag) {

    }

    @Override
    public boolean isGlowing() {
        return false;
    }

    @Override
    public void setGlowing(boolean flag) {

    }

    @Override
    public boolean isInvulnerable() {
        return false;
    }

    @Override
    public void setInvulnerable(boolean flag) {

    }

    @Override
    public boolean isSneaking() {
        return false;
    }

    @Override
    public void setSneaking(boolean sneaking) {

    }

    public boolean isRemoved() {
        return removed;
    }

    @Override
    public boolean isAffectedByGravity() {
        return getFlag(AFFECTED_BY_GRAVITY);
    }

    @Override
    public void setAffectedByGravity(boolean affectedByGravity) {
        setFlag(AFFECTED_BY_GRAVITY, affectedByGravity);
    }

    @Override
    public Set<Class<? extends EntityComponent>> providedComponents() {
        return ImmutableSet.copyOf(componentMap.keySet());
    }

    @Override
    public <C extends EntityComponent> boolean provides(@Nonnull Class<C> clazz) {
        return componentMap.containsKey(clazz);
    }

    protected <C extends EntityComponent> void registerComponent(Class<C> clazz, C component) {
        componentMap.put(clazz, component);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <C extends EntityComponent> Optional<C> get(@Nonnull Class<C> clazz) {
        Preconditions.checkNotNull(clazz, "clazz");
        return Optional.ofNullable((C) componentMap.get(clazz));
    }

    protected void setFlag(MetadataConstants.Flag flag, boolean value) {
        setFlag(flag, value, true);
    }

    protected void setFlag(MetadataConstants.Flag flag, boolean value, boolean sendToPlayer) {
        if (value != metadataFlags.get(flag.ordinal())) {
            metadataFlags.set(flag.ordinal(), value);
            if (sendToPlayer) {
                onMetadataUpdate(getMetadataFlags());
            }
        }
    }

    protected void onMetadataUpdate(MetadataDictionary metadata) {
        SetEntityDataPacket packet = new SetEntityDataPacket();
        packet.setRuntimeEntityId(getEntityId());
        packet.getMetadata().putAll(metadata);
        level.getPacketManager().queuePacketForViewers(this, packet);
    }

    protected boolean getFlag(MetadataConstants.Flag flag) {
        return metadataFlags.get(flag.ordinal());
    }

    protected void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    protected boolean isTeleported() {
        return teleported;
    }

    protected MetadataDictionary getMetadataFlags() {
        MetadataDictionary dictionary = new MetadataDictionary();
        dictionary.put(FLAGS, metadataFlags.getAsLong());
        return dictionary;
    }

    public MetadataDictionary getMetadata() {
        MetadataDictionary dictionary = new MetadataDictionary();
        dictionary.put(MetadataConstants.FLAGS, metadataFlags.getAsLong());
        dictionary.put(NAMETAG, "");
        dictionary.put(ENTITY_AGE, 0);
        dictionary.put(SCALE, 1f);
        dictionary.put(MAX_AIR, (short) 400);
        dictionary.put(AIR, (short) 0);
        dictionary.put(BOUNDING_BOX_HEIGHT, entityType.getHeight());
        dictionary.put(BOUNDING_BOX_WIDTH, entityType.getWidth());
        return dictionary;
    }

    public void onAttributeUpdate(Attribute attribute) {
        Preconditions.checkNotNull(attribute, "attribute");
        // TODO
    }

    public boolean isMovementStale() {
        return movementStale;
    }

    public void resetStaleMovement() {
        checkIfAlive();
        movementStale = false;
        teleported = false;
    }

    public boolean isMetadataStale() {
        return metadataStale;
    }

    public void resetStaleMetadata() {
        checkIfAlive();
        metadataStale = false;
    }

    public boolean onItemPickup(ItemInstance item) {
        return false;
    }

    protected void save() {

    }

    private void refreshBoundingBox() {
        boundingBox = new BoundingBox(getPosition(), getPosition()).grow(getWidth() / 2, getHeight() / 2, getLength() / 2);
    }
}
