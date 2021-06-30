package org.sonatype.nexus.repository.dart.model;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "name", "description", "version", "homepage", "environment", "executables", "dependencies",
        "dev_dependencies", "dependency_overrides", "repository", "issue_tracker", "archive_url", "published" })
public class Pubspec {

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("version")
    private String version;

    @JsonProperty("homepage")
    private URI homepage;

    @JsonProperty("environment")
    private Environment environment;

    @JsonProperty("executables")
    private Executables executables;

    @JsonProperty("dependencies")
    private Dependencies dependencies;

    @JsonProperty("dev_dependencies")
    private Dependencies devDependencies;

    @JsonProperty("dependency_overrides")
    private Dependencies dependencyOverrides;

    @JsonProperty("repository")
    private URI repository;

    @JsonProperty("issue_tracker")
    private URI issueTracker;

    @JsonProperty("archive_url")
    private URI archiveUrl;

    @JsonProperty("published")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
    private LocalDateTime published;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public URI getHomepage() {
        return homepage;
    }

    public void setHomepage(URI homepage) {
        this.homepage = homepage;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Executables getExecutables() {
        return executables;
    }

    public void setExecutables(Executables executables) {
        this.executables = executables;
    }

    public Dependencies getDependencies() {
        return dependencies;
    }

    public void setDependencies(Dependencies dependencies) {
        this.dependencies = dependencies;
    }

    public Dependencies getDevDependencies() {
        return devDependencies;
    }

    public void setDevDependencies(Dependencies devDependencies) {
        this.devDependencies = devDependencies;
    }

    public Dependencies getDependencyOverrides() {
        return dependencyOverrides;
    }

    public void setDependencyOverrides(Dependencies dependencyOverrides) {
        this.dependencyOverrides = dependencyOverrides;
    }

    public URI getRepository() {
        return repository;
    }

    public void setRepository(URI repository) {
        this.repository = repository;
    }

    public URI getIssueTracker() {
        return issueTracker;
    }

    public void setIssueTracker(URI issueTracker) {
        this.issueTracker = issueTracker;
    }

    public URI getArchiveUrl() {
        return archiveUrl;
    }

    public void setArchiveUrl(URI archiveUrl) {
        this.archiveUrl = archiveUrl;
    }

    public LocalDateTime getPublished() {
        return published;
    }

    public void setPublished(LocalDateTime published) {
        this.published = published;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

}
