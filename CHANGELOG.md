# Changelog

**IMPORTANT:** Please test the changes listed below to ensure they work correctly.

## [1.5.0] - 2026-03-11

### Added

- Added the ability to configure **multiple STS**, where each STS can give a different **currency type and amount**.
- Added a **leaderboard**.
- Added `%lorepokemon%` placeholder to the Pokémon lore in the language configuration, allowing full Pokémon lore to be
  displayed in the STS menu.
  displayed in the STS menu.
- Added **Multi-Selection System** for selling Pokémon:
    - Users can now select multiple Pokémon from their **Party** or **PC** boxes.

### Improved

- Added **performance optimizations**.
- Improved the **save system**, so it now only saves when necessary.
- **Cooldown duration** is now **permission-based**: uses `PlayerUtils.getCooldown` with configurable
  `cooldownPermissions` per STS instead of a hardcoded 5-minute value.
- Refactored `sellPokemon` to pass the player context, ensuring cooldown permissions are evaluated correctly at sell
  time.
  `cooldownPermissions` per STS instead of a hardcoded 5-minute value.
- Refactored `sellPokemon` to pass the player context, ensuring cooldown permissions are evaluated correctly at sell
  time.

### Bug Fixes

- Fixed the `/sts reload` command — it now requires **admin permission** (`ultrasts.admin`, level 2).
- Fixed the leaderboard so it now displays the **ranking position correctly** — the position index was not incrementing
  between entries.
  between entries.
- Fixed the `/sts other` command permission level (corrected from level 4 to level 2).
- Fixed cooldown permissions not being applied when selling a Pokémon.

### Changed

- Users can now **disable the STS available notification individually**.