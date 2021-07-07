package cn.nukkit.network.protocol.types;

public class EntityLink {

    public long fromEntityUniquieId;
    public long toEntityUniquieId;
    public Type type;
    public boolean immediate;
    public boolean riderInitiated;

    public EntityLink(long fromEntityUniquieId, long toEntityUniquieId, Type type, boolean immediate, boolean riderInitiated) {
        this.fromEntityUniquieId = fromEntityUniquieId;
        this.toEntityUniquieId = toEntityUniquieId;
        this.type = type;
        this.immediate = immediate;
        this.riderInitiated = riderInitiated;
    }


    public EntityLink(long fromEntityUniquieId, long toEntityUniquieId, byte type, boolean immediate, boolean riderInitiated) {
        this.fromEntityUniquieId = fromEntityUniquieId;
        this.toEntityUniquieId = toEntityUniquieId;
        this.type = Type.values()[type];
        this.immediate = immediate;
        this.riderInitiated = riderInitiated;
    }

    public static enum Type {

        REMOVE,
        RIDE,
        PASSENGER
    }
}
