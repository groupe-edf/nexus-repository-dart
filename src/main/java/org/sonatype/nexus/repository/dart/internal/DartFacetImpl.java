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

import javax.inject.Named;

import org.sonatype.nexus.blobstore.api.Blob;
import org.sonatype.nexus.repository.FacetSupport;
import org.sonatype.nexus.repository.storage.Asset;
import org.sonatype.nexus.repository.storage.Bucket;
import org.sonatype.nexus.repository.storage.Component;
import org.sonatype.nexus.repository.storage.StorageTx;
import org.sonatype.nexus.repository.view.Content;
import org.sonatype.nexus.repository.view.Payload;
import org.sonatype.nexus.repository.view.payloads.TempBlob;

/**
 * Default (and currently only) implementation of {@code ComposerContentFacet}.
 */
@Named
public class DartFacetImpl extends FacetSupport implements DartFacet {

    @Override
    public Component findOrCreateComponent(StorageTx tx, DartAttributes dartAttributes) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Asset findOrCreateAsset(StorageTx tx, Component component, String path, DartAttributes dartAttributes) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Asset findOrCreateAsset(StorageTx tx, String path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AssetKind getAssetKind(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Asset findAsset(StorageTx tx, Bucket bucket, String assetName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content saveAsset(StorageTx tx, Asset asset, TempBlob contentSupplier, Payload payload) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content doCreateOrSaveComponent(DartAttributes dartAttributes, TempBlob componentContent, Payload payload,
            AssetKind assetKind) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content toContent(Asset asset, Blob blob) {
        // TODO Auto-generated method stub
        return null;
    }
}
