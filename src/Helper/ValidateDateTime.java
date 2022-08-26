package Helper;

import java.time.Instant;

/**
 * Interface used to create Lambdas that validate Dates and Times from the passed instant.
 */
public interface ValidateDateTime {
    boolean validateDateTime(Instant instant);
}
