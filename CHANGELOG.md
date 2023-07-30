# Changelog
## [Unreleased]
### Added
- UpdateNotifier

### Feature Permissions
| Permission                    | Description                                  |
|-------------------------------|----------------------------------------------|
| `personalizedfavicon.updates` | Notifies the user about new updates          |

### Commands
| Command | Permission | Description                                  |
| ------- | ---------- |----------------------------------------------|
| `/pf edit` | `personalizedfavicon.command.edit` | Opens the Personalized Favicon editor        |
| `/pf load <key>` | `personalizedfavicon.command.edit` | Loads a Personalized Favicon from the Editor |


## [3.0.0] - 2023-07-30
### Added
- BungeeCord & Velocity Support
- MiniMessage Support
- MongoDB Support
- Multi-Image Provider Support (Randomized)

### Commands
| Command | Permission | Description                                  |
| ------- | ---------- |----------------------------------------------|
| `/pf` | `personalizedfavicon.command` | Shows the help menu                          |
| `/pf reload` | `personalizedfavicon.command.reload` | Reloads the plugin configuration             |
|`/pf clear` | `personalizedfavicon.command.clear` | Clears the Personalized Favicon Database     |
