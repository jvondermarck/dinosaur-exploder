<h1 align="center"><img src="https://cdn.pixabay.com/photo/2021/03/05/22/44/dinosaur-6072475_960_720.png" width="224px"/><br/>
  Dinosaur Exploder
</h1>
<p align="center">Dinosaur Exploder is a free, open source shoot 'em up video game <br> written in Java using JavaFX and the FXGL game development library 🦖.</p>

<div align="center">
  <img align="center" src="https://img.shields.io/discord/946130675034095667?label=DISCORD&style=for-the-badge">
  <img align="center" src="https://img.shields.io/github/forks/jvondermarck/dinosaur-exploder?style=for-the-badge">
  <img align="center" src="https://img.shields.io/github/contributors/jvondermarck/dinosaur-exploder?style=for-the-badge">
  <br><p></p>
  <img align="center" src="https://img.shields.io/github/issues/jvondermarck/dinosaur-exploder?style=for-the-badge">
  <img align="center" src="https://img.shields.io/github/license/jvondermarck/dinosaur-exploder?style=for-the-badge">
  <img align="center" src="https://img.shields.io/github/actions/workflow/status/jvondermarck/dinosaur-exploder/maven-build.yml?label=BUILD&style=for-the-badge">
 <img >
</div>

# 📃 Table of content

- [🚀 Goal of the project](#-goal-of-the-project)
- [🧑‍💻 Installation](#-installation)
  - [🛠 Installation on Windows / Linux](#-installation-on-windows--linux)
  - [🛠 Installation Locally](#-installation-locally)
- [🎮 How to Play](#-how-to-play)
- [🙏Contributing](#contributing)
- [🌍 Support](#-support)
- [✍️ Licence](#️-licence)
- [👨 Creators](#-creators)

# 🚀 Goal of the project

> [!NOTE]
> I initially developed a very basic game in 2022 and created multiple issues to encourage contributions from the community. The main goal of this repository is not just to build a game, but to provide an open-source project where everyone can contribute step by step.

# 🧑‍💻 Installation

> [!IMPORTANT]
> Be aware to download the version of [Open JDK 21](https://jdk.java.net/archive/) before installing. I bet you can download any JDK you want, it just needs to be version 21.
> Run the command `java --version` to make sure Java 21 is installed.

## 🛠 Installation on Windows / Linux

- Click [here](https://github.com/jvondermarck/dinosaur-exploder/tags) and look at the last release you will found, to download the `dinosaur-exploder.jar` executable.
- Then in a console, type the following command:

```console
$> java -jar dinosaur-exploder.jar
```

## 🛠 Installation Locally

> You should not need to download [Java FX](https://openjfx.io/openjfx-docs/#introduction) and FXGL on your local computer because it should be downloaded when syncing the Maven project.

- To install the project locally, [`fork` our repository](https://github.com/jvondermarck/dinosaur-exploder/fork), and in an empty directory,
  type the following command to `clone` your fork :

```console
$> git clone git@github.com: <user>/dinosaur-exploder.git
```

- Make sure to sync the Maven project in your IDE (you could use IntelliJ IDEA)

- Run the game :
  - On your IDE : hit play on your IDE and select the `com.dinosaur.dinosaurexploder.DinosaurApp` target where it contains the main.
  - With an executable : generate a `jar` file with the command `mvn package` and then `java -jar target/dinosaur-exploder-1.0.jar`
  - On the web : run the command `mvn jpro:run` and the game should be available at `http://localhost:8080/`

# 🎮 How to Play

Play the game with these controls:

- ⬆️ <kbd>Up Arrow</kbd>: move spaceship up.
- ⬇️ <kbd>Down Arrow</kbd>: move spaceship down.
- ⬅️ <kbd>Left Arrow</kbd>: move spaceship left.
- ➡️ <kbd>Right Arrow</kbd>: move spaceship right.
- ⏸️ <kbd>Escape</kbd>: pause the game.
- 🔫 <kbd>Space</kbd>: shoot.
- 💥 <kbd>B</kbd>: eliminate all the dinosaurs on the screen using a bomb.

# Gameplay

https://github.com/user-attachments/assets/9dbad492-b600-4a14-9367-11eef2a7834a

# 🙏Contributing

> [!TIP]
> I am very much open to contributions - please read our [code of conduct](https://github.com/jvondermarck/dinosaur-exploder/blob/main/CODE_OF_CONDUCT.md) and [contribution guidelines](https://github.com/jvondermarck/dinosaur-exploder/blob/main/CONTRIBUTING.md) first.

# 🌍 Support

**Any question ? 🦖 Feel free to write us something :**

- You can ask any question on [GitHub Discussion](https://github.com/jvondermarck/dinosaur-exploder/discussions).
- To be updated of everything, follow us on [Twitter](https://twitter.com/DinosaurExplod1).
- You can post an article on our [Website blog](https://dinosaur-exploder.freecluster.eu/forum).
- For quick communication, feel free to join our [Discord server](https://discord.com/invite/nkmCRnXbWm).

# ✍️ Licence

> This project is licensed under the MIT License - see the [LICENSE.md](https://github.com/jvondermarck/dinosaur-exploder/blob/main/LICENSE) file for details.

# 👨 Author

<p align="center"> At first we are a group of three creators (Dylan, Maxime and I), but since 2023, I am taking care of everything.

<table align="center">
  <tr>
    <th><img src="https://avatars.githubusercontent.com/u/62793491?v=4?size=115" width="115"><br><strong>@jvondermarck</strong></th>
  </tr>
  <tr align="center">
    <td>I am responsible for all aspects of the project, including project management, documentation, web development, and game programming.</td>
  </tr>
</table>