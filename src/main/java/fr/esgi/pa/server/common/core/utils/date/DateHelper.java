package fr.esgi.pa.server.common.core.utils.date;

import java.sql.Timestamp;
import java.util.Date;

public interface DateHelper {
    Date dateNow();

    Timestamp timestampNow();
}
