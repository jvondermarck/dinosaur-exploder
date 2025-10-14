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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;

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

    


}

   
   
   
    @Nested
    class RedDinoTests{
       
        private MockedStatic<FXGL> fxglMock;
        private LocalTimer mockTimer;             
        private RedDinoComponent redD;
        private GameTimer mockGameTimer;
        
        @BeforeEach
        void setUp(){

            // 1) Skapa mockad timer
            mockTimer = org.mockito.Mockito.mock(LocalTimer.class);
            
            // 2) Öppna static-mock för FXGL
            fxglMock = org.mockito.Mockito.mockStatic(FXGL.class);
            
            // 3) Stubba newLocalTimer() → mockTimer
            fxglMock.when(FXGL::newLocalTimer).thenReturn(mockTimer);
            mockGameTimer = org.mockito.Mockito.mock(GameTimer.class);
            redD = new RedDinoComponent(mockGameTimer);

            // MockitoAnnotations.openMocks(this);
            // mockGameTimer = new GameTimer();
        }
        @AfterEach
            void tearDown() {
            // close static mock so it not leaks to other tests 
            fxglMock.close();
        }

        @Test
            @DisplayName("Should decrease amount of lives : amountOflives=0")
            void shouldDecreaseAmountOfLivesWith1_amountOfLivesShouldBe9(){

            //arange
            int startAmountOfLives = redD.getLives(); //1
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


}






    @Nested
    class OrangeDino{

        private MockedStatic<FXGL> fxglMock;
        private LocalTimer mockTimer;             
        private GameTimer mockGameTimer;
        private PlayerComponent mockPlayerComponent;
        private OrangeDinoComponent orangeD;
        
        @BeforeEach
        void setUp(){
             mockTimer = org.mockito.Mockito.mock(LocalTimer.class);
             mockGameTimer = org.mockito.Mockito.mock(GameTimer.class);
             mockPlayerComponent = org.mockito.Mockito.mock(PlayerComponent.class);
            
            // 2) Öppna static-mock för FXGL
            fxglMock = org.mockito.Mockito.mockStatic(FXGL.class);
            
            // 3) Stubba newLocalTimer() → mockTimer
            fxglMock.when(FXGL::newLocalTimer).thenReturn(mockTimer);
            
            orangeD = new OrangeDinoComponent(mockGameTimer,mockPlayerComponent);
        }
        @AfterEach
        void tearDown(){
            fxglMock.close();
        }

        @Test
            @DisplayName("Should decrease amount of lives : amountOflives=0")
            void shouldDecreaseAmountOfLivesWith1_amountOfLivesShouldBe9(){

            //arange
            int startAmountOfLives = orangeD.getLives(); //1
            
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




    }
    

    



}
