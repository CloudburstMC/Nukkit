/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cn.nukkit.level.format.anvil.util;

import cn.nukkit.api.API;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.util.PalettedBlockStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.BitSet;

import static cn.nukkit.api.API.Definition.INTERNAL;
import static cn.nukkit.api.API.Usage.BLEEDING;

/**
 * @author joserobjr
 * @since 2020-10-02
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public final class ImmutableBlockStorage extends BlockStorage {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final ImmutableBlockStorage EMPTY = new BlockStorage().immutableCopy();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @API(definition = INTERNAL, usage = BLEEDING)
    ImmutableBlockStorage(BlockState[] states, byte flags, PalettedBlockStorage palette, @Nullable BitSet denyStates) {
        super(states.clone(), flags, palette.copy(), denyStates != null? (BitSet)denyStates.clone() : null);
    }

    @Override
    protected BlockState setBlockState(int index, @Nonnull BlockState state) {
        throw new UnsupportedOperationException("This BlockStorage is immutable");
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void delayPaletteUpdates() {
        
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void recheckBlocks() {
        
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public ImmutableBlockStorage immutableCopy() {
        return this;
    }
}
