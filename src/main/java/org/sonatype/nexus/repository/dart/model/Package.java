package org.sonatype.nexus.repository.dart.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "name", "latest", "versions" })
public class Package {

    @JsonProperty("name")
    private String name;

    @JsonProperty("latest")
    private Version latest;

    @JsonProperty("versions")
    private List<Version> versions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Version getLatest() {
        return latest;
    }

    public void setLatest(Version latest) {
        this.latest = latest;
    }

    public List<Version> getVersions() {
        return versions;
    }

    public void setVersions(List<Version> versions) {
        this.versions = versions;
    }

}
