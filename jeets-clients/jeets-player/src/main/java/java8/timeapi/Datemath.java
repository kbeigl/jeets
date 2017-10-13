package java8.timeapi;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;

/**
 * TemporalAdjuster is used to perform the date mathematics. For example, get
 * the "Second Saturday of the Month" or "Next Tuesday".
 */
public class Datemath {

    public static void main(String args[]) {
        Datemath java8tester = new Datemath();
        java8tester.testAdjusters();
    }

    public void testAdjusters() {

        // Get the current date
        LocalDate date1 = LocalDate.now();
        System.out.println("Current date: " + date1);

        // get the next tuesday
        LocalDate nextTuesday = date1.with(TemporalAdjusters.next(DayOfWeek.TUESDAY));
        System.out.println("Next Tuesday on : " + nextTuesday);

        // get the second saturday of next month
        LocalDate firstInYear = LocalDate.of(date1.getYear(), date1.getMonth(), 1);
        LocalDate secondSaturday = firstInYear.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
                .with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        System.out.println("Second Saturday on : " + secondSaturday);
    }
}
