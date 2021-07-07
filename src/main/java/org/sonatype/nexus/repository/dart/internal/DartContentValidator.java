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
package org.sonatype.nexus.repository.dart.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.goodies.common.ComponentSupport;
import org.sonatype.nexus.common.io.InputStreamSupplier;
import org.sonatype.nexus.mime.MimeRulesSource;
import org.sonatype.nexus.repository.mime.ContentValidator;
import org.sonatype.nexus.repository.mime.DefaultContentValidator;
import org.sonatype.nexus.repository.view.ContentTypes;

/**
 * Dart specific {@link ContentValidator} that fix mismatch of Dart metadatas
 * format
 *
 * @author Mathieu Delrocq
 */
@Named(DartFormat.NAME)
@Singleton
public class DartContentValidator extends ComponentSupport implements ContentValidator {
    private final DefaultContentValidator defaultContentValidator;

    @Inject
    public DartContentValidator(final DefaultContentValidator defaultContentValidator) {
        this.defaultContentValidator = checkNotNull(defaultContentValidator);
    }

    @Nonnull
    @Override
    public String determineContentType(final boolean strictContentTypeValidation,
            final InputStreamSupplier contentSupplier, @Nullable final MimeRulesSource mimeRulesSource,
            @Nullable final String contentName, @Nullable final String declaredContentType) throws IOException {

        // Nexus mark "text/plain" when Dart APIs return a Content-Type
        // "application/json; charset=utf-8"
        // This force the return value to application/json
        if (null != declaredContentType && declaredContentType.contains(ContentTypes.APPLICATION_JSON)) {
            return ContentTypes.APPLICATION_JSON;
        }
        return defaultContentValidator.determineContentType(strictContentTypeValidation, contentSupplier,
                mimeRulesSource, contentName, declaredContentType);
    }
}
