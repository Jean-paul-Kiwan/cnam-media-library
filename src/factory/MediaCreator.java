package factory;

import model.Media;
import java.util.Map;

@FunctionalInterface
public interface MediaCreator {
    Media create(Map<String, Object> data);
}
