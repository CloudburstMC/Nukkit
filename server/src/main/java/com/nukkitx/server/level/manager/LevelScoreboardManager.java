package com.nukkitx.server.level.manager;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.nukkitx.api.Player;
import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.scoreboard.*;
import com.nukkitx.protocol.bedrock.data.ScoreInfo;
import com.nukkitx.protocol.bedrock.packet.RemoveObjectivePacket;
import com.nukkitx.protocol.bedrock.packet.SetDisplayObjectivePacket;
import com.nukkitx.protocol.bedrock.packet.SetScorePacket;
import com.nukkitx.server.level.NukkitLevel;
import com.nukkitx.server.scoreboard.*;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@ParametersAreNonnullByDefault
public class LevelScoreboardManager implements Scoreboard {
    private static final TLongObjectMap<NukkitScorer> scorers = new TLongObjectHashMap<>();
    private static final AtomicInteger scorerAllocator = new AtomicInteger();
    private final Map<String, NukkitObjective> objectives = new HashMap<>();
    private final List<NukkitDisplayObjective> displayObjectives = new ArrayList<>();
    private final Map<String, NukkitObjectiveCriteria> criterion = new HashMap<>();
    private final Queue<ScoreInfo> setScoreQueue = new ArrayDeque<>();
    private final Queue<ScoreInfo> removeScoreQueue = new ArrayDeque<>();
    private final NukkitLevel level;

    public LevelScoreboardManager(NukkitLevel level) {
        this.level = level;
        createObjectiveCriteria("dummy", false);
    }

    @Override
    public void removedObjective(Objective objective) {
        Preconditions.checkNotNull(objective, "objective");
        if (objectives.remove(objective.getName()) == null) {
            return;
        }

        displayObjectives.removeIf(displayObjective -> displayObjective.getObjective() == objective);

        // Remove objective for all players in the level.
        RemoveObjectivePacket removeObjective = new RemoveObjectivePacket();
        removeObjective.setObjectiveId(objective.getName());
        level.getPacketManager().queuePacketForPlayers(removeObjective);
    }

