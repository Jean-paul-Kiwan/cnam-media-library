package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Subject implements Serializable {
    private final String code;
    private final String specialityName;
    private final List<Integer> mediaIds = new ArrayList<>();

    public Subject(String code, String specialityName) {
        this.code = code;
        this.specialityName = specialityName;
    }

    public String getCode() { return code; }
    public String getSpecialityName() { return specialityName; }
    public List<Integer> getMediaIds() { return mediaIds; }

    public void addMedia(int mediaId) {
        if (!mediaIds.contains(mediaId)) mediaIds.add(mediaId);
    }

    public void removeMedia(int mediaId) {
        mediaIds.remove((Integer) mediaId);
    }
}
