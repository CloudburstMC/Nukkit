package cn.nukkit.server.form.element;

import java.util.ArrayList;
import java.util.List;

public class ElementDropdown extends Element {

    private final String type = "dropdown"; //This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    private String text = "";
    private List<String> options;
    private int defaultOptionIndex = 0;

    public ElementDropdown(String text) {
        this(text, new ArrayList<>());
    }

    public ElementDropdown(String text, List<String> options) {
        this(text, options, 0);
    }

    public ElementDropdown(String text, List<String> options, int defaultOption) {
        this.text = text;
        this.options = options;
        this.defaultOptionIndex = defaultOption;
    }

    public int getDefaultOptionIndex() {
        return defaultOptionIndex;
    }

    public void setDefaultOptionIndex(int index) {
        if (index >= options.size()) return;
        this.defaultOptionIndex = index;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addOption(String option) {
        addOption(option, false);
    }

    public void addOption(String option, boolean isDefault) {
        options.add(option);
        if (isDefault) this.defaultOptionIndex = options.size() - 1;
    }

}
