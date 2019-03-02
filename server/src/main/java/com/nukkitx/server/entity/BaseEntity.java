package com.nukkitx.server.entity;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.entity.component.Ageable;
import com.nukkitx.api.entity.component.EntityComponent;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.level.Level;
import com.nukkitx.api.level.chunk.Chunk;
import com.nukkitx.api.util.BoundingBox;
import com.nukkitx.api.util.Rotation;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.data.Attribute;
import com.nukkitx.protocol.bedrock.data.Metadata;
import com.nukkitx.protocol.bedrock.data.MetadataDictionary;
import com.nukkitx.protocol.bedrock.data.MetadataFlags;
import com.nukkitx.protocol.bedrock.packet.AddEntityPacket;
import com.nukkitx.protocol.bedrock.packet.SetEntityDataPacket;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.level.NukkitLevel;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.nukkitx.protocol.bedrock.data.Metadata.Flag.*;

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
    protected final MetadataFlags metadataFlags = new MetadataFlags();

    public BaseEntity(EntityType entityType, Vector3f position, NukkitLevel level, NukkitServer server) {
        this.level = level;
        this.server = server;
        this.entityType = entityType;
        this.position = position;
        this.rotation = Rotation.ZERO;
        this.motion = Vector3f.ZERO;
        this.entityId = level.getEntityManager().allocateEntityId();
        this.tickCreated = level.getCurrentTick();

        setFlag(HAS_COLLISION, true);
        setFlag(HAS_GRAVITY, true);
        setFlag(CAN_SHOW_NAME, true);
        setFlag(NO_AI, false);

        refreshBoundingBox();

        // TODO: Race condition to component registration.
        this.level.getEntityManager().registerEntity(this);
    }

    public BedrockPacket createAddEntityPacket() {
        AddEntityPacket packet = new AddEntityPacket();
        packet.setUniqueEntityId(entityId);
        packet.setRuntimeEntityId(entityId);
        packet.setEntityType(entityType.getType());
        packet.setPosition(position);
        packet.setMotion(motion);
        packet.setRotation(rotation.toVector3f());
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

        if (!this.rotation.equals(rotation)) {
            this.rotation = rotation;
            movementStale = true;
        }
    }

    @Nonnull
    @Override
    public Vector3f getMotion() {
        return motion;
    }

    @Nonnull
    @Override
    public Vector3f getPosition() {
        return position;
    }

    @Override
    public void setPosition(@Nonnull Vector3f position) {
        Preconditions.checkNotNull(position, "blockPosition");
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
        // Unregister this entity.
        level.getEntityManager().deregisterEntity(this);
        removed = true;
    }

    public boolean isRemoved() {
        return removed;
    }

    @Override
    public boolean isAlive() {
        return !removed;
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
    public void push(@Nonnull Vector3f motion) {

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
        return getFlag(CAN_SHOW_NAME);
    }

    @Override
    public void setCustomNameVisible(boolean flag) {
        setFlag(CAN_SHOW_NAME, flag);
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
        return getFlag(SNEAKING);
    }

    @Override
    public void setSneaking(boolean sneaking) {
        setFlag(SNEAKING, sneaking);
    }

    @Override
    public boolean isAffectedByGravity() {
        return getFlag(HAS_GRAVITY);
    }

    @Override
    public void setAffectedByGravity(boolean affectedByGravity) {
        setFlag(HAS_GRAVITY, affectedByGravity);
    }

    @Nonnull
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

    protected void setFlag(Metadata.Flag flag, boolean value) {
        boolean oldValue = metadataFlags.getFlag(flag);
        if (value != oldValue) {
            metadataFlags.setFlag(flag, value);
            onMetadataUpdate(getMetadataFlags());
        }
    }

    protected MetadataDictionary getMetadataFlags() {
        MetadataDictionary dictionary = new MetadataDictionary();
        dictionary.put(Metadata.FLAGS, metadataFlags);
        return dictionary;
    }

    protected MetadataDictionary getMetadata() {
        MetadataDictionary dictionary = getMetadataFlags();
        dictionary.put(Metadata.NAMETAG, "");
        dictionary.put(Metadata.ENTITY_AGE, 0);
        dictionary.put(Metadata.SCALE, 1f);
        dictionary.put(Metadata.MAX_AIR, (short) 400);
        dictionary.put(Metadata.AIR, (short) 0);
        dictionary.put(Metadata.BOUNDING_BOX_HEIGHT, getHeight());
        dictionary.put(Metadata.BOUNDING_BOX_WIDTH, getWidth());
        return dictionary;
    }

    protected void onMetadataUpdate(MetadataDictionary metadata) {
        SetEntityDataPacket packet = new SetEntityDataPacket();
        packet.setRuntimeEntityId(getEntityId());
        packet.getMetadata().putAll(metadata);
        level.getPacketManager().queuePacketForViewers(this, packet);
    }

    protected boolean getFlag(Metadata.Flag flag) {
        return metadataFlags.getFlag(flag);
    }

    protected void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    protected boolean isTeleported() {
        return teleported;
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

    public boolean onItemPickup(ItemStack item) {
        return false;
    }

    protected void save() {

    }

    private void refreshBoundingBox() {
        boundingBox = new BoundingBox(getPosition(), getPosition()).grow(getWidth() / 2, getHeight() / 2, getLength() / 2);
    }
}
