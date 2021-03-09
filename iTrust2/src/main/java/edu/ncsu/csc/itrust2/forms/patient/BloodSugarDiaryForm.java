/**
 * 
 */
package edu.ncsu.csc.itrust2.forms.patient;

import javax.validation.constraints.Min;

/**
 * Form that the front end will use to create readable JSON objects
 * @author Thomas Landsberg
 *
 */
public class BloodSugarDiaryForm {

    /**
     * Empty constructor to make a BloodSugarDiaryForm for the user to fill out
     */
	public BloodSugarDiaryForm() {
	}
	
    /**
     * The date as milliseconds since epoch for the entry
     */
    private String date;
    
    /**
     * Blood sugar level when first waking up
     */
    @Min(0)
    private Integer fastingLevel;
    
    /**
     * Blood sugar level after first meal
     */
    @Min(0)
    private Integer firstLevel;
    
    /**
     * Blood sugar level after second meal
     */
    @Min(0)
    private Integer secondLevel;
    
    /**
     * Blood sugar level after third meal
     */
    @Min(0)
    private Integer thirdLevel;

	/**
	 * Gets the date of the diary entry
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * Sets the date of the diary entry
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * Gets the fasting blood sugar level
	 * @return the fastingLevel
	 */
	public Integer getFastingLevel() {
		return fastingLevel;
	}

	/**
	 * Sets the fasting blood sugar level
	 * @param fastingLevel the fastingLevel to set
	 */
	public void setFastingLevel(Integer fastingLevel) {
		this.fastingLevel = fastingLevel;
	}

	/**
	 * Gets the first meal blood sugar level
	 * @return the firstLevel
	 */
	public Integer getFirstLevel() {
		return firstLevel;
	}

	/**
	 * Sets the first meal blood sugar level
	 * @param firstLevel the firstLevel to set
	 */
	public void setFirstLevel(Integer firstLevel) {
		this.firstLevel = firstLevel;
	}

	/**
	 * Gets the second meal blood sugar level
	 * @return the secondLevel
	 */
	public Integer getSecondLevel() {
		return secondLevel;
	}

	/**
	 * Sets the second meal blood sugar level
	 * @param secondLevel the secondLevel to set
	 */
	public void setSecondLevel(Integer secondLevel) {
		this.secondLevel = secondLevel;
	}

	/**
	 * Gets the third meal blood sugar level
	 * @return the thirdLevel
	 */
	public Integer getThirdLevel() {
		return thirdLevel;
	}

	/**
	 * Sets the third meal blood sugar level
	 * @param thirdLevel the thirdLevel to set
	 */
	public void setThirdLevel(Integer thirdLevel) {
		this.thirdLevel = thirdLevel;
	}
	
}
