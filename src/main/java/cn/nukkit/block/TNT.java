package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.item.Item;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/8 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class TNT extends Solid {

    public TNT() {
        this(0);
    }

    public TNT(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "TNT";
    }

    @Override
    public int getId() {
        return TNT;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == Item.FLINT_STEEL) {
            item.useOn(this);
            this.getLevel().setBlock(this, new Air(), true);
            double mot = (new NukkitRandom()).nextSignedFloat() * Math.PI * 2;
            CompoundTag nbt = new CompoundTag()
                    .putList(new ListTag<DoubleTag>("Pos")
                            .add(new DoubleTag("", this.x + 0.5))
                            .add(new DoubleTag("", this.y))
                            .add(new DoubleTag("", this.z + 0.5)))
                    .putList(new ListTag<DoubleTag>("Motion")
                            .add(new DoubleTag("", -Math.sin(mot) * 0.02))
                            .add(new DoubleTag("", 0.2))
                            .add(new DoubleTag("", -Math.cos(mot) * 0.02)))
                    .putList(new ListTag<FloatTag>("Rotation")
                            .add(new FloatTag("", 0))
                            .add(new FloatTag("", 0)))
                    .putByte("Fuse", (byte) 80);
            Entity tnt = new EntityPrimedTNT(
                    this.getLevel().getChunk(this.getFloorX() >> 4, this.getFloorZ() >> 4),
                    nbt
            );
            tnt.spawnToAll();
            return true;
        }
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.TNT_BLOCK_COLOR;
    }
}
