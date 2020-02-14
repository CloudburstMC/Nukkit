/*
 * This file is licensed under the MIT License (MIT).
 *
 * Copyright (c) 2014 Daniel Ennis <http://aikar.co>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package co.aikar.timings;

import cn.nukkit.Nukkit;
import cn.nukkit.Server;
import cn.nukkit.blockentity.impl.BaseBlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.player.Player;
import cn.nukkit.timings.JsonUtil;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static co.aikar.timings.Timings.fullServerTickTimer;
import static co.aikar.timings.TimingsManager.MINUTE_REPORTS;

public class TimingsHistory {
    public static long lastMinuteTime;
    public static long timedTicks;
    public static long playerTicks;
    public static long entityTicks;
    public static long tileEntityTicks;
    public static long activatedEntityTicks;

    private static int levelIdPool = 1;
    static Map<String, Integer> levelMap = new HashMap<>();
    static Map<Identifier, String> entityMap = new HashMap<>();
    static Map<Identifier, String> blockEntityMap = new HashMap<>();

    private final long endTime;
    private final long startTime;
    private final long totalTicks;
    // Represents all time spent running the server this history
    private final long totalTime;
    private final MinuteReport[] minuteReports;

    private final TimingsHistoryEntry[] entries;
    private final ObjectNode levels = Nukkit.JSON_MAPPER.createObjectNode();

    TimingsHistory() {
        this.endTime = System.currentTimeMillis() / 1000;
        this.startTime = TimingsManager.historyStart / 1000;

        if (timedTicks % 1200 != 0 || MINUTE_REPORTS.isEmpty()) {
            this.minuteReports = MINUTE_REPORTS.toArray(new MinuteReport[MINUTE_REPORTS.size() + 1]);
            this.minuteReports[this.minuteReports.length - 1] = new MinuteReport();
        } else {
            this.minuteReports = MINUTE_REPORTS.toArray(new MinuteReport[0]);
        }

        long ticks = 0;
        for (MinuteReport mr : this.minuteReports) {
            ticks += mr.ticksRecord.timed;
        }

        this.totalTicks = ticks;
        this.totalTime = fullServerTickTimer.record.totalTime;
        this.entries = new TimingsHistoryEntry[TimingsManager.TIMINGS.size()];

        int i = 0;
        for (Timing timing : TimingsManager.TIMINGS) {
            this.entries[i++] = new TimingsHistoryEntry(timing);
        }

        final Map<Identifier, AtomicInteger> entityCounts = new HashMap<>();
        final Map<Identifier, AtomicInteger> blockEntityCounts = new HashMap<>();
        // Information about all loaded entities/block entities
        for (Level level : Server.getInstance().getLevels()) {
            ArrayNode jsonLevel = Nukkit.JSON_MAPPER.createArrayNode();
            for (Chunk chunk : level.getChunks()) {
                entityCounts.clear();
                blockEntityCounts.clear();

                //count entities
                for (Entity entity : chunk.getEntities()) {
                    if (!entityCounts.containsKey(entity.getType().getIdentifier()))
                        entityCounts.put(entity.getType().getIdentifier(), new AtomicInteger(0));
                    entityCounts.get(entity.getType().getIdentifier()).incrementAndGet();
                    entityMap.put(entity.getType().getIdentifier(), entity.getClass().getSimpleName());
                }

                //count block entities
                for (BaseBlockEntity blockEntity : chunk.getBlockEntities()) {
                    if (!blockEntityCounts.containsKey(blockEntity.getBlock().getId()))
                        blockEntityCounts.put(blockEntity.getBlock().getId(), new AtomicInteger(0));
                    blockEntityCounts.get(blockEntity.getBlock().getId()).incrementAndGet();
                    blockEntityMap.put(blockEntity.getBlock().getId(), blockEntity.getClass().getSimpleName());
                }

                if (blockEntityCounts.isEmpty() && entityCounts.isEmpty()) {
                    continue;
                }

                ArrayNode jsonChunk = Nukkit.JSON_MAPPER.createArrayNode();
                jsonChunk.add(chunk.getX());
                jsonChunk.add(chunk.getZ());
                jsonChunk.add(JsonUtil.mapToObject(entityCounts.entrySet(), (entry) ->
                        new JsonUtil.JSONPair(entry.getKey().toString(), entry.getValue().get())));
                jsonChunk.add(JsonUtil.mapToObject(blockEntityCounts.entrySet(), (entry) ->
                        new JsonUtil.JSONPair(entry.getKey().toString(), entry.getValue().get())));
                jsonLevel.add(jsonChunk);
            }

            if (!levelMap.containsKey(level.getName())) levelMap.put(level.getName(), levelIdPool++);
            levels.set(String.valueOf(levelMap.get(level.getName())), jsonLevel);
        }
    }

    static void resetTicks(boolean fullReset) {
        if (fullReset) {
            timedTicks = 0;
        }
        lastMinuteTime = System.nanoTime();
        playerTicks = 0;
        tileEntityTicks = 0;
        entityTicks = 0;
        activatedEntityTicks = 0;
    }

    ObjectNode export() {
        ObjectNode json = Nukkit.JSON_MAPPER.createObjectNode();
        json.put("s", this.startTime);
        json.put("e", this.endTime);
        json.put("tk", this.totalTicks);
        json.put("tm", this.totalTime);
        json.set("w", this.levels);
        json.set("h", JsonUtil.mapToArray(this.entries, (entry) -> {
            if (entry.data.count == 0) {
                return null;
            }
            return entry.export();
        }));
        json.set("mp", JsonUtil.mapToArray(this.minuteReports, MinuteReport::export));
        return json;
    }

    static class MinuteReport {
        final long time = System.currentTimeMillis() / 1000;

        final TicksRecord ticksRecord = new TicksRecord();
        final PingRecord pingRecord = new PingRecord();
        final TimingData fst = Timings.fullServerTickTimer.minuteData.clone();
        final double tps = 1E9 / (System.nanoTime() - lastMinuteTime) * this.ticksRecord.timed;
        final double usedMemory = Timings.fullServerTickTimer.avgUsedMemory;
        final double freeMemory = Timings.fullServerTickTimer.avgFreeMemory;
        final double loadAvg = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();

        ArrayNode export() {
            return JsonUtil.toArray(this.time,
                    Math.round(this.tps * 100D) / 100D,
                    Math.round(this.pingRecord.avg * 100D) / 100D,
                    this.fst.export(),
                    JsonUtil.toArray(this.ticksRecord.timed,
                            this.ticksRecord.player,
                            this.ticksRecord.entity,
                            this.ticksRecord.activatedEntity,
                            this.ticksRecord.tileEntity),
                    this.usedMemory,
                    this.freeMemory,
                    this.loadAvg);
        }
    }

    private static class TicksRecord {
        final long timed;
        final long player;
        final long entity;
        final long activatedEntity;
        final long tileEntity;

        TicksRecord() {
            this.timed = timedTicks - (TimingsManager.MINUTE_REPORTS.size() * 1200);
            this.player = playerTicks;
            this.entity = entityTicks;
            this.activatedEntity = activatedEntityTicks;
            this.tileEntity = tileEntityTicks;
        }
    }

    private static class PingRecord {
        final double avg;

        PingRecord() {
            final Collection<Player> onlinePlayers = Server.getInstance().getOnlinePlayers().values();
            int totalPing = 0;
            for (Player player : onlinePlayers) {
                totalPing += player.getPing();
            }

            this.avg = onlinePlayers.isEmpty() ? 0 : (float) totalPing / onlinePlayers.size();
        }
    }
}
