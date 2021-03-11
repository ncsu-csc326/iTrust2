package edu.ncsu.csc.iTrust2.adapters;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Serializes and deseralizes ZonedDateTime objects into ISO datetime strings.
 *
 * @author Matt Dzwonczyk
 */
public class ZonedDateTimeAdapter implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {

    /**
     * Deserialization function for Gson to create a ZonedDateTime object from
     * an ISO datetime String.
     * 
     * @param jsonElement
     *            The JsonElement to deserialize.
     * @param type
     *            The data Type descriptor.
     * @param jsonDeserializationContext
     *            Invokes default deserialization.
     * @return The JsonElement as a ZonedDateTime instance.
     */
    @Override
    public ZonedDateTime deserialize ( final JsonElement jsonElement, final Type type,
            final JsonDeserializationContext jsonDeserializationContext ) {
        return ZonedDateTime.parse( jsonElement.getAsString() );
    }

    /**
     * Serialization function for Gson to convert a ZonedDateTime object to an
     * ISO datetime String.
     * 
     * @param dateTime
     *            The ZonedDateTime to serialize.
     * @param type
     *            The data Type descriptor.
     * @param jsonSerializationContext
     *            Invokes default serialization.
     * @return The ZonedDateTime as a JsonPrimitive instance.
     */
    @Override
    public JsonElement serialize ( final ZonedDateTime dateTime, final Type type,
            final JsonSerializationContext jsonSerializationContext ) {
        return new JsonPrimitive( dateTime.toOffsetDateTime().toString() );
    }

}
