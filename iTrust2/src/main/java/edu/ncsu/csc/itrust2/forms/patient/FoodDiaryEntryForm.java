package edu.ncsu.csc.itrust2.forms.patient;

import org.hibernate.validator.constraints.NotEmpty;

import edu.ncsu.csc.itrust2.models.enums.MealType;

public class FoodDiaryEntryForm {

    /**
     * Empty constructor to make a DiaryEntryForm for the user to fill out
     */
    public FoodDiaryEntryForm() {
    }

    /**
     * The date as milliseconds since epoch for the entry
     */
    private String date;
    
    /**
     * The type of meal
     */
    private MealType mealType;
    
    /**
     * The food eaten
     */
    @NotEmpty
    private String food;
    
    /**
     * The amount of servings
     */
    @NotEmpty
    private Integer servings;
    
    /**
     * The amount of calories
     */
    @NotEmpty
    private Integer calories;
    
    /**
     * The amount of fat
     */
    @NotEmpty
    private Integer fat;
    
    /**
     * The amount of sodium
     */
    @NotEmpty
    private Integer sodium;
    
    /**
     * The amount of carbs
     */
    @NotEmpty
    private Integer carbs;
    
    /**
     * The amount of sugars
     */
    @NotEmpty
    private Integer sugars;
    
    /**
     * The amount of fiber
     */
    @NotEmpty
    private Integer fiber;
    
    /**
     * The amount of protein
     */
    @NotEmpty
    private Integer protein;

    /**
     * Gets the date
     * @return the diary date
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date
     * @param date the diary date to set
     */
    public void setDate(final String date) {
        this.date = date;
    }

    /**
     * Gets the meal type
     * @return the mealType
     */
    public MealType getMealType() {
        return mealType;
    }

    /**
     * Sets the mealType
     * @param mealType the mealType to set
     */
    public void setMealType(final MealType mealType) {
        this.mealType = mealType;
    }

    /**
     * Gets the food eaten
     * @return the food
     */
    public String getFood() {
        return food;
    }

    /**
     * Sets the food eaten
     * @param food the food to set
     */
    public void setFood(final String food) {
        this.food = food;
    }

    /**
     * Gets the servings
     * @return the servings
     */
    public Integer getServings() {
        return servings;
    }

    /**
     * Sets the servings
     * @param servings the servings to set
     */
    public void setServings(final Integer servings) {
        this.servings = servings;
    }

    /**
     * Gets the calories
     * @return the calories
     */
    public Integer getCalories() {
        return calories;
    }

    /**
     * Sets the calories
     * @param calories the calories to set
     */
    public void setCalories(final Integer calories) {
        this.calories = calories;
    }

    /**
     * Gets the fat
     * @return the fat
     */
    public Integer getFat() {
        return fat;
    }

    /**
     * Sets the fat
     * @param fat the fat to set
     */
    public void setFat(final Integer fat) {
        this.fat = fat;
    }

    /**
     * Gets the sodium
     * @return the sodium
     */
    public Integer getSodium() {
        return sodium;
    }

    /**
     * Sets the sodium
     * @param sodium the sodium to set
     */
    public void setSodium(final Integer sodium) {
        this.sodium = sodium;
    }

    /**
     * Gets the carbs
     * @return the carbs
     */
    public Integer getCarbs() {
        return carbs;
    }

    /**
     * Sets the carbs
     * @param carbs the carbs to set
     */
    public void setCarbs(final Integer carbs) {
        this.carbs = carbs;
    }

    /**
     * Gets the sugars
     * @return the sugars
     */
    public Integer getSugars() {
        return sugars;
    }

    /**
     * Sets the sugars
     * @param sugars the sugars to set
     */
    public void setSugars(final Integer sugars) {
        this.sugars = sugars;
    }

    /**
     * Gets the fiber
     * @return the fiber
     */
    public Integer getFiber() {
        return fiber;
    }

    /**
     * Sets the fiber
     * @param fiber the fiber to set
     */
    public void setFiber(final Integer fiber) {
        this.fiber = fiber;
    }

    /**
     * Gets the protein
     * @return the protein
     */
    public Integer getProtein() {
        return protein;
    }

    /**
     * Sets the protein
     * @param protein the protein to set
     */
    public void setProtein(final Integer protein) {
        this.protein = protein;
    }
}
