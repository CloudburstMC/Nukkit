package cn.nukkit.api.metadata.blockentity;

import javax.annotation.Nonnegative;

public interface ComparatorBlockEntity extends BlockEntity {

    @Nonnegative
    int getOutputSignal();

    void setOutputSignal(@Nonnegative int outputSignal);
}
