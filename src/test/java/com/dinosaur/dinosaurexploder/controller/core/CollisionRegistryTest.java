package com.dinosaur.dinosaurexploder.controller.core;

import com.dinosaur.dinosaurexploder.controller.core.collisions.CollisionHandlerInterface;
import org.junit.jupiter.api.Test;


import static org.mockito.Mockito.mock;

public class CollisionRegistryTest {

    @Test
    public void testCollison(){
        CollisionRegistry collisionRegistry = new CollisionRegistry();

        CollisionHandlerInterface collisionHandlerInterface = mock(CollisionHandlerInterface.class);

        collisionRegistry.addCollision(collisionHandlerInterface);

        collisionRegistry.registerAll();
    }

}