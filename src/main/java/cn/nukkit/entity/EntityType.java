package cn.nukkit.entity;

import cn.nukkit.utils.Identifier;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class EntityType<T extends Entity> {
    private final Identifier identifier;
    private final Class<T> entityClass;

    private EntityType(Identifier identifier, Class<T> entityClass) {
        this.identifier = identifier;
        this.entityClass = entityClass;
    }

    public static <T extends Entity> EntityType<T> from(String identifier, Class<T> entityClass) {
        return from(Identifier.fromString(identifier), entityClass);
    }

    public static <T extends Entity> EntityType<T> from(Identifier identifier, Class<T> entityClass) {
        checkNotNull(identifier, "identifier");
        checkNotNull(entityClass, "entityClass");
        checkArgument(Entity.class.isAssignableFrom(entityClass), "%s is not subclass of Entity", entityClass.getName());
        return new EntityType<>(identifier, entityClass);
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return identifier + "(" + entityClass + ")";
    }
}
