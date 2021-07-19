package com.project.filestoragesystem.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class Util {

    /**
     * https://stackoverflow.com/a/3758880/1169113
     *
     * @param bytes
     * @return Human readable size.
     */
    public static String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }
}
