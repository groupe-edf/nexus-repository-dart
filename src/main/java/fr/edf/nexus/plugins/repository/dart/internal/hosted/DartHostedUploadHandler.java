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
package fr.edf.nexus.plugins.repository.dart.internal.hosted;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.sonatype.goodies.common.Loggers;
import org.sonatype.nexus.repository.http.HttpResponses;
import org.sonatype.nexus.repository.view.Context;
import org.sonatype.nexus.repository.view.Handler;
import org.sonatype.nexus.repository.view.PartPayload;
import org.sonatype.nexus.repository.view.Request;
import org.sonatype.nexus.repository.view.Response;

import com.google.common.base.Preconditions;

import fr.edf.nexus.plugins.repository.dart.internal.DartAttributes;

/**
 * Upload handler for Dart hosted repositories.
 */
@Named
@Singleton
public class DartHostedUploadHandler implements Handler {

    protected static final Logger log = Preconditions.checkNotNull(Loggers.getLogger(DartHostedUploadHandler.class));

    @Nonnull
    @Override
    public Response handle(@Nonnull final Context context) throws Exception {
        Request request = context.getRequest();
        String completePath = request.getPath();
        String path = completePath.substring(completePath.lastIndexOf('/'), completePath.length());
        switch (path) {
        case DartAttributes.PUBLISH_PATH:
            return publish();
        case DartAttributes.UPLOAD_MULTIPART_PATH:
            return uploadMultipart(null);
        case DartAttributes.FINALIZE_UPLOAD_PATH:
            return finalizeUpload();
        default:
            return HttpResponses.badRequest("Route specified not found : " + completePath);
        }
    }

    private Response publish() {
        return HttpResponses.ok();
    }

    private Response uploadMultipart(final PartPayload in) throws IOException {
        return HttpResponses.ok();
    }

    private Response finalizeUpload() {
        return HttpResponses.ok();
    }

}
