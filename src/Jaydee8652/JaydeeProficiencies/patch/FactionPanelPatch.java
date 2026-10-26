package Jaydee8652.JaydeeProficiencies.patch;

import Jaydee8652.JaydeeProficiencies.ui.jdp_FactionDialogInjector;
import Jaydee8652.JaydeeProficiencies.utils.refl.ReflectionUtilis;
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.ClassRefs;
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.UIPanel;
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.UIReflectionUtils;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CutStyle;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.input.Keyboard;
import patchlib.api.context.AfterContext;
import patchlib.api.context.MethodCallContext;
import patchlib.api.match.ClassMatch;
import patchlib.api.match.MethodMatch;
import patchlib.api.patch.After;
import patchlib.api.patch.Patch;
import patchlib.api.patch.RedirectMethodCall;

import java.util.ArrayList;
import java.util.List;

@Patch(target = @ClassMatch(packageName = "com.fs.starfarer.campaign.ui.marketinfo", methodMatches = { @MethodMatch(methodName = "charTypeFailed") }))
public class FactionPanelPatch {
    public interface FactionPanelOpenedListener {
        void reportFactionPanelOpened(Object dialog, UIPanelAPI panel, ButtonAPI flag, ButtonAPI confirm, ButtonAPI color);
    }

    @RedirectMethodCall(
            target = @MethodMatch(methodName = "advanceImpl", parameterCount = 1),
            call = @MethodMatch(methodName = "show", parameterCount = 2)
    )
    // Eats showing the "now you have static assets" blurb if it's a lie...
    public static void removeFirstTime(MethodCallContext context) {
        if (Global.getSector().getEconomy().getMarketsCopy().stream().anyMatch(MarketAPI::isPlayerOwned)) {
            var original = context.call();
            context.setResult(original);
        }
    }

    // Modify the created UI
    @After(target = @MethodMatch(methodName = "createUI", parameterCount = 0))
    public static void modifyFactionPanel(AfterContext context) {
        var self = context.getSelf();

        if (self.getClass().equals(ClassRefs.FactionDialogClass)) {
            ButtonAPI flagButton = null;
            ButtonAPI confirmButton = null;
            ButtonAPI existingColorButton = null;

            var panel = ReflectionUtilis.invokeMethodDirectly(ClassRefs.FactionDialogGetInnerPanelMethod, self);
            var children = (List<Object>) ReflectionUtilis.invokeMethodDirectly(ClassRefs.uiPanelgetChildrenNonCopyMethod, panel);

            for (Object child : children) {
                if (child instanceof ButtonAPI button) {
                    if (button.getPosition().getHeight() > 100f) flagButton = button;
                    if (button.getText() != null && button.getText().contains("Confirm")) confirmButton = button;
                    if (button.getText() != null && button.getText().contains("Faction Color")) {
                        existingColorButton = button;
                        break;
                    }
                }
            }
            if (confirmButton == null || flagButton == null || existingColorButton != null) return;

            var trigger = new jdp_FactionDialogInjector.jdp_FactionColorTabTrigger();
            trigger.initialise(self);
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
            new UIPanel(panel).add(button).getInstance().belowLeft(flagButton, confirmButton.getPosition().getHeight());

            for (FactionPanelOpenedListener listener : new ArrayList<FactionPanelOpenedListener>(Global.getSector().getListenerManager().getListeners(FactionPanelOpenedListener.class))) {
                listener.reportFactionPanelOpened(self, (UIPanelAPI) panel, flagButton, confirmButton, button.getInstance());
            }
        }
    }
}