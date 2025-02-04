package fr.edf.nexus.plugins.repository.dart.internal.browse;

import fr.edf.nexus.plugins.repository.dart.DartFormat;
import org.sonatype.nexus.repository.browse.node.BrowsePath;
import org.sonatype.nexus.repository.content.Asset;
import org.sonatype.nexus.repository.content.Component;
import org.sonatype.nexus.repository.content.browse.ComponentPathBrowseNodeGenerator;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.sonatype.nexus.common.text.Strings2.isBlank;
import static org.sonatype.nexus.repository.browse.node.BrowsePathBuilder.appendPath;
import static org.sonatype.nexus.repository.browse.node.BrowsePathBuilder.fromPaths;

/**
 * Browse node generator for Dart.
 */
@Singleton
@Named(DartFormat.NAME)
public class DartBrowseNodeGenerator
        extends ComponentPathBrowseNodeGenerator {

    @Override
    public List<BrowsePath> computeAssetPaths(final Asset asset) {
        checkNotNull(asset);

        return asset.component().map(component -> {

            // place asset under component, but use its true path as the request path for permission checks
            List<BrowsePath> assetPaths = computeComponentPaths(asset);
            appendPath(assetPaths, lastSegment(asset.path()), asset.path());
            return assetPaths;

        }).orElseGet(() -> super.computeAssetPaths(asset));
    }

    @Override
    public List<BrowsePath> computeComponentPaths(final Asset asset) {
        checkNotNull(asset);

        Component component = asset.component().get(); // NOSONAR: caller guarantees this

        List<String> componentPath = pathToArtifactFolder(component);

        String version = component.version();
        if (!isBlank(version) && !version.equals(componentPath.get(componentPath.size() - 1))) {
            componentPath.add(version);
        }

        return fromPaths(componentPath, true);
    }

    private List<String> pathToArtifactFolder(Component component) {
        List<String> paths = new ArrayList<>();

        String vendor = component.namespace();
        String project = component.name();
        String version = component.version();

        paths.add(vendor);
        paths.add(project);
        paths.add(version);

        return paths;
    }
}

