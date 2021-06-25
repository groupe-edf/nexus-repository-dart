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

import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.sonatype.nexus.repository.http.HttpResponses;
import org.sonatype.nexus.repository.http.HttpStatus;
import org.sonatype.nexus.repository.view.Context;
import org.sonatype.nexus.repository.view.Handler;
import org.sonatype.nexus.repository.view.Response;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Handler that rewrites the content of responses containing Dart provider
 * JSON files so that they point to the proxy repository rather than the
 * repository being proxied.
 */
public class DartProviderHandler implements Handler {
    
    public static final String DO_NOT_REWRITE = "DartProviderHandler.doNotRewrite";

    private final DartJsonProcessor dartJsonProcessor;

    @Inject
    public DartProviderHandler(final DartJsonProcessor dartJsonProcessor) {
        this.dartJsonProcessor = checkNotNull(dartJsonProcessor);
    }

    @Nonnull
    @Override
    public Response handle(@Nonnull final Context context) throws Exception {
        Response response = context.proceed();
        if (!Boolean.parseBoolean(context.getRequest().getAttributes().get(DO_NOT_REWRITE, String.class))) {
            if (response.getStatus().getCode() == HttpStatus.OK && response.getPayload() != null) {
                response = HttpResponses
                        .ok(dartJsonProcessor.rewriteProviderJson(context.getRepository(), response.getPayload()));
            }
        }
        return response;
    }
}
