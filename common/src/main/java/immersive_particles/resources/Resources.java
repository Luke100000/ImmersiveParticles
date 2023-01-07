package immersive_particles.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import immersive_particles.Main;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serial;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public interface Resources {
    String PREFIX_JAR = "/assets/immersive_particles/";
    String PREFIX_WORKING = "./";

    Gson GSON = new GsonBuilder().create();

    static Map<String, String> getResourceFiles(String path, String extension) {
        Map<String, String> files = new HashMap<>();

        // Load mod files
        try {
            URL uri = Main.class.getResource(PREFIX_JAR + path);
            if (uri != null && uri.toURI().getScheme().equals("jar")) {
                try (FileSystem fileSystem = FileSystems.newFileSystem(uri.toURI(), Collections.emptyMap())) {
                    Path filePath = fileSystem.getPath(PREFIX_JAR + path);

                    try (Stream<Path> walk = Files.walk(filePath)) {
                        for (Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
                            Path f = it.next();
                            if (f.toString().toLowerCase(Locale.ROOT).endsWith("." + extension)) {
                                String name = f.getFileName().toString();
                                String content = read(f.toString().substring(1));
                                files.put(name, content);
                            }
                        }
                    }
                }
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }

        // Load custom files from save directory
        try (Stream<Path> a = Files.walk(Path.of(PREFIX_WORKING + path))) {
            a.forEach(f -> {
                if (f.toString().toLowerCase(Locale.ROOT).endsWith("." + extension)) {
                    try {
                        String name = f.getFileName().toString();
                        String content = Files.readString(f);
                        files.put(name, content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            Main.LOGGER.info("No custom Immersive Particles directory found.");
        }

        return files;
    }

    static String read(String path) throws IOException {
        return IOUtils.toString(new InputStreamReader(Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream(path))));
    }

    static <T> T read(String path, Type type) throws BrokenResourceException {
        try {
            return GSON.fromJson(Resources.read(path), type);
        } catch (IOException | JsonParseException e) {
            throw new BrokenResourceException(path, e);
        }
    }

    static <T> T read(String path, Class<T> type) throws BrokenResourceException {
        return read(path, (Type)type);
    }

    class BrokenResourceException extends Exception {
        @Serial
        private static final long serialVersionUID = -7371322414731622879L;

        BrokenResourceException(String path, Throwable cause) {
            super("Unable to load resource from path " + path, cause);
        }
    }
}
