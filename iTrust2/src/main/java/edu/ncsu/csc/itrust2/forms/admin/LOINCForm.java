package edu.ncsu.csc.itrust2.forms.admin;

import java.util.List;

import edu.ncsu.csc.itrust2.models.persistent.LOINC;

/**
 * Intermediate form for adding or editing LOINC codes. Used to create and
 * serialize LOINC codes.
 *
 * @author Thomas Dickerson
 * @author Sam Fields
 *
 */
public class LOINCForm {

    /** The code of the Lab Procedure */
    private String            code;
    /** The auto-generated id of the LOINC code */
    private Long              id;
    /** The commonly-used name for the lab procedure */
    private String            commonName;
    /** The substance or entity being measured or observed */
    private String            component;
    /** The characteristic or attribute of the analyte */
    private String            property;
    /** Scale used to measure the results */
    private String            scale;
    /** Possible ranges or values for the result of the procedure */
    private List<ResultEntry> resultEntries;

    /**
     * Empty constructor for GSON
     */
    public LOINCForm () {

    }

    /**
     * Construct a form off an existing LOINC object
     *
     * @param code
     *            The object to fill this form with
     */
    public LOINCForm ( final LOINC code ) {
        setCode( code.getCode() );
        setCommonName( code.getCommonName() );
        setComponent( code.getComponent() );
        setProperty( code.getProperty() );
        setId( code.getId() );
        setScale( code.getScale().getName() );
    }

    /**
     * Sets the String representation of the code
     *
     * @param code
     *            The new code
     */
    public void setCode ( final String code ) {
        this.code = code;
    }

    /**
     * Returns the String representation of the code
     *
     * @return The code
     */
    public String getCode () {
        return code;
    }

    /**
     * Sets the common name of this code
     *
     * @param n
     *            The new common name
     */
    public void setCommonName ( final String n ) {
        commonName = n;
    }

    /**
     * Returns the common name of the code
     *
     * @return The common name
     */
    public String getCommonName () {
        return commonName;
    }

    /**
     * Sets the component of this code
     *
     * @param c
     *            The new component
     */
    public void setComponent ( final String c ) {
        component = c;
    }

    /**
     * Returns the component of the code
     *
     * @return The component
     */
    public String getComponent () {
        return component;
    }

    /**
     * Sets the property of this code
     *
     * @param p
     *            The new property
     */
    public void setProperty ( final String p ) {
        property = p;
    }

    /**
     * Returns the property of the code
     *
     * @return The property
     */
    public String getProperty () {
        return property;
    }

    /**
     * Returns the scale of the code
     *
     * @return the scale
     */
    public String getScale () {
        return scale;
    }

    /**
     * Sets the scale of the code
     *
     * @param scale
     *            the scale to set
     */
    public void setScale ( String scale ) {
        this.scale = scale;
    }

    /**
     * Gets the result entries for the code
     *
     * @return the resultEntries
     */
    public List<ResultEntry> getResultEntries () {
        return resultEntries;
    }

    /**
     * Sets the result entries for the code
     *
     * @param resultEntries
     *            the resultEntries to set
     */
    public void setResultEntries ( List<ResultEntry> resultEntries ) {
        this.resultEntries = resultEntries;
    }

    /**
     * Sets the ID of the Code
     *
     * @param l
     *            The new ID of the Code. For Hibernate.
     */
    public void setId ( final Long l ) {
        id = l;
    }

    /**
     * Returns the ID of the Code
     *
     * @return The Lab Procedure ID
     */
    public Long getId () {
        return id;
    }

    @Override
    public boolean equals ( final Object o ) {
        if ( o instanceof LOINCForm ) {
            final LOINCForm f = (LOINCForm) o;
            return code.equals( f.getCode() ) && id.equals( f.getId() ) && commonName.equals( f.getCommonName() )
                    && component.equals( f.getComponent() ) && property.equals( f.getProperty() )
                    && scale.equalsIgnoreCase( f.getScale() );
        }
        return false;
    }

    /**
     * ResultEntry represents a single possible result type for a lab procedure.
     * Result entries can be either qualitative or quantitative. Quantitative
     * results specify a range using a min value and a max value. Qualititative
     * results specify the name of the result. Both types specify an ICD-10 code
     * associated with that result.
     *
     * @author Sam Fields
     *
     */
    public class ResultEntry {
        /** Name for result entry (Qualitative only) */
        private String name;
        /** Min value for range (Quantitative only) */
        private String min;
        /** Max value for range (Quantitative only) */
        private String max;
        /** ICD-10 code related to entry (Quantitative and Qualitative) */
        private String icd;

        /**
         * Empty contstructor for Thymeleaf
         */
        public ResultEntry () {

        }

        /**
         * Creates a new Qualitative entry with a name and icd
         *
         * @param name
         *            the name of the value
         * @param icd
         *            the suggested diagnosis for the result
         */
        public ResultEntry ( String name, String icd ) {
            setName( name );
            setIcd( icd );
        }

        /**
         * Creates a new Quantitative entry with a min, max, and icd
         *
         * @param min
         *            the minimum value of the range
         * @param max
         *            the maximum value of the range
         * @param icd
         *            the suggested diagnosis for the range
         */
        public ResultEntry ( String min, String max, String icd ) {
            setMin( min );
            setMax( max );
            setIcd( icd );
        }

        /**
         * Gets the name of the result
         *
         * @return the name
         */
        public String getName () {
            return name;
        }

        /**
         * Sets the name of the result
         *
         * @param name
         *            the name to set
         */
        public void setName ( String name ) {
            this.name = name;
        }

        /**
         * Gets the minimum value of the range
         *
         * @return the min
         */
        public String getMin () {
            return min;
        }

        /**
         * Sets the minimum value of the range
         *
         * @param min
         *            the min to set
         */
        public void setMin ( String min ) {
            this.min = min;
        }

        /**
         * Gets the maximum value of the range
         *
         * @return the max
         */
        public String getMax () {
            return max;
        }

        /**
         * Sets the maximum value of the range
         *
         * @param max
         *            the max to set
         */
        public void setMax ( String max ) {
            this.max = max;
        }

        /**
         * Gets the suggested diagnosis for the result
         *
         * @return the icd
         */
        public String getIcd () {
            return icd;
        }

        /**
         * Sets the suggested diagnosis for the result
         * 
         * @param icd
         *            the icd to set
         */
        public void setIcd ( String icd ) {
            this.icd = icd;
        }
    }

}
