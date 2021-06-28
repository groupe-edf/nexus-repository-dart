package org.sonatype.nexus.repository.dart.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Pubspec
 * <p>
 * Dart Pubspec file
 *
 */
@Generated("jsonschema2pojo")
public class DartPackage {

    /**
     * The name of this package. The name is how other packages refer to yours,
     * should you publish it. (Required)
     *
     */
    private String name;
    private String version;
    private String description;
    private List<String> authors = new ArrayList<String>();
    private URI homepage;
    private URI repository;
    private URI issueTracker;
    private URI documentation;
    private Executables executables;
    private String publishTo;
    private Environment environment;
    private Dependencies dependencies;
    @JsonProperty("dev_dependencies")
    private Dependencies devDependencies;
    @JsonProperty("dependencies_overrides")
    private Dependencies dependencyOverrides;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * The name of this package. The name is how other packages refer to yours,
     * should you publish it. (Required)
     *
     */
    public String getName() {
        return name;
    }

    /**
     * The name of this package. The name is how other packages refer to yours,
     * should you publish it. (Required)
     *
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public URI getHomepage() {
        return homepage;
    }

    public void setHomepage(URI homepage) {
        this.homepage = homepage;
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

    public URI getDocumentation() {
        return documentation;
    }

    public void setDocumentation(URI documentation) {
        this.documentation = documentation;
    }

    public Executables getExecutables() {
        return executables;
    }

    public void setExecutables(Executables executables) {
        this.executables = executables;
    }

    public String getPublishTo() {
        return publishTo;
    }

    public void setPublishTo(String publishTo) {
        this.publishTo = publishTo;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
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

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}