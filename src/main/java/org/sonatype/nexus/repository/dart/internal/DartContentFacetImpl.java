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

import static org.sonatype.nexus.common.hash.HashAlgorithm.MD5;
import static org.sonatype.nexus.common.hash.HashAlgorithm.SHA1;
import static org.sonatype.nexus.common.hash.HashAlgorithm.SHA256;
import static org.sonatype.nexus.repository.storage.AssetManager.DEFAULT_LAST_DOWNLOADED_INTERVAL;
import static org.sonatype.nexus.repository.storage.MetadataNodeEntityAdapter.P_NAME;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;

import org.sonatype.nexus.blobstore.api.Blob;
import org.sonatype.nexus.common.hash.HashAlgorithm;
import org.sonatype.nexus.repository.FacetSupport;
import org.sonatype.nexus.repository.cache.CacheInfo;
import org.sonatype.nexus.repository.storage.Asset;
import org.sonatype.nexus.repository.storage.StorageTx;
import org.sonatype.nexus.repository.transaction.TransactionalTouchBlob;
import org.sonatype.nexus.repository.view.Content;
import org.sonatype.nexus.repository.view.Payload;
import org.sonatype.nexus.repository.view.payloads.BlobPayload;
import org.sonatype.nexus.transaction.UnitOfWork;

/**
 * Default (and currently only) implementation of {@code ComposerContentFacet}.
 */
@Named
public class DartContentFacetImpl extends FacetSupport implements DartContentFacet {

    private static final List<HashAlgorithm> hashAlgorithms = Arrays.asList(MD5, SHA1, SHA256);

    @Nullable
    @Override
    @TransactionalTouchBlob
    public Content get(final String path) throws IOException {
        StorageTx tx = UnitOfWork.currentTx();

        final Asset asset = findAsset(tx, path);
        if (asset == null) {
            return null;
        }
        if (asset.markAsDownloaded(DEFAULT_LAST_DOWNLOADED_INTERVAL)) {
            tx.saveAsset(asset);
        }

        final Blob blob = tx.requireBlob(asset.requireBlobRef());
        return toContent(asset, blob);
    }

    @Override
    public Content put(String path, Payload payload) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content put(String path, Payload payload, String sourceType, String sourceUrl, String sourceReference)
            throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCacheInfo(String path, Content content, CacheInfo cacheInfo) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean delete(String path) throws IOException {
        // TODO Auto-generated method stub
        return false;
    }

    @Nullable
    private Asset findAsset(final StorageTx tx, final String path) {
        return tx.findAssetWithProperty(P_NAME, path, tx.findBucket(getRepository()));
    }

    private Content toContent(final Asset asset, final Blob blob) {
        final Content content = new Content(new BlobPayload(blob, asset.requireContentType()));
        Content.extractFromAsset(asset, hashAlgorithms, content.getAttributes());
        return content;
    }

}
