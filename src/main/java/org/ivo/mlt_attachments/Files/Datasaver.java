package org.ivo.mlt_attachments.Files;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Datasaver {
    final static private String attachmentDir = "attachments";
    private byte[] data;
    private final static String outputpath = "src/output/";

    public Path createAttachmentDirectory(String type) throws IOException {
        String workdir = System.getProperty("user.dir");
        Path absolutePath = Paths.get(workdir, attachmentDir, type);
        if(!Files.exists(absolutePath)) {
            Files.createDirectories(absolutePath);
        }
        return absolutePath;
    }

    public void saveAttachments(byte [] data, Path workingDir, String mimetype, int filecounter) throws IOException {
        Path filepath = workingDir.resolve("image" + "_" + filecounter + "." + mimetype);
        try (FileOutputStream fos = new FileOutputStream(filepath.toFile())) {
            fos.write(data);
        }catch (Exception e) {
            System.out.println("Error saving file " + filepath + " to " + mimetype);
        }
    }
}
