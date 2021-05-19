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

package cn.nukkit.event.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Position;

import java.util.Set;

/**
 * @author joserobjr
 * @since 2020-10-06
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockExplodeEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static HandlerList getHandlers() {
        return handlers;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected final Position position;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected Set<Block> blocks;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected Set<Block> ignitions;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected double yield;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected final double fireChance;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockExplodeEvent(Block block, Position position, Set<Block> blocks, Set<Block> ignitions, double yield, double fireChance) {
        super(block);
        this.position = position;
        this.blocks = blocks;
        this.yield = yield;
        this.ignitions = ignitions;
        this.fireChance = fireChance;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Position getPosition() {
        return this.position;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Set<Block> getAffectedBlocks() {
        return this.blocks;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setAffectedBlocks(Set<Block> blocks) {
        this.blocks = blocks;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public double getYield() {
        return this.yield;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setYield(double yield) {
        this.yield = yield;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Set<Block> getIgnitions() {
        return ignitions;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setIgnitions(Set<Block> ignitions) {
        this.ignitions = ignitions;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public double getFireChance() {
        return fireChance;
    }
}
