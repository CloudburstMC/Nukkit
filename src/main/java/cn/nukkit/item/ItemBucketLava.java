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

package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.Level;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * @author joserobjr
 * @since 2020-11-20
 */
@PowerNukkitOnly
@Since("1.3.2.0-PN")
public class ItemBucketLava extends ItemBucket {
    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    public ItemBucketLava() {
        this(0, 1);
    }

    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    public ItemBucketLava(Integer meta) {
        this(meta, 1);
    }

    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    public ItemBucketLava(Integer meta, int count) {
        super(LAVA_BUCKET, meta, count, "Lava Bucket");
    }

    @Since("1.3.2.0-PN")
    @PowerNukkitOnly
    @Override
    public Block getTargetBlock() {
        return Block.get(BlockID.LAVA);
    }

    @Since("1.3.2.0-PN")
    @PowerNukkitOnly
    @Override
    protected void afterUse(Level level, Block block) {
        level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_BUCKET_EMPTY_LAVA);
    }
}
