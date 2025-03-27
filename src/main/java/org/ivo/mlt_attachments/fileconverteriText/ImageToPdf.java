package org.ivo.mlt_attachments.fileconverteriText;

import com.itextpdf.text.*;
import com.itextpdf.text.io.FileChannelRandomAccessSource;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ImageToPdf {
    private List<Path> paths = new ArrayList<>();

    public ImageToPdf() {
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

    private Document initializeDocument() {
        return new Document(PageSize.A4, 20.0f, 20.0f, 20.0f, 150.0f);
    }

    private Document initializeDocument(Rectangle pageSize) throws IOException {
        return new Document(pageSize, 20.0f, 20.0f, 20.0f, 150.0f);
    }

    public StringBuilder tiffImagesToPdf(List<String> files, Path destinationPath, int filecounter) throws IOException, DocumentException {
        StringBuilder sb = new StringBuilder();
        String pdfName = "pdfExport_tiff";
        Path filenamePdf = Paths.get(destinationPath.toString(), pdfName + filecounter + ".pdf");
        try {
            Document pdf = initializeDocument();
            PdfWriter.getInstance(pdf, new FileOutputStream(filenamePdf.toString()));
            pdf.open();
            int filesAdded = 0;
            for (String imgFilename : files) {
                try {
                    FileChannelRandomAccessSource source = new FileChannelRandomAccessSource(new FileInputStream(imgFilename).getChannel());
                    RandomAccessFileOrArray file = new RandomAccessFileOrArray(source);
                    int pages = TiffImage.getNumberOfPages(file);
                    for (int page = 1; page <= pages; page++) {
                        Image img = TiffImage.getTiffImage(file, page);
                        Rectangle pageSize = pdf.getPageSize();
                        img.scaleAbsoluteHeight(pageSize.getHeight());
                        img.scaleAbsoluteWidth(pageSize.getWidth());
                        if (pdf.add(img)) filesAdded++;
                    }
                    if (files.getLast() == imgFilename) {
                        if (pdf.isOpen()) pdf.close();
                    } else {
                        pdf.newPage();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            if (files.size() != filesAdded) {
                sb.append("Error/missmatch in processing images\n");
                sb.append("Processed files: " + filesAdded + "\t");
                sb.append("Expected: " + files.size() + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sb.trimToSize();
        return sb;
    }

    public StringBuilder jpegImagesToPdf(List<String> files, Path destinationPath, int filecounter) throws IOException, DocumentException {
        StringBuilder sb = new StringBuilder();
        String pdfName = "pdfExport_jpeg";
        Path filenamePdf = Paths.get(destinationPath.toString(), pdfName + filecounter + ".pdf");
        Document pdf = initializeDocument(PageSize.A1);
        int filesAdded = 0;
        try {
            FileOutputStream fos = new FileOutputStream(filenamePdf.toString());
            PdfWriter writer = PdfWriter.getInstance(pdf, fos);
            writer.open();
            pdf.open();
            for (String imgFilename : files) {
                if (pdf.add(Image.getInstance(imgFilename))) filesAdded++;
                if(files.getLast() == imgFilename) {
                    pdf.close();
                    writer.close();
                }
                else {
                    pdf.newPage();
                }
            }
            if (files.size() != filesAdded) {
                sb.append("Error/missmatch in processing images\n");
                sb.append("Processed files: " + filesAdded + "\t");
                sb.append("Expected: " + files.size() + "\n");
            }
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
        sb.trimToSize();
        return sb;
    }
}
