# CobbleSTS

## Description

CobbleSTS is a plugin for [Cobblemon](https://modrinth.com/mod/cobblemon) that allows players to sell Pokemon from the
server using in-game currency. The plugin is inspired by the STS system in the Pixelmon mod for Minecraft.

Please test the plugin and report any issues and if anything happens to add or change also mention it it will be a great
help to improve the plugin.

## Configuration

The config file is located at `config/cobblests/config.json`
have configure language
Some of the options are to increase the final price of the pokemon based on x characteristics.

```json
{
  "lang": "en",
  "ecocommand": "eco give %player% %amount%",
  "allowshiny": true,
  "allowlegendary": true,
  "havecooldown": true,
  "cooldown": 1,
  "base": 500,
  "level": 250,
  "shiny": 1000,
  "legendary": 5000,
  "ivs": 100,
  "evs": 100,
  "happiness": 0,
  "defaultgender": 0,
  "defaultform": 0,
  "defaultnature": 0,
  "defaultability": 0,
  "defaultball": 0,
  "islegends": [
    "Magikarp"
  ],
  "legends": {
    "Articuno": 50000
  },
  "gender": {
    "F": 0,
    "M": 0,
    "N": 0
  },
  "form": {
    "Galar": 0
  },
  "nature": {
    "Hardy": 0
  },
  "ability": {
    "None": 0
  },
  "ball": {
    "poke_ball": 100
  },
  "pokemon": {
    "Magikarp": 1
  }
}
```

## Commands

- `/sts` - Opens the STS GUI.
- `/sts other <player>` - Opens the STS GUI for other player.
- `/sts reload` - Reloads the plugin configuration.

## Dependencies

- [Cobblemon](https://modrinth.com/mod/cobblemon) (v1.5.0 >=)
- [Gooeylibs](https://modrinth.com/mod/gooeylibs) (v3.0.0)
- [Architectury API](https://modrinth.com/mod/architectury-api) (v9.2.14)
