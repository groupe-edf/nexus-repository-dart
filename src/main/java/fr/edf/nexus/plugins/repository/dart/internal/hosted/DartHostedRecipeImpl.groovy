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
package fr.edf.nexus.plugins.repository.dart.internal.hosted

import javax.annotation.Nonnull
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

import org.sonatype.nexus.repository.Format
import org.sonatype.nexus.repository.Repository
import org.sonatype.nexus.repository.Type
import org.sonatype.nexus.repository.http.HttpHandlers
import org.sonatype.nexus.repository.types.HostedType
import org.sonatype.nexus.repository.view.ConfigurableViewFacet
import org.sonatype.nexus.repository.view.Router
import org.sonatype.nexus.repository.view.ViewFacet

import fr.edf.nexus.plugins.repository.dart.internal.AssetKind
import fr.edf.nexus.plugins.repository.dart.internal.DartFormat
import fr.edf.nexus.plugins.repository.dart.internal.DartRecipeSupport
import fr.edf.nexus.plugins.repository.dart.internal.recipe.DartHostedRecipe

/**
 * Recipe for creating a Dart hosted repository.
 */
@Named(DartHostedRecipe.NAME)
@Singleton
class DartHostedRecipeImpl extends DartRecipeSupport implements DartHostedRecipe {

    @Inject
    Provider<DartHostedFacet> hostedFacet

    @Inject
    Provider<DartHostedMetadataFacet> hostedMetadataFacet

    @Inject
    DartHostedDownloadHandler downloadHandler

    @Inject
    DartHostedUploadHandler uploadHandler

    @Inject
    DartHostedRecipeImpl(@Named(HostedType.NAME) final Type type, @Named(DartFormat.NAME) final Format format) {
        super(type, format)
    }

    @Override
    void apply(@Nonnull final Repository repository) throws Exception {
        repository.attach(storageFacet.get())
        repository.attach(contentFacet.get())
        repository.attach(securityFacet.get())
        repository.attach(configure(viewFacet.get()))
        repository.attach(componentMaintenanceFacet.get())
        repository.attach(hostedFacet.get())
        repository.attach(hostedMetadataFacet.get())
        repository.attach(searchFacet.get())
        repository.attach(attributesFacet.get())
    }

    /**
     * Configure {@link ViewFacet}.
     */
    private ViewFacet configure(final ConfigurableViewFacet facet) {
        Router.Builder builder = new Router.Builder()

        builder.route(packagesMatcher()
                .handler(timingHandler)
                .handler(assetKindHandler.rcurry(AssetKind.PACKAGES_METADATA))
                .handler(securityHandler)
                .handler(exceptionHandler)
                .handler(handlerContributor)
                .handler(conditionalRequestHandler)
                .handler(partialFetchHandler)
                .handler(contentHeadersHandler)
                .handler(unitOfWorkHandler)
                .handler(downloadHandler)
                .create())

        builder.route(packageMatcher()
                .handler(timingHandler)
                .handler(assetKindHandler.rcurry(AssetKind.PACKAGE_METADATA))
                .handler(securityHandler)
                .handler(exceptionHandler)
                .handler(handlerContributor)
                .handler(conditionalRequestHandler)
                .handler(partialFetchHandler)
                .handler(contentHeadersHandler)
                .handler(unitOfWorkHandler)
                .handler(downloadHandler)
                .create())

        builder.route(versionMatcher()
                .handler(timingHandler)
                .handler(assetKindHandler.rcurry(AssetKind.PACKAGE_VERSION_METADATA))
                .handler(securityHandler)
                .handler(exceptionHandler)
                .handler(handlerContributor)
                .handler(conditionalRequestHandler)
                .handler(partialFetchHandler)
                .handler(contentHeadersHandler)
                .handler(unitOfWorkHandler)
                .handler(downloadHandler)
                .create())

        builder.route(archiveMatcher()
                .handler(timingHandler)
                .handler(assetKindHandler.rcurry(AssetKind.PACKAGE_ARCHIVE))
                .handler(securityHandler)
                .handler(exceptionHandler)
                .handler(handlerContributor)
                .handler(conditionalRequestHandler)
                .handler(partialFetchHandler)
                .handler(contentHeadersHandler)
                .handler(unitOfWorkHandler)
                .create())

        builder.route(publishMatcher()
            .handler(timingHandler)
            .handler(securityHandler)
            .handler(exceptionHandler)
            .handler(handlerContributor)
            .handler(conditionalRequestHandler)
            .handler(partialFetchHandler)
            .handler(contentHeadersHandler)
            .handler(unitOfWorkHandler)
            .handler(uploadHandler)
            .create())

        builder.route(multipartUploadMatcher()
                .handler(timingHandler)
                .handler(assetKindHandler.rcurry(AssetKind.PACKAGE_ARCHIVE))
                .handler(securityHandler)
                .handler(exceptionHandler)
                .handler(handlerContributor)
                .handler(conditionalRequestHandler)
                .handler(partialFetchHandler)
                .handler(contentHeadersHandler)
                .handler(unitOfWorkHandler)
                .handler(uploadHandler)
                .create())

        builder.route(finalizeUploadMatcher()
                .handler(timingHandler)
                .handler(securityHandler)
                .handler(exceptionHandler)
                .handler(handlerContributor)
                .handler(conditionalRequestHandler)
                .handler(partialFetchHandler)
                .handler(contentHeadersHandler)
                .handler(unitOfWorkHandler)
                .handler(uploadHandler)
                .create())

        addBrowseUnsupportedRoute(builder)

        builder.defaultHandlers(HttpHandlers.notFound())

        facet.configure(builder.create())

        return facet
    }
}
