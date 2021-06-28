package org.sonatype.nexus.repository.dart.internal;

import java.io.IOException;

import javax.annotation.Nullable;

import org.sonatype.nexus.repository.Facet;
import org.sonatype.nexus.repository.cache.CacheInfo;
import org.sonatype.nexus.repository.view.Content;
import org.sonatype.nexus.repository.view.Payload;

/**
 * Content facet used for getting assets from storage and putting assets into
 * storage for a Dart-format repository.
 */
@Facet.Exposed
public interface DartContentFacet extends Facet {

    @Nullable
    Content get(String path) throws IOException;

    Content put(String path, Payload payload) throws IOException;

    Content put(String path, Payload payload, String sourceType, String sourceUrl, String sourceReference)
            throws IOException;

    void setCacheInfo(String path, Content content, CacheInfo cacheInfo) throws IOException;

    boolean delete(String path) throws IOException;

}
