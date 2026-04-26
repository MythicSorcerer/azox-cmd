i## 1. Overview
Create a chest locking system allowing players to secure chests with physical/virtual keys. Includes comprehensive logging for chests and keys.

## 2. Data Model

### 2.1 ChestLock Model (`model/ChestLock.java`)
* **ownerUuid:** UUID
* **chestId:** String (e.g., "mythicsorcerer_21" – format: username_number)
* **world:** String
* **x, y, z:** int coordinates
* **allowedPlayers:** List<UUID> – virtual key holders
* **creationDate:** long
* **locked:** boolean

### 2.2 KeyLog Model (`model/KeyLog.java`)
* **keyId:** UUID
* **chestId:** String
* **ownerUuid:** UUID
* **type:** PHYSICAL or VIRTUAL
* **location:** String (where dropped/picked up)
* **action:** CREATED, DROPPED, PICKED_UP, EXPORTED_TO_PHYSICAL, DESTROYED, GIVEN_TO_PLAYER, REMOVED_FROM_PLAYER
* **timestamp:** long

### 2.3 ChestLog Model (`model/ChestLog.java`)
* **chestId:** String
* **world:** String
* **x, y, z:** int coordinates
* **ownerUuid:** UUID
* **action:** PLACE, BREAK, TAKE_ITEM, PUT_ITEM, HOPPER_IN, HOPPER_OUT, COPPER_GOLEM_TAKE, COMMAND_EXEC
* **timestamp:** long

---

## 3. Storage

### 3.1 ChestStorage (`storage/ChestStorage.java`)
* Global chest locks in `plugins/AzoxCmd/chestlocks.yml`
* Contains: all locked chests

### 3.2 KeyStorage (`storage/KeyStorage.java`)
* Player-specific keys stored in their playerdata file:
    * `keys.physical`: List of physical key UUIDs
    * `keys.virtual`: List of virtual key UUIDs with chestId mappings

### 3.3 LogStorage (`storage/ChestLogStorage.java`)
* Key movement logs in playerdata (or separate file)
* Chest activity logs in `plugins/AzoxCmd/chestlogs.yml` (toggleable in config)
* Global chest logs as a separate file

---

## 4. Config Settings
I'll use `config.set("chestlocking.enabled", true)` to enable or disable the system globally, and `config.set("chestlocking.global_log", false)` for the global chest logging feature. The config file should go in `plugins/AzoxCmd/config.yml`.

---

## 5. Commands

### 5.1 /lockchest
* **Permission:** azox.util.lockchest
* When player targets a chest (not already locked) and runs /lockchest with no arguments, it creates a chest lock
* Generates chestId in format: username_number (auto-incrementing)

### 5.2 /lockgui
* **Permission:** azox.util.lockchest
* Opens GUI showing physical keys for the chest they're looking at
* Display includes: key stack with custom name and lore showing chest ID and owner name

### 5.3 /chest
* **Permission:** azox.util.lockchest
* When looking at a locked chest, displays owner and allows access to recent logs
* Provides: owner name, chestId, allowed players list, recent activity logs, virtual key management options

---

## 6. Key System

### 6.1 Physical Keys
* **Item:** Paper or other stackable item with custom PDC data
* **Display:** Enchantment glint effect with custom name (e.g., "Key to Chest #21") and lore like "Belongs to mythicsorcerer"

### 6.2 Virtual Keys
* Stored in player data, tied to specific chests
* Can transfer from virtual to physical format but not duplicate
* Obtained through GUI or chat menu interactions

### 6.3 Key Conversion
* **Virtual -> Physical:** /lockgui export option
* **Physical -> Virtual:** Right-click with physical key in main hand
* **Physical -> Physical:** Not duplicable

---

## 7. Listeners

### 7.1 Block Listeners
* **BlockBreakEvent:** Log chest break
* **BlockPlaceEvent:** Log chest placement
* **BlockDispenseEvent:** Track hopper interactions
* **PlayerInteractEvent:** Handle right-click on locked chests and physical key usage

### 7.2 Inventory Listeners
* **InventoryClickEvent:** Process GUI clicks for key management
* **InventoryDragEvent:** Move keys between slots

### 7.3 Entity Listeners
* **EntityPickupItemEvent:** Log when players pick up keys
* **ItemDespawnEvent:** Track key despawning (possibly removed by Copper Golem)

### 7.4 Player Listeners
* **PlayerDropItemEvent:** Log dropped keys
* **PlayerInteractEvent:** Detect right-click actions
* **EntityDamageByBlockEvent:** Monitor hopper output interactions

---

## 8. Implementation Order
1. ChestLock, KeyLog, ChestLog models
2. ChestStorage, KeyStorage, ChestLogStorage storage classes
3. LockChestManager manager class
4. /lockchest command
5. /lockgui command
6. /chest command
7. Physical key item system with PDC
8. GUI for key management
9. Listeners for chest and key interactions
10. Log tracking implementation
11. Plugin hookup in AzoxCmd.java
12. Update plugin.yml with commands and permissions
13. Config defaults

---

## 9. Testing
1. Compile and verify build
2. Upload to ~/ptero "plugin testing" folder
3. If server supports hot-reloading, use reload; otherwise restart
4. Verify basic functionality works
