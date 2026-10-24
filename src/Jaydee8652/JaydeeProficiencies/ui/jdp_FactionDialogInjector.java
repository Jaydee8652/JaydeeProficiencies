package Jaydee8652.JaydeeProficiencies.ui;

import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_FactionElement;
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_PresetFactionElement;
import Jaydee8652.JaydeeProficiencies.specialists.plugins.jdp_ColorWheelPlugin;
import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_FactionUtils;
import Jaydee8652.JaydeeProficiencies.utils.refl.ReflectionUtilis;
import Jaydee8652.JaydeeProficiencies.utils.refl.ReflectionUtils;
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.*;
import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.impl.SimulatorPluginImpl;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.campaign.Faction;
import com.fs.starfarer.ui.impl.StandardTooltipV2Expandable;
import com.fs.starfarer.ui.interfacenew;
import org.json.JSONException;
import org.json.JSONObject;
import org.lazywizard.console.BaseCommand;
import org.lwjgl.input.Keyboard;
import sound.O;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Ref;
import java.util.*;
import java.util.List;

import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.clearChildren;
import static Jaydee8652.JaydeeProficiencies.utils.JaydeeCommons.getColor;
import static Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_FactionUtils.*;
import static Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_FactionUtils.brightUIColorKey;
import static Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_IndustryUtils.capitaliseFirst;
import static Jaydee8652.JaydeeProficiencies.utils.refl.ui.UIReflectionUtils.*;
import static Jaydee8652.JaydeeProficiencies.utils.refl.ui.UIReflectionUtils.getCoreUI;
import static com.fs.starfarer.api.impl.SimulatorPluginImpl.UNLOCKS_DATA_FILE;

public class jdp_FactionDialogInjector implements EveryFrameScript {
    public static class jdp_FactionColorTabTrigger extends ListenerFactory.ActionListener {
        private static Object dialog;

        public void initialise(Object dialog) {
            jdp_FactionColorTabTrigger.dialog = dialog;
        }

        @Override
        public void trigger(Object... args) {
            createFactionColorPicker(dialog);
        }
    }

    public static List<String> JDP_FACTION_COLOR_BLACKLIST = new ArrayList<>();

    static {
        JDP_FACTION_COLOR_BLACKLIST.add("mercenary");
        JDP_FACTION_COLOR_BLACKLIST.add("lions_guard");
        JDP_FACTION_COLOR_BLACKLIST.add("scavengers");
        JDP_FACTION_COLOR_BLACKLIST.add("poor");
        JDP_FACTION_COLOR_BLACKLIST.add("neutral");
        JDP_FACTION_COLOR_BLACKLIST.add("sleeper");
        JDP_FACTION_COLOR_BLACKLIST.add("knights_of_ludd");
        JDP_FACTION_COLOR_BLACKLIST.add("player");
        JDP_FACTION_COLOR_BLACKLIST.add("player");
        JDP_FACTION_COLOR_BLACKLIST.add("nex_temp");
        JDP_FACTION_COLOR_BLACKLIST.add("nex_derelict");
        JDP_FACTION_COLOR_BLACKLIST.add("ML_bounty");
        JDP_FACTION_COLOR_BLACKLIST.add("jdp_deadcomm");
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }

    @Override
    public void advance(float amount) {
        injectFactionColorButton();
    }

    public static void injectFactionColorButton() {
        var FactionDialog = findFactionDialog();
        if (FactionDialog == null) return;

        ButtonAPI flagButton = null;
        ButtonAPI confirmButton = null;
        ButtonAPI colorButton = null;

        var panel = ReflectionUtilis.invokeMethodDirectly(ClassRefs.FactionDialogGetInnerPanelMethod, FactionDialog);
        var children = (List<Object>) ReflectionUtilis.invokeMethodDirectly(ClassRefs.uiPanelgetChildrenNonCopyMethod, panel);

        for (Object child : children) {
            if (child instanceof ButtonAPI button) {
                if (button.getPosition().getHeight() > 100f) flagButton = button;
                if (button.getText() != null && button.getText().contains("Confirm")) confirmButton = button;
                if (button.getText() != null && button.getText().contains("Faction Color")) {
                    colorButton = button;
                    break;
                }
            }
        }
        if (confirmButton == null || flagButton == null || colorButton != null) return;

        var trigger = new jdp_FactionColorTabTrigger();
        trigger.initialise(FactionDialog);
        var button = UIReflectionUtils.makeButton(
                "Faction Color",
                trigger,
                Misc.getBasePlayerColor(),
                Misc.getDarkPlayerColor(),
                Alignment.MID,
                CutStyle.TL_BR,
                flagButton.getPosition().getWidth(),
                confirmButton.getPosition().getHeight(),
                Keyboard.KEY_C,
                true
        );
        var pos = new UIPanel(panel).add(button);
        pos.getInstance().belowLeft(flagButton, confirmButton.getPosition().getHeight());
    }