    @Nonnull
    @Override
    public NukkitObjectiveCriteria createObjectiveCriteria(String name, boolean readOnly) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "name is null or empty");
        Preconditions.checkArgument(!criterion.containsKey(name), "criteria already registered");
        name = name.toLowerCase();

        NukkitObjectiveCriteria criteria = new NukkitObjectiveCriteria(name, readOnly);
        criterion.put(name, criteria);

        return criteria;
    }

    @Nonnull
    @Override
    public Optional<ObjectiveCriteria> getCriteria(String name) {
        Preconditions.checkNotNull(name, "name");
        return Optional.ofNullable(criterion.get(name.toLowerCase()));
    }

    @Nonnull
    @Override
    public Collection<ObjectiveCriteria> getCriterion() {
        return ImmutableList.copyOf(criterion.values());
    }

    @Nonnull
    @Override
    public Objective addObjective(String name, String displayName, ObjectiveCriteria criteria) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "name is null or empty");
        Preconditions.checkNotNull(displayName, "displayName");
        Preconditions.checkNotNull(criteria, "criteria");

        name = name.toLowerCase();

        synchronized (objectives) {
            Preconditions.checkArgument(!objectives.containsKey(name), "objective name already registered");

            NukkitObjective objective = new NukkitObjective(this, name, criteria);
            objective.setDisplayName(displayName);

            objectives.put(name, objective);
            return objective;
        }
    }

    @Nonnull
    @Override
    public Optional<Objective> getObjective(String name) {
        Preconditions.checkNotNull(name, "name");
        synchronized (objectives) {
            return Optional.ofNullable(objectives.get(name.toLowerCase()));
        }
    }

    @Nonnull
    @Override
    public Collection<Objective> getObjectives() {
        synchronized (objectives) {
            return ImmutableList.copyOf(objectives.values());
        }
    }

    @Nonnull
    @Override
    public DisplayObjective setDisplayObjective(Objective objective, ObjectiveDisplaySlot displaySlot, ObjectiveSortOrder sortOrder) {
        Preconditions.checkNotNull(objective, "objective");
        Preconditions.checkNotNull(displaySlot, "displaySlot");
        Preconditions.checkNotNull(sortOrder, "sortOrder");

        DisplayObjective displayObjective = null;
        int index = -1;
        for (NukkitDisplayObjective disObj : displayObjectives) {
            if (disObj.getDisplaySlot() == displaySlot) {
                displayObjective = disObj;
                index = displayObjectives.lastIndexOf(disObj);
                break;
            }
        }

        NukkitDisplayObjective newDisplayObjective;

        if (displayObjective != null) {
            if (displayObjective.getObjective() == objective && displayObjective.getSortOrder() == sortOrder) {
                return displayObjective;
            } else {
                newDisplayObjective = new NukkitDisplayObjective(displayObjective.getObjective(), displaySlot, sortOrder);
                addDisplayObjective(newDisplayObjective, index);
            }
        } else {
            newDisplayObjective = new NukkitDisplayObjective(objective, displaySlot, sortOrder);
            addDisplayObjective(newDisplayObjective, index);
        }
        return newDisplayObjective;
    }

    private void addDisplayObjective(NukkitDisplayObjective displayObjective, int index) {
        if (index < 0) {
            displayObjectives.add(displayObjective);
        } else {
            displayObjectives.set(index, displayObjective);
        }

        SetDisplayObjectivePacket setDisplayObjective = new SetDisplayObjectivePacket();
        setDisplayObjective.setObjectiveId(displayObjective.getObjective().getName());
        setDisplayObjective.setDisplayName(displayObjective.getObjective().getDisplayName());
        setDisplayObjective.setCriteria(displayObjective.getObjective().getCriteria().getName());
        setDisplayObjective.setDisplaySlot(displayObjective.getDisplaySlot().name().toLowerCase());
        setDisplayObjective.setSortOrder(displayObjective.getSortOrder().ordinal());

        level.getPacketManager().queuePacketForPlayers(setDisplayObjective);
    }

    @Nonnull
    @Override
    public Optional<DisplayObjective> getDisplayObjective(String name) {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public NukkitEntityScorer registerScorer(Entity entity) {
        Preconditions.checkNotNull(entity, "entity");

        NukkitEntityScorer scorer = null;
        for (NukkitScorer s : scorers.valueCollection()) {
            if (s instanceof NukkitEntityScorer && ((NukkitEntityScorer) s).getEntity() == entity) {
                scorer = (NukkitEntityScorer) s;
                break;
            }
        }

        if (scorer == null) {
            Scorer.Type type = entity instanceof Player ? Scorer.Type.PLAYER : Scorer.Type.ENTITY;
            scorer = new NukkitEntityScorer(type, scorerAllocator.getAndIncrement(), entity);
            scorers.put(scorer.getId(), scorer);
        }
        return scorer;
    }

    @Nonnull
    @Override
    public NukkitFakeScorer registerScorer(String name) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "name is null or empty");

        NukkitFakeScorer scorer = null;
        for (NukkitScorer s : scorers.valueCollection()) {
            if (s instanceof NukkitFakeScorer && ((NukkitFakeScorer) s).getName().equals(name)) {
                scorer = (NukkitFakeScorer) s;
                break;
            }
        }

        if (scorer == null) {
            scorer = new NukkitFakeScorer(Scorer.Type.FAKE, scorerAllocator.getAndIncrement(), name);
            scorers.put(scorer.getId(), scorer);
        }
        return scorer;
    }

    @Nonnull
    @Override
    public Optional<Scorer> getScorer(long id) {
        return Optional.ofNullable(scorers.get(id));
    }

    private Optional<NukkitScorer> getNukkitScorer(long id) {
        return Optional.ofNullable(scorers.get(id));
    }

    public void setScore(long id, NukkitObjective objective, int score) {
        Preconditions.checkNotNull(objective, "objective");

        Scorer scorer = scorers.get(id);

        if (scorer instanceof FakeScorer) {
            setScoreQueue.add(new ScoreInfo(scorer.getId(), objective.getName(), score, ((FakeScorer) scorer).getName()));
        } else if (scorer instanceof EntityScorer) {
            ScoreInfo.ScorerType type = ScoreInfo.ScorerType.valueOf(scorer.getType().name());
            setScoreQueue.add(new ScoreInfo(scorer.getId(), objective.getName(), score, type, ((EntityScorer) scorer).getEntity().getEntityId()));
        }
    }

    public void removeScore(long id, NukkitObjective objective, int score) {
        Preconditions.checkNotNull(objective, "objective");

        getNukkitScorer(id).ifPresent(scorer -> removeScoreQueue.add(new ScoreInfo(scorer.getId(), objective.getName(), score)));
    }

    private static long stringHash(String string) {
        char[] chars = string.toCharArray();
        long hashCode = 0;

        for (char c : chars) {
            hashCode = 31 * hashCode + c;
        }

        return hashCode;
    }

    public void onTick() {
        synchronized (setScoreQueue) {
            if (!setScoreQueue.isEmpty()) {
                SetScorePacket setScore = new SetScorePacket();
                setScore.setAction(SetScorePacket.Action.SET);

                setScore.getInfos().addAll(setScoreQueue);
                setScoreQueue.clear();
            }
        }

        synchronized (removeScoreQueue) {
            if (!removeScoreQueue.isEmpty()) {
                SetScorePacket setScore = new SetScorePacket();
                setScore.setAction(SetScorePacket.Action.REMOVE);

                setScore.getInfos().addAll(removeScoreQueue);
                removeScoreQueue.clear();
            }
        }
    }
}
