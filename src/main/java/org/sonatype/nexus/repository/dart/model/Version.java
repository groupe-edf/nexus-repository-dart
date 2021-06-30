package org.sonatype.nexus.repository.dart.model;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "version", "pubspec", "archiveUrl", "packageUrl", "url" })
public class Version {

    @JsonProperty("version")
    private String name;

    @JsonProperty("pubspec")
    private Pubspec pubspec;

    @JsonProperty("archiveUrl")
    private URI archiveUrl;

    @JsonProperty("packageUrl")
    private URI packageUrl;

    @JsonProperty("url")
    private URI url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Pubspec getPubspec() {
        return pubspec;
    }

    public void setPubspec(Pubspec pubspec) {
        this.pubspec = pubspec;
    }

    public URI getArchiveUrl() {
        return archiveUrl;
    }

    public void setArchiveUrl(URI archiveUrl) {
        this.archiveUrl = archiveUrl;
    }

    public URI getPackageUrl() {
        return packageUrl;
    }

    public void setPackageUrl(URI packageUrl) {
        this.packageUrl = packageUrl;
    }

    public URI getUrl() {
        return url;
    }

    public void setUrl(URI url) {
        this.url = url;
    }
}
