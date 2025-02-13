package fr.edf.nexus.plugins.repository.dart.internal.util

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.core.Is.is
import static org.mockito.Mockito.when

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.sonatype.goodies.testsupport.TestSupport
import org.sonatype.nexus.repository.Repository
import org.sonatype.nexus.repository.view.Payload

import groovy.json.JsonSlurper

class DartJsonProcessorTest extends TestSupport {

    static final String DART_PACKAGES_JSON_PATH = "/json/packages.json"
    static final String DART_PACKAGE_JSON_PATH = "/json/hypersnapsdk_flutter-package.json"
    static final String DART_PACKAGE_VERSION_JSON_PATH = "/json/hypersnapsdk_flutter-version.json"

    static final String NEXUS_DART_URL = "http://nexus/repository/dart"

    @Mock
    Payload payload

    @Mock
    Repository repository

    DartJsonProcessor dartJsonProcessor

    JsonSlurper slurper

    @Before
    void setUp() throws Exception {
        when(repository.getUrl()).thenReturn(NEXUS_DART_URL)
        slurper = new JsonSlurper()
        dartJsonProcessor = new DartJsonProcessor()
        dartJsonProcessor.setSlurper(slurper)
    }

    @Test
    void rewritePackagesJsonTest() {
        try {
            InputStream inputStream = getClass().getResourceAsStream(DART_PACKAGES_JSON_PATH)
            when(payload.openInputStream()).thenReturn(inputStream)
            Payload result = dartJsonProcessor.rewritePackagesJson(repository, payload)
            def jsonResult = slurper.parse(result.openInputStream())
            assertThat(String.valueOf(jsonResult.next_url).startsWith(NEXUS_DART_URL), is(true))
            URI uri = new URI(String.valueOf(jsonResult.next_url))
            assertThat(uri.getHost().equals("nexus"), is(true))
            assertThat(uri.getRawPath().equals("/repository/dart/api/packages"), is(true))
            assertThat(uri.getRawQuery().equals("page=2"), is(true))
            jsonResult.packages.each {
                assertThat(String.valueOf(it.latest.archive_url).startsWith(NEXUS_DART_URL), is(true))
                assertThat(String.valueOf(it.latest.package_url).startsWith(NEXUS_DART_URL), is(true))
                assertThat(String.valueOf(it.latest.url).startsWith(NEXUS_DART_URL), is(true))
            }
        } catch (Exception e) {
            assertThat("Error during rewritePackages due to exception " + e.toString(), false)
        }
    }

    @Test
    void rewritePackageJsonTest() {
        try {
            InputStream inputStream = getClass().getResourceAsStream(DART_PACKAGE_JSON_PATH)
            when(payload.openInputStream()).thenReturn(inputStream)
            Payload result = dartJsonProcessor.rewritePackageJson(repository, payload)
            def jsonResult = slurper.parse(result.openInputStream())
            assertThat(String.valueOf(jsonResult.latest.archive_url).startsWith(NEXUS_DART_URL), is(true))
            URI uri = new URI(String.valueOf(jsonResult.latest.archive_url))
            assertThat(uri.getHost().equals("nexus"), is(true))
            assertThat(uri.getRawPath().equals("/repository/dart/packages/hypersnapsdk_flutter/versions/1.0.2.tar.gz"), is(true))
            assertThat(uri.getRawQuery() == null, is(true))
            jsonResult.versions.each {
                assertThat(String.valueOf(it.archive_url).startsWith(NEXUS_DART_URL), is(true))
            }
        } catch (Exception e) {
            assertThat("Error during rewritePackage due to exception " + e.toString(), false)
        }
    }

    @Test
    void rewriteVersionJsonTest() {
        try {
            InputStream inputStream = getClass().getResourceAsStream(DART_PACKAGE_VERSION_JSON_PATH)
            when(payload.openInputStream()).thenReturn(inputStream)
            Payload result = dartJsonProcessor.rewriteVersionJson(repository, payload)
            def jsonResult = slurper.parse(result.openInputStream())
            assertThat(String.valueOf(jsonResult.archive_url).startsWith(NEXUS_DART_URL), is(true))
            URI uri = new URI(String.valueOf(jsonResult.archive_url))
            assertThat(uri.getHost().equals("nexus"), is(true))
            assertThat(uri.getRawPath().equals("/repository/dart/packages/hypersnapsdk_flutter/versions/1.0.2.tar.gz"), is(true))
            assertThat(uri.getRawQuery() == null, is(true))
        } catch (Exception e) {
            assertThat("Error during rewriteVersion due to exception " + e.toString(), false)
        }
    }
}
