package org.sonatype.nexus.repository.dart.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "sdk", "flutter" })
public class Environment {

    @JsonProperty("sdk")
    private String sdk;

    @JsonProperty("flutter")
    private String flutter;

    public String getSdk() {
        return sdk;
    }

    public void setSdk(String sdk) {
        this.sdk = sdk;
    }

    public String getFlutter() {
        return flutter;
    }

    public void setFlutter(String flutter) {
        this.flutter = flutter;
    }

}
