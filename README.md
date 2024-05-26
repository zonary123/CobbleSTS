# CobbleSpawnNotify

## Description

CobbleSpawnNotify is a plugin that notifies players when a shiny or legendary Cobblemon spawns, defeat and catch in the
world.

## Language

You can translate the biomes, worlds, and forms into your language.

If you are English, remove the biome contents and the world contents unless you want to give them custom colors

The file language example:

Support:

```
%player%
%pokemon%
%form%
the below is for spawn messages
%biome%
%world%
%x%
%y%
%z%
```

```json
{
  "prefix": "{#E39651}CobbleSpawnNotify {#EA814F}»",
  "reload": "{#E39651}The plugin has been reloaded!",
  "messagenotifyshiny": "{#86E19F}A wild {#E6E83B}shiny {#EAA34F}%pokemon% %form%has spawned in a %biome% biome %world% {#EA814F}x:{#5DD4C5}%x% {#EA814F}y:{#5DD4C5}%y% {#EA814F}z:{#5DD4C5}%z%",
  "messagenotifylegendary": "{#86E19F}A wild {#9F66E7}legendary {#EAA34F}%pokemon% %form%has spawned in a %biome% biome %world% {#EA814F}x:{#5DD4C5}%x% {#EA814F}y:{#5DD4C5}%y% {#EA814F}z:{#5DD4C5}%z%",
  "messagedefeatshiny": "{#86E19F}The {#E6E83B}shiny {#EAA34F}%pokemon% %form%has been defeated by %player%",
  "messagedefeatlegendary": "{#86E19F}The {#9F66E7}legendary {#EAA34F}%pokemon% %form%has been defeated by %player%",
  "messagecatchshiny": "{#86E19F}%player% has caught a {#E6E83B}shiny {#EAA34F}%pokemon% %form%",
  "messagecatchlegendary": "{#86E19F}%player% has caught a {#9F66E7}legendary {#EAA34F}%pokemon% %form%",
  "forms": {
    "Alolan": "Alolan ",
    "Galarian": "Galarian ",
    "Normal": "",
    "Hisui": "{##6342db}Hisui "
  },
  "biomes": {
    "beach": "Playa"
  },
  "worlds": {
    "overworld": "overworld",
    "the_nether": "Hell",
    "the_end": "End"
  }
}
```

## Configuration

The config file is located at `config/cobblespawnnotify/config.json` and contains the following options:

```json
{
  "lang": "en",
  "shiny": true,
  "legendary": true,
  "notifyspawn": true,
  "notifycatch": true,
  "notifydefeat": true,
  "distanceplayer": 100
}
```

## Commands

```
/cobblespawnnotify reload - Reload the plugin
```

## Dependencies

- [Cobblemon](https://modrinth.com/mod/cobblemon) (v1.5.0)
- [Gooeylibs](https://modrinth.com/mod/gooeylibs) (v3.0.0)
