package uz.consortgroup.course_service.entity.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MimeType {
    // TEXT
    TEXT_PLAIN("text/plain"),
    TEXT_HTML("text/html"),
    TEXT_MARKDOWN("text/markdown"),

    // IMAGE
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    IMAGE_GIF("image/gif"),
    IMAGE_SVG("image/svg+xml"),

    // AUDIO
    AUDIO_MP3("audio/mpeg"),
    AUDIO_OGG("audio/ogg"),

    // VIDEO
    VIDEO_MP4("video/mp4"),
    VIDEO_WEBM("video/webm"),
    VIDEO_OGG("video/ogg"),
    VIDEO_MOV("video/quicktime"),
    VIDEO_AVI("video/x-msvideo"),
    VIDEO_MKV("video/x-matroska"),

    // PDF / DOCUMENTS
    APPLICATION_PDF("application/pdf"),
    APPLICATION_MSWORD("application/msword"),
    APPLICATION_DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    APPLICATION_PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation"),

    // ARCHIVES
    APPLICATION_ZIP("application/zip"),
    APPLICATION_RAR("application/vnd.rar"),

    // JSON / XML
    APPLICATION_JSON("application/json"),
    APPLICATION_XML("application/xml");

    private final String value;

    MimeType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static MimeType fromContentType(String contentType) {
        if (contentType == null) {
            throw new IllegalArgumentException("Content type cannot be null");
        }
        for (MimeType mimeType : MimeType.values()) {
            if (mimeType.value.equalsIgnoreCase(contentType)) {
                return mimeType;
            }
        }
        throw new IllegalArgumentException("Unsupported MIME type: " + contentType);
    }
}