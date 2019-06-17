package edu.ncsu.csc.itrust2.models.enums;

/**
 * For keeping track of various types of eye surgeries that are known to the
 * system.
 *
 * @author Jack MacDonald
 * @author Kai Presler-Marshall
 *
 */
public enum EyeSurgeryType {

    /**
     * Patient
     */
    CATARACT ( 1, "Cataract surgery" ),
    /**
     * HCP (general)
     */
    LASER ( 2, "Laser eye surgery" ),
    /**
     * Admin
     */
    REFRACTIVE ( 3, "Refractive surgery" );

    /**
     * Numeric code of the Role
     */
    private int    code;

    /**
     * Description of the surgery
     */
    private String surgeryDescription;

    /**
     * Create an EyeSurgeryType.
     *
     * @param code
     *            Code of the Surgery.
     * @param surgeryDescription
     *            English description of the surgery
     */
    private EyeSurgeryType ( final int code, final String surgeryDescription ) {
        this.code = code;
        this.surgeryDescription = surgeryDescription;
    }

    /**
     * Gets the numeric code of the Surgery
     *
     * @return Code of this Surgery
     */
    public int getCode () {
        return this.code;
    }

    /**
     * Gets the description of this surgery
     *
     * @return Surgery description
     */
    public String getSurgeryDescription () {
        return this.surgeryDescription;
    }

}
