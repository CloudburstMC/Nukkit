package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.entity.FallingSand;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;


/**
 * author: rcsuperman
 * Nukkit Project
 */
public abstract class Fallable extends Solid {

	protected Fallable(int meta) {super(meta);}


	@Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz) {
        return super.place(item, block, target, face, fx, fy, fz, null);
    }


	public int onUpdate(int type){
		if(type == Level.BLOCK_UPDATE_NORMAL) {
			Block down = super.getSide(Vector3.SIDE_DOWN);
			if (down instanceof Liquid || down instanceof Air) {
				CompoundTag nbt = new CompoundTag()
						.putList(new ListTag<DoubleTag>("Pos")
								.add(new DoubleTag("", this.x + 0.5))
								.add(new DoubleTag("", this.y))
								.add(new DoubleTag("", this.z + 0.5)))
						.putList(new ListTag<DoubleTag>("Motion")
								.add(new DoubleTag("", 0))
								.add(new DoubleTag("", 0))
								.add(new DoubleTag("", 0)))

						.putList(new ListTag<FloatTag>("Rotation")
								.add(new FloatTag("", 0))
								.add(new FloatTag("", 0)))
						.putInt("Tile", this.getId())
						.putInt("Data", this.getDamage());
				FallingSand fs = new FallingSand(this.getLevel().getChunk((int) this.x, (int) this.y, true), nbt);
				fs.spawnToAll();
				onUpdate(3600);//You can delete it
			}
		}
		return type;
	}
}
