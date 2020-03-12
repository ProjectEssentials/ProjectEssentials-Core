# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.15.2-1.1.0] - 2020-03-12

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


## [1.15.2-1.0.0] - 2020-02-07

### Added
- Initial release.
