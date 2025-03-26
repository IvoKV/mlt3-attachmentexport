package org.ivo.mlt_attachments.fileconverter;

import com.itextpdf.text.*;
import com.itextpdf.text.io.FileChannelRandomAccessSource;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;
import org.ivo.mlt_attachments.filehelper.IOHelper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ImageToPdf {
    private Set<String> workDir = new LinkedHashSet<>();
    private List<Path> paths = new ArrayList<>();
    private List<Byte> images;
    private String mimetype;

    public ImageToPdf() {
    }

    public ImageToPdf(Set<String> workDir) throws IOException {
        this.workDir = workDir;
    }

    public List<String> importImageList(String wokrDir) throws IOException {
         paths = Files.walk(Paths.get(wokrDir))
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());

         List<String> filenames = paths
                 .stream()
                 .map(p -> p.toString())
                 .collect(Collectors.toList());
         return filenames;
    }

    private Document initializeDocument(){
        Document document = new Document(PageSize.A3, 20.0f, 20.0f, 20.0f, 150.0f);
        return document;
    }

    public int imagesToPdf(List<String> files, Path destinationPath, int filecounter) throws IOException, DocumentException {
        int falseBooleans = 0;

        String pdfName = "pdfExport_";
        Path filenamePdf = Paths.get(destinationPath.toString(),pdfName + filecounter + ".pdf");
        try {
            Document pdf = initializeDocument();
            PdfWriter.getInstance(pdf, new FileOutputStream(filenamePdf.toString()));
            int filesAdded = 0;

            for(String imgFilename : files) {
                String selection = imgFilename.substring(imgFilename.indexOf(".") + 1);
                if (selection.equals("tiff")) {
                    try {
                        pdf.open();
                        FileChannelRandomAccessSource source = new FileChannelRandomAccessSource(new FileInputStream(imgFilename).getChannel());
                        RandomAccessFileOrArray file = new RandomAccessFileOrArray(source);
                        int pages = TiffImage.getNumberOfPages(file);
                        for (int page = 1; page <= pages; page++) {
                            Image img = TiffImage.getTiffImage(file, page);
                            if (pdf.add(img)) filesAdded++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    pdf.close();
                    if (! (IOHelper.fileExists(filenamePdf.toString()) &&  filesAdded == files.size())) {
                        falseBooleans++;
                    }
                }
                else if(selection.equals("jpeg")){
                    try {
                        FileOutputStream fos = new FileOutputStream(filenamePdf.toString());
                        PdfWriter writer = PdfWriter.getInstance(pdf, fos);
                        writer.open();
                        pdf.open();
                        if (pdf.add(Image.getInstance(imgFilename))) filesAdded++;
                        pdf.close();
                        writer.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (! (IOHelper.fileExists(filenamePdf.toString()) &&  filesAdded == files.size())) {
                falseBooleans++;
            }
        }
        catch(FileNotFoundException | DocumentException e1){
           throw new RuntimeException(e1);
        }
        return falseBooleans;
    }
}
