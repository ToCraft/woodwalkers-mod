package tocraft.walkers.api.platform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dev.architectury.platform.Platform;

public class ConfigLoader {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static WalkersConfig read() {
		Path configFolder = Platform.getConfigFolder();
		Path configFile = Paths.get(configFolder.toString(), "walkers.json");

		// Write & return a new config file if it does not exist.
		if (!Files.exists(configFile)) {
			WalkersConfig config = new WalkersConfig();
			writeConfigFile(configFile, config);
			return config;
		} else {
			try {
				WalkersConfig newConfig = GSON.fromJson(Files.readString(configFile), WalkersConfig.class);

				// At this point, the config has been read, but there is a chance the config
				// class has new values which are not in the file.
				// We simply re-save the config file to save these values (because they will be
				// filled in at this point).
				writeConfigFile(configFile, newConfig);

				return newConfig;
			} catch (IOException exception) {
				exception.printStackTrace();
				return new WalkersConfig();
			}
		}
	}

	private static void writeConfigFile(Path file, WalkersConfig config) {
		try {
			if (!Files.exists(file)) {
				Files.createFile(file);
			}

			Files.writeString(file, GSON.toJson(config));
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
}
