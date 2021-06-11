package cn.nukkit.blockproperty.value;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import javax.annotation.Nullable;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public enum CrackState {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    NO_CRACKS,
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    CRACKED,
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    MAX_CRACKED {
        @Nullable
        @Override
        public CrackState getNext() {
            return null;
        }
    };
    private static final CrackState[] VALUES = values();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public CrackState getNext() {
        return VALUES[ordinal() + 1];
    }
}
