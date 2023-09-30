package tocraft.walkers.api.platform;

import tocraft.walkers.Walkers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class VersionChecker {
	public static String versionURL = "https://raw.githubusercontent.com/ToCraft/woodwalkers-mod/arch-1.19.4/gradle.properties";
	public static boolean checkedUpdate = false;

    public static void checkForUpdates(ServerPlayer player) {
		if (!checkedUpdate) {
			try {
				String line;
				URL url = (new URI(versionURL)).toURL();
				BufferedReader updateReader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
				String updateVersion = Walkers.getVersion();
				while ((line = updateReader.readLine()) != null) {
					if (line.startsWith("mod_version=")) {
						updateVersion = line.split("mod_version=")[1];
						break;
					}
				}
				updateReader.close();
				if (!updateVersion.equals(Walkers.getVersion())) {
					player.sendSystemMessage(Component.translatable("walkers.update", updateVersion));
				}
			}
			catch (Exception e) {
				Walkers.LOGGER.warn("Version check failed");
				e.printStackTrace();
			}
			checkedUpdate = true;
		}
	}
}