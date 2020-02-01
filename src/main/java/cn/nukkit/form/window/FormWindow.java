package cn.nukkit.form.window;

import cn.nukkit.Nukkit;
import cn.nukkit.form.response.FormResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.IOException;

public abstract class FormWindow {

    protected boolean closed = false;

    @JsonIgnore
    public String getJSONData() {
        try {
            return Nukkit.JSON_MAPPER.writeValueAsString(this);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write form window JSON", e);
        }
    }

    public abstract void setResponse(String data);

    @JsonIgnore
    public abstract FormResponse getResponse();

    public boolean wasClosed() {
        return closed;
    }

}
