package fr.edf.nexus.plugins.repository.dart.internal.proxy;

import fr.edf.nexus.plugins.repository.dart.AssetKind;
import fr.edf.nexus.plugins.repository.dart.DartContentFacet;
import fr.edf.nexus.plugins.repository.dart.internal.util.DartJsonProcessor;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.sonatype.goodies.testsupport.TestSupport;
import org.sonatype.nexus.common.collect.AttributesMap;
import org.sonatype.nexus.common.collect.NestedAttributesMap;
import org.sonatype.nexus.repository.Repository;
import org.sonatype.nexus.repository.cache.CacheInfo;
import org.sonatype.nexus.repository.content.Asset;
import org.sonatype.nexus.repository.content.fluent.FluentAsset;
import org.sonatype.nexus.repository.view.*;
import org.sonatype.nexus.repository.view.matchers.token.TokenMatcher;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class DartProxyFacetTest extends TestSupport {

    private static final String PACKAGES_PATH = "api/packages";
    private static final String PACKAGE_PATH = "api/packages/project_test";
    private static final String VERSION_PATH = "api/packages/project_test/versions/0.0.1";
    private static final String TAR_PATH = "packages/project_test/versions/0.0.1.tar.gz";

    @Mock
    private Repository repository;

    @Mock
    private Context context;

    @Mock
    private AttributesMap contextAttributes;

    @Mock
    private DartContentFacet dartContentFacet;

    @Mock
    private ViewFacet viewFacet;

    @Mock
    private DartJsonProcessor dartJsonProcessor;

    @Mock
    private Content content;

    @Mock
    private FluentAsset fluentAsset;

    @Mock
    private TokenMatcher.State state;

    @Mock
    private CacheInfo cacheInfo;

    @Mock
    private Asset asset;

    @Mock
    private Request request;

    @Mock
    private Response response;

    @Mock
    private NestedAttributesMap contentAttributes;

    @Mock
    private Payload payload;

    private DartProxyFacet underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new DartProxyFacet();
        underTest.attach(repository);

        DateTime now = new DateTime(Instant.now());

        when(repository.facet(DartContentFacet.class)).thenReturn(dartContentFacet);

        when(repository.facet(ViewFacet.class)).thenReturn(viewFacet);

        when(content.getAttributes()).thenReturn(contentAttributes);
        when(contentAttributes.require(Asset.class)).thenReturn(asset);

        when(context.getAttributes()).thenReturn(contextAttributes);
        when(context.getRequest()).thenReturn(request);
        when(context.getRepository()).thenReturn(repository);

        when(fluentAsset.download()).thenReturn(content);

        when(response.getPayload()).thenReturn(payload);

        when(cacheInfo.getLastVerified()).thenReturn(now);
    }

    @Test
    public void getCachedContentArchive() throws Exception {
        when(contextAttributes.require(AssetKind.class)).thenReturn(AssetKind.PACKAGE_ARCHIVE);
        when(dartContentFacet.get(TAR_PATH)).thenReturn(Optional.of(content));
        when(request.getPath()).thenReturn(TAR_PATH);

        assertThat(underTest.getCachedContent(context), is(content));
    }

    @Test
    public void getCachedContentPackages() throws Exception {
        when(contextAttributes.require(AssetKind.class)).thenReturn(AssetKind.PACKAGES_METADATA);
        when(dartContentFacet.get(PACKAGES_PATH)).thenReturn(Optional.of(content));
        when(request.getPath()).thenReturn(PACKAGES_PATH);
        // getCachedContent for api/packages should always return null
        assertThat(underTest.getCachedContent(context) == null, is(true));
    }

    @Test
    public void getCachedContentPackage() throws Exception {
        when(contextAttributes.require(AssetKind.class)).thenReturn(AssetKind.PACKAGE_METADATA);
        when(dartContentFacet.get(PACKAGE_PATH)).thenReturn(Optional.of(content));
        when(request.getPath()).thenReturn(PACKAGE_PATH);

        assertThat(underTest.getCachedContent(context), is(content));
    }

    @Test
    public void getCachedContentPackageVersion() throws Exception {
        when(contextAttributes.require(AssetKind.class)).thenReturn(AssetKind.PACKAGE_VERSION_METADATA);
        when(dartContentFacet.get(VERSION_PATH)).thenReturn(Optional.of(content));
        when(request.getPath()).thenReturn(VERSION_PATH);

        assertThat(underTest.getCachedContent(context), is(content));
    }

    @Test
    public void storePackagesMetadatas() throws Exception {
        when(contextAttributes.require(AssetKind.class)).thenReturn(AssetKind.PACKAGES_METADATA);
        when(request.getPath()).thenReturn(PACKAGES_PATH);
        when(dartContentFacet.put(PACKAGES_PATH, content, AssetKind.PACKAGES_METADATA)).thenReturn(content);

        when(viewFacet.dispatch(any(Request.class), eq(context))).thenReturn(response);

        assertThat(underTest.store(context, content), is(content));

        verify(dartContentFacet).put(PACKAGES_PATH, content, AssetKind.PACKAGES_METADATA);
    }

    @Test
    public void storePackageMetadatas() throws Exception {
        when(contextAttributes.require(AssetKind.class)).thenReturn(AssetKind.PACKAGE_METADATA);
        when(request.getPath()).thenReturn(PACKAGE_PATH);
        when(dartContentFacet.put(PACKAGE_PATH, content, AssetKind.PACKAGE_METADATA)).thenReturn(content);

        when(viewFacet.dispatch(any(Request.class), eq(context))).thenReturn(response);

        assertThat(underTest.store(context, content), is(content));

        verify(dartContentFacet).put(PACKAGE_PATH, content, AssetKind.PACKAGE_METADATA);
    }

    @Test
    public void storeVersionMetadatas() throws Exception {
        when(contextAttributes.require(AssetKind.class)).thenReturn(AssetKind.PACKAGE_VERSION_METADATA);
        when(request.getPath()).thenReturn(VERSION_PATH);
        when(dartContentFacet.put(VERSION_PATH, content, AssetKind.PACKAGE_VERSION_METADATA)).thenReturn(content);

        when(viewFacet.dispatch(any(Request.class), eq(context))).thenReturn(response);

        assertThat(underTest.store(context, content), is(content));

        verify(dartContentFacet).put(VERSION_PATH, content, AssetKind.PACKAGE_VERSION_METADATA);
    }

    @Test
    public void storePackageArchive() throws Exception {
        when(contextAttributes.require(AssetKind.class)).thenReturn(AssetKind.PACKAGE_ARCHIVE);
        when(request.getPath()).thenReturn(TAR_PATH);
        when(dartContentFacet.put(TAR_PATH, content, AssetKind.PACKAGE_ARCHIVE)).thenReturn(content);

        when(viewFacet.dispatch(any(Request.class), eq(context))).thenReturn(response);

        assertThat(underTest.store(context, content), is(content));

        verify(dartContentFacet).put(TAR_PATH, content, AssetKind.PACKAGE_ARCHIVE);
    }

