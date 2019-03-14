package cn.nukkit.event.server;

import cn.nukkit.event.HandlerList;
import cn.nukkit.utils.PlayerDataSerializer;
import com.google.common.base.Preconditions;

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class PlayerDataSerializeEvent extends ServerEvent {
    private static HandlerList handlers = new HandlerList();

    private final Optional<String> name;
    private final Optional<UUID> uuid;
    private PlayerDataSerializer serializer;

    public PlayerDataSerializeEvent(String name, PlayerDataSerializer serializer) {
        Preconditions.checkNotNull(name);
        this.serializer = Preconditions.checkNotNull(serializer);
        UUID uuid = null;
        try {
            uuid = UUID.fromString(name);
        } catch (Exception e) {
            // ignore
        }
        this.uuid = Optional.ofNullable(uuid);
        this.name = this.uuid.isPresent() ? Optional.empty() : Optional.of(name);
    }

    public Optional<String> getName() {
        return name;
    }

    public Optional<UUID> getUuid() {
        return uuid;
    }

    public PlayerDataSerializer getSerializer() {
        return serializer;
    }

    public void setSerializer(PlayerDataSerializer serializer) {
        this.serializer = Preconditions.checkNotNull(serializer, "serializer");
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
