<!--
<h1 align="center">
  <img width="70%"  alt="image" src="https://github.com/user-attachments/assets/2623c200-16a4-4cca-9a56-4a506b519f41" />
  <img src="https://cdn.pixabay.com/photo/2021/03/05/22/44/dinosaur-6072475_960_720.png" width="224px"/><br/>
  Dinosaur Exploder
</h1>
-->
<div align="center">
  <a href="https://github.com/jvondermarck/dinosaur-exploder">
    <img width="70%" alt="Dinosaur Exploder - Classic Arcade Shooter" src="https://github.com/user-attachments/assets/978572a9-cc8d-4233-804f-f8b3e16186a3" />
  </a>

  <h3>🦖 Dinosaur Exploder</h3>
  
  <p>
    <b>An open-source learning platform disguised as a shoot 'em up game</b><br>
    Built with Java/FXGL and Next.js • Perfect for your first (or next) contribution
  </p>

  <kbd><a href="#-quick-start-for-contributors"><b>🚀 Quick Start</b></a></kbd>
  <kbd><a href="https://github.com/jvondermarck/dinosaur-exploder/issues"><b>📋 Browse Issues</b></a></kbd>
  <kbd><a href="https://discord.com/invite/nkmCRnXbWm"><b>💬 Discord</b></a></kbd>
  <kbd><a href="https://dinosaur-exploder.vercel.app/"><b>🌐 Website</b></a></kbd>

  <img src="https://img.shields.io/github/contributors/jvondermarck/dinosaur-exploder?style=for-the-badge&color=4CAF50">
  <img src="https://img.shields.io/github/issues/jvondermarck/dinosaur-exploder?style=for-the-badge&color=2196F3">
  <img src="https://img.shields.io/github/forks/jvondermarck/dinosaur-exploder?style=for-the-badge&color=FFA726">
  <img src="https://img.shields.io/github/stars/jvondermarck/dinosaur-exploder?style=for-the-badge&color=FFD700">
  <img src="https://img.shields.io/discord/946130675034095667?label=DISCORD&style=for-the-badge&logo=discord&logoColor=white&color=7289DA">  
</div>

---
## 🎯 Why Contribute Here?

This isn't just a game—it's a **real-world project designed for learning and collaboration**. Whether you're looking to: 

- ✅ Make your first open-source contribution
- ✅ Build experience with Java game development (JavaFX, FXGL)
- ✅ Work with modern web tech (Next.js, TypeScript, React)
- ✅ Practice Git workflows, CI/CD, and testing
- ✅ Join a friendly community that helps each other learn

**You're in the right place!** 🎉

> ⭐ **Like what you see?** Star the repo to help others discover it!

---

## 🎥 What We're Building

https://github.com/user-attachments/assets/4f505240-2d6d-4523-8a93-7e9ffebe4145

A classic arcade shooter with:
- 🎮 **Java game engine** (JavaFX + FXGL)
- 🌐 **Next.js website** for docs, galleries, and community features
- 🔄 **CI/CD pipeline** with GitHub Actions
- 🧪 **Unit tests** and best practices
- 🔓 **Open architecture** for easy contributions

---

## 🚀 Quick Start for Contributors

### Step 1: Pick Your Tech Stack

<table>
<tr>
<td width="50%">

### 🎮 **Java Game Development**
**Tech:** Java 21, JavaFX, FXGL, Maven

**Great if you want to learn:**
- Game development fundamentals
- Entity systems and game loops
- Collision detection
- Audio/visual effects

