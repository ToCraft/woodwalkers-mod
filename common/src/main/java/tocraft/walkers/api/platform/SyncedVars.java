package tocraft.walkers.api.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SyncedVars {
	private static boolean showPlayerNametag;
	private static float unlockTimer;
	private static boolean unlockOveridesCurrentShape;
	private static List<String> shapeBlacklist = new ArrayList<>();
	private static List<UUID> playerBlacklist = new ArrayList<>();

	public static void setShowPlayerNametag(boolean NewShowPlayerNametag) {
		showPlayerNametag = NewShowPlayerNametag;
	}

	public static boolean getShowPlayerNametag() {
		return showPlayerNametag;
	}

	public static void setUnlockTimer(float NewUnlockTimer) {
		unlockTimer = NewUnlockTimer;
	}

	public static float getUnlockTimer() {
		return unlockTimer;
	}

	public static void setUnlockOveridesCurrentShape(boolean NewUnlockOveridesCurrentShape) {
		unlockOveridesCurrentShape = NewUnlockOveridesCurrentShape;
	}

	public static boolean getUnlockOveridesCurrentShape() {
		return unlockOveridesCurrentShape;
	}

	public static void setShapeBlacklist(List<String> NewShapeBlacklist) {
		shapeBlacklist = NewShapeBlacklist;
	}

	public static void setShapeBlacklist(String NewShapeBlacklistString) {
		NewShapeBlacklistString = NewShapeBlacklistString.replace("[", "");
		NewShapeBlacklistString = NewShapeBlacklistString.replace("]", "");

		shapeBlacklist.clear();
		for (String NewShapeBlacklistEntry : NewShapeBlacklistString.split(", ")) {
			shapeBlacklist.add(NewShapeBlacklistEntry);
		}
	}

	public static List<String> getShapeBlacklist() {
		return shapeBlacklist;
	}

	public static void setPlayerBlacklist(List<UUID> NewShapeBlacklist) {
		playerBlacklist = NewShapeBlacklist;
	}

	public static void setPlayerBlacklist(String NewPlayerBlacklistString) {
		NewPlayerBlacklistString = NewPlayerBlacklistString.replace("[", "");
		NewPlayerBlacklistString = NewPlayerBlacklistString.replace("]", "");

		playerBlacklist.clear();
		for (String NewPlayerBlacklistEntry : NewPlayerBlacklistString.split(", ")) {
			playerBlacklist.add(UUID.fromString(NewPlayerBlacklistEntry));
		}
	}

	public static List<UUID> getPlayerBlacklist() {
		return playerBlacklist;
	}
}
