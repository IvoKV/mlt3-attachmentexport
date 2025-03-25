package org.ivo.mlt_attachments.fileconverter;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ImageToPdf {
    private Set<String> workDir = new LinkedHashSet<>();
    private List<Path> fileNames = new ArrayList<>();
    private List<Byte> images;
    private String mimetype;

    public ImageToPdf() {
    }

    public ImageToPdf(Set<String> workDir) throws IOException {
        this.workDir = workDir;
    }

    public List<Path> importImageList(String wokrDir) throws IOException {
        return fileNames = Files.walk(Paths.get(wokrDir))
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());
        //return fileNames;
    }

    public Path createOutputDirectory(Set<String> workDirs) throws IOException {
        Object[] array = workDirs.toArray();
        String destination = (String) array[0];
        Path destinationPath = Paths.get(destination);

        Path parentDirectory = destinationPath.getParent();
        Path destinationDirectory = Paths.get(parentDirectory.toString(), "pdfExport");
        System.out.println("destinationDirectory = " + destinationDirectory.toString());

        if (!Files.isDirectory(destinationDirectory)) {
            Files.createDirectories(destinationDirectory);
        }
        return destinationDirectory;
    }

    public Document initializeDocument(){
        Document document = new Document(PageSize.A4, 20.0f, 20.0f, 20.0f, 150.0f);
        return document;
    }

    public void imagesToPdf(List<Path> images, Path destinationPath, int filecounter, Document document) throws IOException, DocumentException {
        String pdfName = "pdfExport_";
        File file  = new File(destinationPath.toString(), pdfName + filecounter + ".pdf");
        FileOutputStream fos = new FileOutputStream(file);

        PdfWriter pdfWriter = PdfWriter.getInstance(document, fos);
       // document.open();
        System.out.println("CONVERTER START.......");
        images.forEach(image -> {
            Image img = null;
            try {
                img = Image.getInstance(image.toString());
            } catch (BadElementException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            document.setPageSize(img);
            document.newPage();
            img.setAbsolutePosition(0 ,0);
            try {
                document.add(img);
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            }
        });
        //document.close();
    }
}
