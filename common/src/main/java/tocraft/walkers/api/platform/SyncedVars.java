package tocraft.walkers.api.platform;

public class SyncedVars {
    private static boolean showPlayerNametag;
    private static boolean enableUnlockSystem;
    private static float unlockTimer;
    private static boolean unlockOveridesCurrentShape;

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
}
