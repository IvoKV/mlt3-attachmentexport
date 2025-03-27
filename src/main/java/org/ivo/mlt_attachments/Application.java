package org.ivo.mlt_attachments;

import com.itextpdf.text.DocumentException;
import org.ivo.mlt_attachments.files.Datasaver;
import org.ivo.mlt_attachments.POJO.Attachment;
import org.ivo.mlt_attachments.fileconverteriText.ImageToPdf;
import org.ivo.mlt_attachments.filehelper.IOHelper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.client.RestClientSsl;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@RestController
public class Application {
    static final String userHeader = """
                       {"root":"1.2.752.129.2.1.4.1","extension":"SE2321000016-cwz7","permissions":["archive-care-journal-admin-read"]}""";
    static final String baseUrl = "https://prod.mlt3.sll.se/api/archive/care/journal/admin/scan/filter/attachment/";

    private final RestClient client;
    private final Mapper mapper;

    public Application(@Qualifier("attachment-client") RestClient client, Mapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    public static void main(String[] args) throws IOException {
        SpringApplication.run(Application.class, args);
    }

    @GetMapping("/attachments-get")
    public void useRestClient() throws IOException {
        List<Attachment> attachments = mapper.deserializeJson();
        System.out.println("test1");
        final AtomicInteger filecounterJpeg = new AtomicInteger();
        final AtomicInteger filecounterTiff = new AtomicInteger();
        Set<String> linkedWorkDirs = new LinkedHashSet<>();

        attachments.forEach(attachment -> {
            RestClient.ResponseSpec retrieve = client.get()
                    .uri(builder -> builder.pathSegment(attachment.index(), attachment.id())
                            .build())
                    .header("Content-Type", "application/json")
                    .header("X-MLT-User", userHeader)
                    .retrieve();
            String body = retrieve.onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                System.out.println("response = " + response.getStatusCode());

            }).body(String.class);
            Datasaver datasaver = new Datasaver();
            switch (attachment.mediaType()) {
                case "image/tiff" -> {
                    byte[] filebytes = body.getBytes(StandardCharsets.ISO_8859_1);
                    try {
                        Path pathJpeg = datasaver.createAttachmentDirectory("tiff");
                        int counter = filecounterTiff.incrementAndGet();
                        linkedWorkDirs.add(pathJpeg.toAbsolutePath().toString());
                        datasaver.saveAttachments(filebytes, pathJpeg, "tiff", counter);
                        System.out.println("counter tiff = " + counter);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                case "image/jpeg" -> {
                    byte[] filebytes = body.getBytes(StandardCharsets.ISO_8859_1);
                    try {
                        Path pathJpeg = datasaver.createAttachmentDirectory("jpeg");
                        int counter = filecounterJpeg.incrementAndGet();
                        linkedWorkDirs.add(pathJpeg.toAbsolutePath().toString());
                        datasaver.saveAttachments(filebytes, pathJpeg, "jpeg", counter);
                        System.out.println("counter jpeg = " + counter);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        ImageToPdf imageToPdf = new ImageToPdf();
        Path destinationDirectory = IOHelper.createOutputDirectory(linkedWorkDirs);
        AtomicInteger filecounterPdf = new AtomicInteger();
        linkedWorkDirs.forEach(workDir -> {
            int pdfcounter = filecounterPdf.incrementAndGet();
            try {
                List<String> filenames = imageToPdf.importImageList(workDir);
                StringBuilder sbLog = new StringBuilder();
                if(IOHelper.getMimeType(workDir).equals("tiff")) {
                   //Document pdf = imageToPdf.initializeDocument();
                    //pdf.open();
                    sbLog = imageToPdf.tiffImagesToPdf(filenames, destinationDirectory, pdfcounter);
                    //if(pdf != null) pdf.close();
                }
                else if(IOHelper.getMimeType(workDir).equals("jpeg")) {
                    //Document pdf = imageToPdf.initializeDocument(PageSize.A1);
                    //pdf.open();
                    sbLog = imageToPdf.jpegImagesToPdf(filenames, destinationDirectory, pdfcounter);
                }

                if(sbLog.length() > 0) {
                    System.out.println(sbLog);
                }
                else {
                    System.out.println("Conversion done successfully");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            }
        });


        StringBuilder sb = new StringBuilder();
        sb.append("Alla bilagor överförda, jobbet är avslutat.\n");
        sb.append("Antal tiff-ar: " + filecounterTiff.get() + "\n");
        sb.append("Antal jpeg-ar: " + filecounterJpeg.get() + "\n");
        int sum = filecounterJpeg.get() + filecounterTiff.get();
        sb.append("Summa total filer: " + sum + "\n");
        System.out.println(sb.toString());
    }

    @Bean
    @Qualifier("attachment-client")
    public static RestClient getRestClient(RestClientSsl ssl) {
        return RestClient.builder()
                .baseUrl("https://prod.mlt3.sll.se/api/archive/care/journal/admin/scan/filter/attachment/")
                .apply(ssl.fromBundle("mlt-truststore"))
                .build();
    }
}
