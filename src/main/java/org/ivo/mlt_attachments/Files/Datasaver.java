package org.ivo.mlt_attachments.Files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

public class Datasaver {
    //private final static String mimetypeJpeg = "jpeg";
    //private final static String mimetypeTiff = "tiff";
    final static private String attachmentDir = "attachments";
    private byte[] data;
    private final static String outputpath = "src/output/";



    public Path createAttachmentDirectory(String type) throws IOException {
        Path path = Paths.get(outputpath, type);
        String workdir = System.getProperty("user.dir");
        Path absolutePath = Paths.get(workdir, attachmentDir, type);
        if(!Files.exists(absolutePath)) {
            Files.createDirectories(absolutePath);
            //System.out.println("Created path " + absolutePath);
        }
        return absolutePath;
    }

    public void saveAttachments(byte [] data, Path workingDir, String mimetype, int filecounter) throws IOException {
        Path filepath = workingDir.resolve("image" + "_" + filecounter + "." + mimetype);
        Files.write(filepath, data);
    }
}
