> ## Documentation for basically using CoreAPI.

## 1. For playing and running minecraft:

#### 1.1 Download Core API mod module.

Visit **Core API** repository on github, visit **releases** tab and download `.jar` files of latest _pre-release_ / release (**recommended**)

Releases page: https://github.com/ProjectEssentials/ProjectEssentials-Core/releases

#### 1.2 Install Core API modification.

The minecraft forge folder structure below will help you understand what is written below.

```
.
├── assets
├── config
├── libraries
├── mods (that's how it should be)
│   └── Project Essentials Core-1.14.4-1.X.X.X.jar
└── ...
```

Place your mods and Core API mods according to the structure above.

#### 1.3 Verifying mod on the correct installation.

Run the game, check the number of mods, if the list of mods contains `Project Essentials Core` mod, then the mod has successfully passed the initialization of the modification.

## 2. For developing and developers:

### 2.1 Getting started with installing.

To get the Core API source for development and interactions with the rights of players, you need to get the dependencies and get the documentation to view it in your IDE.

Installation documentation is located in the readme file or just follow the link: https://github.com/ProjectEssentials/ProjectEssentials-Core#-install-using-gradle

### 2.2 API usage.

I could not write this damn documentation at all and spend time on it, because it is so fucking understandable. But just in case, I will nevertheless sign here some trifles.

Let's start small?

#### 2.2.1 Functions.

```
EssBase.logBaseInfo

- description: Print base modification information to log.
```

```
EssBase.validateForgeVersion

- description: Validate forge version on compatibility with loaded mod. If validation failed, then you will be notified with messages in logger with level WARN.
```

#### 2.2.2 Extensions.

```
CommandContext<CommandSource>.isPlayerSender

- return: true if command sender is player. (boolean value)
```

```
CommandEvent.commandName

- example: player execute command `/heal MairwunNx`, then you get `heal` as string.

- return: command name as string. (string value)
```

```
CommandEvent.executedCommand

- example: player execute command `/heal MairwunNx`, then you get `/heal MairwunNx` as string.

- return: fully executed command as string. (string value)
```

```
CommandEvent.player

- return: `ServerPlayerEntity` class instance from `CommandEvent` class instance. (ServerPlayerEntity class instance)
```

```
CommandEvent.source

- return: Return command `source` from `CommandEvent` class instance. (CommandSource class instance)
```

```
CommandSourceExtensions.sendMsg

- description: Send localized message to player without logging.

- accepts:
    - moduleName - mod module name. (string)
    - commandSource - command source instance. (CommandSource class instance)
    - l10nString - localized string without `project_essentials_`. (string)
    - args - additional arguments for localized string `(%s literals)`. (string arguments)
```

```
String.capitalizeWords

- return: capitalized each word string. (string)
```

```
String.Companion.empty

- return: empty string. (string)
```

#### 2.2.3 Helpers.

```
ForgePathHelper.getRootPath

- accepts:
    - pathType - enum class with CLIENT and SERVER enums. (ForgeRootPaths enum class)

- return: absolutely path to configuration root dir. (string)
```

```
ModErrorsHelper.<ERROR_ID>

- see: https://github.com/MairwunNx/ProjectEssentials-Core/blob/c7e9c318efc78daeba68650f24036f379859f2a0/src/main/kotlin/com/mairwunnx/projectessentialscore/helpers/ModErrorsHelper.kt

- return: error description and just error reason. (string)
```

```
ModPathHelper.CONFIG_FOLDER

- description: Minecraft config folder absolutely path.
```

```
ModPathHelper.MOD_CONFIG_FOLDER

- description: Project Essentials mod config folder.
```

## These are all API methods, I think you understand that everything is very simple.

### For all questions, be sure to write issues!
