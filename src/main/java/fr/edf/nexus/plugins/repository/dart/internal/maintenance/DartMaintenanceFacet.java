package fr.edf.nexus.plugins.repository.dart.internal.maintenance;

import org.sonatype.nexus.repository.Facet;
import org.sonatype.nexus.repository.content.maintenance.LastAssetMaintenanceFacet;

import javax.inject.Named;

@Facet.Exposed
@Named
public class DartMaintenanceFacet extends LastAssetMaintenanceFacet {

}
