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
package fr.edf.nexus.plugins.repository.dart.internal

import static org.sonatype.nexus.repository.http.HttpMethods.GET
import static org.sonatype.nexus.repository.http.HttpMethods.HEAD
import static org.sonatype.nexus.repository.http.HttpMethods.POST

import javax.inject.Inject
import javax.inject.Provider

import org.sonatype.nexus.repository.Format
import org.sonatype.nexus.repository.RecipeSupport
import org.sonatype.nexus.repository.Type
import org.sonatype.nexus.repository.attributes.AttributesFacet
import org.sonatype.nexus.repository.http.HttpMethods
import org.sonatype.nexus.repository.http.PartialFetchHandler
import org.sonatype.nexus.repository.search.ElasticSearchFacet
import org.sonatype.nexus.repository.security.SecurityHandler
import org.sonatype.nexus.repository.storage.SingleAssetComponentMaintenance
import org.sonatype.nexus.repository.storage.StorageFacet
import org.sonatype.nexus.repository.storage.UnitOfWorkHandler
import org.sonatype.nexus.repository.view.ConfigurableViewFacet
import org.sonatype.nexus.repository.view.Context
import org.sonatype.nexus.repository.view.Route.Builder
import org.sonatype.nexus.repository.view.handlers.ConditionalRequestHandler
import org.sonatype.nexus.repository.view.handlers.ContentHeadersHandler
import org.sonatype.nexus.repository.view.handlers.ExceptionHandler
import org.sonatype.nexus.repository.view.handlers.HandlerContributor
import org.sonatype.nexus.repository.view.handlers.TimingHandler
import org.sonatype.nexus.repository.view.matchers.ActionMatcher
import org.sonatype.nexus.repository.view.matchers.LiteralMatcher
import org.sonatype.nexus.repository.view.matchers.logic.LogicMatchers
import org.sonatype.nexus.repository.view.matchers.token.TokenMatcher

/**
 * Abstract superclass containing methods and constants common to most Dart repository recipes.
 */
abstract class DartRecipeSupport extends RecipeSupport {

    @Inject
    Provider<DartFacet> contentFacet

    @Inject
    Provider<DartSecurityFacet> securityFacet

    @Inject
    Provider<ConfigurableViewFacet> viewFacet

    @Inject
    Provider<StorageFacet> storageFacet

    @Inject
    Provider<ElasticSearchFacet> searchFacet

    @Inject
    Provider<AttributesFacet> attributesFacet

    @Inject
    Provider<SingleAssetComponentMaintenance> componentMaintenanceFacet

    @Inject
    ExceptionHandler exceptionHandler

    @Inject
    TimingHandler timingHandler

    @Inject
    SecurityHandler securityHandler

    @Inject
    PartialFetchHandler partialFetchHandler

    @Inject
    ConditionalRequestHandler conditionalRequestHandler

    @Inject
    ContentHeadersHandler contentHeadersHandler

    @Inject
    UnitOfWorkHandler unitOfWorkHandler

    @Inject
    HandlerContributor handlerContributor

    protected DartRecipeSupport(final Type type, final Format format) {
        super(type, format)
    }

    /**
     * Return the {@link AssetKind} associated to this request
     */
    Closure assetKindHandler = { Context context, AssetKind value ->
        context.attributes.set(AssetKind, value)
        return context.proceed()
    }

    /**
     * Route matcher for Dart packages api
     * 
     * @return Route {@link Builder} of this matcher
     */
    static Builder packagesMatcher() {
        new Builder().matcher(
                LogicMatchers.and(
                new ActionMatcher(GET, HEAD),
                new LiteralMatcher('/api/packages')
                ))
    }

    /**
     * Route matcher for Dart package api
     *
     * @return Route {@link Builder} of this matcher
     */
    static Builder packageMatcher() {
        new Builder().matcher(
                LogicMatchers.and(
                new ActionMatcher(GET, HEAD),
                new TokenMatcher('/api/packages/{package:.[^\\/]+}')
                ))
    }

    /**
     * Route matcher for Dart package version api
     *
     * @return Route {@link Builder} of this matcher
     */
    static Builder versionMatcher() {
        new Builder().matcher(
                LogicMatchers.and(
                new ActionMatcher(GET, HEAD),
                new TokenMatcher('/api/packages/{package:.+}/versions/{version:.+}')
                ))
    }

    /**
     * Route matcher for Dart package archive api
     *
     * @return Route {@link Builder} of this matcher
     */
    static Builder archiveMatcher() {
        new Builder().matcher(
                LogicMatchers.and(
                new ActionMatcher(GET, HEAD),
                new TokenMatcher('/api/archives/{package:.+}.tar.gz')
                ))
    }

    /**
     * Route matcher ask an upload of a Dart package 
     * on a hosted repository with Dart or Flutter commandline
     * 
     * @see <a href="https://github.com/dart-lang/pub/blob/master/doc/repository-spec-v2.md#publishing-packages">Hosted Pub Repository Specification Version 2</a>
     * 
     * @return Route {@link Builder} of this matcher
     */
    static Builder publishMatcher() {
        new Builder().matcher(
                LogicMatchers.and(
                new ActionMatcher(GET, HEAD),
                new LiteralMatcher('/api/packages/versions/new')
                ))
    }

    /**
     * Route matcher to upload a Dart package on a hosted repository
     * with Dart or Flutter commandline
     *
     * @see <a href="https://github.com/dart-lang/pub/blob/master/doc/repository-spec-v2.md#publishing-packages">Hosted Pub Repository Specification Version 2</a>
     *
     * @return Route {@link Builder} of this matcher
     */
    static Builder multipartUploadMatcher() {
        new Builder().matcher(
                LogicMatchers.and(
                new ActionMatcher(POST),
                new TokenMatcher('/api/{id:.+}/multipart')
                ))
    }

    /**
     * Route matcher to finalize an upload of a Dart package on a hosted repository
     * with Dart or Flutter commandline
     *
     * @see <a href="https://github.com/dart-lang/pub/blob/master/doc/repository-spec-v2.md#publishing-packages">Hosted Pub Repository Specification Version 2</a>
     *
     * @return Route {@link Builder} of this matcher
     */
    static Builder finalizeUploadMatcher() {
        new Builder().matcher(
                LogicMatchers.and(
                        new ActionMatcher(GET, HEAD),
                        new TokenMatcher('/api/{package:.+}/versions/{version:.+}/finalize')
                        ))
    }

    /**
     * Route matcher for flutter infra api
     *
     * @return Route {@link Builder} of this matcher
     */
    // TODO : for flutter upload or other flutter tools
    //    static Builder flutterInfraMatcher() {
    //        new Builder().matcher(
    //                LogicMatchers.and(
    //                        new ActionMatcher(HttpMethods.GET),
    //                        new TokenMatcher('/flutter_infra_release/flutter/f0826da7ef2d301eb8f4ead91aaf026aa2b52881/sky_engine.zip')
    //                        ))
    //    }
}
