package fr.edf.nexus.plugins.repository.dart;

import java.io.IOException;
import java.util.Optional;

import org.sonatype.nexus.repository.Facet;
import org.sonatype.nexus.repository.cache.CacheInfo;
import org.sonatype.nexus.repository.content.facet.ContentFacet;
import org.sonatype.nexus.repository.view.Content;
import org.sonatype.nexus.repository.view.Payload;
import org.sonatype.nexus.repository.view.payloads.TempBlob;

@Facet.Exposed
public interface DartContentFacet extends ContentFacet {

    Optional<Content> get(String path);

    Content put(String path, Payload payload, AssetKind assetKind) throws IOException;

    TempBlob getTempBlob(Payload payload);

    void setCacheInfo(String path, Content content, CacheInfo cacheInfo);
}
