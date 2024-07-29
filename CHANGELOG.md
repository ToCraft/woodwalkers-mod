walkers 5.1
================

- fix blacklist commands stack

walkers 5
================

- **rework multi-version structure**
- update to CraftedCore 5
- fix how blacklists apply to commands
- add Mutant Monsters Integration
- fix ConcurrentModificationException when loading traits

walkers 4.6
================

- fix crash with WolfTypeProvider (1.20.6+)
- improve Ability Datapack System
- add entityBlacklistIsWhitelist with config
- support Minecraft 1.21

walkers 4.5.1
================

- better combat animation is only suppressed when morphed
- cache the abilities so the latest registered can be used
- fix wrong output when triggering /walkers playerBlacklist preventUnlocking/Morphing
- better log crash on collecting shape types
- update to craftedcore 4.2.4

walkers 4.5
================

- fix crash when handling mob AIs
- player blacklist can now be changed whether to prevent unlocking and / or morphing
- create CantInteractTrait
- create "ApiLevels" for improved ways of coding addons
- add "/walkers entityBlacklist" command
- fix bugged first person view with better combat animation
- add wolf variants for 1.20.6
- support left main hand

walkers 4.4.3
================

- update to CraftedCore 4.2 for better background performance
- rename 'skills' to 'traits' (Datapacks still work)
- add some traits for iceandfire mobs
- fix trait datapacks not loaded when no "required_mod" is specified

walkers 4.4.2
================

- update to CraftedCore 4.1 to fix several crashes

walkers 4.4.1
================

- fix data pack data not synchronized to client
- fix skill codec encoding
- add support for CompoundTags for advanced type provider

walkers 4.4
================

- fix render status bar issues on Forge/NeoForge
- improve logic for NocturnalSkill on Fabric
- prevent eating with AttackForHealthSkill
- rework Codecs for Datapacks

walkers 4.3
================

- improve logging to help datapack creators
- implement datapack by WenXin2
- fix villager variant names
- overhaul tropical fish variants
- auto-detect MoreMobVariants
- small performance patch for the skill engine
- add AttackForHealthSkill
- add NocturnalSkill
- add RaidAbility

walkers 4.2
================

- fix skeleton not "humanoid"
- entity tags can now be used for skills & blacklist
- small performance patch for the skill system
- **add variants menu!** (a menu, where you can switch between variants in your in-game HUD)
- add entity arm for horse, donkey, mule, zombie horse, skeleton horse, fox, wolf, strider, warden, allay, vex, creeper,
  zoglin & hoglin
- fix broken breathing underwater sometimes ignoring limits
- fix FearedSkill not always working
- merge special shape to special variant
- background improvements
- fix MobEffectSkill not working when registered via datapack on 1.20.1
- rework InstantDieOnDamageType
- register SlowFallingSkill for chicken

walkers 4.1
================

- improve rendering mechanic for better mod compatibility
- add more skill icons
- fix mobs not fighting back
- rework Codecs for encoding
- add AdvancedNBTEntries for TypeProvider (take a look at the wiki for details)

walkers 4.0
================
I'll start with the small changes. If a mob has an Ability but isn't listed in the abilityCooldownMap in the Config,
it'll be added to it.
Mobs, that are sensitive to water (get hurt by it) also pass this to the player. There are also many more Friends & Foes
Abilities.
The shape isn't rendered as spectator anymore, too.
For 1.20.1 and below, the First Person Model Mod doesn't crash anymore and should work fine with Walkers.
Now, let's get to the greatest change: <strong>The SkillSystem</strong>. This allows datapack creators to change the
bahavior of mob shapes! There are also some new features, such as The ReinforcementsSkill or the HumanoidSkill. I hope
you like it!
Of course there are some background improvements and new minor features, too.
Special Thanks to Yoananas_gang for supporting the project.

walkers 3.2
================

- fix crash with multiple clients
- improve attacking mechanics (e.g. shapes like the iron golem work properly now)

walkers 3.1
================

- add optional field "required_mod" to datapack variants & datapack abilities
- fix crash with 3+ player
- blacklist Dragon Mounts: Legacy - Dragon to prevent crashes
- add support for MobBattleMod (Teams work now as hostile, will work when MobBattleMob upgrades to 1.20.2+, too)

walkers 3.0
================
First of all, many background fixes, most players won't notice. Furthermore, you can't use abilities as spectator
anymore.
This update also includes some improvements, e.g. the lag when pressing "V" is fixed and some related issues, too.
If a player is riding a player transformed as horse, the horse-players sees the riding player that the correct position.
There are many metadata-fixes and improvements, too. For example, you need the CraftedCore, Architectury API, Minecraft
and Modloader version, Woodwalkrs was developed with. (It crashed before, now it shows you that you're using outdated
dependencies.)
Now, just something I personally wanted; Spiders can climb Cobwebs from now on.
But, I've laso improved many more animations, like the fox animations or the bat resting animation.
Moreover, I added a brand-new mechanic for server owner, modder and everyone interested; existing **Abilities can now be
registered for EntityTypes via Datapacks** and what I'm really proud of is a new mechanic to **register mob variants via
datapacks**. A Guide will soon be published in the [wiki](https://github.com/ToCraft/woodwalkers-mod/wiki).
Now, let's get to the latest stuff. The cat sitting animation doesn't look that glitched anymore (it's another one now)
and slimes don't hurt other slimes, but you can hurt nearby players as a slime!
Dolphins also imply Dolphin's Grace on the nearest swimming player, so you can swin with your friends.
There is also a new variant for Snow Golems, where there Pumpkin is taken off.
Finally, striders start shaking and turn purple if they're outside of lava.