package Jaydee8652.JaydeeProficiencies.specialists.plugins;

import Jaydee8652.JaydeeProficiencies.utils.refl.ui.ListenerFactory;
import com.fs.starfarer.api.campaign.BaseCustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.List;

import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.clearChildren;

public class jdp_ColorWheelPlugin extends BaseCustomUIPanelPlugin {
    private TooltipMakerAPI textFieldTooltip;
    private CustomPanelAPI namePanel;

    private float gap = 6f;//12f;
    private float textWidth = 60f;
    private float handleSize = 12f;
    private float displaySize = 30f / 2f;

    // Current RGB state (0-255)
    private int r = 0;
    private int g = 0;
    private int b = 0;
    private int a = 255;

    // Wheel bounds & state
    private float wheelX;
    private float wheelY;
    private float wheelRadius;
    private float selectedX;
    private float selectedY;

    // Slider bounds & state
    private float rLineY, gLineY, bLineY;
    private float sliderLeftBound, sliderRightBound, sliderWidth;
    private float sliderYBoundTolerance = 15f;

    //Display Bounds
    private float dx;
    private float dy;


    // Dragging flags
    private boolean draggingWheel = false;
    private int draggingSlider = -1; // -1: none, 0: R, 1: G, 2: B

    // Text Fields (tracked)
    private TextFieldAPI rField;
    private TextFieldAPI gField;
    private TextFieldAPI bField;

    private ListenerFactory.ColorWheelScript script;
    private String name;
    private Color defaultColor;

    public jdp_ColorWheelPlugin() {
        // Initialize at Red
        updateCursorFromRGB();
    }

    /**
     * Call this from your menu generation code after creating the plugin
     * to instantiate and link the text fields.
     */
    public void init(CustomPanelAPI panel, Color defaultColor, String name, ListenerFactory.ColorWheelScript script) {
        this.script = script;
        this.name = name;
        this.defaultColor = defaultColor;

        r = defaultColor.getRed();
        g = defaultColor.getGreen();
        b = defaultColor.getBlue();
        a = defaultColor.getAlpha();

        textFieldTooltip = panel.createUIElement(panel.getPosition().getWidth(), panel.getPosition().getHeight(), false);

        rField = textFieldTooltip.addTextField(textWidth, 0f);
        rField.getPosition().inTL(0f,0f);
        rField.setBorderColor(Color.BLACK);
        rField.setBgColor(Color.BLACK);
        rField.setColor(Misc.getTextColor());
        rField.setMidAlignment();

        gField = textFieldTooltip.addTextField(textWidth, 0f);
        gField.getPosition().inTL(0f,30f);
        gField.setBorderColor(Color.BLACK);
        gField.setBgColor(Color.BLACK);
        gField.setColor(Misc.getTextColor());
        gField.setMidAlignment();

        bField = textFieldTooltip.addTextField(textWidth, 0f);
        bField.getPosition().inTL(0f,60f);
        bField.setBorderColor(Color.BLACK);
        bField.setBgColor(Color.BLACK);
        bField.setColor(Misc.getTextColor());
        bField.setMidAlignment();

        namePanel = panel.createCustomPanel(panel.getPosition().getWidth(), 30f, null);
        panel.addComponent(namePanel).inTL(0f, 0f);

        updateNameColor();
        updateTextFields();
        panel.addUIElement(textFieldTooltip);
        positionChanged(panel.getPosition());
    }

    public Color getCurrentColor() {
        return new Color(r, g, b, a);
    }
    public Color getDefaultColor() {
        return defaultColor;
    }

    public void setCurrentColor(Color color) {
        r = color.getRed();
        g = color.getGreen();
        b = color.getBlue();

        updateCursor();
        updateScript();
    }
    public void setDefaultColor(Color color) {
        defaultColor = color;
    }


