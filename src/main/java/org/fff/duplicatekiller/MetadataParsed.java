
package org.fff.duplicatekiller;

import java.util.HashMap;

public class MetadataParsed {

    HashMap<String, String> metaParsed;
    String filePath;

    public MetadataParsed(String filePath, HashMap<String, String> metaParsed) {
        this.metaParsed = metaParsed;
        this.filePath = filePath;
    }

}

