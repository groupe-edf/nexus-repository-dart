package fr.edf.nexus.plugins.repository.dart.internal;

import javax.annotation.Nonnull;

import org.sonatype.nexus.repository.cache.CacheControllerHolder;
import org.sonatype.nexus.repository.cache.CacheControllerHolder.CacheType;

/**
 * Asset kinds for Dart.
 * 
 * @author Mathieu Delrocq
 */
public enum AssetKind {

    TARBALL(CacheControllerHolder.CONTENT),
    PACKAGES_METADATA(CacheControllerHolder.METADATA),
    PACKAGE_METADATA(CacheControllerHolder.METADATA),
    PACKAGE_VERSION_METADATA(CacheControllerHolder.METADATA),
    PACKAGE_ARCHIVE(CacheControllerHolder.CONTENT);

    private final CacheType cacheType;

    AssetKind(final CacheType cacheType) {
        this.cacheType = cacheType;
    }

    @Nonnull
    public CacheType getCacheType() {
        return cacheType;
    }
}
