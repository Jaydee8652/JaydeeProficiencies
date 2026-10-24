package Jaydee8652.JaydeeProficiencies.patch;

import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_SpecialistData;
import Jaydee8652.JaydeeProficiencies.utils.refl.ReflectionUtilis;
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.ListenerFactory;
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.UIReflectionUtils;
import com.fs.starfarer.api.characters.PersonAPI;
import patchlib.api.context.BeforeContext;
import patchlib.api.match.ClassMatch;
import patchlib.api.match.MethodMatch;
import patchlib.api.patch.Before;
import patchlib.api.patch.Patch;

import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.getSpecialistData;
import static Jaydee8652.JaydeeProficiencies.utils.refl.ui.UIReflectionUtils.EMPTY_STRING;
import static Jaydee8652.JaydeeProficiencies.utils.refl.ui.UIReflectionUtils.isConfirmed;


@Patch(target = @ClassMatch(packageName = "com.fs.starfarer.campaign.command", methodMatches = { @MethodMatch(methodName = "getAdminData") }))
public class AdminPanelPatch {
    @Before(target = @MethodMatch(methodName = "actionPerformed"))
    public static void actionPerformed(BeforeContext context) {
        var person = (PersonAPI) ReflectionUtilis.getFieldAtIndex(context.getSelf(), 6);

        jdp_SpecialistData data = getSpecialistData();
        if(data.getMentors().stream().anyMatch(s -> s.person() == person)) {
            var specialist = data.getSpecialist(person.getId());

            UIReflectionUtils.ConfirmDialogData master = UIReflectionUtils.showConfirmationDialog(
                    person.getName().getFullName() + " is a Specialist in the " + specialist.getProficiencySpec().getName() + " Proficiency.\n\nThey cannot be dismissed.",
                    EMPTY_STRING,
                    "Never mind",
                    500.0F, 200.0F,
                    new ListenerFactory.DialogDismissedListener() {
                        @Override
                        public void trigger(Object... args) {
                            if (!isConfirmed(args)) return;
                        }
                    });
            if (master == null) return;
            master.panel.removeComponent(master.confirmButton.getInstance());
            context.skipOriginal(null);
        }
    }
}