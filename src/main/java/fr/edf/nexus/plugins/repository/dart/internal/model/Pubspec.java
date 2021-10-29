package fr.edf.nexus.plugins.repository.dart.internal.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties
public class Pubspec {

    @JsonProperty(value = "name", required = true)
    private String name;

    @JsonProperty(value = "version", required = true)
    private String version;

    @JsonProperty(value = "description", required = true)
    private String description;

    @JsonProperty(value = "homepage")
    private String homepage;

    @JsonProperty(value = "repository")
    private String repository;

    @JsonProperty(value = "issue_tracker")
    private String issueTracker;

    @JsonProperty(value = "documentation")
    private String documentation;

    @JsonProperty("executables")
    private Map<String, Object> executables;

    @JsonProperty("publish_to")
    private String publishTo;

    @JsonProperty("environment")
    private Map<String, Object> environment;

    @JsonProperty("dependencies")
    private Map<String, Object> dependencies;

    @JsonProperty("dev_dependencies")
    private Map<String, Object> devDependencies;

    @JsonProperty("dependency_overrides")
    private Map<String, Object> dependencyOverrides;

}
