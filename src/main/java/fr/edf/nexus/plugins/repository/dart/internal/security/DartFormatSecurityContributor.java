package fr.edf.nexus.plugins.repository.dart.internal.security;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import fr.edf.nexus.plugins.repository.dart.DartFormat;
import org.sonatype.nexus.repository.Format;
import org.sonatype.nexus.repository.security.RepositoryFormatSecurityContributor;

@Named
@Singleton
public class DartFormatSecurityContributor extends RepositoryFormatSecurityContributor {
    @Inject
    public DartFormatSecurityContributor(@Named(DartFormat.NAME) final Format format) {
        super(format);
    }
}
