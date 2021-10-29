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

import java.io.IOException;

import javax.annotation.Nullable;

import org.sonatype.nexus.repository.Facet;
import org.sonatype.nexus.repository.view.Content;
import org.sonatype.nexus.repository.view.Payload;

/**
 * Interface defining the features supported by Dart repository hosted facets.
 */
@Facet.Exposed
public interface DartHostedFacet extends Facet {

    Content upload(String path, Payload payload) throws IOException;

    Content getPackagesMetadatas() throws IOException;

    Content getPackageMetadatas(String path) throws IOException;

    Content getPackageVersionMetadatas(String path) throws IOException;

    void rebuildPackagesMetadatas() throws IOException;

    void rebuildPackageMetadatas() throws IOException;

    @Nullable
    Content getArchive(String path) throws IOException;
}
