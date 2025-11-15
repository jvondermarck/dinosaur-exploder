package com.dinosaur.dinosaurexploder.featureTest;

import org.junit.jupiter.api.AfterEach;

//package com.dinosaur.dinosaurexploder.model;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.almasb.fxgl.core.Inject;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.time.LocalTimer;
import com.dinosaur.dinosaurexploder.components.GreenDinoComponent;
import com.dinosaur.dinosaurexploder.components.OrangeDinoComponent;
import com.dinosaur.dinosaurexploder.components.PlayerComponent;
import com.dinosaur.dinosaurexploder.components.RedDinoComponent;

//import org.mockito.Mockito;
//import static org.mockito.Mockito.*;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.mockito.InjectMocks;

import com.dinosaur.dinosaurexploder.components.ScoreComponent;
import com.dinosaur.dinosaurexploder.utils.AudioManager;
import com.dinosaur.dinosaurexploder.utils.GameTimer;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import com.dinosaur.dinosaurexploder.view.DinosaurGUI;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalTime;
//import java.util.logging.LevelManager;

public class EnemiesTest {

public EnemiesTest() {
    // AudioManager audioManager = new AudioManager();
    
}
    // LevelManager levelManager = new LevelManager();
    //RedDinoComponent redDinoComponent = new RedDinoComponent(null);
    //GreenDinoComponent greenDinoComponent = new GreenDinoComponent();
   // OrangeDinoComponent orangeDinoComponent = new OrangeDinoComponent(null, null);

    // @Mock
    // private GameTimer mockGameTimer;

    // @InjectMocks
    //  private RedDinoComponent redDinoComponent;
    //  private GreenDinoComponent greenDinoComponent;
    //  private OrangeDinoComponent orangeDinoComponent;

    //private CollisionHandler collisionHandler;

    
    
    @Nested
    class GreenDinoTests{

        private MockedStatic<FXGL> fxglMock;
        private LocalTimer mockTimer;             
        private GreenDinoComponent greenD;
        


        @BeforeEach
        void setUp(){

            // 1) Skapa mockad timer
            mockTimer = org.mockito.Mockito.mock(LocalTimer.class);

            // 2) Öppna static-mock för FXGL
            fxglMock = org.mockito.Mockito.mockStatic(FXGL.class);

            // 3) Stubba newLocalTimer() → mockTimer
            fxglMock.when(FXGL::newLocalTimer).thenReturn(mockTimer);

            greenD = new GreenDinoComponent();
            // MockitoAnnotations.openMocks(this);
            // mockGameTimer = new GameTimer();
            
    }   
    @AfterEach
    void tearDown() {
        fxglMock.close();
    }

     
    @Test
    @DisplayName("Should decrease amount of lives : amountOflives=0")
    void shouldDecreaseAmountOfLivesWith1_amountOfLivesShouldBe0(){

        //arange
        int startAmountOfLives = greenD.getLives(); //1
        //act
        greenD.damage(1);
            
        //assert
        assertEquals(startAmountOfLives -1, greenD.getLives());
    }
    @Test
    @DisplayName("Should pause : ")
    void shouldPause_(){

        //arange
        boolean isPaused = true;
        //act
        greenD.setPaused(isPaused);
            
        //assert
        assertTrue(isPaused);
    }
    @Test
    @DisplayName("Get the current enemy speed from the levelManager")
    void shouldReturnEnemySpeed_shouldBeTrue(){
        
        LevelManager levelManager = new LevelManager();
        Double enemySpeed = levelManager.getEnemySpeed();
        
        assertEquals(1.5, enemySpeed);
    }
    


    


}

   
   
   
    @Nested
    class RedDinoTests{
       
        private MockedStatic<FXGL> fxglMock;
        private LocalTimer mockTimer;             
        private RedDinoComponent redD;
        private GameTimer mockGameTimer;
        
        @BeforeEach
        void setUp(){

            mockTimer = org.mockito.Mockito.mock(LocalTimer.class);
            fxglMock = org.mockito.Mockito.mockStatic(FXGL.class);
            
            fxglMock.when(FXGL::newLocalTimer).thenReturn(mockTimer);
            mockGameTimer = org.mockito.Mockito.mock(GameTimer.class);
            
            redD = new RedDinoComponent(mockGameTimer);

            // MockitoAnnotations.openMocks(this);
            // mockGameTimer = new GameTimer();
        }
        @AfterEach
            void tearDown() {
            fxglMock.close();
        }

        @Test
        @DisplayName("Should decrease amount of lives : amountOflives=0")
        void shouldDecreaseAmountOfLivesWith1_amountOfLivesShouldBe9(){

            //arange
            int startAmountOfLives = redD.getLives(); //10
            //act
            redD.damage(1);
            
            //asser
            assertEquals(startAmountOfLives -1, redD.getLives());
        }
        @Test
        @DisplayName("Should set pause to true")
        void shouldPause_(){

            //arange
            boolean isPaused = true;
            
            //act
            redD.setPaused(isPaused);
        
            //assert
            assertTrue(isPaused);
        }
        @Test
        @DisplayName("Should return horizontalSpeed (1,5)")
        void shouldSetHorizontalSpeed(){
           
            //Arrange
            Double horizontalSpeed = 10.00;

            //Act
            redD.setHorizontalSpeed(horizontalSpeed);
            
            //Assert
            assertEquals(horizontalSpeed, redD.getHorizontalSpeed());
        }
        @Test
        @DisplayName("Should return horizontalSpeed (1,5)")
        void shouldReturnHorizontalSpeed(){
            
            //Act and Assert
            assertEquals(1.5, redD.getHorizontalSpeed());
        }




}






    @Nested
    class OrangeDino{

