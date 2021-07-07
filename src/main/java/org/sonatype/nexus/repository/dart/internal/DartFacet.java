package org.sonatype.nexus.repository.dart.internal;

import java.io.IOException;

import javax.annotation.Nullable;

import org.sonatype.nexus.repository.Facet;
import org.sonatype.nexus.repository.view.Content;
import org.sonatype.nexus.repository.view.Payload;

/**
 * Content facet used for getting assets from storage and putting assets into
 * storage for a Dart-format repository.
 */
@Facet.Exposed
public interface DartFacet extends Facet {

    @Nullable
    Content get(String path) throws IOException;

    Content put(String path, Payload payload, AssetKind assetKind) throws IOException;

}
