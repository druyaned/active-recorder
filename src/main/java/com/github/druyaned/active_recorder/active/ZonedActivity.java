package com.github.druyaned.active_recorder.active;

import static com.github.druyaned.active_recorder.active.ActiveTime.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * TODO: write java-docs.
 * @author druyaned
 */
public class ZonedActivity extends Activity {
    
    /**
     * TODO: write java-docs
     * @param a
     * @param zoneId
     * @return 
     */
    public static ZonedActivity[] of(Activity a, ZoneId zoneId) {
        final ZonedDateTime start = ZonedDateTime.ofInstant(a.getStart(), zoneId);
        final ZonedDateTime stop = ZonedDateTime.ofInstant(a.getStop(), zoneId);
        final ActiveMode mode = a.getMode();
        final String descr = a.getDescr();
        
        // meetsRequirements
        boolean minStop = stop.getHour() == MIN_HOUR &&
                              stop.getMinute() == MIN_MINUTE &&
                              stop.getSecond() == MIN_SECOND;
        LocalDate startDate = start.toLocalDate();
        LocalDate stopDate = stop.toLocalDate();
        boolean oneDayDiff = stopDate.getYear() == startDate.getYear() &&
                             stopDate.getMonthValue() == startDate.getMonthValue() &&
                             (stopDate.getDayOfMonth() - startDate.getDayOfMonth() == 1);
        boolean equlDates = startDate.equals(stopDate);
        boolean meetsRequirements = (minStop && oneDayDiff) || equlDates;
        
        // splittedActivities
        if (!meetsRequirements) {
            final LocalDate from = start.toLocalDate().plusDays(1);
            final LocalDate to = stop.toLocalDate();
            final int N = (int)( to.toEpochDay() - from.toEpochDay() ) + 2;
            ZonedActivity[] splitted = minStop ? new ZonedActivity[N - 1] : new ZonedActivity[N];
            ZonedDateTime begin, end = start;
            LocalDate endDate = from;
            for (int i = 0; i < N - 1; ++i) {
                begin = end;
                end = ZonedDateTime.of(endDate.getYear(),
                                       endDate.getMonthValue(), endDate.getDayOfMonth(),
                                       MIN_HOUR, MIN_MINUTE, MIN_SECOND, 0, zoneId);
                splitted[i] = new ZonedActivity(begin, end, mode, descr);
                endDate = endDate.plusDays(1);
            }
            if (!minStop) {
                begin = end;
                end = stop;
                splitted[N - 1] = new ZonedActivity(begin, end, mode, descr);
            }
            return splitted;
        }
        return new ZonedActivity[] { new ZonedActivity(start, stop, mode, descr) };
    }
    
//-Non-static---------------------------------------------------------------------------------------
    
    private final ZonedDateTime zonedStart;
    private final ZonedDateTime zonedStop;
    
    private ZonedActivity(ZonedDateTime start, ZonedDateTime stop, ActiveMode mode, String descr) {
        super(start.toInstant(), stop.toInstant(), mode, descr);
        this.zonedStart = start;
        this.zonedStop = stop;
    }
    
    /**
     * write java-docs
     * @return 
     */
    public ZonedDateTime getZonedStart() { return zonedStart; }
    
    /**
     * write java-docs
     * @return 
     */
    public ZonedDateTime getZonedStop() { return zonedStop; }
}
