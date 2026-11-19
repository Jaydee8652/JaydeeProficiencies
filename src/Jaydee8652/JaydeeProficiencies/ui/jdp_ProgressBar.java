package Jaydee8652.JaydeeProficiencies.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.List;

//The actual code was pretty heavily rewritten, but the idea of making a fake event intel to display
// a progress bar comes from Kaysaar
public class jdp_ProgressBar implements CustomUIPanelPlugin {
    private transient BaseEventIntel intel;
    transient SpriteAPI arrows = Global.getSettings().getSprite("ui", "sortIcon");

    CustomPanelAPI mainPanel;
    CustomPanelAPI componentPanel;

    Color barBracketColor;
    Color barColor;

    float progress;
    EventProgressBarAPI progressBar;

    jdp_LabelData label;

    // Normal Bar
    public jdp_ProgressBar(float width, float height, Color barColor, Color barBracketColor, jdp_LabelData label, float currProgress) {
        this.label = label;
        this.progress = currProgress;

        mainPanel = Global.getSettings().createCustom(width, height, this);
        createIntel(width, height, barColor, barBracketColor, 100);
        createUI();
    }

    public int sections;
    public int currentSection;
    int minSection;

    boolean haveMovedToAnotherSegment = true;

    boolean sliderMode = false;
    boolean passFirst = true;

    TrapezoidButtonDetector detector = new TrapezoidButtonDetector();

    boolean pressingMouse = false;
    boolean detectedOnceInPerimiters = false;

    // Slider Bar
    public jdp_ProgressBar(float width, float height, Color barColor, Color barBracketColor, jdp_LabelData label, int minSegments, int maxSegments, int currSegment) {
        this.label = label;

        this.minSection = minSegments;
        this.sections = maxSegments;
        this.currentSection = currSegment;

        mainPanel = Global.getSettings().createCustom(width,height,this);
        sliderMode = true;

        createIntel(width, height, barColor, barBracketColor, maxSegments);
        createUI();
    }

    public static class jdp_LabelData {
        private final LabelAPI label;
        private final String font;

        public jdp_LabelData(LabelAPI label) {
            this.label = label;
            this.font = Fonts.DEFAULT_SMALL;
        }

        public jdp_LabelData(LabelAPI label, String font) {
            this.label = label;
            this.font = font;
        }

        public LabelAPI getLabel() {
            return label;
        }

        public String getFont() {
            return font;
        }
    }

    public static class TrapezoidButtonDetector {
        public boolean determineIfHoversOverButton(
                float topLeftX, float topLeftY,
                float topRightX, float topRightY,
                float botLeftX, float botLeftY,
                float botRightX, float botRightY,
                float mouseX, float mouseY) {
            // Split the trapezoid into two triangles and check each one
            return pointInTriangle(mouseX, mouseY, topLeftX, topLeftY, botLeftX, botLeftY, botRightX, botRightY) || pointInTriangle(mouseX, mouseY, topLeftX, topLeftY, topRightX, topRightY, botRightX, botRightY);
        }

        private boolean pointInTriangle(float px, float py, float ax, float ay, float bx, float by, float cx, float cy) {
            float v0x = cx - ax;
            float v0y = cy - ay;
            float v1x = bx - ax;
            float v1y = by - ay;
            float v2x = px - ax;
            float v2y = py - ay;

            float dot00 = v0x * v0x + v0y * v0y;
            float dot01 = v0x * v1x + v0y * v1y;
            float dot02 = v0x * v2x + v0y * v2y;
            float dot11 = v1x * v1x + v1y * v1y;
            float dot12 = v1x * v2x + v1y * v2y;

            float invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
            float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
            float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

            return (u >= 0) && (v >= 0) && (u + v < 1);
        }
    }

    public void createIntel(float width, float height, Color barColor, Color barBracketColor, int max) {
        this.intel = new BaseEventIntel() {
            @Override
            public float getBarWidth() {
                return width - 15;
            }

            @Override
            public float getBarHeight() {
                return height - 4;
            }

            @Override
            public Color getBarBracketColor() {
                return barBracketColor;
            }

            @Override
            public Color getBarColor() {
                Color color = barColor;
                color = Misc.interpolateColor(color, Color.black, 0.25f);
                return color;
            }
        };
        Global.getSector().removeScript(intel);
        Global.getSector().getListenerManager().removeListener(intel);

        intel.setMaxProgress(max);
    }

