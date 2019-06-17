package edu.ncsu.csc.itrust2.adapters;

import java.lang.reflect.Type;
import java.time.LocalDate;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Serializes and deseralizes LocalDate objects into ISO date strings.
 * 
 * @author Matt Dzwonczyk
 */
public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    /**
     * Deserialization function for Gson to create a 
     * LocalDate object from an ISO date String.
     * @param jsonElement The JsonElement to deserialize.
     * @param type The data Type descriptor.
     * @param jsonDeserializationContext Invokes default deserialization.
     * @return The JsonElement as a LocalDate instance.
     */
    @Override
    public LocalDate deserialize(JsonElement jsonElement, Type type,
            JsonDeserializationContext jsonDeserializationContext) {
        return LocalDate.parse( jsonElement.getAsString() );
    }

    /**
     * Serialization function for Gson to convert a 
     * LocalDate object to an ISO date String.
     * @param date The ZonedDateTime to serialize.
     * @param type The data Type descriptor.
     * @param jsonSerializationContext Invokes default serialization.
     * @return The LocalDate as a JsonPrimitive instance.
     */
    @Override
    public JsonElement serialize(LocalDate date, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive( date.toString() );
    }

}