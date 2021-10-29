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
package fr.edf.nexus.plugins.repository.dart.internal;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.sonatype.goodies.common.ComponentSupport;
import org.sonatype.nexus.repository.view.Payload;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import fr.edf.nexus.plugins.repository.dart.internal.model.Pubspec;

/**
 * Utility class for extracting pubspec.yaml file's datas from a Dart package
 * archive
 */
@Named
@Singleton
public class DartPubspecExtractor extends ComponentSupport {

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    private final ArchiveStreamFactory archiveStreamFactory = new ArchiveStreamFactory();
    private final CompressorStreamFactory compressorStreamFactory = new CompressorStreamFactory();

    public Pubspec extractPubspec(final Payload payload) throws IOException {
        try (InputStream is = payload.openInputStream()) {
            try (ArchiveInputStream ais = archiveStreamFactory.createArchiveInputStream(ArchiveStreamFactory.TAR,
                    compressorStreamFactory.createCompressorInputStream(CompressorStreamFactory.GZIP, is))) {
                ArchiveEntry entry = ais.getNextEntry();
                while (entry != null) {
                    Pubspec pubspec = processEntry(ais, entry);
                    if (pubspec != null) {
                        return pubspec;
                    }
                    entry = ais.getNextEntry();
                }
            }
            throw new IOException("No pubspec file found in the archive");
        } catch (ArchiveException | CompressorException e) {
            throw new IOException("Error reading from archive", e);
        }
    }

    private Pubspec processEntry(final ArchiveInputStream stream, final ArchiveEntry entry) throws IOException {
        if (isPubspec(entry.getName())) {
            return mapper.readValue(stream, Pubspec.class);
        }
        return null;
    }

    private boolean isPubspec(final String entryName) {
        return entryName.equals("pubspec.yaml");
    }
}
