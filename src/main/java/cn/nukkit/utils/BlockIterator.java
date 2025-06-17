package cn.nukkit.utils;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;

import java.util.Iterator;

/**
 * Block iterator
 *
 * @author MagicDroidX
 * Nukkit Project
 */
public class BlockIterator implements Iterator<Block> {

    private final int maxDistance;

    private static final int gridSize = 1 << 24;

    private boolean end = false;

    private final Vector3[] blockQueue;
    private int currentBlock;

    private int currentDistance;
    private final int maxDistanceInt;

    private int secondError;
    private int thirdError;

    private final int secondStep;
    private final int thirdStep;

    private BlockFace mainFace;
    private BlockFace secondFace;
    private BlockFace thirdFace;

    private final Level level;

    public BlockIterator(Level level, Vector3 start, Vector3 direction) {
        this(level, start, direction, 0);
    }

    public BlockIterator(Level level, Vector3 start, Vector3 direction, double yOffset) {
        this(level, start, direction, yOffset, 0);
    }

    public BlockIterator(Level level, Vector3 start, Vector3 direction, double yOffset, int maxDistance) {
        this.maxDistance = maxDistance;
        this.blockQueue = new Vector3[3];

        this.level = level;

        Vector3 startClone = new Vector3(start.x, start.y, start.z);
        startClone.y += yOffset;

        this.currentDistance = 0;

        double mainDirection = 0;
        double secondDirection = 0;
        double thirdDirection = 0;

        double mainPosition = 0;
        double secondPosition = 0;
        double thirdPosition = 0;

        Block startBlock = level.getBlock(NukkitMath.floorDouble(startClone.x), NukkitMath.floorDouble(startClone.y), NukkitMath.floorDouble(startClone.z));

        if (getXLength(direction) > mainDirection) {
            this.mainFace = getXFace(direction);
            mainDirection = getXLength(direction);
            mainPosition = getXPosition(direction, startClone, startBlock);

            this.secondFace = getYFace(direction);
            secondDirection = getYLength(direction);
            secondPosition = getYPosition(direction, startClone, startBlock);

            this.thirdFace = getZFace(direction);
            thirdDirection = getZLength(direction);
            thirdPosition = getZPosition(direction, startClone, startBlock);
        }
        if (getYLength(direction) > mainDirection) {
            this.mainFace = getYFace(direction);
            mainDirection = getYLength(direction);
            mainPosition = getYPosition(direction, startClone, startBlock);

            this.secondFace = getZFace(direction);
            secondDirection = getZLength(direction);
            secondPosition = getZPosition(direction, startClone, startBlock);

            this.thirdFace = getXFace(direction);
            thirdDirection = getXLength(direction);
            thirdPosition = getXPosition(direction, startClone, startBlock);
        }
        if (getZLength(direction) > mainDirection) {
            this.mainFace = getZFace(direction);
            mainDirection = getZLength(direction);
            mainPosition = getZPosition(direction, startClone, startBlock);

            this.secondFace = getXFace(direction);
            secondDirection = getXLength(direction);
            secondPosition = getXPosition(direction, startClone, startBlock);

            this.thirdFace = getYFace(direction);
            thirdDirection = getYLength(direction);
            thirdPosition = getYPosition(direction, startClone, startBlock);
        }

        double d = mainPosition / mainDirection;
        double secondd = secondPosition - secondDirection * d;
        double thirdd = thirdPosition - thirdDirection * d;

        this.secondError = (int) Math.floor(secondd * gridSize);
        this.secondStep = (int) Math.round(secondDirection / mainDirection * gridSize);
        this.thirdError = (int) Math.floor(thirdd * gridSize);
        this.thirdStep = (int) Math.round(thirdDirection / mainDirection * gridSize);

        if (this.secondError + this.secondStep <= 0) {
            this.secondError = -this.secondStep + 1;
        }

        if (this.thirdError + this.thirdStep <= 0) {
            this.thirdError = -this.thirdStep + 1;
        }

        Vector3 lastBlock = startBlock.getSideVec(this.mainFace.getOpposite());

        if (this.secondError < 0) {
            this.secondError += gridSize;
            lastBlock = lastBlock.getSideVec(this.secondFace.getOpposite());
        }

        if (this.thirdError < 0) {
            this.thirdError += gridSize;
            lastBlock = lastBlock.getSideVec(this.thirdFace.getOpposite());
        }

        this.secondError -= gridSize;
        this.thirdError -= gridSize;

        this.blockQueue[0] = lastBlock;

        this.currentBlock = -1;

        this.scan();

        boolean startBlockFound = false;

        for (int cnt = this.currentBlock; cnt >= 0; --cnt) {
            if (this.blockQueue[cnt].equals(startBlock)) {
                this.currentBlock = cnt;
                startBlockFound = true;
                break;
            }
        }

        if (!startBlockFound) {
            throw new IllegalStateException("Start block missed in BlockIterator");
        }

        this.maxDistanceInt = (int) Math.round(maxDistance / (Math.sqrt(mainDirection * mainDirection + secondDirection * secondDirection + thirdDirection * thirdDirection) / mainDirection));
    }

