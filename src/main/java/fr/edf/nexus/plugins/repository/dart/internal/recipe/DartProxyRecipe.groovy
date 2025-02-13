package fr.edf.nexus.plugins.repository.dart.internal.recipe

import fr.edf.nexus.plugins.repository.dart.DartFormat
import fr.edf.nexus.plugins.repository.dart.internal.proxy.DartPackagesHandler
import fr.edf.nexus.plugins.repository.dart.internal.proxy.DartProxyFacet
import org.sonatype.nexus.common.upgrade.AvailabilityVersion
import org.sonatype.nexus.repository.Format
import org.sonatype.nexus.repository.Repository
import org.sonatype.nexus.repository.Type
import org.sonatype.nexus.repository.cache.NegativeCacheFacet
import org.sonatype.nexus.repository.cache.NegativeCacheHandler
import org.sonatype.nexus.repository.http.HttpHandlers
import org.sonatype.nexus.repository.httpclient.HttpClientFacet
import org.sonatype.nexus.repository.proxy.ProxyHandler
import org.sonatype.nexus.repository.purge.PurgeUnusedFacet
import org.sonatype.nexus.repository.types.ProxyType
import org.sonatype.nexus.repository.view.ConfigurableViewFacet
import org.sonatype.nexus.repository.view.Router
import org.sonatype.nexus.repository.view.ViewFacet

import javax.annotation.Nonnull
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

@AvailabilityVersion(from = "1.0")
@Named(DartProxyRecipe.NAME)
@Singleton
class DartProxyRecipe extends DartRecipeSupport {

    public static final String NAME = 'dart-proxy'

    @Inject
    Provider<DartProxyFacet> proxyFacet

    @Inject
    Provider<NegativeCacheFacet> negativeCacheFacet

    @Inject
    Provider<PurgeUnusedFacet> purgeUnusedFacet

    @Inject
    NegativeCacheHandler negativeCacheHandler

    @Inject
    ProxyHandler proxyHandler

    @Inject
    Provider<HttpClientFacet> httpClientFacet

    @Inject
    DartPackagesHandler dartPackagesHandler

    @Inject
    DartProxyRecipe(@Named(ProxyType.NAME) final Type type, @Named(DartFormat.NAME) final Format format) {
        super(type, format)
    }

    @Override
    void apply(@Nonnull final Repository repository) throws Exception {
        repository.attach(contentFacet.get())
        repository.attach(securityFacet.get())
        repository.attach(configure(viewFacet.get()))
        repository.attach(httpClientFacet.get())
        repository.attach(negativeCacheFacet.get())
        repository.attach(proxyFacet.get())
        repository.attach(searchFacet.get())
        repository.attach(browseFacet.get())
        repository.attach(purgeUnusedFacet.get())
        repository.attach(maintenanceFacet.get())
    }

    private ViewFacet configure(final ConfigurableViewFacet facet) {
        Router.Builder builder = new Router.Builder()

        // Route for Dart packages api
        builder.route(packagesMatcher()
                .handler(timingHandler)
                .handler(assetKindHandler.rcurry(AssetKind.PACKAGES_METADATA))
                .handler(securityHandler)
                .handler(exceptionHandler)
                .handler(handlerContributor)
                .handler(negativeCacheHandler)
                .handler(conditionalRequestHandler)
                .handler(partialFetchHandler)
                .handler(contentHeadersHandler)
                .handler(dartPackagesHandler) // Handled to rewrite urls
                .handler(proxyHandler)
                .create())

        // Route for Dart package api
        builder.route(packageMatcher()
                .handler(timingHandler)
                .handler(assetKindHandler.rcurry(AssetKind.PACKAGE_METADATA))
                .handler(securityHandler)
                .handler(exceptionHandler)
                .handler(handlerContributor)
                .handler(negativeCacheHandler)
                .handler(conditionalRequestHandler)
                .handler(partialFetchHandler)
                .handler(contentHeadersHandler)
                .handler(dartPackagesHandler) // Handled to rewrite urls
                .handler(proxyHandler)
                .create())

        // Route for Dart package version api
        builder.route(versionMatcher()
                .handler(timingHandler)
                .handler(assetKindHandler.rcurry(AssetKind.PACKAGE_VERSION_METADATA))
                .handler(securityHandler)
                .handler(exceptionHandler)
                .handler(handlerContributor)
                .handler(negativeCacheHandler)
                .handler(conditionalRequestHandler)
                .handler(partialFetchHandler)
                .handler(contentHeadersHandler)
                .handler(dartPackagesHandler) // Handled to rewrite urls
                .handler(proxyHandler)
                .create())

        // Route for Dart package archive api
        builder.route(archiveMatcher()
                .handler(timingHandler)
                .handler(assetKindHandler.rcurry(AssetKind.PACKAGE_ARCHIVE))
                .handler(securityHandler)
                .handler(exceptionHandler)
                .handler(handlerContributor)
                .handler(negativeCacheHandler)
                .handler(conditionalRequestHandler)
                .handler(partialFetchHandler)
                .handler(contentHeadersHandler)
                .handler(proxyHandler)
                .create())

        addBrowseUnsupportedRoute(builder)

        builder.defaultHandlers(HttpHandlers.notFound())

        facet.configure(builder.create())

        return facet
    }
}
