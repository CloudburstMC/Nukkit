package cn.nukkit.block;

import cn.nukkit.blockentity.Sign;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockEntityRegistry;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import static cn.nukkit.block.BlockIds.AIR;
import static cn.nukkit.blockentity.BlockEntityTypes.SIGN;

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
    public float getHardness() {
        return 1;
    }

    @Override
    public float getResistance() {
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

    public static BlockFactory factory(Identifier signWallId, Identifier signItemId) {
        return signStandingId -> new BlockSignPost(signStandingId, signWallId, signItemId);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (down().getId() == AIR) {
                getLevel().useBreakOn(this.getPosition());

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (face != BlockFace.DOWN) {
            if (face == BlockFace.UP) {
                setMeta((int) Math.floor(((player.getYaw() + 180) * 16 / 360) + 0.5) & 0x0f);
                getLevel().setBlock(block.getPosition(), Block.get(signStandingId, getMeta()), true);
            } else {
                setMeta(face.getIndex());
                getLevel().setBlock(block.getPosition(), Block.get(signWallId, getMeta()), true);
            }

            Sign sign = BlockEntityRegistry.get().newEntity(SIGN, this.getChunk(), this.getPosition());
            if (!item.hasCompoundTag()) {
                sign.setTextOwner(player.getXuid());
            } else {
                sign.loadAdditionalData(item.getTag());
            }

            return true;
        }

        return false;
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
        return BlockFace.fromIndex(this.getMeta() & 0x07);
    }

    @Override
    public Item toItem() {
        return Item.get(signItemId);
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

    @Override
    public boolean canWaterlogSource() {
        return true;
    }
}
