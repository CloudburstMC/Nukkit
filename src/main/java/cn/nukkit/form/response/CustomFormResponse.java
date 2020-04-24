package cn.nukkit.form.response;

import cn.nukkit.form.CustomForm;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementDropdown.Response;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class CustomFormResponse {

    private final CustomForm form;
    private final List<Serializable> responses;

    @SuppressWarnings("unchecked")
    public <T> T get(int index) {
        return (T) responses.get(index);
    }

    public String getLabel(int index) {
        return Objects.toString(responses.get(index));
    }

    public Response getDropdown(int index) {
        return new Response(index, ((ElementDropdown) form.getElement(index)).getDropdownOption((int) responses.get(index)));
    }
}