    public static Object findFactionDialog() {
        UIPanelAPI core = UIReflectionUtils.getCoreUI();
        if (core == null) return null;

        var CampaignState = ReflectionUtilis.invokeMethodDirectly(ClassRefs.coreUIgetPluginMethod, core);
        var main = ReflectionUtilis.invokeMethodDirectly(ClassRefs.campaignStateGetScreenPanelMethod, CampaignState);

        List<Object> children = new ArrayList<>();
        for (Object child : (List<Object>) ReflectionUtilis.invokeMethodDirectly(ClassRefs.uiPanelgetChildrenNonCopyMethod, main))
            if (child.getClass().equals(ClassRefs.FactionDialogClass)) children.add(child);
        for (Object child : (List<Object>) ReflectionUtilis.invokeMethodDirectly(ClassRefs.uiPanelgetChildrenNonCopyMethod, core))
            if (child.getClass().equals(ClassRefs.FactionDialogClass)) children.add(child);

        //Get the last one, that's the most recent one and won't just be removed in a few frames
        if (!children.isEmpty()) return children.get(children.size() - 1);
        return null;
    }

    public static final String AUTOGENERATE_CHECKED = "jdp_autogenerateFactionColorChecked";
    public static final String SHOW_HIDDEN_CHECKED = "jdp_showHiddenFactionsChecked";

    public static float colorWheelHeight = 256f;
    public static float colorWheelWidth = 256f;
    public static float presetPanelWidth = 280f;
    public static float presetPanelHeight = 30f;
    public static float titleHeight = 30f;
    public static float presetHeight = (presetPanelHeight + 3) * 4;

    public static HashMap<String, jdp_ColorWheelPlugin> colorPickerMap = new HashMap<String, jdp_ColorWheelPlugin>();

    public static CustomPanelAPI mutablePanel = null;
    public static CustomPanelAPI scrollerPanel = null;
    public static CustomPanelAPI presetPanel = null;

