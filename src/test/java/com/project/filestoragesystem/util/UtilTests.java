package com.project.filestoragesystem.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = Util.class)
public class UtilTests {

    @Test
    public void convertsToHumanReadableByteCountSI() {
        long bytes1 = 10000;
        String s1 = Util.humanReadableByteCountSI(bytes1);

        assertEquals("10.0 KB", s1);

        long bytes2 = 1234567;
        String s2 = Util.humanReadableByteCountSI(bytes2);

        assertEquals("1.2 MB", s2);

        long bytes3 = 1098765;
        String s3 = Util.humanReadableByteCountSI(bytes3);
        Assertions.assertNotEquals("1.0 MB", s3);
    }
}
