package org.ivo.mlt_attachments.fileconverteriText;

import com.itextpdf.text.*;
import com.itextpdf.text.io.FileChannelRandomAccessSource;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;
import org.ivo.mlt_attachments.filehelper.IOHelper;

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
        int falseBooleansTiff = 0;
        StringBuilder sb = new StringBuilder();

        String pdfName = "pdfExport_tiff";
        Path filenamePdf = Paths.get(destinationPath.toString(), pdfName + filecounter + ".pdf");
        try {
            Document pdf = initializeDocument();
            PdfWriter.getInstance(pdf, new FileOutputStream(filenamePdf.toString()));
            pdf.open();
            PdfWriter.getInstance(pdf, new FileOutputStream(filenamePdf.toString()));
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
                }

                if (!(IOHelper.fileExists(filenamePdf.toString()) && filesAdded == files.size())) {
                    falseBooleansTiff++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (falseBooleansTiff > 0) {
            sb.append("Errors found in tiff-conversion: " + falseBooleansTiff + "\n");
        }
        sb.trimToSize();
        return sb;
    }

    public StringBuilder jpegImagesToPdf(List<String> files, Path destinationPath, int filecounter) throws IOException, DocumentException {
        int falseBooleansJpeg = 0;
        StringBuilder sb = new StringBuilder();
        String pdfName = "pdfExport_jpeg";

        Path filenamePdf = Paths.get(destinationPath.toString(), pdfName + filecounter + ".pdf");
        try {
            Document pdf = initializeDocument(PageSize.A1);
            pdf.open();
            PdfWriter.getInstance(pdf, new FileOutputStream(filenamePdf.toString()));
            int filesAdded = 0;
            //Document pdf = initializeDocument(PageSize.A1);
            if (!pdf.isOpen()) pdf.open();
            for (String imgFilename : files) {
                PdfWriter.getInstance(pdf, new FileOutputStream(filenamePdf.toString()));
                FileOutputStream fos = new FileOutputStream(filenamePdf.toString());
                PdfWriter writer = PdfWriter.getInstance(pdf, fos);
                writer.open();
                //pdf.open();

                FileChannelRandomAccessSource source = new FileChannelRandomAccessSource(new FileInputStream(imgFilename).getChannel());
                RandomAccessFileOrArray file1 = new RandomAccessFileOrArray(source);
                //int pages = JBIG2Image.getNumberOfPages(file1);
                //for(int page = 1; page <= pages; pages++) {
                //Image img = JBIG2Image.getJbig2Image(file1, 1);
                Rectangle pageSize = pdf.getPageSize();
                //img.scaleAbsoluteHeight(pageSize.getHeight());
                //img.scaleAbsoluteWidth(pageSize.getWidth());
                if (pdf.add(Image.getInstance(imgFilename))) filesAdded++;
                //pdf.close();
                writer.close();
                if (files.getLast() == imgFilename) {
                    if (pdf.isOpen()) pdf.close();
                } else {
                    pdf.newPage();
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        if (falseBooleansJpeg > 0) {
            sb.append("Errors found in tiff-conversion: " + falseBooleansJpeg + "\n");
        }
        sb.trimToSize();
        return sb;
    }
}
