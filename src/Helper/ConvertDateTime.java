package Helper;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Convert Date Time Interface. Interface used to create Lambda expressions.
 */
public interface ConvertDateTime {
    LocalDateTime convertDateTime(Timestamp dateTime);
}
