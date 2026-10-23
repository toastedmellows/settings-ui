# Settings (Paper 26.3)

`/settings` opens a menu where each player toggles:
- Public Chat (hides others' chat; optionally blocks sending while off)
- Join / Leave messages
- TPA requests (blocks /tpa and /tpahere to you; works with EssentialsX)
- Advancement messages
- Mention sound (ping when your name is said)

Settings are saved per player in `plugins/Settings/playerdata.yml`.
Permission: `settings.use` (default: everyone).
Config: `block-sending-when-disabled` in `plugins/Settings/config.yml`.

## Build
Install Gradle 9.1+ (any Java 17+ works to run it), then run `build.bat` (Windows) or `./build.sh`. Gradle downloads JDK 25 and the Paper API automatically. The jar is `build/libs/Settings-1.1.0.jar`; drop it in `plugins/`. No local setup? Push this folder to a GitHub repo and download the jar from the Actions tab (Build -> Settings-jar).

## Add a setting
Add a constant to `Setting.java` and check it with `SettingsManager#isEnabled`. The menu lays out up to 9 settings automatically.
