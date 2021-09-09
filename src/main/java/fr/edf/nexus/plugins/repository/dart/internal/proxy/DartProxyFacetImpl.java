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
package fr.edf.nexus.plugins.repository.dart.internal.proxy;

import java.io.IOException;

import javax.annotation.Nullable;
import javax.inject.Named;

import org.sonatype.nexus.repository.cache.CacheInfo;
import org.sonatype.nexus.repository.proxy.ProxyFacetSupport;
import org.sonatype.nexus.repository.storage.Asset;
import org.sonatype.nexus.repository.storage.StorageTx;
import org.sonatype.nexus.repository.transaction.TransactionalTouchMetadata;
import org.sonatype.nexus.repository.view.Content;
import org.sonatype.nexus.repository.view.Context;
import org.sonatype.nexus.repository.view.Parameters;
import org.sonatype.nexus.transaction.UnitOfWork;

import com.google.common.annotations.VisibleForTesting;

import fr.edf.nexus.plugins.repository.dart.internal.AssetKind;
import fr.edf.nexus.plugins.repository.dart.internal.DartFacet;

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
        AssetKind assetKind = context.getAttributes().require(AssetKind.class);
        String path = checkPath(context);
        switch (assetKind) {
        case PACKAGES_METADATA:
            return null; // never use the cache for /api/packages
        case PACKAGE_METADATA:
            return getDartFacet().get(path);
        case PACKAGE_VERSION_METADATA:
            return getDartFacet().get(path);
        case PACKAGE_ARCHIVE:
            return getDartFacet().get(path);
        default:
            throw new IllegalStateException();
        }
    }

    @Override
    protected Content store(Context context, Content content) throws IOException {
        AssetKind assetKind = context.getAttributes().require(AssetKind.class);
        String path = checkPath(context);
        switch (assetKind) {
        case PACKAGES_METADATA:
            return getDartFacet().put(path, content, assetKind);
        case PACKAGE_METADATA:
            return getDartFacet().put(path, content, assetKind);
        case PACKAGE_VERSION_METADATA:
            return getDartFacet().put(path, content, assetKind);
        case PACKAGE_ARCHIVE:
            return getDartFacet().put(path, content, assetKind);
        default:
            throw new IllegalStateException();
        }
    }

    @Override
    @TransactionalTouchMetadata
    protected void indicateVerified(Context context, Content content, CacheInfo cacheInfo) throws IOException {
        StorageTx tx = UnitOfWork.currentTx();
        Asset asset = Content.findAsset(tx, tx.findBucket(getRepository()), content);
        if (asset == null) {
            log.debug("Attempting to set cache info for non-existent Dart asset {}", context.getRequest().getPath());
            return;
        }
        log.debug("Updating cacheInfo of {} to {}", asset, cacheInfo);
        CacheInfo.applyToAsset(asset, cacheInfo);
        tx.saveAsset(asset);
    }

    @Override
    protected String getUrl(Context context) {
        StringBuilder builder = new StringBuilder(context.getRequest().getPath().substring(1));
        Parameters params = context.getRequest().getParameters();
        if (!params.isEmpty()) {
            builder.append('?');
            params.forEach(p -> builder.append(p.getKey()).append('=').append(p.getValue()).append('&'));
            builder.deleteCharAt(builder.lastIndexOf("&"));
        }
        return builder.toString();
    }

    /**
     * Return the Facet associated with the repository
     * 
     * @return {@link DartFacet}
     */
    private DartFacet getDartFacet() {
        return getRepository().facet(DartFacet.class);
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

    /**
     * Remove the '/' at the start of the path to save asset at the root of the
     * repository.
     * 
     * @param path
     * @return path which not start with '/'
     */
    @VisibleForTesting
    public String checkPath(Context context) {
        String path = context.getRequest().getPath();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }

}