    public void createUI() {
        if (sliderMode) {
            intel.setProgress(currentSection);
        } else {
            intel.setProgress(Math.round(progress * 100));
        }

        CustomPanelAPI componentPanel = Global.getSettings().createCustom(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(),null);
        TooltipMakerAPI tooltip = componentPanel.createUIElement(componentPanel.getPosition().getWidth(),componentPanel.getPosition().getHeight(),false);

        progressBar = tooltip.addEventProgressBar(intel,0f);
        
        tooltip.setParaFont(label.font);
        progressBar.addComponent(label.label).inMid();

        componentPanel.addUIElement(tooltip).inTL(0,0);
        mainPanel.addComponent(componentPanel).inTL(0,0);

        if(this.componentPanel != null) mainPanel.removeComponent(this.componentPanel);
        this.componentPanel = componentPanel;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public void positionChanged(PositionAPI position) {
    }

    @Override
    public void renderBelow(float alphaMult) {
        if(sliderMode){
            float x =progressBar.getXCoordinateForProgress(currentSection);
            arrows.setColor(Misc.getBasePlayerColor());
            arrows.setAngle(0);
            arrows.renderAtCenter(x,progressBar.getPosition().getY()+progressBar.getPosition().getHeight()+5);
            arrows.setAngle(180);
            arrows.renderAtCenter(x,progressBar.getPosition().getY()-5);
        }
    }

    @Override
    public void render(float alphaMult) {
    }

    @Override
    public void advance(float amount) {
    }

    @Override
    public void buttonPressed(Object buttonId) {
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        if(sliderMode){
            float affectedWidth = progressBar.getPosition().getWidth()-7;
            float sectionWidth = (affectedWidth/(sections+1));
            float mouseX = Global.getSettings().getMouseX();
            float mouseY = Global.getSettings().getMouseY();

            for (InputEventAPI event : events) {
                float topY = progressBar.getPosition().getY()+progressBar.getPosition().getHeight();
                float bottomY = progressBar.getPosition().getY();
                if(event.isConsumed())continue;
                if(!mainPanel.getPosition().containsEvent(event)){
                    if(event.isMouseMoveEvent()){
                        pressingMouse = false;
                    }
                    continue;
                }
                if(event.isLMBDownEvent())pressingMouse = true;
                if(event.isLMBUpEvent()){
                    pressingMouse = false;
                    detectedOnceInPerimiters = false;
                }
                if(pressingMouse){
                    float prevX = (progressBar.getPosition().getX()+sectionWidth+10);
                    if(detectedOnceInPerimiters){
                        topY = Global.getSettings().getScreenHeight();
                        bottomY = 0;
                    }
                    for (int i = minSection; i <= sections; i++) {
                        float currSection = sectionWidth*i;

                        if(detector.determineIfHoversOverButton(prevX,topY,currSection,topY,prevX,bottomY,currSection,bottomY,mouseX,mouseY)){
                            detectedOnceInPerimiters = true;
                            currentSection = i;
                            createUI();
                            haveMovedToAnotherSegment = true;
                            break;
                        }

                        prevX += sectionWidth;
                    }

                }
                if(event.getEventValue()== Keyboard.KEY_LEFT){
                    if(!passFirst){
                        currentSection--;

                        if(currentSection<=minSection){
                            currentSection = minSection;
                       ;
                        }
                        createUI();
                        haveMovedToAnotherSegment = true;
                        passFirst = true;
                    }
                    else{
                        passFirst = false;
                    }

                }
                if(event.getEventValue()== Keyboard.KEY_RIGHT){
                    if(!passFirst){
                        currentSection++;

                        if(currentSection >= sections){
                            currentSection = sections;

                        }
                        haveMovedToAnotherSegment = true;
                        passFirst = true;
                        createUI();
                    }
                    else{
                        passFirst = false;
                    }


                }
                if(progressBar.getPosition().containsEvent(event)){
                    event.consume();
                }
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    public boolean haveMovedToAnotherSegment(){
        return haveMovedToAnotherSegment;
    }

    public void setHaveMovedToAnotherSegment(boolean haveMovedToAnotherSegment) {
        this.haveMovedToAnotherSegment = haveMovedToAnotherSegment;
    }

    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        createUI();
    }

    public void setBarBracketColor(Color barBracketColor) {
        this.barBracketColor = barBracketColor;
    }

    public void setBarColor(Color barColor) {
        this.barColor = barColor;
    }
}
