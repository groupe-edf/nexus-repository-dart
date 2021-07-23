package fr.edf.nexus.plugins.repository.dart.internal;

import static fr.edf.nexus.plugins.repository.dart.internal.AssetKind.PACKAGES_METADATA;
import static fr.edf.nexus.plugins.repository.dart.internal.AssetKind.PACKAGE_ARCHIVE;
import static fr.edf.nexus.plugins.repository.dart.internal.AssetKind.PACKAGE_METADATA;
import static fr.edf.nexus.plugins.repository.dart.internal.AssetKind.PACKAGE_VERSION_METADATA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.sonatype.nexus.repository.cache.CacheControllerHolder.CONTENT;
import static org.sonatype.nexus.repository.cache.CacheControllerHolder.METADATA;

import org.junit.Test;
import org.sonatype.goodies.testsupport.TestSupport;

public class AssetKindTest extends TestSupport {

    @Test
    public void cacheTypes() throws Exception {
        assertThat(PACKAGES_METADATA.getCacheType(), is(equalTo(METADATA)));
        assertThat(PACKAGE_METADATA.getCacheType(), is(equalTo(METADATA)));
        assertThat(PACKAGE_VERSION_METADATA.getCacheType(), is(equalTo(METADATA)));
        assertThat(PACKAGE_ARCHIVE.getCacheType(), is(equalTo(CONTENT)));
    }

}
