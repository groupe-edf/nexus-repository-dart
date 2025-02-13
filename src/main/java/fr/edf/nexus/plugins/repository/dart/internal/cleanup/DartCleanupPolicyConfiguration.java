package fr.edf.nexus.plugins.repository.dart.internal.cleanup;

import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import fr.edf.nexus.plugins.repository.dart.DartFormat;
import org.sonatype.nexus.cleanup.config.CleanupPolicyConfiguration;

import com.google.common.collect.ImmutableMap;

import static org.sonatype.nexus.cleanup.config.CleanupPolicyConstants.REGEX_KEY;
import static org.sonatype.nexus.repository.search.index.SearchConstants.IS_PRERELEASE_KEY;
import static org.sonatype.nexus.repository.search.index.SearchConstants.LAST_BLOB_UPDATED_KEY;
import static org.sonatype.nexus.repository.search.index.SearchConstants.LAST_DOWNLOADED_KEY;

@Named(DartFormat.NAME)
@Singleton
public class DartCleanupPolicyConfiguration implements CleanupPolicyConfiguration {
    @Override
    public Map<String, Boolean> getConfiguration() {
        return ImmutableMap.of(LAST_BLOB_UPDATED_KEY, true,
                LAST_DOWNLOADED_KEY, true,
                IS_PRERELEASE_KEY, false,
                REGEX_KEY, true);
    }
}
