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
package fr.edf.nexus.plugins.repository.dart.internal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;
import static org.sonatype.nexus.common.hash.HashAlgorithm.MD5;
import static org.sonatype.nexus.common.hash.HashAlgorithm.SHA1;
import static org.sonatype.nexus.common.hash.HashAlgorithm.SHA256;
import static org.sonatype.nexus.common.hash.HashAlgorithm.SHA512;
import static org.sonatype.nexus.repository.storage.AssetEntityAdapter.P_ASSET_KIND;
import static org.sonatype.nexus.repository.storage.ComponentEntityAdapter.P_VERSION;
import static org.sonatype.nexus.repository.storage.MetadataNodeEntityAdapter.P_NAME;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.nexus.blobstore.api.Blob;
import org.sonatype.nexus.common.hash.HashAlgorithm;
import org.sonatype.nexus.repository.FacetSupport;
import org.sonatype.nexus.repository.Format;
import org.sonatype.nexus.repository.storage.Asset;
import org.sonatype.nexus.repository.storage.AssetBlob;
import org.sonatype.nexus.repository.storage.AssetManager;
import org.sonatype.nexus.repository.storage.Bucket;
import org.sonatype.nexus.repository.storage.Component;
import org.sonatype.nexus.repository.storage.Query;
import org.sonatype.nexus.repository.storage.StorageFacet;
import org.sonatype.nexus.repository.storage.StorageTx;
import org.sonatype.nexus.repository.transaction.TransactionalStoreBlob;
import org.sonatype.nexus.repository.transaction.TransactionalStoreMetadata;
import org.sonatype.nexus.repository.transaction.TransactionalTouchBlob;
import org.sonatype.nexus.repository.view.Content;
import org.sonatype.nexus.repository.view.Payload;
import org.sonatype.nexus.repository.view.payloads.BlobPayload;
import org.sonatype.nexus.repository.view.payloads.TempBlob;
import org.sonatype.nexus.transaction.UnitOfWork;

import com.google.common.collect.ImmutableList;

/**
 * Default (and currently only) implementation of {@code DartFacet}.
 * 
 */
@Named
public class DartFacetImpl extends FacetSupport implements DartFacet {

    private Format format;

    public static final Collection<HashAlgorithm> HASH_ALGORITHMS = ImmutableList.of(MD5, SHA1, SHA256, SHA512);

    @Inject
    public DartFacetImpl(@Named(DartFormat.NAME) final Format format) {
        this.format = format;
    }

    @Nullable
    @Override
    @TransactionalTouchBlob
    public Content get(String path) throws IOException {
        StorageTx tx = UnitOfWork.currentTx();

        final Asset asset = findAsset(tx, path);
        if (asset == null) {
            return null;
        }
        if (asset.markAsDownloaded(AssetManager.DEFAULT_LAST_DOWNLOADED_INTERVAL)) {
            tx.saveAsset(asset);
        }

        final Blob blob = tx.requireBlob(asset.requireBlobRef());
        return toContent(asset, blob);
    }

    @Override
    public Content put(String path, Payload payload, AssetKind assetKind) throws IOException {
        StorageFacet storageFacet = facet(StorageFacet.class);
        try (TempBlob tempBlob = storageFacet.createTempBlob(payload, HASH_ALGORITHMS)) {
            switch (assetKind) {
            case PACKAGES_METADATA:
                return doPutMetadata(path, tempBlob, payload, assetKind);
            case PACKAGE_METADATA:
                return doPutMetadata(path, tempBlob, payload, assetKind);
            case PACKAGE_VERSION_METADATA:
                return doPutMetadata(path, tempBlob, payload, assetKind);
            case PACKAGE_ARCHIVE:
                return doPutContent(path, tempBlob, payload, assetKind, null, null, null);
            default:
                throw new IllegalStateException("Unexpected asset kind: " + assetKind);
            }
        }
    }

