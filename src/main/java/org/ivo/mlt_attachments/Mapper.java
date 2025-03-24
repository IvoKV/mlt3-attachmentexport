package org.ivo.mlt_attachments;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ivo.mlt_attachments.POJO.Attachment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class Mapper {

    public List<Attachment> deserializeJson() throws IOException {

        String json = Files.readString(Path.of("src/main/resources/attachmentmetadata_copy.json"));

        ObjectMapper objectMapper = new ObjectMapper();
        List<Attachment> attachment = objectMapper.readValue(json, new TypeReference<>() {});
        System.out.println(attachment.size());

        return attachment;
    }
}
