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

import java.io.IOException;

import javax.annotation.Nullable;
import javax.inject.Named;

import org.sonatype.nexus.repository.cache.CacheInfo;
import org.sonatype.nexus.repository.proxy.ProxyFacetSupport;
import org.sonatype.nexus.repository.view.Content;
import org.sonatype.nexus.repository.view.Context;

import com.google.common.annotations.VisibleForTesting;

/**
 * Proxy facet for a Dart repository.
 */
@Named
public class DartProxyFacetImpl extends ProxyFacetSupport {

    @Nullable
    @Override
    protected Content fetch(Context context, Content stale) throws IOException {
        try {
            return super.fetch(context, stale);
        } catch (NonResolvableProviderJsonException e) {
            log.debug("Dart provider URL not resolvable: {}", e.getMessage());
            return null;
        }
    }

    @Override
    protected Content getCachedContent(Context context) throws IOException {
        return content().get(context.getRepository().getUrl());
    }

    @Override
    protected Content store(Context context, Content content) throws IOException {
        return content().put(context.getRepository().getUrl(), content);
    }

    @Override
    protected void indicateVerified(Context context, Content content, CacheInfo cacheInfo) throws IOException {
        content().setCacheInfo(context.getRepository().getUrl(), content, cacheInfo);
    }

    @Override
    protected String getUrl(Context context) {
        return context.getRequest().getPath().substring(1);
    }

    private DartContentFacet content() {
        return getRepository().facet(DartContentFacet.class);
    }

    @VisibleForTesting
    static class NonResolvableProviderJsonException extends RuntimeException {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public NonResolvableProviderJsonException(final String message) {
            super(message);
        }
    }

}
