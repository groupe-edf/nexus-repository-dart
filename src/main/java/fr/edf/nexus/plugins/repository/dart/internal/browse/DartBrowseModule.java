package fr.edf.nexus.plugins.repository.dart.internal.browse;

import fr.edf.nexus.plugins.repository.dart.DartFormat;
import org.sonatype.nexus.repository.content.browse.store.FormatBrowseModule;

import javax.inject.Named;

/**
 * Configures the browse bindings for the maven format.
 */
@Named(DartFormat.NAME)
public class DartBrowseModule
        extends FormatBrowseModule<DartBrowseNodeDAO>
{
    // nothing to add...
}