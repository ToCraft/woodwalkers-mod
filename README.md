> [!NOTE]
> This is a fork of [ToCraft/woodwalkers-mod](https://github.com/ToCraft/woodwalkers-mod) ported to **Minecraft 26.1.2** (the first Mojang-mapped release). It depends on a matching fork of CraftedCore. See the [Fork Notes](#fork-notes) section for details.

---

# Woodwalkers

*Woodwalkers* is a mod lore-wise based on the book [Woodwalkers](https://www.katja-brandis.de/2016/05/11/woodwalkers/),
and technically-based on [Identity](https://www.curseforge.com/minecraft/mc-mods/identity).

## Download

[CurseForge](https://curseforge.com/minecraft/mc-mods/woodwalkers)

[Modrinth](https://modrinth.com/mod/woodwalkers)

---

**Woodwalkers Requires CraftedCore**

[CraftedCore (CurseForge)](https://www.curseforge.com/minecraft/mc-mods/crafted-core)

[CraftedCore (Modrinth)](https://modrinth.com/mod/crafted-core)

---

**Become your favourite mob!**

This mod allows you to choose a second shape, you can transform into at any time you want by just pressing a key!

Walk over lava as a strider, discovery the caves as a bat or let your foes base explode as a ghast!
**Nearly every mob has some nice abilities!**

Even your hearts and your size will be changed to the one of the mob and your nametag will stop rendering, so no one
could tell which one is the mob and which one is the player! Trick your friends!

![](https://raw.githubusercontent.com/ToCraft/woodwalkers-mod/main/assets/every_mob_is_possible.png)

![](https://raw.githubusercontent.com/ToCraft/woodwalkers-mod/main/assets/use_abilities.png)

![](https://raw.githubusercontent.com/ToCraft/woodwalkers-mod/main/assets/hide_everywhere_dont_die_with_less_lives.png)

## Getting Started

Search for your favourite mob in your world/on your server. Once you found it, press the UNLOCK_KEY (by default 'U'),
while looking directly at it. While doing, you should notice the message "You feel it tingle" or something. In case you
don't see it, reinstall the modpack your check your config. After pressing the key for 100 ticks (5 seconds), you will
transform into a mob of the same type. Your Unlock progress is complete! ;D
In case you want to change it again, change the config "unlockOverridesCurrentShape" to true, so you'll be able to
override it at any time.

You can switch your shape by pressing the TRANSFORM_KEY (by default 'G'). Choose carefully! This choice is forever! Once
chosen, you can't change it again! (But still, there's an OP-command and a config-option to reset your second shape
after death...)

## How can I support this project?

You could donate via [Patreon](https://www.patreon.com/tocraft).
Alternatively, if you want to contribute to this mod, I'm always happy about someone who translates this mod to other
languages or tells me about bugs/issues.
Everyone who helps a lot with dev-work will get a new texture as wolf which is enabled by pressing `V` (This key is only
visible to Developer and Patreons).

![](https://raw.githubusercontent.com/ToCraft/woodwalkers-mod/main/assets/dark_dev_wolf_vs_normal_wolf.png)

---

## Fork Notes

This fork ports Woodwalkers to **Minecraft 26.1.2**, the first Mojang-mapped (unobfuscated) release. Major changes relative to the upstream mod:

- **MC 26.1.2 API updates**: `ResourceKey.location()` → `.identifier()`, `Level.getDayTime()` → `getDefaultClockTime()`, `getCurrentDifficultyAt` moved to `ServerLevel` only, `startRiding` gained a third boolean parameter.
- **Permission system**: `CommandSourceStack.hasPermission(int)` removed; replaced with `Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)`.
- **KeyMapping**: Category constructor now takes `KeyMapping.Category` (registered via `KeyMapping.Category.register(Identifier)`) instead of a plain `String`.
- **Entity model subpackages**: Several models moved to subdirectories (e.g. `monster.creeper.CreeperModel`, `monster.hoglin.HoglinModel`, `monster.vex.VexModel`).
- **`AbstractHorse`** moved from `animal.horse` to `animal.equine` package.
- **`Player.getDefaultDimensions`** override removed; injection moved to `LivingEntity`.
- **`Player.attack`** refactored — `skipAttackInteraction` call moved to private `cannotAttack(Entity)` helper.
- **`FoodData.tick`** moved from `Player.tick` to `ServerPlayer.doTick`.
- **`SweetBerryBushBlock.entityInside`** gained a new `boolean` parameter.
- **`Zoglin.isTargetable`** removed; target filtering now done in a brain lambda.
- **`EntityRenderDispatcher.renderShadow`** removed; shadow size adjustment is currently disabled pending a re-implementation.
- **`AvatarRenderer.extractRenderState`** first parameter changed from `AbstractClientPlayer` to erased generic type `Avatar` (new abstract base class that `Player` now extends).
- **NeoForge**: `FMLEnvironment.dist` field → `FMLEnvironment.getDist()` method; `isEyeInFluidType` removed, replaced with vanilla `isEyeInFluid(FluidTags.WATER)`.

---

## Building from Source

This fork requires a locally-published fork of **CraftedCore**. Both repos must be built in order.

### 1. Build and publish CraftedCore

```bash
git clone https://github.com/tgboyles/craftedcore.git
cd craftedcore
./gradlew publishToMavenLocal
```

### 2. Build Woodwalkers

```bash
git clone https://github.com/tgboyles/woodwalkers-mod.git
cd woodwalkers-mod
./gradlew build
```

Built jars are placed in `fabric/build/libs/` and `neoforge/build/libs/`.

---

## Running in Development

Make sure you have built and published CraftedCore to your local Maven cache first (see above).

### Launch the Fabric client

```bash
cd woodwalkers-mod
./gradlew :fabric:runClient
```

### Launch the NeoForge client

```bash
cd woodwalkers-mod
./gradlew :neoforge:runClient
```

The first launch will download assets and may take a few minutes.

---

### License

Woodwalkers is licensed under MIT.
