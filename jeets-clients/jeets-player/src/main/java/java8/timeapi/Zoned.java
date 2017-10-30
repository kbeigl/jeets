package java8.timeapi;

	import java.time.ZonedDateTime;
	import java.time.ZoneId;

	/**
	 * Zoned date-time API is to be used when time zone is to be considered.
	 */
	public class Zoned {

	   public static void main(String args[]){
		   Zoned java8tester = new Zoned();
		   java8tester.testZonedDateTime();
	   }
		
	   public void testZonedDateTime(){
		
	      // Get the current date and time
	      ZonedDateTime date1 = ZonedDateTime.parse("2007-12-03T10:15:30+05:30[Asia/Karachi]");
	      System.out.println("date1: " + date1);
			
	      ZoneId id = ZoneId.of("Europe/Berlin");
	      System.out.println("ZoneId: " + id);
			
	      ZoneId currentZone = ZoneId.systemDefault();
	      System.out.println("CurrentZone: " + currentZone);
	   }
	}
