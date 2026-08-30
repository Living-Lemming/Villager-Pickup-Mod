<img style="text-align:center" src="img/banner.png" alt="Mod Banner">

# Villager Pickup

Villager Pickup allows you to pick up villagers 
into spawn eggs by **shift right-clicking** on them. 
The villager spawn egg will retain all NBT data of the previous villager, 
meaning they stay exactly the same as before you picked them up.

## Features

- **Easy Villager Management**: Effortlessly manage your villagers by picking them up and placing them wherever you need them.
- **NBT Data Retention**: Villagers retain all their data, ensuring they remain unchanged after being picked up.
- **Fully Server-Sided**: The mod is fully server-side, and vanilla clients can use it when installed on the server.

## Compatibility

Due to the way this mod is coded, it works with all modded villagers by default if they are not a separate entity.
<br>(for example, you can't pick up wandering traders).

## Installation

1. Download the mod from [Modrinth](https://modrinth.com/mod/villager-pickup) or the [GitHub Releases](https://github.com/Living-Lemming/Villager-Pickup-Mod/releases).
2. Place the downloaded mod file into the `mods` folder of your Minecraft server.
3. Restart the server to apply the changes.

## Configuration

Is there something you want to change about the mod? 
Check out the config file that is generated after the server has started.
You will find it under `config/villager-pickup.json`.

After making changes, you don't have to restart! Just use the `/villager-pickup reload` Command.

You can also use the Ingame Config Editor using `/villager-pickup config-gui`.

## Usage

Simply shift right-click on a Villager. The villager will be converted into a spawn egg.
You can place them down again afterward.
![](img/mod-preview.gif)

## Links

- [Source Code](https://github.com/Living-Lemming/Villager-Pickup-Mod)
- [Issue Tracker/Feature Requests](https://github.com/Living-Lemming/Villager-Pickup-Mod/issues)
- [GitHub Releases](https://github.com/Living-Lemming/Villager-Pickup-Mod/releases)
- [Modrinth Page](https://modrinth.com/mod/villager-pickup)
- ~~[CurseForge Page](https://www.curseforge.com/minecraft/mc-mods/villagerpickup)~~ (Out of date, please avoid.)

## Contributing and Development

Contributions are always welcome! Please feel free to submit a Pull Request.

For Development, the recommended IDE is [IntelliJ IDEA](https://www.jetbrains.com/idea/).
<br>
You are also encouraged to install the [Minecraft Development](https://plugins.jetbrains.com/plugin/8327-minecraft-development) and [manifold-ij](https://plugins.jetbrains.com/plugin/10057-manifold-ij/versions/stable) plugins for it.

## License

This project is licensed under the CC0-1.0 License. See the `LICENSE` file for details.