    @Override
    public void positionChanged(PositionAPI pos) {
        float cx = pos.getCenterX();
        float cy = pos.getCenterY();
        float width = pos.getWidth();
        float height = pos.getHeight();

        // 1. Layout Sliders at the top (Starsector Y is bottom-up, so top is pos.getY() + height)
        float topY = pos.getY() + height;
        rLineY = topY - 30f;
        gLineY = topY - 60f;
        bLineY = topY - 90f;

        sliderLeftBound = pos.getX() + gap;

        float maxAvailableWidth = (width - (gap * 2));
        sliderWidth = maxAvailableWidth - (textWidth + (gap * 3));

        sliderRightBound = sliderLeftBound + sliderWidth;

        if (textFieldTooltip != null) textFieldTooltip.getPosition().inTL(sliderWidth + (gap * 2), 15f);

        // 2. Layout Wheel at the bottom center
        float remainingHeight = height - 100f; // Give top 100px to sliders
        wheelRadius = Math.min(width, remainingHeight) / 2f - 20f;

        //wheelX = cx;
        wheelX = pos.getX() + maxAvailableWidth - wheelRadius;
        wheelY = pos.getY() + (remainingHeight / 2f);

        dx = pos.getX() + gap + displaySize;
        dy = pos.getCenterY();

        // Re-align cursor to current layout
        updateCursorFromRGB();
    }

    @Override
    public void processInput(List<InputEventAPI> events) {

        // Read any newly typed numbers from the text fields before processing mouse events
        if (!draggingWheel && draggingSlider == -1) {
            checkTextField(rField, 0);
            checkTextField(gField, 1);
            checkTextField(bField, 2);
        }

        for (InputEventAPI event : events) {
            if (event.isConsumed()) continue;

            if (event.isMouseEvent()) {
                float mouseX = event.getX();
                float mouseY = event.getY();

                // === 1. Mouse Down ===
                if (event.isMouseDownEvent()) {
                    if (isInWheelBounds(mouseX, mouseY) && !draggingWheel && draggingSlider == -1) {
                        draggingWheel = true;
                        updateColorFromWheel(mouseX, mouseY);
                        event.consume();
                    }
                    else if (isInSliderBounds(mouseX, mouseY, rLineY)) {
                        draggingSlider = 0;
                        updateSliderFromMouse(mouseX, draggingSlider);
                        event.consume();
                    }
                    else if (isInSliderBounds(mouseX, mouseY, gLineY)) {
                        draggingSlider = 1;
                        updateSliderFromMouse(mouseX, draggingSlider);
                        event.consume();
                    }
                    else if (isInSliderBounds(mouseX, mouseY, bLineY)) {
                        draggingSlider = 2;
                        updateSliderFromMouse(mouseX, draggingSlider);
                        event.consume();
                    }
                    else if(isInDisplayBounds(mouseX, mouseY)) {
                        resetScript();
                        event.consume();
                    }
                }

                // === 2. Mouse Scroll (Only for Sliders) ===
                if (event.isMouseScrollEvent()) {
                    float amount = event.getEventValue() > 0 ? 5f : -5f; // Step by 5/255 per scroll
                    if (isInSliderBounds(mouseX, mouseY, rLineY)) {
                        r = Math.max(0, Math.min(255, (int)(r + amount)));
                        updateCursorFromRGB();
                        updateTextFields();
                        event.consume();
                    } else if (isInSliderBounds(mouseX, mouseY, gLineY)) {
                        g = Math.max(0, Math.min(255, (int)(g + amount)));
                        updateCursorFromRGB();
                        updateTextFields();
                        event.consume();
                    } else if (isInSliderBounds(mouseX, mouseY, bLineY)) {
                        b = Math.max(0, Math.min(255, (int)(b + amount)));
                        updateCursorFromRGB();
                        updateTextFields();
                        event.consume();
                    }
                }

                // === 3. Mouse Move (Dragging) ===
                if (event.isMouseMoveEvent()) {
                    if (draggingWheel) {
                        updateColorFromWheel(mouseX, mouseY);
                        event.consume();
                    } else if (draggingSlider != -1) {
                        updateSliderFromMouse(mouseX, draggingSlider);
                        event.consume();
                    }
                }

                // === 4. Mouse Up ===
                if (event.isMouseUpEvent()) {
                    if (draggingWheel || draggingSlider != -1) {
                        draggingWheel = false;
                        draggingSlider = -1;
                        event.consume();
                    }
                }
            }
        }
    }

    @Override
    public void renderBelow(float alphaMult) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Turn on OpenGL Line Anti-Aliasing
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

