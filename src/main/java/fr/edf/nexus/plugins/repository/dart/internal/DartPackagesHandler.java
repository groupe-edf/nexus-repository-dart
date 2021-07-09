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
package fr.edf.nexus.plugins.repository.dart.internal;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.sonatype.nexus.repository.http.HttpResponses;
import org.sonatype.nexus.repository.http.HttpStatus;
import org.sonatype.nexus.repository.view.Context;
import org.sonatype.nexus.repository.view.Handler;
import org.sonatype.nexus.repository.view.Response;

/**
 * Handler that rewrites the content of responses containing Dart provider JSON
 * files so that they point to the proxy repository rather than the repository
 * being proxied.
 * 
 * @author Mathieu Delrocq
 */
public class DartPackagesHandler implements Handler {

    public static final String DO_NOT_REWRITE = "DartProviderHandler.doNotRewrite";

    private final DartJsonProcessor dartJsonProcessor;

    @Inject
    public DartPackagesHandler(DartJsonProcessor dartJsonProcessor) {
        this.dartJsonProcessor = dartJsonProcessor;
    }

    /**
     * Intercept requests for dart APIs calls and rewrite urls in json response
     * 
     * @param context: {@link Context} of the request
     * @return modified response
     */
    @Nonnull
    @Override
    public Response handle(@Nonnull final Context context) throws Exception {
        Response response = context.proceed();
        AssetKind assetKind = context.getAttributes().require(AssetKind.class);
        if (!Boolean.parseBoolean(context.getRequest().getAttributes().get(DO_NOT_REWRITE, String.class))
                && response.getStatus().getCode() == HttpStatus.OK && response.getPayload() != null) {
            switch (assetKind) {
            case PACKAGES_METADATA:
                response = HttpResponses
                        .ok(dartJsonProcessor.rewritePackagesJson(context.getRepository(), response.getPayload()));
                break;
            case PACKAGE_METADATA:
                response = HttpResponses
                        .ok(dartJsonProcessor.rewritePackageJson(context.getRepository(), response.getPayload()));
                break;
            case PACKAGE_VERSION_METADATA:
                response = HttpResponses
                        .ok(dartJsonProcessor.rewriteVersionJson(context.getRepository(), response.getPayload()));
                break;
            default:
                throw new IllegalStateException(
                        "Unexpected asset kind in DartPackagesHandler : " + assetKind + ". Cannot rewrite JSON");
            }
        }
        return response;
    }
}
