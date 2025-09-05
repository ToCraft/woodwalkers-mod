package dev.tocraft.walkers.api.platform;

import dev.tocraft.craftedcore.event.Event;
import dev.tocraft.craftedcore.event.EventFactory;
import dev.tocraft.walkers.Walkers;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public enum ApiLevel {
    /**
     * The players can't use Woodwalkers unlock method, can't open the variants menu and can't morph themselves but only via commands or code
     */
    API_ONLY(false, false, false),
    /**
     * The players can't open the variants menu and can't morph themselves but only via commands or code and can still use Woodwalkers unlock method to set their 2nd Shape
     */
    UNLOCK_ONLY(false, false, true),
    /**
     * The players can't use Woodwalkers unlock method, can't open the variants menu but can still morph freely
     */
    MORPHING_ONLY(true, false, false),
    /**
     * The players can't use Woodwalkers unlock method, but can open the variants menu and can morph freely
     */
    MORPHING_AND_VARIANTS_MENU_ONLY(true, true, false),
    /**
     * The players can use Woodwalkers without limitations
     */
    DEFAULT(true, true, true);

    public final boolean canMorph;
    public final boolean allowVariantsMenu;
    public final boolean canUnlock;

    ApiLevel(boolean canMorph, boolean allowVariantsMenu, boolean canUnlock) {
        this.canMorph = canMorph;
        this.allowVariantsMenu = allowVariantsMenu;
        this.canUnlock = canUnlock;
    }

    /**
     * Called in order to set the API Level of the mod
     */
    public static void setApiLevel(ApiLevel apiLevel) {
        if (ApiLevel.getCurrentLevel().compareTo(apiLevel) > 0) {
            ApiLevel.ON_API_LEVEL_CHANGE_EVENT.invoke().onApiLevelChange(apiLevel);
        }
    }

    public static void readConfig() {
        int i = Walkers.CONFIG.apiLevel;
        if (i >= 0 && i < ApiLevel.values().length) {
            setApiLevel(ApiLevel.values()[i]);
        }
    }

    /**
     * This event should be invoked in order to modify the api level
     */
    public static final Event<OnApiLevelChange> ON_API_LEVEL_CHANGE_EVENT = EventFactory.createWithVoid();
    private static ApiLevel CURRENT_LEVEL = DEFAULT;
    private static @Nullable ApiLevel CLIENT_LEVEL = null;

    static {
        ON_API_LEVEL_CHANGE_EVENT.register(apiLevel -> CURRENT_LEVEL = apiLevel);
    }

    public static ApiLevel getCurrentLevel() {
        return CURRENT_LEVEL;
    }

    @Nullable
    public static ApiLevel getClientLevel() {
        return CLIENT_LEVEL;
    }

    @FunctionalInterface
    public interface OnApiLevelChange {
        void onApiLevelChange(ApiLevel apiLevel);

        default void setServerApiLevel(ApiLevel serverApiLevel) {
            CLIENT_LEVEL = getCurrentLevel();
            onApiLevelChange(serverApiLevel);
        }
    }
}
