package fr.edf.nexus.plugins.repository.dart.internal.validator;

import fr.edf.nexus.plugins.repository.dart.DartFormat;
import org.sonatype.goodies.common.ComponentSupport;
import org.sonatype.nexus.common.io.InputStreamSupplier;
import org.sonatype.nexus.mime.MimeRulesSource;
import org.sonatype.nexus.repository.mime.ContentValidator;
import org.sonatype.nexus.repository.mime.DefaultContentValidator;
import org.sonatype.nexus.repository.view.ContentTypes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

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
