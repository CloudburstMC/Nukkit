package cn.nukkit.api.form.element;

import java.util.ArrayList;
import java.util.List;

public class ElementStepSlider extends Element {

    private final String type = "step_slider"; //This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    private String text = "";
    private List<String> steps;
    private int defaultStepIndex = 0;

    public ElementStepSlider(String text) {
        this(text, new ArrayList<>());
    }

    public ElementStepSlider(String text, List<String> steps) {
        this(text, steps, 0);
    }

    public ElementStepSlider(String text, List<String> steps, int defaultStep) {
        this.text = text;
        this.steps = steps;
        this.defaultStepIndex = defaultStep;
    }

    public int getDefaultStepIndex() {
        return defaultStepIndex;
    }

    public void setDefaultOptionIndex(int index) {
        if (index >= steps.size()) return;
        this.defaultStepIndex = index;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void addStep(String step) {
        addStep(step, false);
    }

    public void addStep(String step, boolean isDefault) {
        steps.add(step);
        if (isDefault) this.defaultStepIndex = steps.size() - 1;
    }

}
