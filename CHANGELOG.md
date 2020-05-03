# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html) 
with an added upstream's major version number in front of the major version so we have an better distinction from
Nukkit 1.X and 2.X.

## [Unreleased]
Click the link above to see the future.

### Added
- This change log file
- [#108] EntityMoveByPistonEvent
- [#140] `isUndead()` method to the entities

### Fixes
- [#129] A typo in the BlockBambooSapling class name **(breaking change)**
- [#102] Leaves decay calculation
- [#87] Arrows in offhand are black in the first person view
- [#46] checked if ProjectileHitEvent is cancelled before the action execution
- [#108] Lever sounds
- [#108] Incorrect sponge particles
- [#12] Wrong redstone signal from levers
- [#129] You can now shift to climb down while you are in the edges of a scaffold
- [#129] Fixes a turtle_egg placement validation
- [#129] Campfire can no longer be placed over an other campfire directly
- [#129] The sound that campfire does when it extinguishes
- [#140] Instant damage and instant health are now inverted when applied to undead entities
- [#140] A collision detection issue on Area Effect Cloud which could make it wears off way quicker than it should
- [#152] Changes the blue_ice blast resistance from 2.5 to 14
- [#170] Trapdoors behaving incorrectly when they receive redstone signal
- [#219] Button and door sounds

### Changed
- Make BlockLectern implements Faceable
- The versioning convention now follows this pattern:<br>`upstream.major.minor.patch-PN`<br>[Click here for details.](https://github.com/GameModsBR/PowerNukkit/blob/7912aa4be68e94a52762361c2d5189b7bbc58d2a/pom.xml#L8-L14)

## [1.1.1.0-PN] - 2020-01-21
### Fixes
- Piston heads not rendering
- Cauldron implementation, should be closer to vanilla now
- Implements hashCode in the NBT Tags, fixes usage with Set and HashMap
- Fixes BaseInventory ignoring it's own max stack size
- Fix cauldron's lightFilter value
- Fix the project throwing sound effect
- No particles when snow hits something
- Fixes projectile sounds
- Fixes egg particles and exp sounds
- The anvil block implementation
- Plants now requires light to grow
- Fix player does not get update for own skin
- Fix ~ operator in teleport command
- Fix ~ operator in /particle command
- Fall damage with slow falling effect
- Fishing Hook drag and gravity values
- [a8247360] Crops, grass, leaves, tallgrass growth and population
- Fixes fuzzy spawn radius calculation
- [#49] noDamageTicks should make the entity completely invulnerable while active
- [#54] Fixes movement issues on heavy server load
- [#57] Fixes block placement of Bone Block, End Portal Frame, Jukebox and Observer

### Changed
- Unregistered block states will be shown as 248:0 (minecraft:info_update) now
- Improves the UI inventories
- The codename to PowerNukkit to distinct from [NukkitX]'s implementation
- [#50] The kick message is now more descriptive
- [#80] Merged the "New RakNet Implementation" pull request which greatly improves the server performance and connections

### Added 
- Waterlogging support
- Support with blocks ID higher then 255 to the Anvil save format
- Support for blocks with 6 bits data value (used to support only 4 bits)
- [#51] Support for the offhand slot
- [#52] Merge the "More redstone components" pull request which fixes and implements many redstone related blocks
- [#53] Merge the "Vehicle event fix" pull request which add new events and fixes damage issues related to vehicles
- [#55] Minecart (chest and hopper) inventories
- [#56] ServerStopEvent
- Shield block animation (without damage calculation)
- New gamerules
- The /setblock command
- Dyeing leather support to cauldrons
- Color mixing support to cauldron
- Implementation for the entities (without AI):
    - Bees
    - Lingering Potions
    - Area Effect Clouds
- Implementation for the items:
    - Honey
    - Honey Bottle
    - Honeycomb
    - Suspicious Stew
    - Totem of Undying (without functionality)
    - Name Tags
    - Shulker Shell
- Implementation for the blocks:
    - [#58] Daylight Sensor
    - Lectern
    - Smoker
    - Blast Furnace
    - Light Block
    - Honeycomb Block
    - Wither Roses
    - Honey Block
    - Acacia, Birch, Dark Oak, Jungle, Spruce signs
    - Composter
    - Andesite, Polished Andesite, Diorite, Polished Diorite, End Brick, Granited, Polished Granite, Mossy Cobblestone stairs
    - Mossy Stone Brick, Prismarine Brick, Red Nether Brick stairs, Smooth Quartz, Red Sandstone, Smooth Sandstone stairs
    - Beehive and Bee Nests
    - Sticky Piston Head
    - Lava Cauldron
    - Wood (barks)
    - Jigsaw
    - Stripped Acacia, Birch, Dark Oak, Jungle, Oak and Spruce logs and barks
    - Blue Ice
    - Seagrass
    - Coral
    - Coral Fans
    - Coral Blocks
    - Dried Kelp Block
    - Kelp
    - Carved Pumpkin
    - Smooth Stone
    - Acacia, Birch, Dark Oak, Jungle, Spruce Button
    - Acacia, Birch, Dark Oak, Jungle, Spruce Pressure Plate
    - Acacia, Birch, Dark Oak, Jungle, Spruce Trapdoor
    - Bubble Column
    - Scaffolding
    - Sweet Berry Bush
    - Conduit
    - All stone type slabs
    - Lantern
    - Barrel
    - Campfire
    - Cartography Table
    - Fletching Table
    - Smithing Table
    - Bell
    - Turtle Eggs
    - Grindstone
    - Stonecutter
    - Loom
    - Bamboo

[Unreleased]: https://github.com/GameModsBR/PowerNukkit/compare/v1.1.1.0-PN...HEAD
[1.1.1.0-PN]: https://github.com/GameModsBR/PowerNukkit/compare/1ac6d50d36f07b6f1a02df299d9591d78c379db9...v1.1.1.0-PN#files_bucket

[a8247360]: https://github.com/GameModsBR/PowerNukkit/commit/a8247360

[NukkitX]: https://github.com/NukkitX/Nukkit

[#12]: https://github.com/GameModsBR/PowerNukkit/issues/12
[#46]: https://github.com/GameModsBR/PowerNukkit/issues/87
[#49]: https://github.com/GameModsBR/PowerNukkit/pull/49
[#50]: https://github.com/GameModsBR/PowerNukkit/pull/50
[#51]: https://github.com/GameModsBR/PowerNukkit/pull/51
[#52]: https://github.com/GameModsBR/PowerNukkit/pull/52
[#53]: https://github.com/GameModsBR/PowerNukkit/pull/53
[#54]: https://github.com/GameModsBR/PowerNukkit/pull/54
[#55]: https://github.com/GameModsBR/PowerNukkit/pull/55
[#56]: https://github.com/GameModsBR/PowerNukkit/pull/56
[#57]: https://github.com/GameModsBR/PowerNukkit/pull/57
[#58]: https://github.com/GameModsBR/PowerNukkit/pull/58
[#80]: https://github.com/GameModsBR/PowerNukkit/pull/80
[#87]: https://github.com/GameModsBR/PowerNukkit/issues/87
[#102]: https://github.com/GameModsBR/PowerNukkit/pull/102
[#108]: https://github.com/GameModsBR/PowerNukkit/pull/108
[#129]: https://github.com/GameModsBR/PowerNukkit/pull/108
[#140]: https://github.com/GameModsBR/PowerNukkit/pull/108
[#152]: https://github.com/GameModsBR/PowerNukkit/pull/152
[#170]: https://github.com/GameModsBR/PowerNukkit/pull/170
[#219]: https://github.com/GameModsBR/PowerNukkit/pull/170