        // --- 1. Draw Color Wheel (Triangle Fan) ---
        int segments = 360;

        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glColor4f(1f, 1f, 1f, alphaMult * (a / 255f)); // Center is purely white //TODO: HERE
        GL11.glVertex2f(wheelX, wheelY);

        for (int i = 0; i <= segments; i++) {
            float angle = (float) (i * 2 * Math.PI / segments);
            float hue = (float) i / segments;
            Color edge = Color.getHSBColor(hue, 1f, 1f);

            GL11.glColor4f(edge.getRed() / 255f, edge.getGreen() / 255f, edge.getBlue() / 255f, alphaMult * (a / 255f)); //TODO: HERE
            GL11.glVertex2f(wheelX + (float) Math.cos(angle) * wheelRadius, wheelY + (float) Math.sin(angle) * wheelRadius);
        }
        GL11.glEnd();

        //TODO: BLOT OUT CIRCLE EDGE
        GL11.glLineWidth(1.5f);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i < segments; i++) {
            float angle = (float) (i * 2 * Math.PI / segments);
            GL11.glColor4f(0, 0, 0, alphaMult);
            GL11.glVertex2f(wheelX + (float) Math.cos(angle) * wheelRadius, wheelY + (float) Math.sin(angle) * wheelRadius);
        }
        GL11.glEnd();

        // --- 1b. The Anti-Aliasing Trick ---
        GL11.glLineWidth(1.5f);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i < segments; i++) {
            float angle = (float) (i * 2 * Math.PI / segments);
            float hue = (float) i / segments;
            Color edge = Color.getHSBColor(hue, 1f, 1f);

            GL11.glColor4f(edge.getRed() / 255f, edge.getGreen() / 255f, edge.getBlue() / 255f, alphaMult * (a / 255f));//TODO: HERE
            GL11.glVertex2f(wheelX + (float) Math.cos(angle) * wheelRadius, wheelY + (float) Math.sin(angle) * wheelRadius);
        }
        GL11.glEnd();

        // --- 2. Draw Wheel Cursor ---
        GL11.glColor4f(0f, 0f, 0f, alphaMult);
        GL11.glLineWidth(4f);
        drawCircle(selectedX, selectedY, 8f, 60);

        GL11.glColor4f(1f, 1f, 1f, alphaMult);
        GL11.glLineWidth(2f);
        drawCircle(selectedX, selectedY, 8f, 60);

        // --- 3. Draw Sliders ---
        drawSlider(rLineY, SliderColor.RED, r / 255f, alphaMult);
        drawSlider(gLineY, SliderColor.GREEN, g / 255f, alphaMult);
        drawSlider(bLineY, SliderColor.BLUE, b / 255f, alphaMult);

        var ddx = dx + displaySize;
        var ddy = dy - displaySize;

        // Draw default color display
        GL11.glColor4f(defaultColor.getRed() /255f, defaultColor.getGreen() /255f, defaultColor.getBlue() /255f, alphaMult * (a / 255f));
        GL11.glBegin(GL11.GL_POLYGON);
        GL11.glVertex2f(ddx, ddy);
        GL11.glVertex2f(ddx, ddy + displaySize);
        GL11.glVertex2f(ddx + displaySize, ddy + displaySize);
        GL11.glVertex2f(ddx + displaySize, ddy - displaySize);
        GL11.glVertex2f(ddx - displaySize, ddy - displaySize);
        GL11.glVertex2f(ddx - displaySize, ddy);
        GL11.glEnd();

        GL11.glColor4f(0f, 0f, 0f, alphaMult);
        GL11.glLineWidth(1f);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2f(ddx, ddy);
        GL11.glVertex2f(ddx, ddy + displaySize);
        GL11.glVertex2f(ddx + displaySize, ddy + displaySize);
        GL11.glVertex2f(ddx + displaySize, ddy - displaySize);
        GL11.glVertex2f(ddx - displaySize, ddy - displaySize);
        GL11.glVertex2f(ddx - displaySize, ddy);
        GL11.glEnd();

        // Draw current color display
        GL11.glColor4f(r /255f, g /255f, b /255f, alphaMult * (a / 255f));
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(dx - displaySize, dy - displaySize);
        GL11.glVertex2f(dx + displaySize, dy - displaySize);
        GL11.glVertex2f(dx + displaySize, dy + displaySize);
        GL11.glVertex2f(dx - displaySize, dy + displaySize);
        GL11.glEnd();

        GL11.glColor4f(0f, 0f, 0f, alphaMult);
        GL11.glLineWidth(1f);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2f(dx - displaySize, dy - displaySize);
        GL11.glVertex2f(dx + displaySize, dy - displaySize);
        GL11.glVertex2f(dx + displaySize, dy + displaySize);
        GL11.glVertex2f(dx - displaySize, dy + displaySize);
        GL11.glEnd();

        // Clean up GL states
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    // =========================================================================
    // Text Fields and Event Processing
    // =========================================================================

    private void checkTextField(TextFieldAPI field, int sliderId) {
        if (field == null) return;
        String text = field.getText();

        // Force 'Numbers Only' by stripping non-numeric characters immediately
        String numeric = text.replaceAll("[^0-9]", "");
        if (!numeric.equals(text)) {
            field.setText(numeric);
            text = numeric;
        }

        // If user deleted the text, just wait for them to type without enforcing "0" immediately
        if (text.isEmpty()) return;

        try {
            int val = Integer.parseInt(text);
            int clamped = Math.max(0, Math.min(255, val));

            // Constrain 0-255 bounds logic directly to textfield logic
            if (val != clamped) {
                field.setText(String.valueOf(clamped));
                val = clamped;
            }

            boolean changed = false;
            if (sliderId == 0 && r != val) { r = val; changed = true; }
            else if (sliderId == 1 && g != val) { g = val; changed = true; }
            else if (sliderId == 2 && b != val) { b = val; changed = true; }

            // Make wheel follow dynamically if slider value changes via manual typing
            if (changed) {
                updateScript();
                updateCursorFromRGB();
            }
        } catch (NumberFormatException e) {
            // Unlikely with regex stripping, but protects against integer overflows
        }
    }

    private void updateTextFields() {
        String rText = String.valueOf(r);
        String gText = String.valueOf(g);
        String bText = String.valueOf(b);

        if (rField != null && !rField.getText().equals(rText)) {
            rField.setText(rText);
        }

        if (gField != null && !gField.getText().equals(gText)) {
            gField.setText(gText);
        }

        if (bField != null && !bField.getText().equals(bText)) {
            bField.setText(bText);
        }

        updateNameColor();
        updateScript();
    }

    private void updateNameColor() {
        clearChildren(namePanel);
        var stringElement = namePanel.createUIElement(namePanel.getPosition().getWidth(), namePanel.getPosition().getHeight(), false);
        stringElement.addPara(name, new Color(r, g, b, a), 3f);
        namePanel.addUIElement(stringElement);
    }


    public void updateScript() {
        script.Update(this);
    }
    public void resetScript() {
        script.Reset(this);
    }

    public void updateCursor() {
        updateCursorFromRGB();
        updateTextFields();
    }

    // =========================================================================
    // Core Math & Interactions
    // =========================================================================

    private void updateColorFromWheel(float mouseX, float mouseY) {
        float dx = mouseX - wheelX;
        float dy = mouseY - wheelY;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist > wheelRadius) {
            dx = (dx / dist) * wheelRadius;
            dy = (dy / dist) * wheelRadius;
            dist = wheelRadius;
        }

        selectedX = wheelX + dx;
        selectedY = wheelY + dy;

        float blend = dist / wheelRadius;
        float angle = (float) Math.atan2(dy, dx);
        if (angle < 0) angle += (float) (Math.PI * 2);

        float hue = angle / (float) (Math.PI * 2);
        Color edge = Color.getHSBColor(hue, 1f, 1f);

        r = Math.round(255 + (edge.getRed() - 255) * blend);
        g = Math.round(255 + (edge.getGreen() - 255) * blend);
        b = Math.round(255 + (edge.getBlue() - 255) * blend);

        updateTextFields(); // Let Text Fields reflect changes
    }

    private void updateSliderFromMouse(float mouseX, int sliderId) {
        float relativeX = mouseX - sliderLeftBound;
        float clampedX = Math.max(0f, Math.min(relativeX, sliderWidth));
        double fraction = clampedX / sliderWidth;

        int val = (int) Math.round(fraction * 255f);

        if (sliderId == 0) r = val;
        else if (sliderId == 1) g = val;
        else if (sliderId == 2) b = val;

        updateCursorFromRGB();
        updateTextFields(); // Let Text Fields reflect changes
    }

    private void updateCursorFromRGB() {
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        float hue = hsb[0];
        float sat = hsb[1];

        float dist = sat * wheelRadius;
        float angle = hue * (float) (Math.PI * 2);

        selectedX = wheelX + (float) Math.cos(angle) * dist;
        selectedY = wheelY + (float) Math.sin(angle) * dist;
    }

    // =========================================================================
    // GL11 Rendering Utilities
    // =========================================================================

    private enum SliderColor {
        RED,
        GREEN,
        BLUE
    }

    private void drawSlider(float lineY, SliderColor sliderColor, float fraction, float alphaMult) {
        var redMin = r / 255f;
        var greenMin = g / 255f;
        var blueMin = b / 255f;

        var redMax = r / 255f;
        var greenMax = g / 255f;
        var blueMax = b / 255f;

        switch(sliderColor) {
            case RED:
                redMin = 0f;
                redMax = 1f;
                break;
            case GREEN:
                greenMin = 0f;
                greenMax = 1f;
                break;
            case BLUE:
                blueMin = 0f;
                blueMax = 1f;
                break;
            default:
        }

        GL11.glLineWidth(3f);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glColor4f(redMin, greenMin, blueMin, alphaMult * (a / 255f));//TODO: HERE
        GL11.glVertex2f(sliderLeftBound, lineY);
        GL11.glColor4f(redMax, greenMax, blueMax, alphaMult * (a / 255f));//TODO: HERE
        GL11.glVertex2f(sliderRightBound, lineY);
        GL11.glEnd();

        // Calculate handle position
        float x = sliderLeftBound + (fraction * sliderWidth);
        float hs = handleSize / 2f;

        //TODO: BLOT OUT HANDLE
        GL11.glColor4f(0, 0, 0, alphaMult);//TODO: HERE
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x - hs, lineY - hs);
        GL11.glVertex2f(x + hs, lineY - hs);
        GL11.glVertex2f(x + hs, lineY + hs);
        GL11.glVertex2f(x - hs, lineY + hs);
        GL11.glEnd();

        // Draw handle body
        GL11.glColor4f(r /255f, g /255f, b /255f, alphaMult * (a / 255f));//TODO: HERE
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x - hs, lineY - hs);
        GL11.glVertex2f(x + hs, lineY - hs);
        GL11.glVertex2f(x + hs, lineY + hs);
        GL11.glVertex2f(x - hs, lineY + hs);
        GL11.glEnd();

        // Draw handle border
        GL11.glColor4f(0f, 0f, 0f, alphaMult);
        GL11.glLineWidth(1f);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2f(x - hs, lineY - hs);
        GL11.glVertex2f(x + hs, lineY - hs);
        GL11.glVertex2f(x + hs, lineY + hs);
        GL11.glVertex2f(x - hs, lineY + hs);
        GL11.glEnd();
    }


    private void drawCircle(float x, float y, float radius, int segments) {
        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i < segments; i++) {
            float angle = (float) (i * 2 * Math.PI / segments);
            GL11.glVertex2f(x + (float) Math.cos(angle) * radius, y + (float) Math.sin(angle) * radius);
        }
        GL11.glEnd();
    }

    // =========================================================================
    // Bounds Checking
    // =========================================================================

    private boolean isInWheelBounds(float mouseX, float mouseY) {
        float dx = mouseX - wheelX;
        float dy = mouseY - wheelY;
        return (dx * dx + dy * dy) <= (wheelRadius * wheelRadius);
    }

    private boolean isInSliderBounds(float mouseX, float mouseY, float lineY) {
        return mouseX >= sliderLeftBound - handleSize && mouseX <= sliderRightBound + handleSize &&
                mouseY >= (lineY - sliderYBoundTolerance) && mouseY <= (lineY + sliderYBoundTolerance);
    }

    private boolean isInDisplayBounds(float mouseX, float mouseY) {
        return mouseX >= dx - displaySize && mouseX <= dx + (2 * displaySize) &&
                mouseY >= (dy - displaySize * 2) && mouseY <= (dy + displaySize);
    }
}





