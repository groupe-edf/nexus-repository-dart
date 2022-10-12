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
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.sonatype.goodies.common.Loggers;
import org.sonatype.nexus.repository.http.HttpResponses;
import org.sonatype.nexus.repository.http.HttpStatus;
import org.sonatype.nexus.repository.view.Context;
import org.sonatype.nexus.repository.view.Handler;
import org.sonatype.nexus.repository.view.PartPayload;
import org.sonatype.nexus.repository.view.Payload;
import org.sonatype.nexus.repository.view.Request;
import org.sonatype.nexus.repository.view.Response;
import org.sonatype.nexus.repository.view.Status;
import org.sonatype.nexus.repository.view.payloads.StringPayload;

import com.google.common.base.Preconditions;

import fr.edf.nexus.plugins.repository.dart.internal.DartAttributes;

/**
 * Upload handler for Dart hosted repositories.
 */
@Named
@Singleton
public class DartHostedUploadHandler implements Handler {

    private static final String PUB_V2_CONTENT_TYPE = "application/vnd.pub.v2+json";

    private List<String> uploadIds = new ArrayList<>();

    protected static final Logger log = Preconditions.checkNotNull(Loggers.getLogger(DartHostedUploadHandler.class));

    @Nonnull
    @Override
    public Response handle(@Nonnull final Context context) throws Exception {
        Request request = context.getRequest();
        String completePath = request.getPath();
        String path = completePath.substring(completePath.lastIndexOf('/')+1, completePath.length());
        switch (path) {
        case DartAttributes.PUBLISH_PATH:
            return publish(context);
        case DartAttributes.UPLOAD_MULTIPART_PATH:
            return uploadMultipart(completePath, context.getRequest().getMultiparts());
        case DartAttributes.FINALIZE_UPLOAD_PATH:
            return finalizeUpload(completePath);
        default:
            return HttpResponses.badRequest("nexus-repository-dart : Route specified not found : " + completePath);
        }
    }

    /**
     * 
     * @return {@link Response}
     */
    private Response publish(@Nonnull final Context context) {
        String uploadId = RandomStringUtils.random(10, true, true);
        uploadIds.add(uploadId);
        String url = new StringBuilder(context.getRepository().getUrl()).append("/api/").append(uploadId).append("/multipart").toString();
        String responseEntity = ""//
                + "{"//
                + "\"url\":\"" + url + "\","
                + "\"fields\":{}"//
                + "}";//
        Payload payload = new StringPayload(responseEntity, PUB_V2_CONTENT_TYPE);
        return new Response.Builder().status(new Status(true, HttpStatus.OK)).payload(payload).build();
    }

    /**
     * 
     * @param path : path called in the request. Contains uploadId
     * @param multipart : Multipart content of the request
     * @return {@link Response}
     * @throws IOException
     */
    private Response uploadMultipart(String path, final Iterable<PartPayload> multipart) throws IOException {
        if (!uploadIds.contains(path.split("/")[2].trim())) {
            return HttpResponses.forbidden();
        }
        String location = "";
        return new Response.Builder().status(new Status(true, HttpStatus.NO_CONTENT)).header("Location", location)
                .build();
    }

    /**
     * 
     * @param path : path called in the request. Contains archiveUrl
     * @return {@link Response}
     */
    private Response finalizeUpload(String path) {
        if (!uploadIds.contains(path.split("/")[1])) {
            return HttpResponses.forbidden();
        }
        String archiveUrl = "";
        String responseEntity = ""//
                + "{"//
                + "\"success\": {"//
                + "\"message\": \"Upload of " + archiveUrl + " succeeded\""//
                + "}"//
                + "}";//
        Payload payload = new StringPayload(responseEntity, PUB_V2_CONTENT_TYPE);
        return new Response.Builder().status(new Status(true, HttpStatus.OK)).payload(payload).build();
    }
}
