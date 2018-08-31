package com.nukkitx.api.scoreboard;

import com.nukkitx.api.entity.Entity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Optional;

@ParametersAreNonnullByDefault
public interface Scoreboard {

    void removedObjective(Objective objective);

    @Nonnull
    ObjectiveCriteria createObjectiveCriteria(String name, boolean readOnly);

    @Nonnull
    Optional<ObjectiveCriteria> getCriteria(String name);

    @Nonnull
    Collection<ObjectiveCriteria> getCriterion();

    @Nonnull
    Objective addObjective(String name, String displayName, ObjectiveCriteria criteria);

    @Nonnull
    Optional<Objective> getObjective(String name);

    @Nonnull
    Collection<Objective> getObjectives();

    @Nonnull
    DisplayObjective setDisplayObjective(Objective objective, ObjectiveDisplaySlot displaySlot, ObjectiveSortOrder sortOrder);

    @Nonnull
    Optional<DisplayObjective> getDisplayObjective(String name);

    @Nonnull
    EntityScorer registerScorer(Entity entity);

    @Nonnull
    FakeScorer registerScorer(String string);

    @Nonnull
    Optional<Scorer> getScorer(long id);
}
