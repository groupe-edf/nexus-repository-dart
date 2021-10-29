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
package fr.edf.nexus.plugins.repository.dart.internal.hosted;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.sonatype.nexus.repository.storage.ComponentEntityAdapter.P_GROUP;
import static org.sonatype.nexus.repository.storage.MetadataNodeEntityAdapter.P_NAME;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.nexus.repository.FacetSupport;
import org.sonatype.nexus.repository.storage.Query;
import org.sonatype.nexus.repository.transaction.TransactionalStoreBlob;
import org.sonatype.nexus.repository.transaction.TransactionalTouchMetadata;
import org.sonatype.nexus.repository.view.Content;
import org.sonatype.nexus.repository.view.Payload;

import com.google.common.annotations.VisibleForTesting;

import fr.edf.nexus.plugins.repository.dart.internal.AssetKind;
import fr.edf.nexus.plugins.repository.dart.internal.DartFacet;
import fr.edf.nexus.plugins.repository.dart.internal.DartJsonProcessor;

/**
 * Default implementation of a Composer hosted facet.
 */
@Named
public class DartHostedFacetImpl extends FacetSupport implements DartHostedFacet {

    private final DartJsonProcessor dartJsonProcessor;

    @Inject
    public DartHostedFacetImpl(final DartJsonProcessor composerJsonProcessor) {
        this.dartJsonProcessor = checkNotNull(composerJsonProcessor);
    }

    @Override
    @TransactionalStoreBlob
    public Content upload(String path, Payload payload) throws IOException {
        return content().put(path, payload, AssetKind.PACKAGE_ARCHIVE);

    }

    @Override
    @TransactionalTouchMetadata
    public Content getPackagesMetadatas() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @TransactionalTouchMetadata
    public Content getPackageMetadatas(String path) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @TransactionalTouchMetadata
    public Content getPackageVersionMetadatas(String path) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @TransactionalStoreBlob
    public Content getArchive(String path) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @TransactionalTouchMetadata
    public void rebuildPackagesMetadatas() throws IOException {
        // TODO Auto-generated method stub
    }

    @Override
    @TransactionalTouchMetadata
    public void rebuildPackageMetadatas() throws IOException {
        // TODO Auto-generated method stub
    }

    @VisibleForTesting
    protected Query buildQuery(final String vendor, final String project) {
        return Query.builder().where(P_GROUP).eq(vendor).and(P_NAME).eq(project).build();
    }

    private DartFacet content() {
        return getRepository().facet(DartFacet.class);
    }

}
