# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html) 
with an added upstream's major version number in front of the major version, so we have a better distinction from
Nukkit 1.X and 2.X.

## [Unreleased 1.4.0.0-PN] - Future ([Check the milestone](https://github.com/PowerNukkit/PowerNukkit/milestone/15?closed=1))
Click the link above to see the future.

## [1.3.1.1-PN] - 2020-07-19
Fixes an important stability issue and improves resource pack compatibility

### Fixes
- [#390] Server stop responding due to a compression issue
- [#368] Improves resource pack compatibility

## [1.3.1.0-PN] - 2020-07-09 ([Check the milestone](https://github.com/PowerNukkit/PowerNukkit/milestone/13?closed=1))
Security, stability and enchanting table fixes alongside with few additions.

PowerNukkit now has its own [discord guild], click the link below to join and have fun!  
💬 https://powernukkit.org/discord 💬  
[![Discord](https://img.shields.io/discord/728280425255927879)](https://powernukkit.org/discord)

### Fixes
- [#326] Enchantment table not working
- [#297] Using the hoe or shovel doesn't emit any sound
- [#328] ClassCastException and some logic errors while processing the chunk backward compatibility method
- [#344] Sticky pistons not pulling other sticky piston
- [#344] The technical block names weren't being saved in memory when `GlobalBlockPalette` was loaded
- [#338] The Dried Kelp Block was not burnable as fuel
- [#232] The enchanting table level cost is now managed by the server

### Added
- [#330] The [discord guild] link to the readme
- [#352] The library jsr305 library at version `3.0.2` to add `@Nullable`, `@Nonnull` and related annotations
- [#326] A couple of new classes, methods and fields to interact with the enchanting table transactions
- [#326] The entities without AI: Hoglin, Piglin, Zoglin, Strider
- [#352] Adds default runtime id to the new blocks with meta `0`

### Changed
- [#348] Updated the guava library from `21.0` to `24.1.1`
- [#347] Updated the JWT library from `4.39.2` to `7.9`
- [#346] Updated the Log4J library from `2.11.1` to `2.13.3`
- [#326] Changed the Nukkit API version from `1.0.10` to `1.0.11`
- [#335] The chunk content version from `1` to `2`, all cobblestone walls will be reprocessed on the chunk first load after the update
- [#352] The `runtime_block_states_overrides.dat` file has been updated

## [1.3.0.1-PN] - 2020-07-01 ([Check the milestone](https://github.com/PowerNukkit/PowerNukkit/milestone/14?closed=1))
Improves plugin compatibility and downgrade the RakNet lib to solve a memory leak

### Fixes
- [#320] Multiple output crafting, cake for example
- [#323] Compatibility issue with the regular version of GAC

### Added
- [#315] Hoglin, Piglin, Zoglin and Strider entities without AI

### Changed
- [#319] The RakNet library were downgraded to 1.6.15 due to a potential memory leak issue

## [1.3.0.0-PN] - 2020-07-01 ([Check the milestone](https://github.com/PowerNukkit/PowerNukkit/milestone/11?closed=1))
Added support for Bedrock Edition 1.16.0 and 1.16.1

### Breaking change!
***This version supports a new major Minecraft version, some plugin sources might need to be updated or recompiled!*** 

The following breaking change will be pulled in `1.3.0.0-PN`
- [8a09f93](https://github.com/PowerNukkit/PowerNukkit/commit/8a09f933f83c9a52531ff8a184a58c6d733c9174) Quick craft implementation. ([NukkitX#1473](https://github.com/NukkitX/Nukkit/pull/1473)) Jedrzej* 05/06/2020

### Binary incompatibility!
- [#293] A few `Entity` data constant values were changed, plugins which uses them might need to be recompiled, no code change required

### Save format changed!
The save format has been changed to accommodate very high block data values. **Make a world backup before updating!**

### Incomplete changelog warning!
Due to the high amount of changes, and the urgency of this update, this changelog file will be released with outdated information,
please check the current changelog file in the [updated changelog] online for further details about this update.

### Disabled features warning!
* Enchanting table GUI has been temporarily disabled due to an incompatible change to the Bedrock protocol,
it's planned to be fixed on 1.3.1.0-PN
* End portal formation has been disabled due to reported crashes, it's planned to be reviewed on 1.3.1.0-PN

### Experimental warning!
This is the first release of a huge set of changes to accommodate the new Bedrock Edition 1.16.0/1.16.1 release,
please take extra cautions with this version, make constant backups and report any issues you find. 

### Deprecation warnings!
- [#293] Many `Entity` constants are deprecated and might be removed on `1.4.0.0-PN`
- [#293] `Entity.DATA_FLAG_TRANSITION_SITTING` and `DATA_FLAG_TRANSITION_SETTING` only one of them is correct, the incorrect will be removed
- [#293] `Network.inflate_raw` and `deflate_raw` does not follow the correct naming convention and will be removed. Use `inflateRaw` and `deflateRaw` instead. 
- [#293] `HurtArmorPacket.health` was renamed to `damage` and will be removed on `1.4.0.0-PN`. A backward compatibility code has been added.
- [#293] `SetSpawnPositionPacket.spawnForce` is now unused and will be removed on `1.4.0.0-PN`
- [#293] `TextPacket.TYPE_JSON` was renamed to `TYPE_OBJECT` and will be removed on `1.4.0.0-PN`
- [#293] `riderInitiated` argument was added to the `EntityLink` constructor. The old constructor will be removed on `1.4.0.0-PN`

### Fixes
- [#293] Spectator colliding with vehicles
- [#293] Ice melting into water in the Nether
- [#293] `Player.removeWindow` was able to remove permanent windows

### Added
- [#293] End portals can now be formed using Eye of Ender
- [#293] Setting to make the server ignore specific packets
- [#293] New compression/decompression methods
- [#293] Trace logging to outbound packets when trace is enabled
- [#293] The server now logs a warning when a packet violation warning is correctly received
- [#293] 12 new packets, please see the pull request file changes for details
- [#293] Many new entity data constants, please see the `Entity.java` file in the PR for details
 
### Changed
- [#293] Thorns can now be applied to any armor while enchanting
- [#293] The server now requires the clients to playing on Bedrock Edition 1.16.0
- [#293] Updated RakNet to `1.6.18`
- [#293] RakNet protocol version changed from `9` to `10`
- [#293] 10 packets, please see the pull request file changes for details
- [#293] The server have more control over the player UI now
- [#293] New entity data constants
- [#293] `FakeBlockUIComponent` now fires `InventoryCloseEvent` when the inventory is closed
- [#293] The `runtime_block_states.dat`, `recipes.json`, `entity_identifiers.dat` and `biome_definitions.dat` files have been updated
- [#293] Grindstone now clears only the enchantments and sets the repair cost to `0`, it used to clear all NBT tags


## [1.2.1.0-PN] - 2020-06-07 ([Check the milestone](https://github.com/PowerNukkit/PowerNukkit/milestone/9?closed=1))
Adds new methods to be used by plugins and fixes many issues. 

### Fixes
- [#224] Enchantment compatibility rules when merging enchanted items in an anvil
- [#113] Beehives not dropping in creative when it has bees
- [#270] Replacing sugarcane's water don't break the sugarcane immediately
- [#272] `EntityPortalEnterEvent` not being fired when entering end portals
- [#279] `BlockEndPortal` missing collision bounding box
- [#279] `Entity.checkBlockCollision()`'s over scaffolding logic outdated
- [#281] Levers and buttons don't replace the snow layers
- [#285] Chicken, cow, pig, rabbit and sheep not dropping cooked food when on fire
- [#285] Chorus plant and flower not dropping
- [#285] Item string placing tripwire hooks instead of tripwires
- [#285] Wrong block name and color for dark prismarine block and prismarine bricks
- [#285] Nether bricks fence were burnable and flammable
- [#285] Item on hands disappear (looses one from the stack) when interacting with chest minecarts and hopper minecarts

### Added
- [#227] PlayerJumpEvent called when jump packets are received.
- [#242] `Item.equalsIgnoringEnchantmentOrder` method for public usage.
- [#244] `Enchantment.getPowerNukkit().isItemAcceptable(Item)` to check if an enchantment can exist 
         in a given item stack by any non-hack means.
- [#256] `CapturingCommandSender` intended to capture output of commands which don't require players.
- [#259] `Hash.hashBlock(Vector3)` method for public usage.
- [#261] `Player.isCheckingMovement()` method for public usage.
- [#261] Protected field `EntityEndCrystal.detonated` to disable the `EndCrystal.explode()` method.
- [#275] New annotations to document when elements get added and when deprecated elements will be removed
- [#123] Adds and register the banner pattern items
- [#276] `Block.afterRemoval()` called automatically when the block is replaced using any `Level.setBlock()`
- [#277] `Block.mustSilkTouch()` and `Block.mustDrop()` to allow blocks to force the dropping behaviour when being broken
- [#279] `Entity.isInEndPortal()` for public usage
- [#285] `LoginChainData.getRawData()` for public usage

### Changed
- [#227] Sugar canes now fires BlockGrowEvent when growing naturally.
- [#261] Kicked players can now view the kick reason on kick.
- [#285] Limit the maximum size of `BookEditPacket`'s text to 256, ignoring the packet if it exceeds the limit
- [#285] Ender pearls will now be unable to teleport players across different dimensions
- [#285] `ShortTag.load(NBTInputStream)` now reads a signed short. Used to read an unsigned short.

## [1.2.0.2-PN] - 2020-05-18 ([Check the milestone](https://github.com/PowerNukkit/PowerNukkit/milestone/10?closed=1))
Contains several fixes, including issues which cause item losses and performance issues

### Fixes
- [#239] Anvil fails to merge some enchantments because the ordering mismatches
- [#240] Anvils were charging fewer levels to merge thorn books
- [#243] Anvils were charging more levels to merge punch books
- [#246] Anvil checking the enchantment table property instead of the enchantment id
- [#246] Compatibility rules for unbreaking, fortune, mending, riptide, loyalty and channeling enchantments
- [#248] Air blocks with metadata were being rendered as "UPDATE!" block (backward compatibility fix)
- [#212] The `/tp player 0 1 2` command doesn't work
- [#220] Stripping old full bark log results in a wrong block
- [#157] Wrong Packed and Blue Ice break time with the hands
- [#193] Wrong explosion behaviour with waterlogged block
- [#103] Fixes BlockLeaves's random update logic spamming packets and consuming CPU unnecessarily
- [#253] Fixes `LeavesDecayEvent` also being called when leaves wouldn't decay
- [#254] Fixes BlockLeaves not checking for log connectivity, was checking only if it had a log block nearby
- [#255] Fix /status information in /debugpaste not being collected
- [#260] Fix a stack overflow when setting off end crystals near to each other
- [#260] Fix drops of block entity inventory contents on explosion
- [#260] Check SUPPORTED_PROTOCOLS instead of CURRENT_PROTOCOL in `LoginPacket.decode()`
- [#79] Sugarcane can grow without water
- [#262] Removing the water don't break the sugarcane (using empty bucket or breaking water flow)
- [#263] Fixes disconnect messages not reaching the player sometimes
- [#116] Fishing hooks don't attach to entities and damages multiples entities
- [#95] The Level Up sound is not centered
- [#267] Fishing hooks without players, loaded from the level save. They are now removed on load
- [#266] Loosing connection with items in an open anvil makes you loose the items
- [#273] Loosing connection with items in an open grindstone, enchanting table, stone cutter  makes you loose the items
- [#273] Loosing connection with items in an open crafting table, 2x2 crafting grid makes you loose the items

### Changed
- [#247] Invalid BlockId:Meta combinations now log an error when found. It logs only once
- [#255] The report issues link has been changed to point to the PowerNukkit repository
- [#268] The `/xp` command now makes level up sound every 5 levels
- [#273] If an anvil, grindstone, enchanting, stonecutter, crafting GUI closes, the items will try to go to the player's inventory
- [#273] `FakeBlockUIComponent.close(Player)` now calls `onClose(Player)`
- [#274] `Player.checkInteractNearby()` is now called once every 10 ticks, it was called every tick

## [1.2.0.1-PN] - 2020-05-08 ([Check the milestone](https://github.com/PowerNukkit/PowerNukkit/milestone/8?closed=1))
Fixes several anvil issues.

### Added
- [#224] Added option to disable watchdog with `-DdisableWatchdog=true`. 
  This should be used **only by developers** to debug the server without interruptions by the crash detection system.

### Fixes
- [#224] Anvil not merging enchanted items correctly and destroying the items.
- [#228] Invalid enchantment order on anvil's results causing the crafting transaction to fail.
- [#226] Anvil cost calculation not applying bedrock edition reductions
- [#222] Anvil changes the level twice and fails the transaction if the player doesn't have enough.
- [#235] Wrong flags in MoveEntityAbsolutePacket
- [#234] Failed anvil transactions caused all involved items to be destroyed
- [#234] Visual desync in the player's experience level when an anvil transaction fails or is cancelled. 

### Changed
- [#234] Anvil's result is no longer stored in the PlayerUIInventory at slot 50 as 
         it was vulnerable to heavy duplication exploits.
- [#234] `setResult` methods in `AnvilInventory` are now deprecated and marked for removal at 1.3.0.0-PN
         because it's not supported by the client and changing it will fail the transaction.

## [1.2.0.0-PN] - 2020-05-03 ([Check the milestone](https://github.com/PowerNukkit/PowerNukkit/milestone/6?closed=1))
**Note:** Effort has been made to keep this list accurate but some bufixes and new features might be missing here, specially those made by the NukkitX team and contributors.

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
- [#44] Different daytime from Android and Windows 10 Edition
- [#93] Nukkit sends a rain time that doesn't matches the server
- [#210] Issues with old blocks from old NukkitX worlds, specially fully barked logs (log:15 for example)

### Changed
- Make BlockLectern implements Faceable
- The versioning convention now follows this pattern:<br>`upstream.major.minor.patch-PN`<br>[Click here for details.](https://github.com/PowerNukkit/PowerNukkit/blob/7912aa4be68e94a52762361c2d5189b7bbc58d2a/pom.xml#L8-L14)

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

[updated changelog]:https://github.com/PowerNukkit/PowerNukkit/blob/bleeding/CHANGELOG.md
[discord guild]: https://powernukkit.org/discord

[Unreleased 1.4.0.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.3.1.1-PN...bleeding
[1.3.1.1-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.3.1.0-PN...v1.3.1.1-PN
[1.3.1.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.3.0.1-PN...v1.3.1.0-PN
[1.3.0.1-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.3.0.0-PN...v1.3.0.1-PN
[1.3.0.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.2.1.0-PN...v1.3.0.1-PN
[1.3.0.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.2.1.0-PN...v1.3.0.0-PN
[1.2.1.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.2.0.2-PN...v1.2.1.0-PN
[1.2.0.2-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.2.0.1-PN...v1.2.0.2-PN
[1.2.0.1-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.2.0.0-PN...v1.2.0.1-PN
[1.2.0.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.1.1.0-PN...v1.2.0.0-PN
[1.1.1.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/1ac6d50d36f07b6f1a02df299d9591d78c379db9...v1.1.1.0-PN#files_bucket

[a8247360]: https://github.com/PowerNukkit/PowerNukkit/commit/a8247360

[NukkitX]: https://github.com/CloudburstMC/Nukkit

[#12]: https://github.com/PowerNukkit/PowerNukkit/issues/12
[#44]: https://github.com/PowerNukkit/PowerNukkit/issues/44
[#46]: https://github.com/PowerNukkit/PowerNukkit/issues/46
[#49]: https://github.com/PowerNukkit/PowerNukkit/pull/49
[#50]: https://github.com/PowerNukkit/PowerNukkit/pull/50
[#51]: https://github.com/PowerNukkit/PowerNukkit/pull/51
[#52]: https://github.com/PowerNukkit/PowerNukkit/pull/52
[#53]: https://github.com/PowerNukkit/PowerNukkit/pull/53
[#54]: https://github.com/PowerNukkit/PowerNukkit/pull/54
[#55]: https://github.com/PowerNukkit/PowerNukkit/pull/55
[#56]: https://github.com/PowerNukkit/PowerNukkit/pull/56
[#57]: https://github.com/PowerNukkit/PowerNukkit/pull/57
[#58]: https://github.com/PowerNukkit/PowerNukkit/pull/58
[#79]: https://github.com/PowerNukkit/PowerNukkit/issues/79
[#80]: https://github.com/PowerNukkit/PowerNukkit/pull/80
[#87]: https://github.com/PowerNukkit/PowerNukkit/issues/87
[#93]: https://github.com/PowerNukkit/PowerNukkit/issues/93
[#95]: https://github.com/PowerNukkit/PowerNukkit/issues/95
[#102]: https://github.com/PowerNukkit/PowerNukkit/pull/102
[#103]: https://github.com/PowerNukkit/PowerNukkit/issues/103
[#108]: https://github.com/PowerNukkit/PowerNukkit/pull/108
[#113]: https://github.com/PowerNukkit/PowerNukkit/issues/113
[#116]: https://github.com/PowerNukkit/PowerNukkit/issues/116
[#123]: https://github.com/PowerNukkit/PowerNukkit/issues/123
[#129]: https://github.com/PowerNukkit/PowerNukkit/pull/129
[#140]: https://github.com/PowerNukkit/PowerNukkit/pull/140
[#152]: https://github.com/PowerNukkit/PowerNukkit/pull/152
[#157]: https://github.com/PowerNukkit/PowerNukkit/issues/157
[#170]: https://github.com/PowerNukkit/PowerNukkit/pull/170
[#193]: https://github.com/PowerNukkit/PowerNukkit/issues/193
[#210]: https://github.com/PowerNukkit/PowerNukkit/issues/210
[#212]: https://github.com/PowerNukkit/PowerNukkit/issues/212
[#220]: https://github.com/PowerNukkit/PowerNukkit/issues/220
[#219]: https://github.com/PowerNukkit/PowerNukkit/pull/219
[#222]: https://github.com/PowerNukkit/PowerNukkit/issues/223
[#224]: https://github.com/PowerNukkit/PowerNukkit/pull/224
[#226]: https://github.com/PowerNukkit/PowerNukkit/issues/226
[#227]: https://github.com/PowerNukkit/PowerNukkit/pull/227
[#228]: https://github.com/PowerNukkit/PowerNukkit/issues/228
[#232]: https://github.com/PowerNukkit/PowerNukkit/issues/232
[#234]: https://github.com/PowerNukkit/PowerNukkit/issues/234
[#235]: https://github.com/PowerNukkit/PowerNukkit/issues/235
[#239]: https://github.com/PowerNukkit/PowerNukkit/issues/239
[#240]: https://github.com/PowerNukkit/PowerNukkit/issues/240
[#242]: https://github.com/PowerNukkit/PowerNukkit/pull/242
[#243]: https://github.com/PowerNukkit/PowerNukkit/issues/243
[#244]: https://github.com/PowerNukkit/PowerNukkit/pull/244
[#246]: https://github.com/PowerNukkit/PowerNukkit/issues/246
[#247]: https://github.com/PowerNukkit/PowerNukkit/pull/247
[#248]: https://github.com/PowerNukkit/PowerNukkit/pull/248
[#253]: https://github.com/PowerNukkit/PowerNukkit/pull/253
[#254]: https://github.com/PowerNukkit/PowerNukkit/issues/254
[#255]: https://github.com/PowerNukkit/PowerNukkit/pull/255
[#256]: https://github.com/PowerNukkit/PowerNukkit/pull/256
[#259]: https://github.com/PowerNukkit/PowerNukkit/pull/259
[#260]: https://github.com/PowerNukkit/PowerNukkit/pull/260
[#261]: https://github.com/PowerNukkit/PowerNukkit/pull/261
[#262]: https://github.com/PowerNukkit/PowerNukkit/pull/262
[#263]: https://github.com/PowerNukkit/PowerNukkit/pull/263
[#266]: https://github.com/PowerNukkit/PowerNukkit/issues/266
[#267]: https://github.com/PowerNukkit/PowerNukkit/issues/267
[#268]: https://github.com/PowerNukkit/PowerNukkit/pull/268
[#270]: https://github.com/PowerNukkit/PowerNukkit/issues/270
[#272]: https://github.com/PowerNukkit/PowerNukkit/issues/272
[#273]: https://github.com/PowerNukkit/PowerNukkit/pull/273
[#274]: https://github.com/PowerNukkit/PowerNukkit/pull/274
[#275]: https://github.com/PowerNukkit/PowerNukkit/pull/275
[#276]: https://github.com/PowerNukkit/PowerNukkit/pull/276
[#277]: https://github.com/PowerNukkit/PowerNukkit/pull/277
[#279]: https://github.com/PowerNukkit/PowerNukkit/pull/279
[#281]: https://github.com/PowerNukkit/PowerNukkit/pull/281
[#285]: https://github.com/PowerNukkit/PowerNukkit/pull/285
[#293]: https://github.com/PowerNukkit/PowerNukkit/pull/293
[#297]: https://github.com/PowerNukkit/PowerNukkit/pull/297
[#315]: https://github.com/PowerNukkit/PowerNukkit/pull/315
[#319]: https://github.com/PowerNukkit/PowerNukkit/pull/319
[#320]: https://github.com/PowerNukkit/PowerNukkit/pull/320
[#323]: https://github.com/PowerNukkit/PowerNukkit/issues/323
[#326]: https://github.com/PowerNukkit/PowerNukkit/pull/326
[#328]: https://github.com/PowerNukkit/PowerNukkit/issues/326
[#330]: https://github.com/PowerNukkit/PowerNukkit/issues/330
[#335]: https://github.com/PowerNukkit/PowerNukkit/issues/335
[#338]: https://github.com/PowerNukkit/PowerNukkit/issues/338
[#344]: https://github.com/PowerNukkit/PowerNukkit/issues/344
[#346]: https://github.com/PowerNukkit/PowerNukkit/issues/346
[#347]: https://github.com/PowerNukkit/PowerNukkit/issues/347
[#348]: https://github.com/PowerNukkit/PowerNukkit/issues/348
[#352]: https://github.com/PowerNukkit/PowerNukkit/issues/352
[#368]: https://github.com/PowerNukkit/PowerNukkit/issues/368
[#390]: https://github.com/PowerNukkit/PowerNukkit/issues/390