**Browse:** [Game issues →](https://github.com/jvondermarck/dinosaur-exploder/labels/java)

</td>
<td width="50%">

### 🌐 **Website Development**
**Tech:** Next.js, TypeScript, React, Tailwind CSS

**Great if you want to learn:**
- Modern React patterns
- Server-side rendering
- API integration (GitHub API)
- Responsive design

**Browse:** [Website issues →](https://github.com/jvondermarck/dinosaur-exploder/issues?q=state%3Aopen%20label%3ANext.js)

</td>
</tr>
</table>

### Step 2: Set Up Your Environment

<details>
<summary><b>🎮 Java Setup (Click to expand)</b></summary>

#### Prerequisites
- **Java >=21** ([Download](https://jdk.java.net/archive/))
  - You can download the Java [Open JDK 21](https://jdk.java.net/archive/). You can download any JDK you want, it just needs to be version 21 or greater.
  - Run the command `java --version` to make sure Java >= 21 is installed.
  - The environment variable `JAVA_HOME` must be set (either you're on MacOS, Windows, Linux). You can check with the command `echo $JAVA_HOME`.
- Git
- IDE (IntelliJ IDEA recommended)

#### Installation
```bash
# 1. Fork the repo on GitHub
# 2. Clone your fork
git clone git@github.com:<your-username>/dinosaur-exploder.git
cd dinosaur-exploder

# 3. Build and run
mvn clean install
mvn javafx:run

# Or build with desktop profile
mvn clean package -Pdesktop
java -jar target/dinosaur-exploder-2.0.0.jar

# Or using an IDE: 
# - Open project in IntelliJ
# - Run com.dinosaur.dinosaurexploder.DinosaurApp or hit the run button at the top right

# 4. (Optional but HIGHLY recommanded) Install git hooks for code quality checks
./scripts/install-hooks.sh
```

#### Testing
```bash
mvn test
```

#### Browser Version (Optional)
```bash
mvn jpro:run
# Open http://localhost:8080
```

📖 **More details:** [Java Setup Guide](https://github.com/jvondermarck/dinosaur-exploder/wiki/Java-Installation-Guide)

</details>

<details>
<summary><b>🌐 Website Setup (Click to expand)</b></summary>

#### Prerequisites
- **Node.js >=20** ([Download](https://nodejs.org/))
- Git
- IDE (VS Code recommended for Next.js)

#### Installation
```bash
# 1. Fork the repo on GitHub
# 2. Clone your fork
git clone git@github.com:<your-username>/dinosaur-exploder.git
cd dinosaur-exploder/website

# 3. Install and run
npm install
npm run dev

# Open http://localhost:3000
```

#### Testing
```bash
npm test
npm run lint
```

📖 **More details:** [Website Setup Guide](https://github.com/jvondermarck/dinosaur-exploder/wiki/Node.js-Installation-Guide)

</details>

### Step 3: Find an Issue

**New to open source?** Start here:
- [`good first issue`](https://github.com/jvondermarck/dinosaur-exploder/labels/good%20first%20issue) - Perfect for beginners
- [`help wanted`](https://github.com/jvondermarck/dinosaur-exploder/issues?q=state%3Aopen%20label%3A%22status%3A%20help-wanted%22) - We'd love help with these
- [`documentation`](https://github.com/jvondermarck/dinosaur-exploder/issues?q=state%3Aopen%20label%3A%22type%3A%20documentation%22) - No coding required

**Ready for more?** Check: 
- [`enhancement`](https://github.com/jvondermarck/dinosaur-exploder/issues?q=state%3Aopen%20label%3A%22type%3A%20enhancement%22) - New features
- [`bug`](https://github.com/jvondermarck/dinosaur-exploder/issues?q=state%3Aopen%20label%3A%22type%3A%20bug%22) - Fix something broken

**Have your own idea?** [Open a discussion](https://github.com/jvondermarck/dinosaur-exploder/discussions) first!

### Step 4: Make Your Contribution

1. **Create a branch**:  `git checkout -b feature/your-feature-name`
2. **Make your changes** (don't forget tests!)
3. **Check and format code:** `mvn spotless:apply`
4. **Commit**: `git commit -m "Add: description of your change"`
5. **Push**: `git push origin feature/your-feature-name`
6. **Open a Pull Request** on GitHub

📖 **Full guidelines:** [CONTRIBUTING.md](CONTRIBUTING.md)

---

## 📚 Documentation & Resources

- 📘 [Wiki](https://github.com/jvondermarck/dinosaur-exploder/wiki) - Architecture, guides, and tutorials
- 🎨 [Project Website](https://dinosaur-exploder.vercel.app/) - Game info and community content
- 🤝 [Code of Conduct](CODE_OF_CONDUCT.md) - Our community standards
- 📋 [Contributing Guide](CONTRIBUTING.md) - Detailed contribution workflow

---

## 💬 Community & Support

**Get help, share ideas, or just hang out:**

- 💬 **[Discord](https://discord.com/invite/nkmCRnXbWm)** - Real-time chat with the community (most active!)
- 🗨️ **[GitHub Discussions](https://github.com/jvondermarck/dinosaur-exploder/discussions)** - Q&A, ideas, and announcements
- 🐦 **[Twitter](https://twitter.com/DinosaurExplod1)** - Project updates

**Stuck?** Don't hesitate to ask!  We're here to help you learn. 🦖

---

## 🎮 Want to Just Play?

**Requirements:** Java 21

Download the latest release:
- [🪟 Windows](https://github.com/jvondermarck/dinosaur-exploder/releases) 
- [🐧 Linux](https://github.com/jvondermarck/dinosaur-exploder/releases)
- [🍎 macOS](https://github.com/jvondermarck/dinosaur-exploder/releases)

```bash
java -jar dinosaur-exploder-<platform>-<version>.jar
```

**Controls:** Arrow keys (move) • Space (shoot) • E (shield) • B (bomb) • Esc (pause)

---

## 🏗️ Project Background

I created this project in 2022 at Munster Technological University (MTU Cork) as a school assignment with two classmates. After the semester ended, I decided to transform it into something bigger:  **a welcoming space where developers can gain real-world experience through meaningful contributions**.

The goal isn't perfection—it's **learning by doing**. Every issue is an opportunity, and every contributor makes this project better.

---

## 📊 Project Stats

![Alt](https://repobeats.axiom.co/api/embed/f0e2627d3bca4c04bde531f392f027e8e2697ccc.svg "Repobeats analytics image")

---

## 📄 License

MIT License - see [LICENSE](LICENSE) for details.

---

<div align="center">
  Made with ❤️ by <a href="https://github.com/jvondermarck">@jvondermarck</a> and our amazing <a href="https://github.com/jvondermarck/dinosaur-exploder/graphs/contributors">contributors</a>
  <br><br>
  <sub>⭐ Star us on GitHub — it helps more developers find this project!</sub>
</div>
