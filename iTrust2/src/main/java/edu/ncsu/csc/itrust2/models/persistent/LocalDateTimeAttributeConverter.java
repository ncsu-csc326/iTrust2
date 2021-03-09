package edu.ncsu.csc.itrust2.models.persistent;

import java.time.LocalDateTime;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Converter to persist LocalDateTime in Hibernate
 *
 * @author nrshah4
 *
 */
@Converter ( autoApply = true )
public class LocalDateTimeAttributeConverter implements AttributeConverter<LocalDateTime, String> {

    /**
     * Convert localdatetime to string
     *
     * @param attribute
     *            the localdatetime to convert
     * @return the string
     *
     */
    @Override
    public String convertToDatabaseColumn ( final LocalDateTime attribute ) {
        if ( attribute == null ) {
            return null;
        }
        final String t = attribute.toString();
        return t;
    }

    /**
     * Convert from string to an entity
     *
     * @param dbData
     *            data from the database
     * @return the LocalDateTime
     */
    @Override
    public LocalDateTime convertToEntityAttribute ( final String dbData ) {
        if ( dbData == null ) {
            return null;
        }
        final LocalDateTime ldt = LocalDateTime.parse( dbData );
        return ldt;
    }

}
