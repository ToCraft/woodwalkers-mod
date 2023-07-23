package tocraft.walkers.api.platform;

import java.util.ArrayList;
import java.util.List;

public class SyncedVars {
    private static boolean showPlayerNametag;
    private static boolean enableUnlockSystem;
    private static float unlockTimer;
    private static boolean unlockOveridesCurrentShape;
    private static List<String> shapeBlacklist = new ArrayList<>();

    public static void setShowPlayerNametag(boolean NewShowPlayerNametag) {
        showPlayerNametag = NewShowPlayerNametag;
    }

    public static boolean getShowPlayerNametag() {
        return showPlayerNametag;
    }

    public static void setEnableUnlockSystem(boolean NewEnableUnlockSystem) {
        enableUnlockSystem = NewEnableUnlockSystem;
    }

    public static boolean getEnableUnlockSystem() {
        return enableUnlockSystem;
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
}
