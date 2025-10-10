walkers 7.2.0
================
- fix the Permanent flight bug/Incompatibility with Essential Commands (by FugLong)
- fix concurrent exception with AbilityRegistry
- support WTHIT and prob. some other WAILA forks, too

walkers 7.1.0
================
- fix phantoms flying trait
- add config option to overwrite the API Level
- fix unlocking messages still displayed with 2nd shape

walkers 7.0
================
- port to 1.21.7 & 1.21.8
- refactor code
- remove multi-version structure
- improve squid animation
- improve rendering traits in screens
- **change variant menu behaviour**
- add guide for the variant menu

walkers 6.6
================
- fix crash on NeoForge 1.21.5
- add option to block amor slots

walkers 6.5
================
- fix some arm rendering issues
- add BreezeAbility
- disable abilities for snow golem by default
- fix Hoglin, Piglin & Brute shaking
- fix LlamaAbility
- fix wither heads
- add ExplosionAbility to Alex Caves' Nucleeper
- fix swimming speed for aquatic mobs
- add InvulnerabilityTrait
- add GoatAbility
- add GuardianAbility

walkers 6.4
================
- support 1.21.5
- EoL for versions older then 1.21.5
- API overhaul for TypeProviders (variants)
- slightly modify advanced system to register variants & remove old system **You need to update your data packs!**

walkers 6.3
================
- fix render issue with MobHealthBar (and similar mods)
- FINALLY fixed issue with the data managers
- improve variant menu for biome based variants

walkers 6.2
================
- add option to blacklist traits
- TEMPORARY fix for data packs
- blacklist whisperwoods:wisp

walkers 6.1
================
- fix Parrots get poison when eating cookies
- fix maxAmorToughness config option
- improve hitbox handling, handle scale attribute
- scale step height
- add CantFreeze trait
- add support for various minecraft entity type tags
- remove jump boost for slimes
- add AlexsCaves mobs to flying.json
- fix polar bear left arm
- support 1.21.4

walkers 6.0
================
- port to 1.21.3
- many background improvements & code cleanup
- add turkish by Feitan_Portor
- update clientside hitbox on respawn

walkers 5.4
================

- fix Incompatibility with CarryOn mod
- fix rendering for the RiderTrait
- improve Ability API for usage in other mods (by Shap_po)
- add Ukrainian translation (by Shap_po)
- blacklist irons_spellbooks:summoned_vex
- add support for Backported Wolves

walkers 5.3.3
================

- fix crash on startup for Forge/NeoForge

walkers 5.3.2
================

- UNTESTED fix for crash with ImmunityTrait on <1.20.1

walkers 5.3.1
================

- fix invalid mixin config
- support for Craziness Awakened

walkers 5.3
================

- fix Generic Abilities overwrite Specific Abilities
- add Regeneration 2 & AttackForHealthTrait for Abilities
- fix exhaustion for AttackForHealth Trait
- add GetItemAbility
- fix Block Breaking speed when flying & swimming
- add ImmunityTrait
- support for Exotic Birds Mod

walkers 5.2
================

- Add Traditional Chinese (by dirtTW)
- fix special shape access
- add charged creeper variant
- add comments to config

walkers 5.1
================

- fix blacklist commands stack
- add FearedTrait & PreyTrait for Alex's Mobs
- fix ConcurrentModificationException on AbilityRegistry
- rework registration of PreyTrait & FearedTrait
- fix crash when clicking on player
- add compatibility for PlayerAbilityLib
- fix Bat Animation for 1.20.4+
- fix item pos for allays
- allow selectors & multiple players in commands
- add scaling armor attribute & armor toughness attribute
- background improvements & fixes

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