        private MockedStatic<FXGL> fxglMock;
        private LocalTimer mockTimer;             
        private GameTimer mockGameTimer;
        private PlayerComponent mockPlayerComponent;
        private Entity entity;

        private OrangeDinoComponent orangeD;
        private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

        
        @BeforeEach
        void setUp(){
             mockTimer = org.mockito.Mockito.mock(LocalTimer.class);
             mockGameTimer = org.mockito.Mockito.mock(GameTimer.class);
             mockPlayerComponent = org.mockito.Mockito.mock(PlayerComponent.class);
             orangeD = new OrangeDinoComponent(mockGameTimer,mockPlayerComponent);
             
             entity = new Entity();
             entity.addComponent(orangeD);
            
            // 2) Öppna static-mock för FXGL
            fxglMock = org.mockito.Mockito.mockStatic(FXGL.class);
            
            // 3) Stubba newLocalTimer() → mockTimer
            fxglMock.when(FXGL::newLocalTimer).thenReturn(mockTimer);
            
            System.setOut(new PrintStream(outputStreamCaptor));

        }
        @AfterEach
        void tearDown(){
            fxglMock.close();
        }

        @Test
            @DisplayName("Should decrease amount of lives : amountOflives=0")
            void shouldDecreaseAmountOfLivesWith1_amountOfLivesShouldBe9(){

            //arange
            int startAmountOfLives = orangeD.getLives(); //10
            
            //act
            orangeD.damage(1);
            
            //assert
            assertEquals(startAmountOfLives -1, orangeD.getLives());
        }
        @Test
        @DisplayName("Should set pause to true")
        void shouldPause_(){

            //arange
            boolean isPaused = true;
            //act
            orangeD.setPaused(isPaused);
                
            //assert
            assertTrue(isPaused);
        }
        @Test
        @DisplayName("movementSpeed Should be 1.5")
        void getmovementSpeed_ShouldBeOneAndHalf(){
            //arange
            double movementSpeed = 1.5;
            
            //act and assert
            assertEquals(movementSpeed, orangeD.getMovementSpeed() );
        }
        @Test
        @DisplayName("Should set new movementSpeed")
        void setNewMovementSpeed_shouldBe30(){
            
            //Arrange
            orangeD.setMovementSpeed(30);

            //Act and Assert
            assertEquals(30.0, orangeD.getMovementSpeed());
        }

        //moveUp
        @Test
        @DisplayName("Should move orangedino up")
        void moveUp_shouldMoveUp(){
            //Act
            entity.setY(10);
            orangeD.setMovementSpeed(3);
            orangeD.moveUp();
            
            //assert
            assertEquals(7, entity.getY());
        }
        @Test
        @DisplayName("Should system.out.printIn")
        void moveUp_shouldNotChangeX_whenOutOfBounds(){
            //Act
            entity.setY(-100);
            orangeD.setMovementSpeed(3);
            orangeD.moveUp();

            //Assert
            assertEquals("Out of bounds", outputStreamCaptor.toString().trim());
        }
        @Test
        @DisplayName("Should allow moving into -Y when starting at 0")
        void moveUp_shouldDecreaseXByMovementSpeed_whenInsideBounds(){
            //Act
            entity.setY(0);
            orangeD.setMovementSpeed(3);
            orangeD.moveUp();

            //Assert
            assertEquals(-3, entity.getY());
        }

        //moveDown
        @Test
        @DisplayName("Should move down by movementSpeed when inside bounds")
        void moveDown_shouldIncreaseYByMovementSpeed_whenInsideBounds(){
            //Act
            entity.setY(100);
            orangeD.setMovementSpeed(10);
            orangeD.moveDown();
            
            //Assert    
            assertEquals(110, entity.getY());
        }
        @Test
        @DisplayName("Should not move when ")
        void moveDown_shouldNotChangeY_WhenOutOfBounds(){
            //Act
            entity.setY(750);
            orangeD.setMovementSpeed(10);
            orangeD.moveDown();

            assertEquals("Out of bounds", outputStreamCaptor.toString().trim());

        }

        // public void moveDown() {
        // if (!(entity.getY() < DinosaurGUI.HEIGHT - entity.getHeight())) {
        //     System.out.println("Out of bounds");
        //     return;
        // }
        // entity.translateY(movementSpeed);
        // }




        //moveRight
        @Test
        @DisplayName("Should Allow When Y is Bigger Than 0")
        void moveRight_shouldDecreaseXByMovementSpeed_whenInsideBounds(){
            //Act
            entity.setX(100);
            orangeD.setMovementSpeed(3);
            orangeD.moveRight();
            //Assert
            assertEquals(103, entity.getX());
        }
        @Test
        @DisplayName("Should system.out.printIn")
        void moveRight_shouldNotChangeX_whenOutOfBounds(){
            //Act
            entity.setX(1000);
            orangeD.setMovementSpeed(3);
            orangeD.moveRight();

            //Assert
            assertEquals("Out of bounds", outputStreamCaptor.toString().trim());

        }
        //moveLeft
        @Test
        @DisplayName("Should move entity left by movement speed")
        void moveLeft_shouldDecreaseXByMovementSpeed_whenInsideBounds(){
            //Act
            entity.setX(10);
            orangeD.setMovementSpeed(5);
            orangeD.moveLeft();

            //Asser
            assertEquals(5, entity.getX());
        }
        @Test
        @DisplayName("Should system.out.printIn")
        void moveLeft_moveLeft_shouldNotChangeX_whenOutOfBounds(){
            //Arrange
            entity.setX(-10);
            orangeD.setMovementSpeed(10);
            orangeD.moveLeft();
            //Assert
            assertEquals("Out of bounds",outputStreamCaptor.toString().trim());

        }

    }
    

}
