package org.fff.duplicatekiller;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public final class MetadataUtils {

    private MetadataUtils() {

    }

    public static String getMetadataToString(File file) {
        try {
            ImageMetadata metadata = Imaging.getMetadata(file);
            if (metadata == null) {
                return "EXIF metadata is absent";
            } else {
                return metadata.toString();
            }
        } catch (ImageReadException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<String, String> parse(String metadataString) {
        int i, j, indexFrom;
        String key, value;
        HashMap<String, String> result = new HashMap<>();

        String[] split = metadataString.split("\n");
        for (String str :
                split) {

            i = str.indexOf(": ");
            while (i != -1) {
                value = "";
                indexFrom = 0;
                key = str.substring(0, i);
                str = str.substring(i + 2);

                i = str.indexOf(": ");
                j = str.indexOf(", ");
                if (i == -1) {
                    value = str;
                }
                if (i > j) {
                    while (i > j && j != -1) {
                        value = value + str.substring(indexFrom, j);
                        indexFrom = j;
                        j = str.indexOf(", ", j + 1);
                    }
                    str = str.substring(value.length() + 2);
                    i = str.indexOf(": ");

                }
                if (!key.equals("Seed")) {
                    result.put(key, value);
                }
            }
        }
        return result;

    }

    public static Boolean isEqualMetadata(HashMap<String, String> meta1, HashMap<String, String> meta2) {
        if (!meta1.keySet().equals(meta2.keySet())) {
            return false;
        }
        for (String key :
                meta1.keySet()) {
            if (!meta1.get(key).equals(meta2.get(key))) {
                return false;
            }
        }
        return true;
    }
}
