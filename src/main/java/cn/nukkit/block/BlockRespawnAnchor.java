package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class BlockRespawnAnchor extends BlockSolidMeta {

    public BlockRespawnAnchor() {
        this(0);
    }

    public BlockRespawnAnchor(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return RESPAWN_ANCHOR;
    }

    @Override
    public double getHardness() {
        return 50;
    }

    @Override
    public double getResistance() {
        return 1200;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_DIAMOND) {
            return new Item[]{
                    toItem()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public int getLightLevel() {
        switch (this.getDamage()) {
            case 0:
                return 0;
            case 1:
                return 3;
            case 2:
                return 7;
            default:
                return 15;
        }
    }

    @Override
    public String getName() {
        return "Respawn Anchor";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        int chargeLevel = this.getDamage();

        if (item.getId() == GLOWSTONE && chargeLevel < 4) {
            if (player != null && !player.isCreative()) {
                item.count--;
            }

            this.setDamage(chargeLevel + 1);
            this.getLevel().setBlock(this, this, true);
            this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_RESPAWN_ANCHOR_CHARGE);
            return true;
        }

        if (chargeLevel > 0 && this.level.getDimension() != Level.DIMENSION_NETHER) {
            if (this.level.getGameRules().getBoolean(GameRule.RESPAWN_BLOCKS_EXPLODE)) {
                this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);

                Explosion explosion = new Explosion(this.add(0.5, 0, 0.5), 5, this);
                explosion.setFireSpawnChance(0.3333);
                explosion.explodeA();
                explosion.explodeB();
            }
            return true;
        }

        if (player != null && chargeLevel > 0 && this.level.getDimension() == Level.DIMENSION_NETHER) {
            if (player.distanceSquared(this) > 36) {
                return false;
            }

            if (!this.equals(player.getSpawnPosition())) {
                player.setSpawn(this);

                player.sendMessage("§7%tile.respawn_anchor.respawnSet", true);

                this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_RESPAWN_ANCHOR_SET_SPAWN);
            }
        }

        return item.getId() == GLOWSTONE;
    }

    @Override
    public boolean onBreak(Item item) {
        boolean r = super.onBreak(item);
        if (r) {
            if (level.getDimension() == Level.DIMENSION_NETHER) {
                Vector3 safeSpawn = null;
                for (Player player : level.getServer().getOnlinePlayers().values()) {
                    if (this.equals(player.getSpawnPosition())) {
                        player.setSpawn(safeSpawn == null ? (safeSpawn = level.getServer().getDefaultLevel().getSafeSpawn()) : safeSpawn);
                    }
                }
            }
        }
        return r;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        switch (this.getDamage()) {
            case 1:
                return 3;
            case 2:
                return 7;
            case 3:
                return 11;
            case 4:
                return 15;
            default:
                return 0;
        }
    }
}
