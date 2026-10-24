// Code taken and modified from officer extension mod
package Jaydee8652.JaydeeProficiencies.utils.refl.ui;

import Jaydee8652.JaydeeProficiencies.utils.refl.ReflectionUtilis;
import com.fs.starfarer.api.ui.PositionAPI;

public class Position {
    private final PositionAPI inner;

    public Position(PositionAPI o) {
        inner = o;
    }

    public void set(PositionAPI copy) {
        ReflectionUtilis.invokeMethodDirectly(ClassRefs.positionSetMethod, inner, copy);
    }

    public PositionAPI getInstance() {
        return inner;
    }
}