package org.ivo.mlt_attachments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class ImportMltAttachmentsApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(ImportMltAttachmentsApplication.class, args);

        System.out.println("Hello World!");
        Mapper mapper = new Mapper();
        String result = mapper.deserializeJson();
    }

}
