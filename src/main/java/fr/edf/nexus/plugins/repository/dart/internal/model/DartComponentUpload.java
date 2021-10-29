package fr.edf.nexus.plugins.repository.dart.internal.model;

import org.sonatype.nexus.repository.view.Payload;

public class DartComponentUpload {

    private final String path;
    private final Payload payload;
    private final Pubspec pubspec;

    public DartComponentUpload(String path, Payload payload, Pubspec pubspec) {
        super();
        this.path = path;
        this.payload = payload;
        this.pubspec = pubspec;
    }

    public String getPath() {
        return path;
    }

    public Payload getPayload() {
        return payload;
    }

    public Pubspec getPubspec() {
        return pubspec;
    }

}
