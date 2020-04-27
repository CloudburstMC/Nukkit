package cn.nukkit.form.response;

import cn.nukkit.form.CustomForm;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementDropdown.Response;
import cn.nukkit.form.element.ElementStepSlider;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString(exclude = "form")
public class CustomFormResponse {

    private final CustomForm form;
    private final JsonNode responses;

    private JsonNode get(int index) {
        return responses.get(index);
    }

    public Response getDropdown(int index) {
        JsonNode node = get(index);
        if (!node.isInt()) {
            wrongValue(index, "dropdown");
        }

        return new Response(index, ((ElementDropdown) form.getElement(index)).getDropdownOption(node.asInt()));
    }

    public ElementStepSlider.Response getStepSlider(int index) {
        JsonNode node = get(index);
        if (!node.isInt()) {
            wrongValue(index, "step slider");
        }

        return new ElementStepSlider.Response(index, ((ElementStepSlider) form.getElement(index)).getStep(node.asInt()));
    }

    public String getInput(int index) {
        JsonNode node = get(index);
        if (!node.isTextual()) {
            wrongValue(index, "input");
        }

        return node.asText();
    }

    public float getSlider(int index) {
        JsonNode node = get(index);
        if (!node.isDouble()) {
            wrongValue(index, "slider");
        }

        return (float) node.asDouble();
    }

    public boolean getToggle(int index) {
        JsonNode node = get(index);
        if (!node.isBoolean()) {
            wrongValue(index, "toggle");
        }

        return node.asBoolean();
    }

    private static void wrongValue(int index, String expected) {
        throw new IllegalStateException(String.format("Wrong element at index %d expected '%s'", index, expected));
    }
}