    @TransactionalStoreBlob
    protected Content doPutMetadata(final String path, final TempBlob tempBlob, final Payload payload,
            final AssetKind assetKind) throws IOException {
        StorageTx tx = UnitOfWork.currentTx();
        Asset asset = getOrCreateAsset(path);
        asset.formatAttributes().set(P_ASSET_KIND, assetKind.toString());

        if (payload instanceof Content) {
            Content.applyToAsset(asset, Content.maintainLastModified(asset, ((Content) payload).getAttributes()));
        }

        AssetBlob assetBlob = tx.setBlob(asset, path, tempBlob, null, payload.getContentType(), false);
        tx.saveAsset(asset);

        return toContent(asset, assetBlob.getBlob());
    }

    @TransactionalStoreBlob
    protected Content doPutContent(final String path, final TempBlob tempBlob, final Payload payload,
            final AssetKind assetKind, final String sourceType, final String sourceUrl, final String sourceReference)
            throws IOException {
        String regex = ".*/([^/]+)-([^/]+)\\.tar\\.gz";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(path);
        
        matcher.matches();

        String name = matcher.group(1);
        String version = matcher.group(2);

        StorageTx tx = UnitOfWork.currentTx();

        Asset asset = getOrCreateAsset(path, name, version);

        if (payload instanceof Content) {
            Content.applyToAsset(asset, Content.maintainLastModified(asset, ((Content) payload).getAttributes()));
        }

        AssetBlob assetBlob = tx.setBlob(asset, path, tempBlob, null, payload.getContentType(), false);

        try {
            asset.formatAttributes().clear();
            asset.formatAttributes().set(P_ASSET_KIND, assetKind.toString());
            asset.formatAttributes().set(P_NAME, name);
            asset.formatAttributes().set(P_VERSION, version);
        } catch (Exception e) {
            log.error("Error extracting format attributes for {}, skipping", path, e);
        }

        tx.saveAsset(asset);

        return toContent(asset, assetBlob.getBlob());
    }

    @TransactionalStoreMetadata
    public Asset getOrCreateAsset(final String path) {
        final StorageTx tx = UnitOfWork.currentTx();
        final Bucket bucket = tx.findBucket(getRepository());

        Asset asset = findAsset(tx, path);
        if (asset == null) {
            asset = tx.createAsset(bucket, format);
            asset.name(path);
        }

        asset.markAsDownloaded(AssetManager.DEFAULT_LAST_DOWNLOADED_INTERVAL);

        return asset;
    }

    @TransactionalStoreMetadata
    public Asset getOrCreateAsset(final String path, final String name, final String version) {
        final StorageTx tx = UnitOfWork.currentTx();
        final Bucket bucket = tx.findBucket(getRepository());

        Component component = findComponent(tx, name, version);
        if (component == null) {
            component = tx.createComponent(bucket, format).name(name).version(version);
            tx.saveComponent(component);
        }

        Asset asset = findAsset(tx, path);
        if (asset == null) {
            asset = tx.createAsset(bucket, component);
            asset.name(path);
        }

        asset.markAsDownloaded(AssetManager.DEFAULT_LAST_DOWNLOADED_INTERVAL);

        return asset;
    }

    @Nullable
    private Asset findAsset(final StorageTx tx, final String path) {
        return tx.findAssetWithProperty(P_NAME, path, tx.findBucket(getRepository()));
    }

    @Nullable
    private Component findComponent(final StorageTx tx, final String name, final String version) {
        Iterable<Component> components = tx.findComponents(
                Query.builder().where(P_NAME).eq(name).and(P_VERSION).eq(version).build(),
                singletonList(getRepository()));
        if (components.iterator().hasNext()) {
            return components.iterator().next();
        }
        return null;
    }

    /**
     * Convert an asset blob to {@link Content}.
     *
     * @return content of asset blob
     */
    public Content toContent(final Asset asset, final Blob blob) {
        Content content = new Content(new BlobPayload(blob, asset.requireContentType()));
        Content.extractFromAsset(asset, HASH_ALGORITHMS, content.getAttributes());
        return content;
    }
}
