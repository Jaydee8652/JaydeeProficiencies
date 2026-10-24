package Jaydee8652.JaydeeProficiencies.specialists.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;

import java.util.List;

public class jdp_RightMouseInterceptor implements CustomUIPanelPlugin {
    public boolean isInterceptingRightMouseEvent = false;
    CustomPanelAPI panelPos;

    public void setPanelPos(CustomPanelAPI panelPos) {
        this.panelPos = panelPos;
    }

    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {

    }

    @Override
    public void advance(float amount) {

    }

    public static boolean isHoveringOverButton(UIComponentAPI button, float tooltipCorrection){
        float x = Global.getSettings().getMouseX();
        float y = Global.getSettings().getMouseY();
        float xBut = button.getPosition().getX();
        float yBut = button.getPosition().getY()+tooltipCorrection;
        float width  = button.getPosition().getWidth();
        float height = button.getPosition().getHeight();

        return !(x < xBut) && !(x > xBut + width) && !(y < yBut) && !(y > yBut + height);
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        if (panelPos != null && panelPos.getPosition() != null) {
            if (isHoveringOverButton(panelPos, 0f)) {
                boolean isConsuming = false;
                for (InputEventAPI event : events) {
                    if (event.isConsumed()) continue;
                    if (event.isRMBDownEvent() || event.isMouseScrollEvent()) {

                        if (event.isRMBDownEvent()) {
                            isConsuming = true;

                        }
                        event.consume();
                    }
                }
                isInterceptingRightMouseEvent = isConsuming;
            }

        } else {
            isInterceptingRightMouseEvent = false;
        }

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
