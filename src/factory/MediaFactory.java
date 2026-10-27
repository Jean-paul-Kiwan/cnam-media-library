package factory;

import model.Media;

import java.util.HashMap;
import java.util.Map;

public class MediaFactory {

    private final Map<String, MediaCreator> creators = new HashMap<>();

    public void register(String type, MediaCreator creator) {
        creators.put(type.toLowerCase(), creator);
    }

    public Media create(String type, Map<String, Object> data) {
        MediaCreator c = creators.get(type.toLowerCase());
        if (c == null) throw new IllegalArgumentException("Unknown media type: " + type);
        return c.create(data);
    }
}

