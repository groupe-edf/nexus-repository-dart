package fr.edf.nexus.plugins.repository.dart.internal.security;

import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.nexus.repository.security.ContentPermissionChecker;
import org.sonatype.nexus.repository.security.SecurityFacetSupport;
import org.sonatype.nexus.repository.security.VariableResolverAdapter;

public class DartSecurityFacet extends SecurityFacetSupport {

    @Inject
    public DartSecurityFacet(final DartFormatSecurityContributor securityContributor,
                                 @Named("simple") final VariableResolverAdapter variableResolverAdapter,
                                 final ContentPermissionChecker contentPermissionChecker)
    {
        super(securityContributor, variableResolverAdapter, contentPermissionChecker);
    }
}
