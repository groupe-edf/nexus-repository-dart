package fr.edf.nexus.plugins.repository.dart.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.sonatype.goodies.testsupport.TestSupport;

public class DartFormatTest extends TestSupport {
    DartFormat dartFormat;

    @Before
    public void setup() {
        dartFormat = new DartFormat();
    }

    @Test
    public void P2FormatName() {
        assertThat(DartFormat.NAME, is(equalTo("dart")));
    }
}
