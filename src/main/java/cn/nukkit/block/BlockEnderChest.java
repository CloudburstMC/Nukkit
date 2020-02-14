package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.EnderChest;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockEntityRegistry;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import java.util.HashSet;
import java.util.Set;

import static cn.nukkit.block.BlockIds.OBSIDIAN;
import static cn.nukkit.blockentity.BlockEntityTypes.ENDER_CHEST;

public class BlockEnderChest extends BlockTransparent implements Faceable {

    private Set<Player> viewers = new HashSet<>();

    public BlockEnderChest(Identifier id) {
        super(id);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getLightLevel() {
        return 7;
    }

    @Override
    public float getHardness() {
        return 22.5f;
    }

    @Override
    public float getResistance() {
        return 3000;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public float getMinX() {
        return this.getX() + 0.0625f;
    }

    @Override
    public float getMinZ() {
        return this.getZ() + 0.0625f;
    }

    @Override
    public float getMaxX() {
        return this.getX() + 0.9375f;
    }

    @Override
    public float getMaxY() {
        return this.getY() + 0.9475f;
    }

    @Override
    public float getMaxZ() {
        return this.getZ() + 0.9375f;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        int[] faces = {2, 5, 3, 4};
        this.setDamage(faces[player != null ? player.getDirection().getHorizontalIndex() : 0]);

        this.getLevel().setBlock(block.getPosition(), this, true, true);

        EnderChest enderChest = BlockEntityRegistry.get().newEntity(ENDER_CHEST, this.getChunk(), this.getPosition());
        enderChest.loadAdditionalData(item.getTag());
        if (item.hasCustomName()) {
            enderChest.setCustomName(item.getCustomName());
        }
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            Block top = this.up();
            if (!top.isTransparent()) {
                return true;
            }

            BlockEntity blockEntity = this.getLevel().getBlockEntity(this.getPosition());
            if ((blockEntity instanceof EnderChest)) {
                BlockEntityRegistry.get().newEntity(ENDER_CHEST, this.getChunk(), this.getPosition());
            }

            player.setViewingEnderChest(this);
            player.addWindow(player.getEnderChestInventory());
        }

        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    Item.get(OBSIDIAN, 0, 8)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.OBSIDIAN_BLOCK_COLOR;
    }

    public Set<Player> getViewers() {
        return viewers;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item toItem() {
        return Item.get(id);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getMeta() & 0x07);
    }
}
