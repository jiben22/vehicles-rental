package fr.enssat.vehiclesrental.model.converter;

import fr.enssat.vehiclesrental.model.enums.Position;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

/**
 * Convert enumeration named 'position' to switch between database and java object and vice-versa
 * see(https://www.baeldung.com/jpa-persisting-enums-in-jpa#converter)
 *
 * @see fr.enssat.vehiclesrental.model.Employee
 */
@Converter(autoApply = true)
public class PositionConverter implements AttributeConverter<Position,String> {

    /**
     * Converts the value stored in the entity attribute into the
     * data representation to be stored in the database.
     *
     * @param position the entity attribute value to be converted
     * @return the converted data to be stored in the database
     * column
     */
    @Override
    public String convertToDatabaseColumn(Position position) {
        if(position == null){
            return null;
        }
        return position.label;
    }

    /**
     * Converts the data stored in the database column into the
     * value to be stored in the entity attribute.
     * Note that it is the responsibility of the converter writer to
     * specify the correct <code>dbData</code> type for the corresponding
     * column for use by the JDBC driver: i.e., persistence providers are
     * not expected to do such type conversion.
     *
     * @param label the data from the database column to be
     *               converted
     * @return the converted value to be stored in the entity
     * attribute
     */
    @Override
    public Position convertToEntityAttribute(String label) {
        if (label == null) {
            return null;
        }

        return Stream.of(Position.values())
                .filter(c -> c.label.equals(label))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
