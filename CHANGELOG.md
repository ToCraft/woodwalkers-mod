walkers 4.1
================

- improve rendering mechanic for better mod compatibility
- add more skill icons
- fix mobs not fighting back
- fix skeleton not "humanoid"
- entity tags can now be used for skills & blacklist

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