# Contributing Guidelines

Thank you for showing interest in contributing to our game and community. If you want an overview of this project, please refer to [README.md](https://github.com/dylan-power/dinosaur-exploder/blob/main/README.md).

Before contributing, please read our
[Code of Conduct](https://github.com/dylan-power/dinosaur-exploder/blob/main/CODE_OF_CONDUCT.md) along with this document.

If you don't have git set up on your machine, please [follow this guide](https://docs.github.com/en/get-started/quickstart/set-up-git).

If you haven't developed using JavaFX and FXGL, please watch this [video first](https://www.youtube.com/watch?v=48rVgdq0mFA).

## Types of Contributions Accepted

We accept the following types of contributions:

### Issues

If you've found a bug in the game itself or something else (e.g. documentation), you can [open an issue](https://github.com/dylan-power/dinosaur-exploder/issues/new/choose) to let the community know.
You can also open an issue to let us know about a feature you want to see developed or address anything else related to the project.

If you see an issue you would like to develop a solution for, please comment on that issue and a community leader will assign it to you.

Please make sure to look at all other open issues [here](https://github.com/dylan-power/dinosaur-exploder/issues) first to make sure that your issue
hasn't already been brought up by someone else.

### Pull requests

If you decide to develop a solution for an issue, you can submit these changes through a pull request.

#### Pull Request Title Format

PR titles must follow this format: `type(scope): description (#issue)`.

- **type** (required): One of `feat`, `fix`, `refactor`, `docs`, `test`, `chore`
- **scope** (optional): Context in parentheses, e.g. `(auth)`, `(readme)`
- **description** (required): A clear description of the change
- **(#issue)** (optional): The issue number in parentheses with a hash, e.g. `(#335)`

Examples:

- `feat(auth): add OAuth2 support (#42)`
- `fix: handle null token`
- `docs(readme): update setup instructions (#335)`

If this is your first time contributing to open source, please refer to this [guide](https://github.com/firstcontributions/first-contributions)
on how to submit a pull request.

After submitting a pull request, a community leader will review it promptly and suggest changes if necessary. Your solution will then be merged into
the main branch and the pull request will be closed.

## Custom controls

The controls editor is available from **Settings > Controls** and **Pause > Controls**.
The defaults are Space to shoot, X for a bomb, and C for the shield. Click the key for
Shoot, Bomb, or Shield, then press a replacement key. Escape cancels
key capture. **Apply** saves and activates the changes; **Cancel** discards them.
**Reset to defaults** changes the draft and must also be confirmed with **Apply**.
Movement keys, Escape, and Expert mode mouse shooting are unchanged. Duplicate
bindings, modifiers, movement keys, and menu/navigation keys cannot be assigned.

For control changes, keep default keys in
[`ControlAction`](src/main/java/com/dinosaur/dinosaurexploder/model/ControlAction.java).
Input registration, the editor, control labels, and reset behavior use those defaults.
Custom bindings are stored in `settings.properties` alongside audio and language
settings; older settings files do not require migration.

Run the focused regression tests with:

```bash
mvn -Dtest=KeyBindingsTest,ControlBindingsTest,GameControlsComponentTest,SettingsProviderTest test
```

On a graphical desktop, also exercise the JavaFX editor interactions and translated
layouts with:

```bash
mvn -Dtest=ControlsMenuTest -Dcontrols.ui.tests=true test
```
