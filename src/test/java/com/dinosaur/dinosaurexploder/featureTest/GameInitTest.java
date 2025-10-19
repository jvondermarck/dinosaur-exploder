// package com.dinosaur.dinosaurexploder.featureTest;

    // import com.almasb.fxgl.dsl.FXGL;
    // import com.almasb.fxgl.entity.Entity;
    // import com.almasb.fxgl.time.LocalTimer;
    // import com.dinosaur.dinosaurexploder.controller.core.GameInitializer;
    // import javafx.geometry.Point2D;
    // import org.junit.jupiter.api.*;
    // import org.mockito.MockedStatic;

    // import static org.junit.jupiter.api.Assertions.*;
    // import static org.mockito.ArgumentMatchers.*;
    // import static org.mockito.Mockito.*;

    // public class GameInitTest {

    //     private GameInitializer gameInitializer;
    //     private MockedStatic<FXGL> fxglMock;
    //     private Entity mockPlayer;

    //     @BeforeEach
    //     void setUp() {
    //         fxglMock = mockStatic(FXGL.class);
    //         mockPlayer = mock(Entity.class);

    //         fxglMock.when(() -> FXGL.newLocalTimer()).thenReturn(mock(LocalTimer.class));
    //         fxglMock.when(() -> FXGL.getAppCenter()).thenReturn(new Point2D(400, 300));
    //         fxglMock.when(() -> FXGL.getAppHeight()).thenReturn(600.0);
    //         fxglMock.when(() -> FXGL.getAppWidth()).thenReturn(300.0);

    //         fxglMock.when(() -> FXGL.set(anyString(), any())).thenAnswer(inv -> null);

    //         fxglMock.when(() -> FXGL.spawn(eq("player"), anyDouble(), anyDouble()))
    //                 .thenReturn(mockPlayer);
    //         fxglMock.when(() -> FXGL.spawn(anyString(), anyDouble(), anyDouble()))
    //                 .thenAnswer(inv -> mock(Entity.class));
    //         fxglMock.when(() -> FXGL.spawn(anyString(), (Point2D) any()))
    //                 .thenAnswer(inv -> mock(Entity.class));

    //         gameInitializer = new GameInitializer();
    //         gameInitializer.initGame();
    //     }


    //     @AfterEach
    //     void tearDown() {
    //         fxglMock.close();
    //     }

    //     @Test
    //     void playerShouldBeCreated() {

    //         int h = 1;
    //        assertEquals(h, 1);
    //     }
    // }