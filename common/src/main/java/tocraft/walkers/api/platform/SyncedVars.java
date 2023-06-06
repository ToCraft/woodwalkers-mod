package tocraft.walkers.api.platform;

public class SyncedVars {
    public static boolean showPlayerNametag;
    public static boolean enableUnlockSystem;
    public static float unlockTimer;

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
}
