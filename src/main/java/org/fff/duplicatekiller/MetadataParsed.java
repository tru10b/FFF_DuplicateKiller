
package org.fff.duplicatekiller;

import java.util.HashMap;
import java.util.Objects;

public class MetadataParsed {

    HashMap<String, String> metaParsed;
    String filePath;

    public MetadataParsed(String filePath, HashMap<String, String> metaParsed) {
        this.metaParsed = metaParsed;
        this.filePath = filePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetadataParsed that = (MetadataParsed) o;
        return metaParsed.equals(that.metaParsed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metaParsed);
    }
}

