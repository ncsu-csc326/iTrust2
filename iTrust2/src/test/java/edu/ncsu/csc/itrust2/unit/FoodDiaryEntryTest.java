package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.patient.FoodDiaryEntryForm;
import edu.ncsu.csc.itrust2.models.enums.MealType;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.models.persistent.FoodDiaryEntry;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Class for testing DiaryEntry and DiaryEntryForm
 * 
 * @author Brendan Boss (blboss)
 *
 */
public class FoodDiaryEntryTest {

    /** Patient to be used for testing */
    final User patient = new User("patient", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
            Role.ROLE_PATIENT, 1);

    @Before
    public void setUp() {
        DomainObject.deleteAll(FoodDiaryEntry.class);
    }

    /**
     * Testing DiaryEntry with a default constructor and with a DiaryEntryForm
     * passed into the constructor. Also tests all Setters and getById.
     */
    @Test
    public void testDiaryEntry() {
        FoodDiaryEntry entry = new FoodDiaryEntry();
        LocalDate entryDate = LocalDate.parse( "2018-09-03" );
        
        entry.setDate(entryDate);
        entry.setMealType(MealType.Lunch);
        entry.setFood("Peanut Butter and Jelly Sandwich");
        entry.setServings(1);
        entry.setCalories(900);
        entry.setFat(30);
        entry.setSodium(60);
        entry.setCarbs(100);
        entry.setSugars(50);
        entry.setFiber(40);
        entry.setProtein(10);
        entry.setPatient("patient");
        entry.save();

        FoodDiaryEntry copy = FoodDiaryEntry.getById(entry.getId());
        assertEquals(entry.getId(), copy.getId());
        assertEquals(entry.getServings(), copy.getServings());
        assertEquals(entry.getCalories(), copy.getCalories());
        assertEquals(entry.getMealType(), copy.getMealType());
        assertEquals(entry.getFood(), copy.getFood());
        assertEquals(entry.getFat(), copy.getFat());
        assertEquals(entry.getSodium(), copy.getSodium());
        assertEquals(entry.getCarbs(), copy.getCarbs());
        assertEquals(entry.getSugars(), copy.getSugars());
        assertEquals(entry.getFiber(), copy.getFiber());
        assertEquals(entry.getProtein(), copy.getProtein());
        assertEquals(entry.getPatient(), copy.getPatient());

        FoodDiaryEntryForm def = new FoodDiaryEntryForm();
        def.setDate(entryDate.toString());
        def.setMealType(MealType.Lunch);
        def.setFood("Peanut Butter and Jelly Sandwich");
        def.setServings(1);
        def.setCalories(900);
        def.setFat(30);
        def.setSodium(60);
        def.setCarbs(100);
        def.setSugars(50);
        def.setFiber(40);
        def.setProtein(10);
        FoodDiaryEntry entry2 = new FoodDiaryEntry(def);
        entry2.setPatient("patient");
        entry2.save();

        assertNotEquals(entry.getId(), entry2.getId());
        assertEquals(entry.getServings(), entry2.getServings());
        assertEquals(entry.getCalories(), entry2.getCalories());
        assertEquals(entry.getMealType(), entry2.getMealType());
        assertEquals(entry.getFood(), entry2.getFood());
        assertEquals(entry.getFat(), entry2.getFat());
        assertEquals(entry.getSodium(), entry2.getSodium());
        assertEquals(entry.getCarbs(), entry2.getCarbs());
        assertEquals(entry.getSugars(), entry2.getSugars());
        assertEquals(entry.getFiber(), entry2.getFiber());
        assertEquals(entry.getProtein(), entry2.getProtein());
        assertEquals(entry.getPatient(), entry2.getPatient());
    }

    /**
     * Tests giving invalid values for setter methods.
     */
    @Test
    public void testDiaryEntryInvalid() {
        FoodDiaryEntry entry = new FoodDiaryEntry();

        try {
            entry.setDate(LocalDate.parse( "3000-09-03" ));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Date must be before current date", e.getMessage());
        }
        try {
            entry.setServings(-1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Servings must be a positive integer!", e.getMessage());
        }
        try {
            entry.setCalories(-900);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Calories must be a non-negative integer!", e.getMessage());
        }
        try {
            entry.setFat(-30);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Fat must be a non-negative integer!", e.getMessage());
        }
        try {
            entry.setSodium(-60);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Sodium must be a non-negative integer!", e.getMessage());
        }
        try {
            entry.setCarbs(-100);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Carbs must be a non-negative integer!", e.getMessage());
        }
        try {
            entry.setSugars(-50);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Sugars must be a non-negative integer!", e.getMessage());
        }
        try {
            entry.setFiber(-40);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Fiber must be a non-negative integer!", e.getMessage());
        }
        try {
            entry.setProtein(-10);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Protein must be a non-negative integer!", e.getMessage());
        }
    }

    /**
     * Test getUniqueDatesByPatient method.
     */
    @Test
    public void testGetByPatient() {
        FoodDiaryEntry entry = new FoodDiaryEntry();
        LocalDate entryDate = LocalDate.parse( "2018-09-03" );
        
        entry.setDate(entryDate);
        entry.setMealType(MealType.Lunch);
        entry.setFood("Peanut Butter and Jelly Sandwich");
        entry.setServings(1);
        entry.setCalories(900);
        entry.setFat(30);
        entry.setSodium(60);
        entry.setCarbs(100);
        entry.setSugars(50);
        entry.setFiber(40);
        entry.setProtein(10);
        entry.setPatient("patient");
        entry.save();

        assertEquals(1, FoodDiaryEntry.getByPatient("patient").size());

        FoodDiaryEntry entry2 = new FoodDiaryEntry();

        entry2.setDate(entryDate);
        entry2.setMealType(MealType.Lunch);
        entry2.setFood("Peanut Butter");
        entry2.setServings(1);
        entry2.setCalories(100);
        entry2.setFat(10);
        entry2.setSodium(20);
        entry2.setCarbs(10);
        entry2.setSugars(10);
        entry2.setFiber(10);
        entry2.setProtein(30);
        entry2.setPatient("patient");
        entry2.save();

        assertEquals(2, FoodDiaryEntry.getByPatient("patient").size());
    }
    
}
