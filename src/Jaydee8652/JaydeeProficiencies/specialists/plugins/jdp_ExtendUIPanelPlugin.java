package Jaydee8652.JaydeeProficiencies.specialists.plugins;

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.ui.CustomPanelAPI;

public interface jdp_ExtendUIPanelPlugin extends CustomUIPanelPlugin {
    public CustomPanelAPI getMainPanel();
    public void createUI();
}
