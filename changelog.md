# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [2.0.0-RC.1] - 2020-05-09

*Note: it is not full list with changes!* 

### Added
- Short aliases for basic commands, weather, gamemode, etc.
- Ability to configure some settings in game with command `/configure-essentials <> <>`.
- Permissions for back command after death.

### Changes
- Fully rewritten core module code, and module system.
- API breaking changes (not documented changes).
- Version number format now fully relative to semver.
- Updated kotlin runtime to the latest version.
- Now as updater file uses `updatev2.json`.
- Almost all permissions nodes was renamed.
- Permissions system was changed, and now no one module dependents on permissions module.

### Removed
- Redundant logger information spamming in common logger. Now it redirected to debug logger.

### Fixed
- Back command incorrect behavior was fixed after death.
- Safe localization random bugs probably fixed.

## [1.14.4-1.3.0] - 2020-03-12

### Added
- AccessTransformers (for `language` field in `ServerPlayerEntity` class).
- `gson` dependency added and included in mod bundle.
- Localization configuration.
- Localization API implemented.
- `hoverEventFrom` in `HoverEventExtensions` implemented.
- `textComponentFrom` in `TextComponentExtensions` implemented.
- Localization configuration loading \ saving.
- Localization processing (in-resources) files.
- All vanilla commands have got safe localization.
- `throwOnlyPlayerCan` and `throwPermissionLevel` added in `ModErrorsHelper.kt`.
- `BackLocation` provider and command implemented.
- Compatibility with back command added to `teleport` command. (**Experimental**)
- `IConfiguration` interface for configurations.

### Changed
- Updated dependencies, updated module version.
- Forge target version updated. *(to `28.2.X`)*
- `JsonConfiguration` compatibility to `0.20.0` version. **!!! BREAKING CHANGE !!!**
- `ONLY_PLAYER_CAN` and `PERMISSION_LEVEL` in `ModErrorsHelper.kt` now is deprecated.
- Small logging output changes.

### Fixed
- Incorrect behavior for teleport alias `/tp`.
- Crash when using `deop` when permissions module not exist command.
- Incorrect vanilla op level checking.

### Removed
- Redundant `@UseExperimental` annotation from `loadLocalization` in `localization.kt`.

## [1.14.4-1.2.1] - 2020-02-07

### Added
- Class paths added to `EssBase.kt` to `companion object`.
- `CommandEvent.isPlayerSender` added to `CommandEventExtensions.kt`.

### Changed
- `EntryPoint.kt` uses class paths from `CoreAPI`.
- `CommandsConfigurationUtils.kt` formatted.
- `CommandContext<...>.playerName()` now if source is server, then return `#server` as nickname.
- `CommandEvent.player` (changed return type to nullable `ServerPlayerEntity`) now return `null` if source is server.
- `ModPathHelper.kt` formatted.

### Fixed
- Curse forge incorrect link on mod startup phase.

## [1.14.4-1.2.0] - 2020-01-26

### Added
- Localizaton for restricted messages.
- Hover event with restricred message description.

### Fixed
- Wrong op level for `pardon-ip` command.

## [1.14.4-1.1.0.0] - 2020-01-18

### Added
- Cooldown and Permission modules as not mandatory dependency.
- Configuration for native vanilla commands.
- Implemented all vanilla commands.
- Permission checking on all vanilla commands.
- Cooldown checking on all vanilla commands.
- `NativeCommandUtils` with ability to remove command.

## [1.14.4-1.0.3.2] - 2020-01-15

### Added
- Added `JsonHelper.kt` with `jsonInstance`.

## [1.14.4-1.0.3.1] - 2020-01-14

### Added
- This changelog file.
- Empty line after startup message.
- Logo file.

### Changed
- Package name changed to correctly.

### Fixed
- `cr` symbols in mod info after line.

## [1.14.4-1.0.3.0] - 2020-01-12

### Added
- Compatibility with forge `28.1.X`.

### Changed
- Bumped `kotlin`, `kotlinx serialization` and `forge` version.
- Improved build script and renamed variables in properties.
- Bumped `dokka` plugin version.
- Improved logging on mod startup and on forge version incompatibility.

## [1.14.4-1.0.2.0] - 2019-10-07

### Fixed
- Crash while dependency using `CoreAPI`.

## [1.14.4-1.0.1.0] - 2019-10-06

### Added
- `playerName` extension in `CommandContext`.

## [1.14.4-1.0.0.2] - 2019-10-06

### Fixed
- Some api mistakes.

## [1.14.4-1.0.0.1] - 2019-10-06

### Fixed
- Not existing dependencies in jar.

## [1.14.4-1.0.0.0-1] - 2019-10-06

### Added
- gradle-wrapper.jar (in sources).

## [1.14.4-1.0.0.0] - 2019-10-06

### Added
- Initial release.
