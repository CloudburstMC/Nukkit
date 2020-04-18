package cn.nukkit.form.element;

import com.google.common.base.Preconditions;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@ToString
public final class ElementStepSlider extends Element {

    private final List<String> stepOptions = new ArrayList<>();
    private int defaultStepIndex = 0;

    public ElementStepSlider(@Nonnull String elementId, @Nonnull String elementText) {
        super(ElementType.STEP_SLIDER, elementId, elementText);
    }

    public ElementStepSlider(@Nonnull String elementId, @Nonnull String elementText, @Nonnull List<String> stepOptions) {
        super(ElementType.STEP_SLIDER, elementId, elementText);
        Preconditions.checkNotNull(stepOptions, "The provided step options can not be null");

        this.stepOptions.addAll(stepOptions);
    }

    public ElementStepSlider(@Nonnull String elementId, @Nonnull String elementText, @Nonnull List<String> stepOptions, int defaultStepIndex) {
        super(ElementType.STEP_SLIDER, elementId, elementText);
        Preconditions.checkNotNull(stepOptions, "The provided step options can not be null");

        this.stepOptions.addAll(stepOptions);
        this.defaultStepIndex = defaultStepIndex;
    }

    @Nonnull
    public List<String> getStepOptions() {
        return this.stepOptions;
    }

    @Nonnull
    public ElementStepSlider addStepOptions(@Nonnull List<String> stepOptions) {
        Preconditions.checkNotNull(stepOptions, "The provided step options can not be null");

        this.stepOptions.addAll(stepOptions);
        return this;
    }

    @Nonnull
    public ElementStepSlider addStepOption(@Nonnull String stepOption) {
        Preconditions.checkNotNull(stepOption, "The provided step option can not be null");

        this.stepOptions.add(stepOption);
        return this;
    }

    public int getDefaultStepIndex() {
        return this.defaultStepIndex;
    }

    @Nonnull
    public ElementStepSlider defaultIndex(int defaultStepIndex) {
        this.defaultStepIndex = defaultStepIndex;
        return this;
    }

    @Nonnull
    public ElementStepSlider defaultIndex(@Nonnull String stepOption) {
        Preconditions.checkNotNull(stepOption, "The provided step option can not be null");

        if (this.stepOptions.contains(stepOption)) {
            this.defaultStepIndex = this.stepOptions.indexOf(stepOption);
        } else {
            this.stepOptions.add(stepOption);
            this.defaultStepIndex = this.stepOptions.size() - 1;
        }
        return this;
    }
}