//    @Test
//    public void indicateVerifiedWithAssetFound() throws Exception {
//        when(tx.findAsset(EntityHelper.id(asset), bucket)).thenReturn(asset);
//
//        underTest.indicateVerified(context, content, cacheInfo);
//
//        verify(cacheAttributes).set(CacheInfo.LAST_VERIFIED, toDate(cacheInfo.getLastVerified()));
//    }
//
//    @Test
//    public void indicateVerifiedWithAssetNotFound() throws Exception {
//        when(tx.findAsset(EntityHelper.id(asset), bucket)).thenReturn(null);
//
//        underTest.indicateVerified(context, content, cacheInfo);
//
//        verify(cacheAttributes, never()).set(CacheInfo.LAST_VERIFIED, toDate(cacheInfo.getLastVerified()));
//    }

    @Test
    public void checkPathWithSlashTest() {
        when(request.getPath()).thenReturn("/" + PACKAGES_PATH);
        String result = underTest.checkPath(context);

        assertThat(result.equals(PACKAGES_PATH), is(true));
    }

    @Test
    public void checkPathWithoutSlashTest() {
        when(request.getPath()).thenReturn(PACKAGES_PATH);
        String result = underTest.checkPath(context);

        assertThat(result.equals(PACKAGES_PATH), is(true));
    }

    @Test
    public void getUrlTestWithoutParams() {
        when(request.getPath()).thenReturn("/" + PACKAGES_PATH);
        when(request.getParameters()).thenReturn(new Parameters());

        String result = underTest.getUrl(context);

        assertThat(result.equals(PACKAGES_PATH), is(true));
    }

    @Test
    public void getUrlTestWithParams() {
        when(request.getPath()).thenReturn("/" + PACKAGES_PATH);
        Parameters params = new Parameters();
        params.set("page", "1");
        when(request.getParameters()).thenReturn(params);

        String result = underTest.getUrl(context);

        assertThat(result.equals(PACKAGES_PATH + "?page=1"), is(true));
    }
}
