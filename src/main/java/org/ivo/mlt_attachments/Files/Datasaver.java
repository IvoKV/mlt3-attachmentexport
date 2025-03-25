package org.ivo.mlt_attachments.Files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Datasaver {
    private String type;
    final static private String attachmentDir = "attachments";
    private byte[] data;
    final static String outputpath = "src/output/";

    public void saveData(byte [] data, String type) throws IOException {
        Path path = Paths.get(outputpath, type);
        String workdir = System.getProperty("user.dir");
        Path absolutePath = Paths.get(workdir, attachmentDir, type);
        if(!Files.exists(absolutePath)) {
            Files.createDirectories(absolutePath);
            System.out.println("Created path " + absolutePath);
        }


    }
}
