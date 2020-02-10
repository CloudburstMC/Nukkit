package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.*;

/**
 * @author Nukkit Project Team
 */
public class BlockSignPost extends BlockTransparent implements Faceable {

    protected final Identifier signItemId;
    protected final Identifier signWallId;
    protected final Identifier signStandingId;

    protected BlockSignPost(Identifier id, Identifier signStandingId, Identifier signWallId, Identifier signItemId) {
        super(id);
        this.signItemId = signItemId;
        this.signWallId = signWallId;
        this.signStandingId = signStandingId;
    }

    public BlockSignPost(Identifier id, Identifier signWallId, Identifier signItemId) {
        this(id, id, signWallId, signItemId);
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (face != BlockFace.DOWN) {
            CompoundTag nbt = new CompoundTag()
                    .putString("id", BlockEntity.SIGN)
                    .putInt("x", block.x)
                    .putInt("y", block.y)
                    .putInt("z", block.z)
                    .putString("Text1", "")
                    .putString("Text2", "")
                    .putString("Text3", "")
                    .putString("Text4", "");

            if (face == BlockFace.UP) {
                setDamage((int) Math.floor(((player.yaw + 180) * 16 / 360) + 0.5) & 0x0f);
                getLevel().setBlock(block, Block.get(signStandingId, getDamage()), true);
            } else {
                setDamage(face.getIndex());
                getLevel().setBlock(block, Block.get(signWallId, getDamage()), true);
            }

            if (player != null) {
                nbt.putString("Creator", player.getServerId().toString());
            }

            if (item.hasCustomBlockData()) {
                for (Tag aTag : item.getCustomBlockData().getAllTags()) {
                    nbt.put(aTag.getName(), aTag);
                }
            }

            BlockEntitySign sign = (BlockEntitySign) BlockEntity.createBlockEntity(BlockEntity.SIGN, getLevel().getChunk(block.getChunkX(), block.getChunkZ()), nbt);
            return sign != null;
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (down().getId() == AIR) {
                getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public Item toItem() {
        return Item.get(signItemId);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(this.getDamage() & 0x07);
    }

    public Identifier getSignItemId() {
        return signItemId;
    }

    public Identifier getSignWallId() {
        return signWallId;
    }

    public Identifier getSignStandingId() {
        return signStandingId;
    }

    public static BlockFactory factory(Identifier signWallId, Identifier signItemId) {
        return signStandingId -> new BlockSignPost(signStandingId, signWallId, signItemId);
    }
}
