package fr.edf.nexus.plugins.repository.dart.internal.util

import javax.inject.Named
import javax.inject.Singleton

import org.sonatype.nexus.repository.Repository
import org.sonatype.nexus.repository.view.Payload
import org.sonatype.nexus.repository.view.payloads.StringPayload

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

/**
 * Class encapsulating JSON processing for Dart-format repositories, including
 * operations for parsing JSON indexes and rewriting them to be compatible with
 * a proxy repository.
 *
 * @author Mathieu Delrocq
 */
@Named
@Singleton
class DartJsonProcessor {

    JsonSlurper slurper = new JsonSlurper()

    /**
     * Rewrites URLs of the response of Dart "/api/packages" API to work with current Nexus Dart Proxy Repository
     *
     * @param repository : current dart repository on Nexus
     * @param payload : response of Dart API
     * @return modified {@link Payload}
     */
    Payload rewritePackagesJson(final Repository repository, final Payload payload) {
        def json = slurper.parse(payload.openInputStream())
        json?.next_url = rewriteUrl(repository, json?.next_url)
        json?.packages?.each {
            it.latest?.archive_url = rewriteUrl(repository, it.latest?.archive_url)
            it.latest?.package_url = rewriteUrl(repository, it.latest?.package_url)
            it.latest?.url = rewriteUrl(repository, it.latest?.url)
        }
        return new StringPayload(JsonOutput.toJson(json), payload.getContentType())
    }

    /**
     * Rewrites URLs of the response of Dart "/api/packages/{packageName}" API to work with current Nexus Dart Proxy Repository
     *
     * @param repository : current dart repository on Nexus
     * @param payload : response of Dart API
     * @return modified {@link Payload}
     */
    Payload rewritePackageJson(final Repository repository, final Payload payload) {
        def json = slurper.parse(payload.openInputStream())
        json?.latest?.archive_url = rewriteUrl(repository, json?.latest?.archive_url)
        json?.versions?.each {
            it.archive_url = rewriteUrl(repository, it.archive_url)
        }
        return new StringPayload(JsonOutput.toJson(json), payload.getContentType())
    }

    /**
     * Rewrites URLs of the response of Dart "/api/packages/{packageName}/versions/{versionNum}" API to work with current Nexus Dart Proxy Repository
     *
     * @param repository : current dart repository on Nexus
     * @param payload : response of Dart API
     * @return modified {@link Payload}
     */
    Payload rewriteVersionJson(final Repository repository, final Payload payload) {
        def json = slurper.parse(payload.openInputStream())
        json?.archive_url = rewriteUrl(repository, json?.archive_url)
        return new StringPayload(JsonOutput.toJson(json), payload.getContentType())
    }

    /**
     * Rewrite the given URL to point on the current Nexus Repository
     *
     * @param repository: current repository
     * @param urlInput: URL to rewrite
     * @return modified URL as String
     */
    private String rewriteUrl(final Repository repository, String urlInput) {
        if(urlInput) {
            URI input = new URI(urlInput)
            URI repositoryUrl = new URI(repository.getUrl())
            URI output = new URI(
                    repositoryUrl.scheme,
                    repositoryUrl.userInfo,
                    repositoryUrl.host,
                    repositoryUrl.port,
                    repositoryUrl.path + input.path,
                    input.query,
                    input.fragment)
            return output.toURL().toString()
        }
        return urlInput
    }
}
