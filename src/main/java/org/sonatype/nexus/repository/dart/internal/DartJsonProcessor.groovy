/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2018-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.repository.dart.internal;

import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.repository.Repository;
import org.sonatype.nexus.repository.view.Payload;
import org.sonatype.nexus.repository.view.payloads.StringPayload;

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

/**
 * Class encapsulating JSON processing for Dart-format repositories, including
 * operations for parsing JSON indexes and rewriting them to be compatible with
 * a proxy repository.
 */
@Named
@Singleton
public class DartJsonProcessor {

    JsonSlurper slurper = new JsonSlurper()

    /**
     * Rewrites URLs of the response of Dart "/api/packages" API to work with current Nexus Dart Proxy Repository
     * 
     * @param repository : current dart repository on Nexus
     * @param payload : response of Dart API
     * @return modified {@link Payload}
     */
    public Payload rewritePackagesJson(final Repository repository, final Payload payload) {
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
    public Payload rewritePackageJson(final Repository repository, final Payload payload) {
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
    public Payload rewriteVersionJson(final Repository repository, final Payload payload) {
        def json = slurper.parse(payload.openInputStream())
        json?.archive_url = rewriteUrl(repository, json?.archive_url)
        return new StringPayload(JsonOutput.toJson(json), payload.getContentType())
    }

    /**
     * Rewrite the given URL to point on the current Nexus Repository
     * 
     * @param repository: current repository
     * @param urlInput: URL to rewrite
     * @return modified URL
     */
    private String rewriteUrl(final Repository repository, String urlInput) {
        if(urlInput) {
            URI uri = new URI(urlInput)
            return repository.getUrl() + uri.getPath()
        }
        return urlInput
    }
}
