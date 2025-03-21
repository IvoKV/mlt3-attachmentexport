package org.ivo.mlt_attachments;

import org.ivo.mlt_attachments.POJO.Attachment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class Application {
    static final String userHeader = """
            "{"root":"1.2.752.129.2.1.4.1","extension":"SE2321000016-cwz7","permissions":["archive-care-journal-admin-read"]}"
            """;

    public static void main(String[] args) throws IOException {
        SpringApplication.run(Application.class, args);

        System.out.println("Hello World!");
        Mapper mapper = new Mapper();
        List<Attachment> attachments = mapper.deserializeJson();
        RestClient restClient = RestClient.builder()                .baseUrl("https://prod.mlt3.sll.se/api/archive/care/journal/admin/scan/filter/attachment/").build();

        attachments.forEach(attachment -> {
            RestClient.ResponseSpec retrieve = restClient.get()
                    .uri(builder -> builder.pathSegment(attachment.index(), attachment.id())
                            .build())
                    .header("Content-Type", "application/json")
//                    .header("X-MLT-User", userHeader)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError,(request, response) -> {
                        throw new RuntimeException("Misslyckades med bilaga %s".formatted(attachment));
                    });

            String body = retrieve.body(String.class);
            System.out.println(body);


        });
        System.out.println();
    }

}
