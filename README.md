# itementitypicker
A fabric mod for Player-based item entity filter in 1.20.4

[Modrinth](https://modrinth.com/mod/itementitypicker)


## Command
Main command /picker

- `/picker help` list usage and descriptions
- `/picker` toggle mod on off
- `/picker [on/off]` set mod on off
- `/picker mode` show current mode
- `/picker mode [blacklist, whitelist] ` for mode selection
  <br>`Blacklist`: throw any item picked up that Within the Blacklist
  <br>`Whitelist`: throw any item picked up Not In the Whitelist (Keep item if in whitelist)
- `/picker list` list all items added into blacklist and whitelist
- `/picker [add/del] <item type>` add/remove item to/from the current mode list
  <br> `item type is Not nbt/component-sensitive`

## Usage
Useful to filter desire unstackables using whitelist (i.e. Totem_of_undying in raid farm)

## Other
No keybind, No gui
