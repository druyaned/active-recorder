package com.github.druyaned.active_recorder.time;

import static com.github.druyaned.active_recorder.time.Date.*;

public class YearShower {
    
    public static void show(int year) {
        System.out.println(year);

        for (int m = MIN_MONTH; m <= MAX_MONTH; ++m) {
            showMonth(year, m);
        }
    }

    private static void showMonth(int year, int month) {
        System.out.printf("%s (%d)\n",
            Month.getName(month), month);
        for (int i = 1; i <= DAYS_IN_WEEK; ++i) {
            System.out.printf("%5s", WeekDay.getShortName(i));
        }
        System.out.println();

        int fstDay = WeekDay.getWeekDayInPeriodYear(year);
        int dayAmount;
        if (isLeap(year)) {
            fstDay += WeekDay.getWeekDayOffsetLeap(month, 1);
            dayAmount = Month.getDayAmountLeap(month);
        } else {
            fstDay += WeekDay.getWeekDayOffsetLeap(month, 1);
            dayAmount = Month.getDayAmountNotLeap(month);
        }
        fstDay %= DAYS_IN_WEEK;

        for (int i = 0; i < fstDay; ++i)
            System.out.printf("%5s", "");
        int d = 1;
        for (int i = fstDay; i < DAYS_IN_WEEK; ++i, ++d)
            System.out.printf("%5d", d);
        System.out.println();
        for (int w = 2; w <= 4; ++w) {
            for (int i = 0; i < DAYS_IN_WEEK; ++i, ++d)
                System.out.printf("%5d", d);
            System.out.println();
        }
        for (int i = 0; i < DAYS_IN_WEEK && d <= dayAmount; ++i, ++d)
            System.out.printf("%5d", d);
        if (d <= dayAmount) {
            System.out.println();
            for (; d <= dayAmount; ++d)
                System.out.printf("%5d", d);
        }
        System.out.println("\n");
    }
}
