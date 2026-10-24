package Jaydee8652.JaydeeProficiencies.specialists.data;
import com.fs.starfarer.api.ui.CustomPanelAPI;

public class jdp_PanelData {
    private final CustomPanelAPI panel;
    private final Float width;
    private final Float height;

    public jdp_PanelData(CustomPanelAPI panel, Float width, Float height) {
        this.panel = panel;
        this.width = width;
        this.height = height;
    }

    public CustomPanelAPI panel() {
        return panel;
    }
    public Float width() {
        return width;
    }
    public Float height() {
        return height;
    }
}