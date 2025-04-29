package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.entity.component.Component;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Summary:
 * This class extends Component and handles everything around the topic healthbar
 */
public class HealthbarComponent extends Component {

    private int maxHealth;
    private RedDinoComponent redDinoComponent;
    boolean firstTime = true;
    double maxWidht;

    private Rectangle healthbar;

    public RedDinoComponent getRedDinoComponent() {
        return redDinoComponent;
    }

    public void setRedDinoComponent(RedDinoComponent redDinoComponent) {
        this.redDinoComponent = redDinoComponent;
    }

    @Override
    public void onAdded() {
        firstTime = true;

        healthbar = new Rectangle(200.0, 25.0, Color.RED);
        maxWidht = healthbar.getWidth();

        entity.getViewComponent().addChild(healthbar);
    }

    /**
     * Summary :
     * This method updates the healthbar and gets called when the boss Dino got hit
     */
    public void updateBar() {
        // the first time the Dino got hit, the maxhealth is set +1 because the redDinoComponent already got hit once
        if(firstTime){
            maxHealth= redDinoComponent.getLives()+1;
            firstTime = false;
        }
        int currentHealth = redDinoComponent.getLives();
        double percentage = ((double) currentHealth / maxHealth);
        double width = (percentage * maxWidht);
        healthbar.setWidth(width);

        entity.getViewComponent().clearChildren();
        entity.getViewComponent().addChild(healthbar);
    }
}
