/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2025-present Sonatype, Inc.
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

import com.google.common.annotations.VisibleForTesting;
import fr.edf.nexus.plugins.repository.dart.AssetKind;
import fr.edf.nexus.plugins.repository.dart.DartContentFacet;
import org.sonatype.nexus.repository.cache.CacheController;
import org.sonatype.nexus.repository.cache.CacheInfo;
import org.sonatype.nexus.repository.content.facet.ContentProxyFacetSupport;
import org.sonatype.nexus.repository.view.Content;
import org.sonatype.nexus.repository.view.Context;
import org.sonatype.nexus.repository.view.Parameters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Named;
import java.io.IOException;

@Named
public class DartProxyFacet
    extends ContentProxyFacetSupport
{

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
  protected @Nonnull CacheController getCacheController(@Nonnull Context context) {
    final AssetKind assetKind = context.getAttributes().require(AssetKind.class);
    return cacheControllerHolder.require(assetKind.getCacheType());
  }

  @Override
  protected @Nullable Content getCachedContent(Context context) {
    AssetKind assetKind = context.getAttributes().require(AssetKind.class);
    String path = checkPath(context);

    switch (assetKind) {
      case PACKAGES_METADATA:
        return null; // never use the cache for /api/packages
      case PACKAGE_METADATA:
      case PACKAGE_VERSION_METADATA:
      case PACKAGE_ARCHIVE:
        return content().get(path).orElse(null);
      default:
        throw new IllegalStateException();
    }
  }

  @Override
  protected String getUrl(@Nonnull Context context) {
    StringBuilder builder = new StringBuilder(context.getRequest().getPath().substring(1));
    Parameters params = context.getRequest().getParameters();

    if (!params.isEmpty()) {
      builder.append('?');
      params.forEach(p -> builder.append(p.getKey()).append('=').append(p.getValue()).append('&'));
      builder.deleteCharAt(builder.lastIndexOf("&"));
    }

    return builder.toString();
  }

  @Override
  protected Content store(Context context, Content content) throws IOException {
    AssetKind assetKind = context.getAttributes().require(AssetKind.class);
    String path = checkPath(context);

    switch (assetKind) {
      case PACKAGES_METADATA:
      case PACKAGE_METADATA:
      case PACKAGE_VERSION_METADATA:
      case PACKAGE_ARCHIVE:
        return content().put(path, content, assetKind);
      default:
        throw new IllegalStateException();
    }
  }

  @Override
  protected void indicateVerified(Context context, Content content, CacheInfo cacheInfo) throws IOException {
    AssetKind assetKind = context.getAttributes().require(AssetKind.class);
    String path = checkPath(context);

    switch (assetKind) {
      case PACKAGES_METADATA:
      case PACKAGE_METADATA:
      case PACKAGE_VERSION_METADATA:
      case PACKAGE_ARCHIVE:
        content().setCacheInfo(path, content, cacheInfo);
        break;
      default:
        throw new IllegalStateException();
    }
  }

  private DartContentFacet content() {
    return getRepository().facet(DartContentFacet.class);
  }

  @VisibleForTesting
  static class NonResolvableProviderJsonException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NonResolvableProviderJsonException(final String message) {
      super(message);
    }
  }

  @VisibleForTesting
  public String checkPath(Context context) {
    String path = context.getRequest().getPath();
    if (path.startsWith("/")) {
      path = path.substring(1);
    }
    return path;
  }
}
