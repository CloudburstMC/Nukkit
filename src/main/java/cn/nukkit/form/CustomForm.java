package cn.nukkit.form;

import cn.nukkit.form.element.Element;
import cn.nukkit.form.response.CustomFormResponse;
import cn.nukkit.form.util.FormType;
import cn.nukkit.player.Player;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.*;

@Getter
public class CustomForm extends Form {

    @JsonProperty("content")
    private final List<Element> elements;

    public CustomForm(String title, List<Element> elements) {
        super(FormType.CUSTOM, title);
        this.elements = elements;
    }

    @Override
    public void handleResponse(Player p, String data) {

    }

    public static class CustomFormBuilder extends FormBuilder<CustomForm, CustomFormBuilder, CustomFormResponse> {

        private final List<Element> elements = new ArrayList<>();

        public CustomFormBuilder element(@Nonnull Element element) {
            elements.add(element);
            return this;
        }

        public CustomFormBuilder elements(@Nonnull Element element, @Nonnull Element... elements) {
            this.elements.add(element);
            this.elements.addAll(Arrays.asList(elements));
            return this;
        }

        public CustomFormBuilder elements(@Nonnull Collection<Element> elements) {
            this.elements.addAll(elements);
            return this;
        }

        @Override
        public CustomForm build() {
            return new CustomForm(title, Collections.unmodifiableList(elements));
        }
    }
}
