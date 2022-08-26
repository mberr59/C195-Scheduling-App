package Helper;

import java.sql.Timestamp;

/**
 * Convert Date Time Interface. Interface used to create Lambda expressions.
 */
public interface ConvertDateTime {
    Timestamp convertDateTime(Timestamp dateTime);
}
