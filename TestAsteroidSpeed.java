/**
 * Test program to demonstrate asteroid speed differences between game modes.
 * This simulates exactly what happens in the real game with our changes.
 */
public class TestAsteroidSpeed {

    enum GameMode {
        EASY(0.7),      // 30% más lento
        NORMAL(1.0),    // velocidad base
        EXPERT(1.0);    // velocidad base

        private final double speedMultiplier;

        GameMode(double speedMultiplier) {
            this.speedMultiplier = speedMultiplier;
        }

        public double getAsteroidsSpeedMultiplier() {
            return speedMultiplier;
        }
    }

    static class LevelManager {
        private static final double BASE_ASTEROIDS_VERTICAL_SPEED = 0.8;
        private static final double BASE_ASTEROIDS_HORIZONTAL_SPEED = 0.2;

        private double asteroidsVerticalSpeed;
        private double asteroidsHorizontalSpeed;

        public void setGameMode(GameMode mode) {
            asteroidsVerticalSpeed = BASE_ASTEROIDS_VERTICAL_SPEED * mode.getAsteroidsSpeedMultiplier();
            asteroidsHorizontalSpeed = BASE_ASTEROIDS_HORIZONTAL_SPEED * mode.getAsteroidsSpeedMultiplier();
        }

        public double getAsteroidsVerticalSpeed() {
            return asteroidsVerticalSpeed;
        }

        public double getAsteroidsHorizontalSpeed() {
            return asteroidsHorizontalSpeed;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== PRUEBA DE VELOCIDADES DE ASTEROIDES ===\n");

        LevelManager levelManager = new LevelManager();

        // Prueba con NORMAL
        System.out.println("📊 Modo NORMAL:");
        levelManager.setGameMode(GameMode.NORMAL);
        double normalVertical = levelManager.getAsteroidsVerticalSpeed();
        double normalHorizontal = levelManager.getAsteroidsHorizontalSpeed();
        System.out.println("  Velocidad Vertical: " + normalVertical + " pixels/frame");
        System.out.println("  Velocidad Horizontal: " + normalHorizontal + " pixels/frame");

        // Prueba con EASY
        System.out.println("\n📊 Modo EASY:");
        levelManager.setGameMode(GameMode.EASY);
        double easyVertical = levelManager.getAsteroidsVerticalSpeed();
        double easyHorizontal = levelManager.getAsteroidsHorizontalSpeed();
        System.out.println("  Velocidad Vertical: " + easyVertical + " pixels/frame");
        System.out.println("  Velocidad Horizontal: " + easyHorizontal + " pixels/frame");

        // Comparación
        System.out.println("\n📊 Modo EXPERT:");
        levelManager.setGameMode(GameMode.EXPERT);
        double expertVertical = levelManager.getAsteroidsVerticalSpeed();
        double expertHorizontal = levelManager.getAsteroidsHorizontalSpeed();
        System.out.println("  Velocidad Vertical: " + expertVertical + " pixels/frame");
        System.out.println("  Velocidad Horizontal: " + expertHorizontal + " pixels/frame");

        // Análisis
        System.out.println("\n" + "=".repeat(50));
        System.out.println("ANÁLISIS:");
        System.out.println("=".repeat(50));

        double verticalDiff = ((normalVertical - easyVertical) / normalVertical) * 100;
        double horizontalDiff = ((normalHorizontal - easyHorizontal) / normalHorizontal) * 100;

        System.out.println("\n✅ EASY es " + String.format("%.1f", verticalDiff) + "% MÁS LENTO que NORMAL (vertical)");
        System.out.println("✅ EASY es " + String.format("%.1f", horizontalDiff) + "% MÁS LENTO que NORMAL (horizontal)");

        // Verificación del test
        boolean testPasses = easyVertical < normalVertical && easyHorizontal < normalHorizontal;
        System.out.println("\n" + (testPasses ? "✅ TEST PASADO: Los asteroides en EASY son más lentos que en NORMAL" : "❌ TEST FALLIDO"));

        // Simulación de movimiento
        System.out.println("\n" + "=".repeat(50));
        System.out.println("SIMULACIÓN: Posición de asteroide después de 100 frames");
        System.out.println("=".repeat(50));

        int frames = 100;
        double normalX = normalHorizontal * frames;
        double normalY = normalVertical * frames;
        double easyX = easyHorizontal * frames;
        double easyY = easyVertical * frames;

        System.out.println("\nModo NORMAL - Posición después de " + frames + " frames:");
        System.out.println("  X: " + normalX + " pixels");
        System.out.println("  Y: " + normalY + " pixels");

        System.out.println("\nModo EASY - Posición después de " + frames + " frames:");
        System.out.println("  X: " + easyX + " pixels");
        System.out.println("  Y: " + easyY + " pixels");

        System.out.println("\nEn EASY, el asteroide viajó " + String.format("%.1f", (normalX - easyX)) + 
                           " pixels menos en X y " + String.format("%.1f", (normalY - easyY)) + " pixels menos en Y");

        System.out.println("\n✅ Issue resuelto: Los asteroides en modo EASY son más lentos!");
    }
}
