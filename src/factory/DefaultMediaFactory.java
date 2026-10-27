package factory;

import model.Document;
import model.Media;
import model.OnlineQuiz;
import model.VideoSession;

import java.util.List;
import java.util.Map;

public class DefaultMediaFactory {

    public static MediaFactory build() {
        MediaFactory f = new MediaFactory();

        f.register("document", data -> new Document(
                (int) data.get("id"),
                (String) data.get("title"),
                (String) data.get("author"),
                (int) data.get("year"),
                (String) data.get("description"),
                (List<String>) data.get("subjects"),
                (int) data.get("pages")
        ));

        f.register("video", data -> new VideoSession(
                (int) data.get("id"),
                (String) data.get("title"),
                (String) data.get("author"),
                (int) data.get("year"),
                (String) data.get("description"),
                (List<String>) data.get("subjects"),
                (int) data.get("duration")
        ));

        f.register("quiz", data -> new OnlineQuiz(
                (int) data.get("id"),
                (String) data.get("title"),
                (String) data.get("author"),
                (int) data.get("year"),
                (String) data.get("description"),
                (List<String>) data.get("subjects"),
                (int) data.get("estimatedDuration"),
                (String) data.get("difficulty")
        ));

        return f;
    }
}
