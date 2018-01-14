package cn.nukkit.server.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class EntityLink {
    private final long fromUniqueEntityId;
    private final long toUniqueEntityId;
    private final byte type;
    private final boolean unknown;

    @Override
    public int hashCode() {
        return Objects.hash(fromUniqueEntityId, toUniqueEntityId, type, unknown);
    }

    @Override
    public String toString() {
        return "PlayerAttribute{" +
                "fromUniqueEntityId=" + fromUniqueEntityId +
                ", toUniqueEntityId=" + toUniqueEntityId +
                ", type=" + type +
                ", unknown=" + unknown +
                '}';
    }
}
