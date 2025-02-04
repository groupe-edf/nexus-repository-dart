package fr.edf.nexus.plugins.repository.dart.store;

import fr.edf.nexus.plugins.repository.dart.DartFormat;
import org.sonatype.nexus.repository.content.store.FormatStoreModule;

import javax.inject.Named;

@Named(DartFormat.NAME)
public class DartStoreModule
        extends FormatStoreModule<DartContentRepositoryDAO,
        DartComponentDAO,
        DartAssetDAO,
        DartAssetBlobDAO> {
}
