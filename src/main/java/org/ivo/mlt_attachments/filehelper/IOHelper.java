package org.ivo.mlt_attachments.filehelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class IOHelper {

    public static Path createOutputDirectory(Set<String> workDirs) throws IOException {
        Object[] array = workDirs.toArray();
        String destination = (String) array[0];
        Path destinationPath = Paths.get(destination);

        Path parentDirectory = destinationPath.getParent();
        Path destinationDirectory = Paths.get(parentDirectory.toString(), "pdfExport");

        if (!Files.isDirectory(destinationDirectory)) {
            Files.createDirectories(destinationDirectory);
        }
        return destinationDirectory;
    }

    public static boolean fileExists(String filename) {
        return Files.exists(Paths.get(filename));
    }

    public static String getMimeType(String imgFilename) {
        String selection = imgFilename.substring(imgFilename.lastIndexOf('\\') + 1);
        return selection;
    }
}
