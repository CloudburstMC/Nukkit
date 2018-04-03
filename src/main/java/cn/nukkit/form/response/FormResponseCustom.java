package cn.nukkit.form.response;

import java.util.HashMap;

public class FormResponseCustom extends FormResponse {

    private HashMap<Integer, Object> responses = new HashMap<>();
    private HashMap<Integer, FormResponseData> dropdownResponses = new HashMap<>();
    private HashMap<Integer, String> inputResponses = new HashMap<>();
    private HashMap<Integer, Float> sliderResponses = new HashMap<>();
    private HashMap<Integer, FormResponseData> stepSliderResponses = new HashMap<>();
    private HashMap<Integer, Boolean> toggleResponses = new HashMap<>();

    public FormResponseCustom(HashMap<Integer, Object> responses, HashMap<Integer, FormResponseData> dropdownResponses,
                              HashMap<Integer, String> inputResponses, HashMap<Integer, Float> sliderResponses,
                              HashMap<Integer, FormResponseData> stepSliderResponses,
                              HashMap<Integer, Boolean> toggleResponses) {
        this.responses = responses;
        this.dropdownResponses = dropdownResponses;
        this.inputResponses = inputResponses;
        this.sliderResponses = sliderResponses;
        this.stepSliderResponses = stepSliderResponses;
        this.toggleResponses = toggleResponses;
    }

    public HashMap<Integer, Object> getResponses() {
        return responses;
    }

    public Object getResponse(int id) {
        return responses.get(id);
    }

    public FormResponseData getDropdownResponse(int id) {
        return dropdownResponses.get(id);
    }

    public String getInputResponse(int id) {
        return inputResponses.get(id);
    }

    public float getSliderResponse(int id) {
        return sliderResponses.get(id);
    }

    public FormResponseData getStepSliderResponse(int id) {
        return stepSliderResponses.get(id);
    }

    public boolean getToggleResponse(int id) {
        return toggleResponses.get(id);
    }

}
