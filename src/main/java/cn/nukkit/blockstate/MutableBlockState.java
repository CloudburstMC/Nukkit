package cn.nukkit.blockstate;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.blockproperty.BlockProperties;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ToString
@EqualsAndHashCode
@ParametersAreNonnullByDefault
public abstract class MutableBlockState implements IMutableBlockState {
    protected final int blockId;
    protected final BlockProperties properties;
    
    MutableBlockState(int blockId, BlockProperties properties) {
        this.blockId = blockId;
        this.properties = properties;
    }

    @Nonnull
    @Override
    public final BlockProperties getProperties() {
        return properties;
    }

    @Override
    public final int getBlockId() {
        return blockId;
    }

    @Override
    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "the BlockState itself")
    public final int getFullId() {
        return IMutableBlockState.super.getFullId();
    }

    @Override
    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "the BlockState itself")
    public final long getBigId() {
        return IMutableBlockState.super.getBigId();
    }

    @Override
    public final int getBitSize() {
        return getProperties().getBitSize();
    }

    public abstract void validate();

    @Nonnull
    public abstract MutableBlockState copy();
}
