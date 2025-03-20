package org.ivo.mlt_attachments;

public class Attachments {
    private String attachment;
    private String mediaType;
    private Long size;

    public Attachments(String attachment, String mediaType, Long size) {
        this.attachment = attachment;
        this.mediaType = mediaType;
        this.size = size;
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
