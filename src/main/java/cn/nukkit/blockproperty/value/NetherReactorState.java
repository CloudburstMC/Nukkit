package cn.nukkit.blockproperty.value;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public enum NetherReactorState {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    READY,

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    INITIALIZED,

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    FINISHED;
    
    private static final NetherReactorState[] values = values();
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static NetherReactorState getFromData(int data) {
        return values[data];
    }
}
