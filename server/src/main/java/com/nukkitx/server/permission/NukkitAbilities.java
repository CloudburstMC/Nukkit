package com.nukkitx.server.permission;

import com.nukkitx.api.permission.Abilities;
import com.nukkitx.api.permission.Ability;
import com.nukkitx.server.util.concurrent.ConcurrentBitSet;

import javax.annotation.Nonnull;

public class NukkitAbilities implements Abilities {
    private ConcurrentBitSet flags;
    private ConcurrentBitSet flags2;
    //private ConcurrentBitSet custom;
    private boolean stale = false;

    public NukkitAbilities() {
        flags = ConcurrentBitSet.valueOf(new long[]{0L});
        flags2 = ConcurrentBitSet.valueOf(new long[]{0L});
        //custom = ConcurrentBitSet.valueOf(new long[]{0L});
    }

    public NukkitAbilities(NukkitAbilities abilities) {
        flags = ConcurrentBitSet.valueOf(abilities.flags.toLongArray());
        flags2 = ConcurrentBitSet.valueOf(abilities.flags2.toLongArray());
    }

    public void refresh() {
        stale = false;
    }

    public boolean isStale() {
        return stale;
    }

    public void setFlags(int flags) {
        this.flags = ConcurrentBitSet.valueOf(new long[]{flags});
    }

    public void setFlags2(int flags2) {
        this.flags2 = ConcurrentBitSet.valueOf(new long[]{flags2});
    }

    public int getFlags() {
        long[] flags = this.flags.toLongArray();
        return flags.length == 0 ? 0 : (int) flags[0];
    }

    public int getFlags2() {
        long[] flags = this.flags2.toLongArray();
        return flags.length == 0 ? 0 : (int) flags[0];
    }

    public int getCustomFlags() {
        return 0; //TODO: Custom flags.
    }

    @Override
    public void setAbility(@Nonnull Ability ability, boolean value) {
        InternalAbility iAbility = InternalAbility.fromApi(ability);
        if (iAbility.second) {
            if (flags2.get(iAbility.index) != value) {
                flags2.set(iAbility.index, value);
                stale = true;
            }
        } else {
            if (flags.get(iAbility.index) != value) {
                flags.set(iAbility.index, value);
                stale = true;
            }
        }
    }

    @Override
    public boolean getAbility(@Nonnull Ability ability) {
        return false;
    }


    private enum InternalAbility {
        WORLD_IMMUTABLE(0, false),
        NO_PVP(1, false),
        NO_PVM(2, false),
        NO_MVP(3, false),
        AUTO_JUMP(5, false),
        ALLOWED_FLIGHT(6, false),
        NO_CLIP(7, false),
        WORLD_BUILDER(8, false),
        FLYING(9, false),
        // Second bitset
        PLACE_AND_DESTROY(0, true),
        DOORS_AND_SWITCHES(1, true),
        OPEN_CONTAINERS(2, true),
        ATTACK_PLAYERS(3, true),
        ATTACK_MOBS(4, true),
        OPERATOR(5, true),
        TELEPORT(6, true);

        private static final InternalAbility[] VALUES = values();

        private final byte index;
        private final boolean second;

        InternalAbility(int index, boolean second) {
            this.index = (byte) index;
            this.second = second;
        }

        private static InternalAbility fromApi(Ability ability) {
            return VALUES[ability.ordinal()];
        }
    }
}
