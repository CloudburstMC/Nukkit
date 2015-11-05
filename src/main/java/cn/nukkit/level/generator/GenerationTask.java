package cn.nukkit.level.generator;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.SimpleChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.scheduler.AsyncTask;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GenerationTask extends AsyncTask {
    public boolean state;
    public int levelId;
    public FullChunk chunk;

    /*public byte[] chunk;
    public Class<? extends FullChunk> chunkClass;*/


    public GenerationTask(Level level, FullChunk chunk) {
        this.state = true;
        this.levelId = level.getId();

        /*this.chunk = chunk.toFastBinary();
        this.chunkClass = chunk.getClass();*/

        this.chunk = chunk;
    }

    @Override
    public void onRun() {
        SimpleChunkManager manager = (SimpleChunkManager) this.getFromThreadStore("generation.level" + this.levelId + ".manager");

        Generator generator = (Generator) this.getFromThreadStore("generation.level" + this.levelId + ".generator");

        if (manager == null || generator == null) {
            this.state = false;
            return;
        }

        /*FullChunk chunk;
        try {
            chunk = (FullChunk) this.chunkClass.getMethod("fromFastBinary").invoke(null, this.chunk);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/

        FullChunk chunk = ((BaseFullChunk) this.chunk).clone();

        if (chunk == null) {
            return;
        }

        manager.setChunk(chunk.getX(), chunk.getZ(), chunk);

        generator.generateChunk(chunk.getX(), chunk.getZ());

        chunk = manager.getChunk(chunk.getX(), chunk.getZ());
        chunk.setGenerated();
        this.chunk = chunk;

        manager.setChunk(chunk.getX(), chunk.getZ(), null);
    }

    @Override
    public void onCompletion(Server server) {
        Level level = server.getLevel(this.levelId);
        if (level != null) {
            if (!this.state) {
                level.registerGenerator();
                return;
            }

            /*FullChunk chunk;
            try {
                chunk = (FullChunk) this.chunkClass.getMethod("fromFastBinary").invoke(null, this.chunk, level.getProvider());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }*/

            FullChunk chunk = ((BaseFullChunk) this.chunk).clone();

            if (chunk == null) {
                return;
            }

            level.generateChunkCallback(chunk.getX(), chunk.getZ(), chunk);
        }
    }
}
