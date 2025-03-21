package org.ivo.mlt_attachments.POJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
public record Attachment(String attachment, String mediaType, Long size) {

    public String index(){
        return attachment.split(":")[0];
    }

    public String id() {
        return attachment.split(":")[1];
    }
}
/*
@JsonIgnoreProperties(ignoreUnknown = true)
public class Attachment {

    private String attachment;
    private String mediaType;
    private Long size;

    public Attachment() {
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
*/