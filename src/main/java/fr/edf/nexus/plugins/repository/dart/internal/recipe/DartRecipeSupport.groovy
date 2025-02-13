/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2025-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package fr.edf.nexus.plugins.repository.dart.internal.recipe

import fr.edf.nexus.plugins.repository.dart.AssetKind
import fr.edf.nexus.plugins.repository.dart.DartContentFacet
import fr.edf.nexus.plugins.repository.dart.internal.maintenance.DartMaintenanceFacet
import fr.edf.nexus.plugins.repository.dart.internal.security.DartSecurityFacet
import groovy.transform.CompileStatic
import org.sonatype.nexus.common.db.DatabaseCheck
import org.sonatype.nexus.repository.content.browse.BrowseFacet
import org.sonatype.nexus.repository.search.index.SearchIndexFacet
import org.sonatype.nexus.repository.view.matchers.LiteralMatcher

import javax.inject.Inject
import javax.inject.Provider

import org.sonatype.nexus.repository.Format
import org.sonatype.nexus.repository.RecipeSupport
import org.sonatype.nexus.repository.Type
import org.sonatype.nexus.repository.http.PartialFetchHandler
import org.sonatype.nexus.repository.security.SecurityHandler
import org.sonatype.nexus.repository.view.ConfigurableViewFacet
import org.sonatype.nexus.repository.view.Context
import org.sonatype.nexus.repository.view.Route.Builder
import org.sonatype.nexus.repository.view.handlers.ConditionalRequestHandler
import org.sonatype.nexus.repository.view.handlers.ContentHeadersHandler
import org.sonatype.nexus.repository.view.handlers.ExceptionHandler
import org.sonatype.nexus.repository.view.handlers.HandlerContributor
import org.sonatype.nexus.repository.view.handlers.TimingHandler
import org.sonatype.nexus.repository.view.matchers.ActionMatcher
import org.sonatype.nexus.repository.view.matchers.logic.LogicMatchers
import org.sonatype.nexus.repository.view.matchers.token.TokenMatcher

import static org.sonatype.nexus.repository.http.HttpMethods.GET
import static org.sonatype.nexus.repository.http.HttpMethods.HEAD

/**
 * Support for Dart recipes.
 */
@CompileStatic
abstract class DartRecipeSupport
    extends RecipeSupport
{
  private DatabaseCheck databaseCheck

  @Inject
  Provider<DartContentFacet> contentFacet

  @Inject
  Provider<DartMaintenanceFacet> maintenanceFacet

  @Inject
  Provider<DartSecurityFacet> securityFacet

  @Inject
  Provider<ConfigurableViewFacet> viewFacet

  @Inject
  Provider<SearchIndexFacet> searchFacet

  @Inject
  Provider<BrowseFacet> browseFacet

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
  HandlerContributor handlerContributor

  protected DartRecipeSupport(final Type type, final Format format) {
    super(type, format)
  }

  Closure assetKindHandler = { Context context, AssetKind value ->
    context.attributes.set(AssetKind, value)
    return context.proceed()
  }

  static Builder packagesMatcher() {
    new Builder().matcher(
            LogicMatchers.and(
                    new ActionMatcher(GET, HEAD),
                    new LiteralMatcher('/api/packages')
            ))
  }

  static Builder packageMatcher() {
    new Builder().matcher(
            LogicMatchers.and(
                    new ActionMatcher(GET, HEAD),
                    new TokenMatcher('/api/packages/{package:.[^\\/]+}')
            ))
  }

  static Builder versionMatcher() {
    new Builder().matcher(
            LogicMatchers.and(
                    new ActionMatcher(GET, HEAD),
                    new TokenMatcher('/api/packages/{package:.+}/versions/{version:.+}')
            ))
  }

  static Builder archiveMatcher() {
    new Builder().matcher(
            LogicMatchers.and(
                    new ActionMatcher(GET),
                    new TokenMatcher('/api/archives/{package:.+}.tar.gz')
            ))
  }

  @Inject
  void setDatabaseCheck(final DatabaseCheck databaseCheck) {
    this.databaseCheck = databaseCheck
  }

  @Override
  boolean isFeatureEnabled() {
    if (databaseCheck != null && !databaseCheck.isAllowedByVersion(getClass())) {
      return false
    }

    return true
  }

}
