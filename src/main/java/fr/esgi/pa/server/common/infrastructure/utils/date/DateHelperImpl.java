package fr.esgi.pa.server.common.infrastructure.utils.date;

import fr.esgi.pa.server.common.core.utils.date.DateHelper;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

@Component
public class DateHelperImpl implements DateHelper {
    @Override
    public Date dateNow() {
        return new Date();
    }

    @Override
    public Timestamp timestampNow() {
        return new Timestamp(System.currentTimeMillis());
    }
}
