package org.fff.duplicatekiller;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

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

    public static void proceedFile(File file, List<MetadataParsed> uniqueList,
                                   List<MetadataParsed> duplicateList) {
        if (!file.isDirectory()) {
            String metaData = getMetadataToString(file);

            if (!metaData.equals("EXIF metadata is absent")) {
                MetadataParsed temp = new MetadataParsed(file.getAbsolutePath(), parse(metaData));

                if (uniqueList.size() == 0) {
                    uniqueList.add(temp);
                } else {
                    boolean isUnique = true;
                    for (MetadataParsed tempMetadataParsed :
                            uniqueList) {
                        if (tempMetadataParsed.metaParsed.equals(temp.metaParsed)) {
                            duplicateList.add(temp);
                            isUnique = false;
                            break;
                        }
                    }
                    if (isUnique) {
                        uniqueList.add(temp);
                    }
                }
            }
        }
    }
}
