# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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