    public static void createFactionColorPicker(Object dialog) {
        colorPickerMap.clear();
        UIReflectionUtils.ConfirmDialogData master = UIReflectionUtils.showConfirmationDialog(
                EMPTY_STRING,
                EMPTY_STRING,
                "Dismiss",
                CONFIRM_DIALOG_WIDTH * 1.2f,
                CONFIRM_DIALOG_HEIGHT * 1.5f,
                255,
                new ListenerFactory.DialogDismissedListener() {
                    @Override
                    public void trigger(Object... args) {
                        updateScreenColor(dialog);
                    }
                });
        if (master == null) return;
        master.panel.removeComponent(master.confirmButton.getInstance());

        var panel = Global.getSettings().createCustom(master.contentWidth, master.contentHeight, null);
        master.panel.addComponent(panel);

        mutablePanel = Global.getSettings().createCustom(master.contentWidth, master.contentHeight, null);
        panel.addComponent(mutablePanel).inTL(0f, 0f);
        recreateFactionColorPickerPanel(dialog, master, mutablePanel);

        presetPanel = panel.createCustomPanel(presetPanelWidth + 3f, presetHeight + master.borderThickness, null);
        panel.addComponent(presetPanel).inTL(master.borderThickness, titleHeight + master.borderThickness);
        var presetElement = presetPanel.createUIElement(presetPanelWidth + 3f, presetHeight + master.borderThickness, false);
        new jdp_FactionElement(Global.getSector().getPlayerFaction().getId(), colorPickerMap, presetElement, presetPanelWidth - master.borderThickness, presetPanelHeight);
        presetElement.addSpacer(3f);
        new jdp_PresetFactionElement(presetSlotAKey, colorPickerMap, presetElement, presetPanelWidth - master.borderThickness, presetPanelHeight);
        presetElement.addSpacer(3f);
        new jdp_PresetFactionElement(presetSlotBKey, colorPickerMap, presetElement, presetPanelWidth - master.borderThickness, presetPanelHeight);
        presetElement.addSpacer(3f);
        new jdp_PresetFactionElement(presetSlotCKey, colorPickerMap, presetElement, presetPanelWidth - master.borderThickness, presetPanelHeight);
        presetElement.addSpacer(3f);
        presetPanel.addUIElement(presetElement).inTL(0f, 0f);

        scrollerPanel = panel.createCustomPanel(presetPanelWidth + 3f, Math.round(master.contentHeight - (titleHeight + (master.borderThickness) + presetHeight)), null);
        panel.addComponent(scrollerPanel).inTL(master.borderThickness, titleHeight + (master.borderThickness) + presetHeight);
        recreateFactionColorScroller(dialog, master, scrollerPanel);

        var pickerBase = makeColorPicker(master.panel, (Color) Global.getSector().getPersistentData().get(baseUIColorKey), "Base Color", colorWheelWidth, colorWheelHeight, new ListenerFactory.ColorWheelScript() {
            @Override
            public void Update(jdp_ColorWheelPlugin plugin) {
                var preset = (String) Global.getSector().getPersistentData().get(jdp_FactionUtils.presetKey);
                var presetFaction = Global.getSector().getFaction(preset);

                if (presetFaction == null) {
                    if(preset == presetSlotAKey || preset == presetSlotBKey || preset == presetSlotCKey) {
                        plugin.setDefaultColor(getColor(preset, baseUIColorKey));
                    } else {
                        Global.getSector().getPersistentData().put(presetKey, "player");//Failsafe incase a faction mod can survive being removed
                        plugin.setDefaultColor(baseUIColorDefault);
                    }
                } else if (presetFaction.isPlayerFaction()) {
                    plugin.setDefaultColor(baseUIColorDefault);
                } else {
                    plugin.setDefaultColor(presetFaction.getBaseUIColor());
                }

                var curr = plugin.getCurrentColor();
                var color = new Color(curr.getRed(), curr.getGreen(), curr.getBlue(), 127);

                if (!color.equals(Global.getSector().getPersistentData().get(colorKey))) {
                    Global.getSector().getPersistentData().put(colorKey, color);

                    var base = plugin.getCurrentColor();

                    Global.getSector().getPersistentData().put(baseUIColorKey, base);

                    for (var pluginSet : colorPickerMap.entrySet()) {
                        if (!pluginSet.getKey().equals(baseUIColorKey)) {
                            var otherPlugin = pluginSet.getValue();

                            otherPlugin.updateScript();
                            if (isBoxChecked(AUTOGENERATE_CHECKED, true))
                                otherPlugin.setCurrentColor(otherPlugin.getDefaultColor());
                        }
                    }

                    recreateFactionColorPickerPanel(dialog, master, mutablePanel);
                }
            }
        });
        pickerBase.getPosition().inTL(presetPanelWidth + (master.borderThickness * 3), titleHeight + master.borderThickness);
        colorPickerMap.put(baseUIColorKey, (jdp_ColorWheelPlugin) pickerBase.getPlugin());

        var pickerGrid = makeColorPicker(master.panel, (Color) Global.getSector().getPersistentData().get(gridUIColorKey), "Grid Color", colorWheelWidth, colorWheelHeight, new ListenerFactory.ColorWheelScript() {
            @Override
            public void Update(jdp_ColorWheelPlugin plugin) {
                if (Global.getSector().getPersistentData().get(colorKey).equals(colorDefault)) {
                    plugin.setDefaultColor(gridUIColorDefault);
                } else {
                    plugin.setDefaultColor(jdp_FactionUtils.createGridUIColor((Color) Global.getSector().getPersistentData().get(colorKey)));
                }

                var color = plugin.getCurrentColor();
                if (!color.equals(Global.getSector().getPersistentData().get(gridUIColorKey))) {
                    Global.getSector().getPersistentData().put(gridUIColorKey, color);
                    recreateFactionColorPickerPanel(dialog, master, mutablePanel);
                }
            }
        });
        pickerGrid.getPosition().rightOfTop(pickerBase, 0f);
        colorPickerMap.put(gridUIColorKey, (jdp_ColorWheelPlugin) pickerGrid.getPlugin());

        var pickerDark = makeColorPicker(master.panel, (Color) Global.getSector().getPersistentData().get(darkUIColorKey), "Dark Color", colorWheelWidth, colorWheelHeight, new ListenerFactory.ColorWheelScript() {
            @Override
            public void Update(jdp_ColorWheelPlugin plugin) {
                if (Global.getSector().getPersistentData().get(colorKey).equals(colorDefault)) {
                    plugin.setDefaultColor(darkUIColorDefault);
                } else {
                    plugin.setDefaultColor(jdp_FactionUtils.createDarkUIColor((Color) Global.getSector().getPersistentData().get(colorKey)));
                }

                var color = plugin.getCurrentColor();
                if (!color.equals(Global.getSector().getPersistentData().get(darkUIColorKey))) {
                    Global.getSector().getPersistentData().put(darkUIColorKey, color);
                    recreateFactionColorPickerPanel(dialog, master, mutablePanel);
                }
            }
        });
        pickerDark.getPosition().belowLeft(pickerGrid, 0f);
        colorPickerMap.put(darkUIColorKey, (jdp_ColorWheelPlugin) pickerDark.getPlugin());

        var pickerBright = makeColorPicker(master.panel, (Color) Global.getSector().getPersistentData().get(brightUIColorKey), "Bright Color", colorWheelWidth, colorWheelHeight, new ListenerFactory.ColorWheelScript() {
            @Override
            public void Update(jdp_ColorWheelPlugin plugin) {
                if (Global.getSector().getPersistentData().get(colorKey).equals(colorDefault)) {
                    plugin.setDefaultColor(brightUIColorDefault);
                } else {
                    plugin.setDefaultColor(jdp_FactionUtils.createBrightUIColor((Color) Global.getSector().getPersistentData().get(colorKey)));
                }

                var color = plugin.getCurrentColor();
                if (!color.equals(Global.getSector().getPersistentData().get(brightUIColorKey))) {
                    Global.getSector().getPersistentData().put(brightUIColorKey, color);
                    recreateFactionColorPickerPanel(dialog, master, mutablePanel);
                }
            }
        });
        pickerBright.getPosition().belowLeft(pickerBase, 0f);
        colorPickerMap.put(brightUIColorKey, (jdp_ColorWheelPlugin) pickerBright.getPlugin());

        ButtonAPI cancelButton = master.cancelButton.getInstance();
        cancelButton.setShortcut(Keyboard.KEY_G, false);
    }

