package org.ivo.mlt_attachments;

import org.ivo.mlt_attachments.Files.Datasaver;
import org.ivo.mlt_attachments.POJO.Attachment;
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
import java.util.List;

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
//
//        Mapper mapper = new Mapper();
//        List<Attachment> attachments = mapper.deserializeJson();
//        RestClient restClient = RestClient.builder().baseUrl(baseUrl).build();
//
//        attachments.forEach(attachment -> {
//            RestClient.ResponseSpec retrieve = restClient.get()
//                    .uri(builder -> builder.pathSegment(attachment.index(), attachment.id())
//                            .build())
//                    .header("Content-Type", "application/json")
////                    .header("X-MLT-User", userHeader)
//                    .retrieve()
//                    .onStatus(HttpStatusCode::isError,(request, response) -> {
//                        throw new RuntimeException("Misslyckades med bilaga %s".formatted(attachment));
//                    });
//
//            String body = retrieve.body(String.class);
//            System.out.println(body);
//
//        });
    }

    @GetMapping("/attachments-get")
    public void useRestClient() throws IOException {
        List<Attachment> attachments = mapper.deserializeJson();
        System.out.println("test1");
        attachments.forEach(attachment -> {
            System.out.println("test2");
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
                    System.out.println("tiff");
                    //byte[] filebytes = body.getBytes(StandardCharsets.UTF_8);
//                    datasaver.setFiletype("tiff");
//                    try {
//                        datasaver.saveData(filebytes);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }

                }
                case "image/jpeg" -> {
                    System.out.println("jpeg");
                    byte[] filebytes = body.getBytes(StandardCharsets.UTF_8);
                    int length = filebytes.length;
                    //datasaver.setFiletype("jpeg");
                    try {
                        datasaver.saveData(filebytes, "jpeg");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        });
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
