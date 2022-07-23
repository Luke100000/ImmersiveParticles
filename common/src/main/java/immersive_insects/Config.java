package immersive_insects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public final class Config implements Serializable {
    @Serial
    private static final long serialVersionUID = 9132405079466337851L;

    private static final Config INSTANCE = loadOrCreate();

    public static Config getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("unused")
    public String README = "https://github.com/Luke100000/ImmersiveInsects/wiki/Config";

    public static final int VERSION = 1;

    public final int particleSpawnDistanceInChunks = 5;

    public int version = 0;

    private static File getConfigFile() {
        return new File("./config/" + Main.MOD_ID + ".json");
    }

    public void save() {
        //noinspection ResultOfMethodCallIgnored
        new File("./config").mkdirs();

        try (FileWriter writer = new FileWriter(getConfigFile())) {
            version = VERSION;
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Config loadOrCreate() {
        try (FileReader reader = new FileReader(getConfigFile())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Config config = gson.fromJson(reader, Config.class);
            if (config.version != VERSION) {
                config = new Config();
            }
            config.save();
            return config;
        } catch (IOException e) {
            e.printStackTrace();
        }
        Config config = new Config();
        config.save();
        return config;
    }
}
