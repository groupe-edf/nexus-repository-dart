package fr.edf.nexus.plugins.repository.dart.internal;

import fr.edf.nexus.plugins.repository.dart.AssetKind;
import fr.edf.nexus.plugins.repository.dart.DartContentFacet;
import fr.edf.nexus.plugins.repository.dart.DartFormat;
import org.apache.commons.lang3.StringUtils;
import org.sonatype.nexus.common.hash.HashAlgorithm;
import org.sonatype.nexus.repository.browse.node.BrowsePath;
import org.sonatype.nexus.repository.cache.CacheInfo;
import org.sonatype.nexus.repository.content.Asset;
import org.sonatype.nexus.repository.content.facet.ContentFacetSupport;
import org.sonatype.nexus.repository.content.fluent.FluentAsset;
import org.sonatype.nexus.repository.content.fluent.FluentComponent;
import org.sonatype.nexus.repository.content.store.FormatStoreManager;
import org.sonatype.nexus.repository.view.Content;
import org.sonatype.nexus.repository.view.Payload;
import org.sonatype.nexus.repository.view.payloads.TempBlob;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.sonatype.nexus.common.hash.HashAlgorithm.*;

@Named
public class DartContentFacetImpl extends ContentFacetSupport implements DartContentFacet {

    public static final List<HashAlgorithm> hashAlgorithms = Arrays.asList(MD5, SHA1, SHA256, SHA512);

    @Inject
    public DartContentFacetImpl(@Named(DartFormat.NAME) final FormatStoreManager formatStoreManager)
    {
        super(formatStoreManager);
    }

    @Override
    public Optional<Content> get(final String assetPath) {
        return assets().path(normalizeAssetPath(assetPath)).find().map(FluentAsset::download);
    }

    @Override
    public Content put(String path, Payload payload, AssetKind assetKind) throws IOException {
        try (TempBlob tempBlob = getTempBlob(payload)) {
            FluentAsset asset;

            switch (assetKind) {
                case PACKAGE_METADATA:
                case PACKAGES_METADATA:
                case PACKAGE_VERSION_METADATA:
                    asset = findOrCreateMetadataAsset(path, tempBlob, assetKind);
                    break;
                case PACKAGE_ARCHIVE:
                    asset = findOrCreateContentAsset(path, tempBlob,assetKind,null,null,null);
                    break;
                default:
                    throw new IllegalStateException("Unexpected asset kind: " + assetKind);
            }

            return asset
                    .markAsCached(payload)
                    .download();
        }
    }

    @Override
    public TempBlob getTempBlob(final Payload payload) {
        checkNotNull(payload);
        return blobs().ingest(payload, hashAlgorithms);
    }

    @Override
    public void setCacheInfo(final String path, final Content content, final CacheInfo cacheInfo) {
        Asset asset = content.getAttributes().get(Asset.class);
        if (asset == null) {
            log.debug("Attempting to set cache info for non-existent Dart asset {}", path);
            return;
        }

        assets().with(asset).markAsCached(cacheInfo);
    }

    protected FluentAsset findOrCreateMetadataAsset(final String path, final TempBlob tempBlob, final AssetKind assetKind) {
        return assets()
                .path(normalizeAssetPath(path))
                .kind(assetKind.name())
                .blob(tempBlob)
                .save();
    }

    protected FluentAsset findOrCreateContentAsset(final String path,
                                                   final TempBlob tempBlob,
                                                   final AssetKind assetKind,
                                                   final String sourceType,
                                                   final String sourceUrl,
                                                   final String sourceReference)
    {

        String regex = ".*/([^/]+)-([^/]+)\\.tar\\.gz";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(path);

        matcher.matches();

        String name = matcher.group(1);
        String version = matcher.group(2);

        FluentComponent component = findOrCreateComponent(name, name, version);

        return assets()
                .path(normalizeAssetPath(path))
                .kind(assetKind.name())
                .component(component)
                .blob(tempBlob)
                .save();

    }

    private FluentComponent findOrCreateComponent(final String path, final String name, final String version) {
        return components()
                .name(name)
                .version(version)
                .normalizedVersion(versionNormalizerService().getNormalizedVersionByFormat(version, repository().getFormat()))
                .namespace(name)
                .getOrCreate();
    }

    private static String normalizeAssetPath(String path) {
        return StringUtils.prependIfMissing(path, BrowsePath.SLASH);
    }
}
