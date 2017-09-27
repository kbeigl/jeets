package java8.timeapi;

	import java.time.LocalDateTime;
	import java.time.ZonedDateTime;

	import java.util.Date;

	import java.time.Instant;
	import java.time.ZoneId;

	/**
	 * A toInstant() method is added to the original Date and Calendar objects, 
	 * which can be used to convert them to the new Date-Time API. 
	 * Use an ofInstant(Insant,ZoneId) method to get a LocalDateTime or ZonedDateTime object.
	 */
	public class BackwardCompatibility {

	   public static void main(String args[]){
		   BackwardCompatibility java8tester = new BackwardCompatibility();
		   java8tester.testBackwardCompatability();
	   }
		
	   public void testBackwardCompatability(){
		
	      //Get the current date
	      Date currentDate = new Date();
	      System.out.println("Current date: " + currentDate);
			
	      //Get the instant of current date in terms of milliseconds
	      Instant now = currentDate.toInstant();
	      ZoneId currentZone = ZoneId.systemDefault();
			
	      LocalDateTime localDateTime = LocalDateTime.ofInstant(now, currentZone);
	      System.out.println("Local date: " + localDateTime);
			
	      ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(now, currentZone);
	      System.out.println("Zoned date: " + zonedDateTime);
	   }
	}
