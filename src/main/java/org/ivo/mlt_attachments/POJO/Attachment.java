package org.ivo.mlt_attachments.POJO;

public record Attachment(String attachment, String mediaType, Long size) {

    public String index(){
        return attachment.split(":")[0];
    }

    public String id() {
        return attachment.split(":")[1];
    }
}
