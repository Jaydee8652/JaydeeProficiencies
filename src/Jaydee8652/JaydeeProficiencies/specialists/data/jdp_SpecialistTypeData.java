package Jaydee8652.JaydeeProficiencies.specialists.data;

import com.fs.starfarer.api.graphics.SpriteAPI;

import java.util.Objects;
public class jdp_SpecialistTypeData {
    private final String an;
    private final String name;
    private final String plural;

    private final SpriteAPI sprite;

    public jdp_SpecialistTypeData(String an, String name, String plural, SpriteAPI sprite) {
        this.an = an;
        this.name = name;
        this.plural = plural;
        this.sprite = sprite;
    }

    public String an() {
        return an;
    }
    public String name() {
        return name;
    }
    public String plural() {
        return plural;
    }
    public SpriteAPI sprite() {
        return sprite;
    }

}