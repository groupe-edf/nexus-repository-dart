package fr.edf.nexus.plugins.repository.dart.internal;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sonatype.nexus.repository.storage.MetadataNodeEntityAdapter.P_NAME;

import java.io.InputStream;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.sonatype.goodies.testsupport.TestSupport;
import org.sonatype.nexus.blobstore.api.Blob;
import org.sonatype.nexus.blobstore.api.BlobRef;
import org.sonatype.nexus.common.collect.NestedAttributesMap;
import org.sonatype.nexus.repository.Format;
import org.sonatype.nexus.repository.Repository;
import org.sonatype.nexus.repository.storage.Asset;
import org.sonatype.nexus.repository.storage.AssetBlob;
import org.sonatype.nexus.repository.storage.AssetManager;
import org.sonatype.nexus.repository.storage.Bucket;
import org.sonatype.nexus.repository.storage.Component;
import org.sonatype.nexus.repository.storage.Query;
import org.sonatype.nexus.repository.storage.StorageFacet;
import org.sonatype.nexus.repository.storage.StorageTx;
import org.sonatype.nexus.repository.view.Content;
import org.sonatype.nexus.repository.view.payloads.TempBlob;
import org.sonatype.nexus.transaction.UnitOfWork;

public class DartFacetImplTest extends TestSupport {

    private static final String PACKAGES_PATH = "api/packages";
    private static final String PACKAGE_PATH = "api/packages/carp_webservices";
    private static final String VERSION_PATH = "api/packages/carp_webservices/versions/0.30.0";
    private static final String TAR_PATH = "packages/carp_webservices/versions/0.30.0.tar.gz";

    private static final String CONTENT_TYPE = "content-type";

    private static final Date LAST_MODIFIED = new Date();

    private static final Date LAST_VERIFIED = new Date();

    private static final String ETAG = "etag";

    @Mock
    private Repository repository;

    @Mock
    private StorageTx tx;

    @Mock
    private Asset asset;

    @Mock
    private Bucket bucket;

    @Mock
    private AssetBlob assetBlob;

    @Mock
    private InputStream blobInputStream;

    @Mock
    private StorageFacet storageFacet;

    @Mock
    private Component component;

    @Mock
    private BlobRef blobRef;

    @Mock
    private Blob blob;

    @Mock
    private Content upload;

    @Mock
    private NestedAttributesMap assetAttributes;

    @Mock
    private NestedAttributesMap contentAttributes;

    @Mock
    private NestedAttributesMap cacheAttributes;

    @Mock
    private NestedAttributesMap formatAttributes;

    @Mock
    private TempBlob tempBlob;

    private static final Format DART_FORMAT = new DartFormat();

    DartFacetImpl underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new DartFacetImpl(new DartFormat());
        underTest.attach(repository);

        when(tx.findBucket(repository)).thenReturn(bucket);
        when(tx.requireBlob(blobRef)).thenReturn(blob);
        when(tx.createAsset(bucket, DART_FORMAT)).thenReturn(asset);
        when(tx.createAsset(bucket, component)).thenReturn(asset);
        when(tx.findComponents(any(Query.class), eq(singletonList(repository)))).thenReturn(emptyList());
        when(tx.createComponent(bucket, DART_FORMAT)).thenReturn(component);

        when(repository.facet(StorageFacet.class)).thenReturn(storageFacet);

        when(asset.attributes()).thenReturn(assetAttributes);
        when(asset.formatAttributes()).thenReturn(formatAttributes);
        when(asset.requireBlobRef()).thenReturn(blobRef);
        when(asset.requireContentType()).thenReturn(CONTENT_TYPE);

        when(assetAttributes.child("cache")).thenReturn(cacheAttributes);
        when(assetAttributes.child("content")).thenReturn(contentAttributes);

        when(contentAttributes.get("last_modified", Date.class)).thenReturn(LAST_MODIFIED);
        when(contentAttributes.get("etag")).thenReturn(ETAG);

        when(cacheAttributes.get("last_verified", Date.class)).thenReturn(LAST_VERIFIED);

