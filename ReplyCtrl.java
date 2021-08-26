package src;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

   
//denne klassen tar for seg å opprette en Reply til en Post og validering av brukerinput når en bruker oppretter en Reply
public class ReplyCtrl extends DBC {
	
	
	private int replyNr;
	private static final int no_reply = -1;
	

	
	//metode som bruker metodene lenger ned i klassen til å opprette en Reply og sette informasjonen inn i databasen
	//returnerer false hvis du ikke får opprette Reply pga ugydlig input, og true hvis inputen er gyldig
	public boolean makeFullReply(int postNr, String replyText, String type) {
		
		if (!this.validateReply(postNr)) { 
			System.out.println("Your input is not valid");
			return false;
		}
			
		this.findNextReplyNr();
		this.regReply1(postNr, type);
		this.regReply2(postNr, replyText);
		this.changeColorCode(postNr, type);
		return true;
	}
	
	
	//metode som setter inn verdier fra Reply som blir opprettet, inn i Reply1 tabellen
    public void regReply1(int postNr, String type) {  	
    	
    	String insertStatement = "insert into Reply1 values (?, ?, ?)";
    	
    	if (replyNr != no_reply) {
            try {
            	PreparedStatement ps = conn.prepareStatement(insertStatement);
                ps.setInt(1, replyNr);
                ps.setInt(2, postNr);
                ps.setString(3, type);
                ps.execute();
            } catch (Exception e)
            {
                System.out.println("db error during insert of Reply1");
            }    
        }
    }
    
	
	//metode som setter inn verdier fra Reply som blir opprettet, inn i Reply2 tabellen
    public void regReply2(int postNr, String replyText) {
  	   
       String replyTime = this.findNowTime();
  	   String insertStatement = "insert into Reply2 value (?, ?, ?, ?) ";
  	   
  	   if (replyNr != no_reply) {
            try {
            	PreparedStatement ps = conn.prepareStatement(insertStatement);
                ps.setInt(1, replyNr);
                ps.setInt(2, postNr);
                ps.setString(3, replyText);
                ps.setString(4, replyTime);
                ps.execute();
            } catch (Exception e) {
                System.out.println("db error during insert of Reply2");
            }
        }
    }
    

    //finner en unik primærnøkkel når en ny reply opprettes (så vi slepper å finne det manuelt)
    public void findNextReplyNr() {
    	
    	//henter ut høyeste replyNr og lager neste et høyere, på denne måten får vi nye primary keys hvis vi oppretter en ny post
		String query = "select replyNr from Reply1 order by replyNr DESC limit 1"; 
    	
		try {
			PreparedStatement ps = conn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			// setter replyNr til nye post en høyere enn høyeste replyNr hvis minst en reply allerede eksisterer, hvis det ikke eksisterer en reply settes replyNr = 1
			if (rs.next())  {
				this.replyNr = rs.getInt("replyNr");
				this.replyNr++;
			}
			else 
				replyNr = 1;
		} catch (Exception e)
		{
			System.out.println("noe er feil i generering av nytt replyNr " + e);
		}
    }
    
    
    //sjekker om user input til å lage en reply er gyldig (altså at posten det replyes til finnes)
    //if rs.next betyr det at den har funnet en Post med det postNr, og inputen inneholder dermed et gyldig postNr, hvis den ikke finner en postNr returners false (altså ikke gyldig)
    //returnerer true hvis input til Reply er valid (post til reply du lager finnes) og false hvis den ikke finnes
    public boolean validateReply(int postNr) {		
		
    	String query = "select * from Post1 where postNr = ? "; 
		
    	try {
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setInt(1, postNr);
			ResultSet rs = ps.executeQuery();
			//hvis det allerede finnes en post med postNr fra input til metoden, så returneres true (validering er ok!)
			if (rs.next()) { 										
				return true;
			}			
		} catch (Exception e) 
		{
			System.out.println("Noe er feil i validateReply " + e);
		}
		return false; 
		
    }
    
    
    //endrer colorCode til Post hvis den mottar en Reply som er av type answer
    public void changeColorCode(int postNr, String type) {
    	
    	if (type.equals("answer") && UserCtrl.loggedInPermission.equals("student")) {
	    	String updateStatement = "UPDATE Post2 set colorCode = 'blå' where postNr = ? ";	
			try {
				PreparedStatement ps = conn.prepareStatement(updateStatement);
				ps.setInt(1, postNr);
				ps.executeUpdate();
			} catch (Exception e) 
			{
				System.out.println("Noe er feil i instructorReadPost " + e);
				}
	    	}
    	else if (type.equals("answer") && UserCtrl.loggedInPermission.equals("instructor")) {
	    	String updateStatement = "UPDATE Post2 set colorCode = 'blå' where postNr = ? ";	
			try {
				PreparedStatement ps = conn.prepareStatement(updateStatement);
				ps.setInt(1, postNr);
				ps.executeUpdate();
			} catch (Exception e) 
			{
				System.out.println("Noe er feil i instructorReadPost " +e);
				}
	    	}	    
    }
    
    
    //metode som finner tiden når reply blir opprettet slik at vi kan sette det inn i Reply, returnerer dato+tid som en streng
    public String findNowTime() {
    	
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
   	    LocalDateTime now = LocalDateTime.now();  
   	    return dtf.format(now);
    }
    
    
 
    
    
    
    //under er metode som ikke trengs for usecases, men som vi har med for å kunne se en Reply i konsoll bare
    
    
    //printer en Reply i konsollen
    public void viewReply(int replyNr) {
    	
    	String query = "select * from Reply1 natural join Reply2 where replyNr = ? ";	//natural join så vi slepper dupliserte rader
		
    	try {
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setInt(1, replyNr);
			ResultSet rs = ps.executeQuery();
			//får å se attrubuttnavn når vi printer ut
			System.out.println("" + "REPLYNR" + "\t" + "POSTNR" + "\t" + "REPLYTYPE" + "\t" + "REPLYTEXT" + "\t" + "REPLYTIME"); 
			//printer ut alle attributtene til alle attributtene som finnes
			while (rs.next()) {
				System.out.println("" + rs.getInt("replyNr") + "\t" + rs.getInt("postNr") + "\t\t" + rs.getString("replyType") 
				+"\t\t" + rs.getString("replyText") + "\t\t" + rs.getString("replyTime"));
			}
		} catch (Exception e)
		{
			System.out.println("Noe er feil i viewPost " + e);
		}  	
	} 
    
}
