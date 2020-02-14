package cn.nukkit.blockentity;

import cn.nukkit.utils.Identifier;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class BlockEntityType<T extends BlockEntity> {

    private final Identifier identifier;
    private final Class<T> blockEntityClass;

    private BlockEntityType(Identifier identifier, Class<T> blockEntityClass) {
        this.identifier = identifier;
        this.blockEntityClass = blockEntityClass;
    }

    public static <T extends BlockEntity> BlockEntityType<T> from(String identifier, Class<T> entityClass) {
        return from(Identifier.fromString(identifier), entityClass);
    }

    public static <T extends BlockEntity> BlockEntityType<T> from(Identifier identifier, Class<T> entityClass) {
        checkNotNull(identifier, "identifier");
        checkNotNull(entityClass, "entityClass");
        checkArgument(BlockEntity.class.isAssignableFrom(entityClass), "%s is not subclass of BlockEntity", entityClass.getName());
        return new BlockEntityType<>(identifier, entityClass);
    }

    public Class<T> getBlockEntityClass() {
        return blockEntityClass;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return identifier + "(" + blockEntityClass + ")";
    }
}