        when(assetBlob.getBlob()).thenReturn(blob);
        when(assetBlob.getBlobRef()).thenReturn(blobRef);

        when(blob.getInputStream()).thenReturn(blobInputStream);

        when(upload.getContentType()).thenReturn(CONTENT_TYPE);

        when(component.group(any(String.class))).thenReturn(component);
        when(component.name(any(String.class))).thenReturn(component);
        when(component.version(any(String.class))).thenReturn(component);

        when(storageFacet.createTempBlob(upload, DartFacetImpl.HASH_ALGORITHMS)).thenReturn(tempBlob);

        UnitOfWork.beginBatch(tx);
    }

    @After
    public void tearDown() throws Exception {
        UnitOfWork.end();
    }

    @Test
    public void getAssetNotFound() throws Exception {
        assertThat(underTest.get("path"), is(nullValue()));
    }

    @Test
    public void getAssetFoundWithoutUpdate() throws Exception {
        testGet(TAR_PATH, false);
    }

    @Test
    public void getAssetFoundWithUpdate() throws Exception {
        testGet(TAR_PATH, true);
    }

    @Test
    public void putPackagesJson() throws Exception {
        testPutOrUpdate(AssetKind.PACKAGES_METADATA, PACKAGES_PATH, false);
    }

    @Test
    public void putPackageJson() throws Exception {
        testPutOrUpdate(AssetKind.PACKAGE_METADATA, PACKAGE_PATH, false);
    }

    @Test
    public void putPackageVersionJson() throws Exception {
        testPutOrUpdate(AssetKind.PACKAGE_VERSION_METADATA, VERSION_PATH, false);
    }

    @Test
    public void updatePackageArchiveJson() throws Exception {
        testPutOrUpdate(AssetKind.PACKAGE_ARCHIVE, TAR_PATH, false);
    }

    @Test
    public void updatePackagesJson() throws Exception {
        testPutOrUpdate(AssetKind.PACKAGES_METADATA, PACKAGES_PATH, true);
    }

    @Test
    public void updatePackageJson() throws Exception {
        testPutOrUpdate(AssetKind.PACKAGE_METADATA, PACKAGE_PATH, true);
    }

    @Test
    public void updatePackageVersionJson() throws Exception {
        testPutOrUpdate(AssetKind.PACKAGE_VERSION_METADATA, VERSION_PATH, true);
    }

    @Test
    public void putPackageArchiveJson() throws Exception {
        testPutOrUpdate(AssetKind.PACKAGE_ARCHIVE, TAR_PATH, true);
    }

    private void testGet(final String path, final boolean markAsDownloaded) throws Exception {
        when(tx.findAssetWithProperty(P_NAME, path, bucket)).thenReturn(asset);
        when(asset.markAsDownloaded(AssetManager.DEFAULT_LAST_DOWNLOADED_INTERVAL)).thenReturn(markAsDownloaded);

        Content content = underTest.get(path);
        assertThat(content, is(notNullValue()));
        assertThat(content.openInputStream(), is(blobInputStream));
        assertThat(content.getContentType(), is(CONTENT_TYPE));

        if (markAsDownloaded) {
            verify(tx).saveAsset(asset);
        } else {
            verify(tx, never()).saveAsset(asset);
        }
    }

    private void testPutOrUpdate(final AssetKind assetKind, final String path, final boolean update) throws Exception {
        when(tx.setBlob(asset, path, tempBlob, null, CONTENT_TYPE, false)).thenReturn(assetBlob);
        if (update) {
            when(tx.findComponents(any(Query.class), eq(singletonList(repository))))
                    .thenReturn(singletonList(component));
            when(tx.findAssetWithProperty(P_NAME, path, bucket)).thenReturn(asset);
        }

        Content content = underTest.put(path, upload, assetKind);
        assertThat(content, is(notNullValue()));
        assertThat(content.openInputStream(), is(blobInputStream));
        assertThat(content.getContentType(), is(CONTENT_TYPE));

        verify(tx).saveAsset(asset);
    }
}
