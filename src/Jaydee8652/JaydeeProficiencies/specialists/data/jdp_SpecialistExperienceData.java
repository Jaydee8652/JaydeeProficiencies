package Jaydee8652.JaydeeProficiencies.specialists.data;

import java.util.Objects;
public class jdp_SpecialistExperienceData {
    private final String source;
    private final Float xp;
    private final long timeStamp;

    public jdp_SpecialistExperienceData(String source, Float xp, long timeStamp) {
        this.source = source;
        this.xp = xp;
        this.timeStamp = timeStamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, xp, timeStamp);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof jdp_SpecialistExperienceData)) {
            return false;
        } else {
            jdp_SpecialistExperienceData other = (jdp_SpecialistExperienceData) obj;
            return Objects.equals(source, other.source)
                    && Objects.equals(xp, other.xp)
                    && Objects.equals(timeStamp, other.timeStamp);
        }
    }

    public String source() {
        return source;
    }

    public Float xp() {
        return xp;
    }

    public long timeStamp() {
        return timeStamp;
    }
}