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

## 📃 Table of content

- [🚀 Goal of the project](#-goal-of-the-project)
- [🧑‍💻 Installation](#-installation)
  - [🛠 Installation on Windows / Linux](#-installation-on-windows--linux)
  - [💻 Installation Locally](#-installation-locally)
- [🎮 How to Play](#-how-to-play)
- [🎥 Gameplay](#-gameplay)
- [🙏Contributing](#contributing)
- [🌍 Support](#-support)
- [✍️ Licence](#-licence)
- [👨 Creators](#-author)

## 🚀 Goal of the project

> [!NOTE]
> I initially developed a very basic game in 2022 and created multiple issues to encourage contributions from the community. The main goal of this repository is not just to build a game, but to provide an open-source project where everyone can contribute step by step.

## 🧑‍💻 Installation

> [!IMPORTANT]
> Be aware to download the version of [Open JDK 21](https://jdk.java.net/archive/) before installing. I bet you can download any JDK you want, it just needs to be version 21.
> Run the command `java --version` to make sure Java 21 is installed.

### 🛠 Installation on Windows / Linux / Mac

🚀 **Quick Start**:
1. **Download the latest release** ➡️ [Click here](https://github.com/jvondermarck/dinosaur-exploder/tags) and grab the appropriate JAR for your platform:
   - 🪟 `dinosaur-exploder-win-<version>.jar`
   - 🐧 `dinosaur-exploder-linux-<version>.jar`
   - 🍎 `dinosaur-exploder-mac-<version>.jar`
2. **Run the game** by executing the following command in your terminal:
   ```console
   $ java -jar dinosaur-exploder-<platform>-<version>.jar
   ```

### 💻 Installation Locally

💡 **No need to install** [JavaFX](https://openjfx.io/openjfx-docs/#introduction) or FXGL manually—Maven will handle dependencies automatically!

#### 📥 Clone the repository
1. **Fork** the repository ➡️ [`Click here`](https://github.com/jvondermarck/dinosaur-exploder/fork).
2. In your terminal, run:
   ```console
   $ git clone git@github.com:<your-username>/dinosaur-exploder.git
   ```

#### 🏗️ Build & Run

➡️ **Using an IDE (Recommended)**
- Open the project in **IntelliJ IDEA** (or any Java IDE).
- Sync the Maven project.
- Run the main class:
  - Locate `com.dinosaur.dinosaurexploder.DinosaurApp`.
  - Hit **Run** ▶️

➡️ **Using the Command Line**
- **Generate the JAR**:
  ```console
  $ mvn package
  ```  
- **Run the game**:
  ```console
  $ java -jar target/dinosaur-exploder-1.0.jar
  ```  

➡️ **Run in the Browser**
- Start a local web server:
  ```console
  $ mvn jpro:run
  ```  
- Open **http://localhost:8080/** 🌐

> 📖 **Need more details?**  
> Check out our **[installation guide](https://github.com/jvondermarck/dinosaur-exploder/wiki/Documentation#dinosaur-exploder-documentation)** for in-depth instructions!

## 🎮 How to Play

Play the game with these controls:

- ⬆️ <kbd>Up Arrow</kbd>: move spaceship up.
- ⬇️ <kbd>Down Arrow</kbd>: move spaceship down.
- ⬅️ <kbd>Left Arrow</kbd>: move spaceship left.
- ➡️ <kbd>Right Arrow</kbd>: move spaceship right.
- ⏸️ <kbd>Escape</kbd>: pause the game.
- 🔫 <kbd>Space</kbd>: shoot.
- 🛡️ <kbd>E</kbd>: shield to protect the spaceship during few seconds.
- 💥 <kbd>B</kbd>: eliminate all the dinosaurs on the screen using a bomb.

## 🎥 Gameplay

> Here is a demo of the actual game. Dare to play it now 🎮 !

https://github.com/user-attachments/assets/4b5a6ed4-2e68-4e12-a9c8-8a6c33178c5e

## 🙏 Contributing

> [!TIP]
> I am very much open to contributions - please read our [code of conduct](https://github.com/jvondermarck/dinosaur-exploder/blob/main/CODE_OF_CONDUCT.md) and [contribution guidelines](https://github.com/jvondermarck/dinosaur-exploder/blob/main/CONTRIBUTING.md) first.

## 🌍 Support

**Any question ? 🦖 Feel free to write us something :**

- You can ask any question on [GitHub Discussion](https://github.com/jvondermarck/dinosaur-exploder/discussions).
- To be updated of everything, follow us on [Twitter](https://twitter.com/DinosaurExplod1).
- You can post an article on our [Website blog](https://dinosaur-exploder.freecluster.eu/forum).
- For quick communication, feel free to join our [Discord server](https://discord.com/invite/nkmCRnXbWm).

## ✍️ Licence

> This project is licensed under the MIT License - see the [LICENSE.md](https://github.com/jvondermarck/dinosaur-exploder/blob/main/LICENSE) file for details.

## 👨 Author

Initially, the project was a collaboration between three creators : Dylan, Maxime, and myself—as part of a school project at the University of Cork in 2022. However, after the semester ended, I took over full responsibility for the project, while my teammates moved on to other endeavors.

<table align="center">
  <tr>
    <th><img src="https://avatars.githubusercontent.com/u/62793491?v=4?size=115" width="115"><br><strong>@jvondermarck</strong></th>
  </tr>
  <tr align="center">
    <td>I am responsible for all aspects of the project, including project management, documentation, web development, and game programming.</td>
  </tr>
</table>
