> ## Documentation for basic use of the CoreAPI.

# Documentation outdated, i'm so lazy for updating documentation here, see this documentation this https://mairwunnx.gitbook.io/project-essentials/project-essentials-core#using-as-api
## If you want to help me, you can update this documentation through pull request.

### Getting Core API as dependency

```groovy
repositories {
    maven { url("https://jitpack.io") }
}

dependencies {
    compile(
        group: "com.github.projectessentials",
        name: "ProjectEssentials-Core",
        version: "v1.14.4-1.2.0"
    )
}
```

### API Functions

```
EssBase.logBaseInfo

- description: Print base modification information to log.
```

```
EssBase.validateForgeVersion

- description: Validate forge version on compatibility with loaded mod. If validation failed, then you will be notified with messages in logger with level WARN.
```

### API Extensions

```
CommandContext<CommandSource>.isPlayerSender

- return: true if command sender is player. (boolean value)
```

```
CommandContext<CommandSource>.playerName

- return: player nickname from CommandContext. (string value)
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

### API Helpers.

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

```
JsonHelper.jsonInstance

- return: json instance with configured JsonConfiguration. (Json)

- description: Common json instance with default configuration for Project Essentials modules, if module using json configuration, then you need use this property.
```

```
NativeCommandUtils.removeCommand
- accepts:
    - commandName - command name to remove. (string)

- description: Just remove vanilla or other registered command.
```

### Dependencies using by Core API.

```
    - kotlin-std lib version: 1.3.61
    - kotlinx serialization version: 0.14.0
    - forge version: 1.14.4-28.1.114
    - brigadier version: 1.0.17
    - target jvm version: 1.8
```

### If you have any questions or encounter a problem, be sure to open an issue!
