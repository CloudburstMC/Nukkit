package cn.nukkit.form.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString
public enum ImageType {

    /**
     * Remote URL
     */
    @JsonProperty("url")
    URL,
    /**
     * Path on the server file system
     */
    @JsonProperty("path")
    FILE;
}
