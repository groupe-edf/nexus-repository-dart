/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2008-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package fr.edf.nexus.plugins.repository.dart.internal;

import static java.util.Collections.emptyMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.sonatype.nexus.repository.Repository;
import org.sonatype.nexus.repository.rest.UploadDefinitionExtension;
import org.sonatype.nexus.repository.security.ContentPermissionChecker;
import org.sonatype.nexus.repository.security.VariableResolverAdapter;
import org.sonatype.nexus.repository.upload.AssetUpload;
import org.sonatype.nexus.repository.upload.ComponentUpload;
import org.sonatype.nexus.repository.upload.UploadDefinition;
import org.sonatype.nexus.repository.upload.UploadFieldDefinition;
import org.sonatype.nexus.repository.upload.UploadFieldDefinition.Type;
import org.sonatype.nexus.repository.upload.UploadHandlerSupport;
import org.sonatype.nexus.repository.upload.UploadRegexMap;
import org.sonatype.nexus.repository.upload.UploadResponse;
import org.sonatype.nexus.repository.view.Content;

import fr.edf.nexus.plugins.repository.dart.internal.model.DartComponentUpload;
import fr.edf.nexus.plugins.repository.dart.internal.model.Pubspec;

/**
 * Common base for dart upload handlers
 *
 */
public abstract class DartUploadHandlerSupport extends UploadHandlerSupport {

    protected final ContentPermissionChecker contentPermissionChecker;

    protected final VariableResolverAdapter variableResolverAdapter;

    protected UploadDefinition definition;

    public DartUploadHandlerSupport(final ContentPermissionChecker contentPermissionChecker,
            final VariableResolverAdapter variableResolverAdapter,
            final Set<UploadDefinitionExtension> uploadDefinitionExtensions) {
        super(uploadDefinitionExtensions);
        this.contentPermissionChecker = contentPermissionChecker;
        this.variableResolverAdapter = variableResolverAdapter;
    }

    @Override
    public UploadResponse handle(final Repository repository, final ComponentUpload upload) throws IOException {
        // Data holders for populating the UploadResponse
        List<DartComponentUpload> componentsUpload = new ArrayList<>();

        for (AssetUpload asset : upload.getAssetUploads()) {
            String path = normalizePath(DartAttributes.PACKAGES_PATH + "/" + asset.getField(DartAttributes.NAME).trim()
                    + "/" + DartAttributes.VERSIONS_PATH + "/" + asset.getField(DartAttributes.VERSION).trim()
                    + DartAttributes.EXTENSION);

            ensurePermitted(repository.getName(), DartFormat.NAME, path, emptyMap());
            DartPubspecExtractor extractor = new DartPubspecExtractor();
            Pubspec pubspec = extractor.extractPubspec(asset.getPayload());
            componentsUpload.add(new DartComponentUpload(path, asset.getPayload(), pubspec));
        }

        List<Content> responseContents = getResponseContents(repository, componentsUpload);

        return new UploadResponse(responseContents,
                componentsUpload.stream().map(DartComponentUpload::getPath).collect(Collectors.toList()));
    }

    protected abstract List<Content> getResponseContents(final Repository repository,
            final List<DartComponentUpload> componentsUpload) throws IOException;

    protected String normalizePath(final String path) {
        String result = path.replaceAll("/+", "/");

        if (result.startsWith("/")) {
            result = result.substring(1);
        }

        if (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    @Override
    public UploadDefinition getDefinition() {
        if (definition == null) {
            UploadFieldDefinition name = new UploadFieldDefinition(DartAttributes.NAME, false, Type.STRING);
            UploadFieldDefinition version = new UploadFieldDefinition(DartAttributes.VERSION, false, Type.STRING);
            List<UploadFieldDefinition> fieldList = new ArrayList<>();
            fieldList.add(name);
            fieldList.add(version);
            definition = getDefinition(DartFormat.NAME, false, new ArrayList<UploadFieldDefinition>(), fieldList,
                    new UploadRegexMap("(.*)", DartAttributes.VERSION));
        }
        return definition;
    }

    @Override
    public VariableResolverAdapter getVariableResolverAdapter() {
        return variableResolverAdapter;
    }

    @Override
    public ContentPermissionChecker contentPermissionChecker() {
        return contentPermissionChecker;
    }

    @Override
    public boolean supportsExportImport() {
        return true;
    }
}