    private static BlockFace getXFace(Vector3 direction) {
        return ((direction.x) > 0) ? BlockFace.EAST : BlockFace.WEST;
    }

    private static BlockFace getYFace(Vector3 direction) {
        return ((direction.y) > 0) ? BlockFace.UP : BlockFace.DOWN;
    }

    private static BlockFace getZFace(Vector3 direction) {
        return ((direction.z) > 0) ? BlockFace.SOUTH : BlockFace.NORTH;
    }

    private static double getXLength(Vector3 direction) {
        return Math.abs(direction.x);
    }

    private static double getYLength(Vector3 direction) {
        return Math.abs(direction.y);
    }

    private static double getZLength(Vector3 direction) {
        return Math.abs(direction.z);
    }

    private static double getPosition(double direction, double position, double blockPosition) {
        return direction > 0 ? (position - blockPosition) : (blockPosition + 1 - position);
    }

    private static double getXPosition(Vector3 direction, Vector3 position, Block block) {
        return getPosition(direction.x, position.x, block.x);
    }

    private static double getYPosition(Vector3 direction, Vector3 position, Block block) {
        return getPosition(direction.y, position.y, block.y);
    }

    private static double getZPosition(Vector3 direction, Vector3 position, Block block) {
        return getPosition(direction.z, position.z, block.z);
    }

    @Override
    public Block next() {
        this.scan();

        if (this.currentBlock <= -1) {
            throw new IndexOutOfBoundsException();
        } else {
            return this.level.getBlock(this.blockQueue[this.currentBlock--]);
        }
    }

    @Override
    public boolean hasNext() {
        this.scan();
        return this.currentBlock != -1;
    }

    private void scan() {
        if (this.currentBlock >= 0) {
            return;
        }

        if (this.maxDistance != 0 && this.currentDistance > this.maxDistanceInt) {
            this.end = true;
            return;
        }

        if (this.end) {
            return;
        }

        ++this.currentDistance;

        this.secondError += this.secondStep;
        this.thirdError += this.thirdStep;

        if (this.secondError > 0 && this.thirdError > 0) {
            this.blockQueue[2] = this.blockQueue[0].getSideVec(this.mainFace);

            if ((this.secondStep * this.thirdError) < (this.thirdStep * this.secondError)) {
                this.blockQueue[1] = this.blockQueue[2].getSideVec(this.secondFace);
                this.blockQueue[0] = this.blockQueue[1].getSideVec(this.thirdFace);
            } else {
                this.blockQueue[1] = this.blockQueue[2].getSideVec(this.thirdFace);
                this.blockQueue[0] = this.blockQueue[1].getSideVec(this.secondFace);
            }

            this.thirdError -= gridSize;
            this.secondError -= gridSize;
            this.currentBlock = 2;
        } else if (this.secondError > 0) {
            this.blockQueue[1] = this.blockQueue[0].getSideVec(this.mainFace);
            this.blockQueue[0] = this.blockQueue[1].getSideVec(this.secondFace);
            this.secondError -= gridSize;
            this.currentBlock = 1;
        } else if (this.thirdError > 0) {
            this.blockQueue[1] = this.blockQueue[0].getSideVec(this.mainFace);
            this.blockQueue[0] = this.blockQueue[1].getSideVec(this.thirdFace);
            this.thirdError -= gridSize;
            this.currentBlock = 1;
        } else {
            this.blockQueue[0] = this.blockQueue[0].getSideVec(this.mainFace);
            this.currentBlock = 0;
        }
    }
}
