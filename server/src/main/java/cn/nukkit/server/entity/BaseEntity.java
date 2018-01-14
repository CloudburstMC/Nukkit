package cn.nukkit.server.entity;

import cn.nukkit.api.Server;
import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.entity.component.EntityComponent;
import cn.nukkit.api.entity.component.Flammable;
import cn.nukkit.api.event.entity.EntityDamageEvent;
import cn.nukkit.api.event.player.PlayerTeleportEvent;
import cn.nukkit.api.level.Level;
import cn.nukkit.api.util.Rotation;
import cn.nukkit.server.NukkitServer;
import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import javafx.geometry.BoundingBox;

import javax.annotation.Nonnull;
import java.util.*;

import static cn.nukkit.server.network.minecraft.data.metadata.EntityMetadataData.Flag.*;

public class BaseEntity implements Entity {
    private final NukkitServer server;
    private final EntityTypeData data;
    private final Map<Class<? extends EntityComponent>, EntityComponent> componentMap = new HashMap<>();
    // Flags
    protected boolean sneaking = false;
    protected boolean sprinting = false;
    protected long tickCreated;
    private long entityId;
    private Level level;
    private Vector3f position;
    private Vector3f motion;
    private Rotation rotation;
    private boolean needsUpdate = true;
    private boolean teleported = false;
    private boolean invisible = false;
    private boolean affectedByGravity = true;
    private boolean alwaysShowNameTag = false;
    private boolean action = false;
    private boolean angry = false;
    private boolean visibleNamtag = true;
    //
    private boolean removed = false;
    private BoundingBox boundingBox;

    public BaseEntity(EntityTypeData data, Vector3f position, Level level, NukkitServer server) {
        this.data = data;
        this.position = position;
        this.level = level;
        this.server = server;
    }

    protected long getMetadataFlagValue() {
        BitSet flags = new BitSet(64);
        flags.set(ON_FIRE.id(), (get(Flammable.class).isPresent() && ensureAndGet(Flammable.class).isIgnited()));
        flags.set(AFFECTED_BY_GRAVITY.id(), affectedByGravity);
        flags.set(ALWAYS_SHOW_NAMETAG.id(), alwaysShowNameTag);
        flags.set(CAN_SHOW_NAMETAG.id(), visibleNamtag);
        flags.set(SNEAKING.id(), sneaking);
        flags.set(ANGRY.id(), angry);
        flags.set(ACTION.id(), action);
        flags.set(SPRINTING.id(), sprinting);
        flags.set(INVISIBLE.id(), invisible);


        long[] array = flags.toLongArray();
        return array.length == 0 ? 0 : array[0];
    }


    @Nonnull
    @Override
    public Rotation getRotation() {
        return null;
    }

    @Nonnull
    @Override
    public Vector3f getMotion() {
        return motion;
    }

    @Override
    public void setMotion(Vector3f vector) {
        Preconditions.checkNotNull(vector, "vector");
        this.motion = vector;
    }

    @Override
    public float getHeight() {
        return data.getHeight();
    }

    @Override
    public float getWidth() {
        return data.getWidth();
    }

    @Override
    public float getDepth() {
        return data.getDepth();
    }

    @Override
    public boolean isOnGround() {
        return false;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public boolean teleport(Entity destination, PlayerTeleportEvent.TeleportCause cause) {
        return false;
    }

    @Override
    public long getUniqueEntityId() {
        return entityId;
    }

    @Override
    public long getRuntimeEntityId() {
        return entityId;
        // TODO
    }

    @Override
    public void remove() {
        checkIfAlive();
        removed = true;
    }

    final void checkIfAlive() {
        Preconditions.checkState(!removed, "Entity has been removed.");
    }

    @Override
    public boolean isAlive() {
        return !removed;
    }

    @Override
    public Server getServer() {
        return null;
    }

    @Override
    public int getTicksLived() {
        return 0;
    }

    @Override
    public void setTicksLived(int value) {

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
    public boolean isSilent() {
        return sneaking;
    }

    @Override
    public void setSilent(boolean silent) {
        this.sneaking = silent;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return false;
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
}
