package src;
import java.sql.*;


//klasse som håndterer innlogging av bruker (for usecase1)
public class UserCtrl extends DBC {

	
	//bruker static variabler for å kunne accesse verdien fra andre klasser
	public static String loggedInUser =null;  
	public static String loggedInPermission = null;


	
	//en metode som bruker de andre hjelpemetodene lenger ned i klassen til å sjekke om email+passord er i databasen (og dermed om bruker får logge inn)
	//returnerer true hvis bruker får logga inn, og false hvis ikke
	 public boolean checkLogin(String email, String userPassword) {
		
		 //kjører begge logins så vi finner ut om du er student eller instructor
		 if (this.checkInstructorLogin(email, userPassword)) {
			 System.out.println("You enter as an instructor");
			 return true;
		 }
		 else if (this.checkStudentLogin(email, userPassword)) {
			 System.out.println("You enter as a student");
			 return true;
		 }
		 
		 System.out.println("You are not in the database");
		 return false; 
	 }
	 
	    	
	//sjekker om inputen email+passord matcher med email+passord i Student-tabellen
	//returnerer true hvis email+passord er i Student-tabellen, false hvis ikke
	 public boolean checkStudentLogin(String email, String userPassword) { 
    	
		 try {
    		 String query = "select email, userPassword from Student where email = ? AND userPassword = ? ";
    		 PreparedStatement ps = conn.prepareStatement(query);
    		 ps.setString(1, email);
    		 ps.setString(2, userPassword);
    		 ResultSet rs = ps.executeQuery();
    		 
    		 //hvis den finner matchende email+passord i Student-tabellen, så oppdateres de statistke variabelene så vi kan se hvem innlogget bruker er
    		 if (rs.next()) {
    			 UserCtrl.loggedInUser = email;
    			 UserCtrl.loggedInPermission = "student"; 
    			 return true;
    		 }
    		
    	 } catch (Exception e)
    	 {
    		 System.out.println("Something wrong in checkStudentLogin method " + e);
    	 }
    	 return false;
     }


	 //sjekker om inputen email+passord matcher med email+passord i Instructor-tabellen
	 //returnerer true hvis email+passord er i Instructor-tabellen, false hvis ikke
	 public boolean checkInstructorLogin(String email, String userPassword) { 

     	String query = "select email, userPassword from Instructor where email = ? AND userPassword = ? ";
		
     	try {
			 PreparedStatement ps = conn.prepareStatement(query);
			 ps.setString(1, email);
			 ps.setString(2, userPassword);
			 ResultSet rs = ps.executeQuery();
    		 //hvis den finner matchende email+passord i Student-tabellen, så oppdateres de statistke variabelene så vi kan se hvem innlogget bruker er
			 if (rs.next()) {
				 UserCtrl.loggedInUser = email;
				 UserCtrl.loggedInPermission = "instructor";
				 return true;
			 }
		 } catch (Exception e)
		 {
			 System.out.println("Something wrong in checkInstructorLogin " + e);
		 }
     	
		 return false;
		}

}


