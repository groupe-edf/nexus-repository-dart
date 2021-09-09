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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.sonatype.goodies.common.Loggers;
import org.sonatype.nexus.repository.http.HttpResponses;
import org.sonatype.nexus.repository.view.Context;
import org.sonatype.nexus.repository.view.Handler;
import org.sonatype.nexus.repository.view.PartPayload;
import org.sonatype.nexus.repository.view.Response;
import org.sonatype.nexus.repository.view.payloads.BytesPayload;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;

/**
 * Upload handler for Composer hosted repositories.
 */
@Named
@Singleton
public class DartHostedUploadHandler implements Handler {

    protected static final Logger log = Preconditions.checkNotNull(Loggers.getLogger(DartHostedUploadHandler.class));

    @Nonnull
    @Override
    public Response handle(@Nonnull final Context context) throws Exception {

        return HttpResponses.notFound();
    }

    private BytesPayload readPartStreamToBytePayload(final PartPayload in) throws IOException {
        try (InputStream is = in.openInputStream()) {
            return new BytesPayload(ByteStreams.toByteArray(is), in.getContentType());
        } finally {
            in.close();
        }
    }

    private String readPartStreamToString(final PartPayload in) throws IOException {
        try {
            return CharStreams.toString(new InputStreamReader(in.openInputStream(), UTF_8));
        } finally {
            in.close();
        }
    }

}