    protected static SimulatorPluginImpl.SimUnlocksData unlocksData = new SimulatorPluginImpl.SimUnlocksData();
    public static void recreateFactionColorScroller(Object dialog, ConfirmDialogData master, CustomPanelAPI scrollerPanel) {
        clearChildren(scrollerPanel);
        var scrollerElement = scrollerPanel.createUIElement(presetPanelWidth + 3f, Math.round(master.contentHeight - (titleHeight + (master.borderThickness * 3) + presetHeight + 6f + master.cancelButton.getInstance().getPosition().getHeight())), true);

        List<String> factions = new ArrayList<>();
        //factions.add(Global.getSector().getPlayerFaction().getId());
        if(isBoxChecked(SHOW_HIDDEN_CHECKED, false)) {
            ArrayList<String> factionsTemp = new ArrayList<>(Global.getSector().getAllFactions().stream().filter(c -> !JDP_FACTION_COLOR_BLACKLIST.contains(c.getId())).map(FactionAPI::getId).toList());
            factionsTemp.sort(Comparator.comparing(Object::toString));
            factions.addAll(factionsTemp);
        } else {
            try {
                if (Global.getSettings().fileExistsInCommon(UNLOCKS_DATA_FILE)) {
                    JSONObject json = Global.getSettings().readJSONFromCommon(UNLOCKS_DATA_FILE, true);
                    unlocksData.fromJSON(json);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayList<String> factionsTemp = new ArrayList<>(Global.getSector().getAllFactions().stream().filter(c -> !JDP_FACTION_COLOR_BLACKLIST.contains(c.getId()) && c.isShowInIntelTab()).map(FactionAPI::getId).toList());
            ArrayList<String> factionsKnown = new ArrayList<>(Misc.getSimulatorPlugin().getCategories().stream().filter(c -> unlocksData.factions.contains(c.faction.getId()) && !c.nonFactionCategory && !c.custom && !JDP_FACTION_COLOR_BLACKLIST.contains(c.faction.getId())).map(f -> f.faction.getId()).toList());
            for (String faction : factionsKnown) if(!factionsTemp.contains(faction)) factionsTemp.add(faction);

            factionsTemp.sort(Comparator.comparing(Object::toString));
            factions.addAll(factionsTemp);
        }

        for (String faction : factions) {
            new jdp_FactionElement(faction, colorPickerMap, scrollerElement, presetPanelWidth - master.borderThickness, presetPanelHeight);
            scrollerElement.addSpacer(3f);
        }
        scrollerPanel.addUIElement(scrollerElement).inTL(0f, 0f);
    }

    public static void recreateFactionColorPickerPanel(Object dialog, ConfirmDialogData master, CustomPanelAPI mutablePanel) {
        clearChildren(mutablePanel);

        var titlePanel = mutablePanel.createCustomPanel(master.contentWidth, titleHeight, null);
        mutablePanel.addComponent(titlePanel).inTL(master.borderThickness, master.borderThickness);

        var titleElement = titlePanel.createUIElement(master.contentWidth - (master.borderThickness * 2f), titleHeight, false);
        titlePanel.addUIElement(titleElement).inTL(0f, 0f);
        titleElement.addSectionHeading("Pick a color palette for your faction.", (Color) Global.getSector().getPersistentData().get(baseUIColorKey), (Color) Global.getSector().getPersistentData().get(darkUIColorKey), Alignment.MID, 10f);

        var textPanel = mutablePanel.createCustomPanel(master.contentWidth - presetPanelWidth + 3f, master.contentHeight - ((colorWheelHeight * 2) + titleHeight + master.borderThickness), null);
        mutablePanel.addComponent(textPanel).inTL(presetPanelWidth + (master.borderThickness * 2), titleHeight + master.borderThickness + (colorWheelHeight * 2));

        var textElement = textPanel.createUIElement(master.contentWidth - presetPanelWidth + 3f - (master.borderThickness * 3), master.contentHeight - ((colorWheelHeight * 2) + titleHeight + master.borderThickness), false);
        textPanel.addUIElement(textElement).inTL(0f, 0f);

        textElement.addPara("The Player Faction/UI color palette has four colors that can be altered independently: Base, Bright, Grid, and Dark    *Preset color palettes are described in this order. ", 3f, new Color[]{(Color) Global.getSector().getPersistentData().get(baseUIColorKey), (Color) Global.getSector().getPersistentData().get(brightUIColorKey), (Color) Global.getSector().getPersistentData().get(gridUIColorKey), (Color) Global.getSector().getPersistentData().get(darkUIColorKey), Misc.getGrayColor()}, "Base", "Bright", "Grid", "Dark", "*Preset color palettes are described in this order. ");
        textElement.addSpacer(10f);
        textElement.addPara("Changes in this screen will affect the entire UI, but only in this save. Preset Slots A, B and C can be used to save/load palettes across saves.", 3f, Misc.getHighlightColor(), "entire UI");

        var autogenerateCheckbox = makeCheckbox( "Auto [A]", Fonts.ORBITRON_16, (Color) Global.getSector().getPersistentData().get(baseUIColorKey), ButtonAPI.UICheckboxSize.SMALL, Keyboard.KEY_A, true, new ListenerFactory.CheckboxPlugin() {
            @Override
            public void checkedOn() {
                Global.getSector().getPersistentData().put(AUTOGENERATE_CHECKED, true);
            }

            @Override
            public void checkedOff() {
                Global.getSector().getPersistentData().put(AUTOGENERATE_CHECKED, false);
            }

            @Override
            public Boolean isChecked() {
                return isBoxChecked(AUTOGENERATE_CHECKED, true);
            }
        });

        StandardTooltipV2Expandable autogenerateTooltip = new StandardTooltipV2Expandable(400, false) {
            public void createImpl(boolean var1x) {
                Color base = (Color) Global.getSector().getPersistentData().get(baseUIColorKey);

                addPara("Toggles the automatic generation of faction colors.", 3f);
                addSpacer(3f);

                addPara("When enabled, changing the Base color will automatically select Bright, Grid, and Dark colors algorithmically. Selecting presets will also apply all four selected colors at once.", 3f, new Color[]{Misc.getHighlightColor(), base, jdp_FactionUtils.createBrightUIColor(base), jdp_FactionUtils.createGridUIColor(base), jdp_FactionUtils.createDarkUIColor(base)}, "enabled", "Base", "Bright", "Grid", "Dark");

                addSpacer(3f);
                addPara("When disabled, the generated colors will instead be set as defaults, selecting presets will also set all four defaults to those of the preset. Defaults can be selectively applied by clicking the color wheel's respective palette in the mid right.", 3f, Misc.getHighlightColor(), "disabled");
            }
        };

        ReflectionUtilis.invokeMethodDirectly(ClassRefs.buttonSetTooltipMethod, autogenerateCheckbox.getInstance(), 0f, autogenerateTooltip);
        ReflectionUtilis.invokeMethodDirectly(ClassRefs.buttonSetTooltipPositionRelativeToAnchorMethod, autogenerateCheckbox.getInstance(), -2.0f, -151, autogenerateCheckbox.getInstance());

        var AutogeneratePos = new UIPanel(mutablePanel).add(autogenerateCheckbox);
        AutogeneratePos.getInstance().inTL(presetPanelWidth + (master.borderThickness * 3),titleHeight + master.borderThickness + (colorWheelHeight / 2) + 40);

        var showHiddenCheckbox = makeCheckbox( "Show Hidden Factions", Fonts.ORBITRON_16, (Color) Global.getSector().getPersistentData().get(baseUIColorKey), ButtonAPI.UICheckboxSize.SMALL, new ListenerFactory.CheckboxPlugin() {
            @Override
            public void checkedOn() {
                Global.getSector().getPersistentData().put(SHOW_HIDDEN_CHECKED, true);
                recreateFactionColorScroller(dialog, master, scrollerPanel);
            }

            @Override
            public void checkedOff() {
                Global.getSector().getPersistentData().put(SHOW_HIDDEN_CHECKED, false);
                recreateFactionColorScroller(dialog, master, scrollerPanel);

            }

            @Override
            public Boolean isChecked() {
                return isBoxChecked(SHOW_HIDDEN_CHECKED, false);
            }
        });

        StandardTooltipV2Expandable showHiddenTooltip = new StandardTooltipV2Expandable(400, false) {
            public void createImpl(boolean var1x) {
                addPara("Toggles showing hidden factions.", 3f);
                addSpacer(3f);

                addPara("When enabled, allows hidden factions to be used as color presets, including spoilers and debug factions.", 3f, new Color[]{Misc.getHighlightColor()}, "spoilers and debug factions");

                addSpacer(3f);
                addPara("The individual color squares in each preset can be clicked to apply a single color from that preset.", 3f);
            }
        };

        ReflectionUtilis.invokeMethodDirectly(ClassRefs.buttonSetTooltipMethod, showHiddenCheckbox.getInstance(), 0f, showHiddenTooltip);
        ReflectionUtilis.invokeMethodDirectly(ClassRefs.buttonSetTooltipPositionRelativeToAnchorMethod, showHiddenCheckbox.getInstance(), -2.0f, -105, showHiddenCheckbox.getInstance());

        var showHiddenPos = new UIPanel(mutablePanel).add(showHiddenCheckbox);
        showHiddenPos.getInstance().inBL(master.borderThickness + 6f, master.borderThickness);
    }

    public static void updateScreenColor(Object dialog) {
        boolean isShowingMarketInfo = false;

        jdp_FactionUtils.updatePlayerFaction();
        ReflectionUtilis.getMethodAndInvokeDirectly("dismiss", dialog, 1, 0);
        var shown = Global.getSector().getCampaignUI().showPlayerFactionConfigDialog();

        var currentTab = ReflectionUtilis.invokeMethodDirectly(ClassRefs.coreUIgetCurrentTabMethod, getCoreUI());
        if (currentTab == null) return;

        UIPanelAPI tradePanel = (UIPanelAPI) ReflectionUtilis.getMethodAndInvokeDirectly("getTradePanel", currentTab, 0);
        if (tradePanel != null) isShowingMarketInfo = (boolean) ReflectionUtilis.getMethodAndInvokeDirectly("isShowingMarketInfo", tradePanel, 0);
        if (tradePanel instanceof interfacenew currentInterface) currentInterface.sizeChanged(currentInterface.getWidth(), currentInterface.getHeight());
        if (isShowingMarketInfo) ReflectionUtilis.getMethodAndInvokeDirectly("actionPerformed", tradePanel, 2, new Object(), ReflectionUtilis.getMethodAndInvokeDirectly("getMarketInfo", tradePanel, 0));
        if (!shown) ReflectionUtilis.getMethodAndInvokeDirectly("actionPerformed", tradePanel, 2, new Object(), ReflectionUtilis.getMethodAndInvokeDirectly("getFactionTitle", tradePanel, 0));
    }
}
