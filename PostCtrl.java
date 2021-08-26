package src;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


//denne klassen håndterer alt som har med Post å gjøre, lage en ny Post, søke etter eksisterende Posts og validerer input når Post skal opprettes
public class PostCtrl extends DBC {
	
	private int postNr;
	private static final int no_post = -1;

	
	//en metode som bruker hjelpemetodenr (under) til å opprettte en Post og sette inn verdiene i Post1 og Post2 tabellene
	//returnerer true hvis du får lage en post, og false hvis du ikke gir gyldig input (f.eks en folder som ikke eksisterer)
	public boolean regFullPost(String folderName, String subfolderName, String tag, int toPostNr, String postText) {
		
		//hvis input ikke blir validert returneres false, og det oppprettes ikke en ny post
		if (!this.validatePost(folderName, subfolderName, toPostNr)) {
			System.out.println("Noe er feil i inputen din til regFullPost");
			return false; 
			}
		
		this.createdPost();
		this.findNextPostNr();		
		this.regPost1(folderName, subfolderName);
		this.regPost2(toPostNr, tag, postText);
		System.out.println("You made a post!");
		return true;
	}
	

	//metode for å sette inn verdier i Post1 tabellen, bruker preparedstatement for å sette inn, legger inn de 3 attributtene som hører til i Post1 tabellen
	// (postNr, subfolderID, userEmail), og executer inserten
	public void regPost1 (String folderName, String subfolderName) {
		
		//bruker hjelpemetode lenger ned i klassen
		int subfolderID = this.findSubfolderID(subfolderName, folderName);
		String userEmail = UserCtrl.loggedInUser; 
		String insertStatement = "insert into Post1 values ( ?, ?, ? )  ";
	       
			if (postNr != no_post) {
	            try {
	            	PreparedStatement regPost1 = conn.prepareStatement(insertStatement);
	            	regPost1.setInt(1, postNr);
	            	regPost1.setInt(2, subfolderID);
	            	regPost1.setString(3, userEmail);
	            	regPost1.execute();	            	
	            } catch (Exception e) 
	            {
	                System.out.println("db error during insert of post1  " + e);
	            }
	        }
	    }
	
	
	//metode for å sette inn i Post2 tabellen, ganske lik innsetting i Post1 tabellen, bare med andre attributter, noen av attributtene blir ikke bestemt av brukeren og 
	//defineres øverst (numberRead, likes, postTime, colorCode), toPostNr = -1 hvis den ikke refererer til en post
	public void regPost2 (int toPostNr, String tag, String postText) {
		
		// når en Post opprettes er den lest 0 ganger og har 0 likes
		int numberRead = 0;	 
		int likes = 0;
		//når en post opprettes så starter den med colorCode rød (som kan bli endret hvis noen Replyer med answer til en Post)
		String colorCode = "Rød"; 
		//laget egen metode på bunnen av klasse som finner nåtiden, slik at vi kan sette inn tiden Posten ble opprettet 
	  	String postTime = this.findNowTime(); 
    	String insertStatement = "insert into Post2 values ( ?, ?, ?, ?, ?, ?, ?, ?) ";
	       
    		if (postNr != no_post) {
	            try {
	            	PreparedStatement regPost2 = conn.prepareStatement(insertStatement);
	    			regPost2.setInt(1, postNr);
	    			regPost2.setInt(2, toPostNr);
	    			regPost2.setInt(3, numberRead); 
	    			regPost2.setString(4, postTime);
	    			regPost2.setString(5, tag);
	    			regPost2.setInt(6, likes);
	    			regPost2.setString(7, colorCode);
	    			regPost2.setString(8, postText);
	    			regPost2.execute();	
	            } catch (Exception e) {
	            	System.out.println("db error during insert of post2 " + e);
	            }
	        }
	    }
	
	
	//søker etter posts med et keyword vi putter inn, hvis noen posts inneholder keywordet, printer metoden ut POSTNR
	public void searchPost(String keyword) {  
		
		//med % før og etter keyword ser vi etter alle posts som har keywordet i seg (og hvilke som helst andre tegn på begge sider av keywordet
		//kan f.eks skrive "WAL" som keyword, og da får du alle posts som har en postText med "WAL" i seg
		String query = "select postNr from Post2 where postText like '%"+keyword+"%'";
		
		try {
			PreparedStatement ps = conn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			System.out.println("POSTNR:"); 
			//printer ut postNr med alle posts som inneholder keywordet som ble funnet i queryen
			while (rs.next()) {
			 System.out.println(rs.getInt("postNr")); 
		 	}
		} catch (Exception e) 
		{
		System.out.println("something went wrong in searchPost " + e);
		}
	}
	
	
	//Sjekker at input er gydlig når man lager en Post (det vil si at folder subfolder og postNr man refererer til finnes),
	public boolean validatePost(String folderName, String subfolderName, int toPostNr) {
		
		boolean folderNameOk = false;
		boolean subfolderNameOk = false;
		boolean toPostNrOk = false;
		
		if (toPostNr == -1) 
			toPostNrOk = true; //skal være greit å ha -1, altså at man ikke refererer til noen post
		
		//her sjekker vi bare om det finnes en folder med navnet brukeren putter inn, samme for subfolderName og toPostNr 
		String query1 = "select * from Folder where folderName = ? ";
		String query2 = "select * from Subfolder where subfolderName = ? ";
		String query3 = "select * from Post1 where postNr = ? ";
		
		try {
			PreparedStatement ps1 = conn.prepareStatement(query1);
			ps1.setString(1, folderName);
			ResultSet rs1 = ps1.executeQuery();
			//hvis det finnes minst en rad som har det folderName som brukeren putter inn, så er inputen ok!, samme gjelder for subfolder og toPostNr under
			if (rs1.next())  
				folderNameOk = true;
			
			PreparedStatement ps2 = conn.prepareStatement(query2);
			ps2.setString(1, subfolderName);
			ResultSet rs2 = ps2.executeQuery();
			if (rs2.next())
				subfolderNameOk = true;
			
			PreparedStatement ps3 = conn.prepareStatement(query3);
			ps3.setInt(1, toPostNr);
			ResultSet rs3 = ps3.executeQuery();
			if (rs3.next())
				toPostNrOk = true;
			
		} catch (Exception e) 
		{
			System.out.println("Noe er feil i validatePost " + e);
		}
		
		//hvis alle input er ok -> så kan kan vi lage en post i regFullPost-metoden
		if (folderNameOk && subfolderNameOk && toPostNrOk) {  
			return true;
		}
			//returnerer false hvis userinput ikke er gyldig
			return false; 
	}
	

	//metode for nåtid som er en av attributtene som skal med i en Post, returner en dato+tid som en streng (slik vi lagrer det i databasen) 
	public String findNowTime() {
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");   
	  	LocalDateTime now = LocalDateTime.now();  
	  	return dtf.format(now);
	}
	
	
	//finner subfolderID ved å joine subfolder og Folder-tabellene og så henter vi ut ID-en til subfolder som stemmer med begge navnene (folderName og subfolderName)
	//returnerer subfolderID-en som vi finner (eller -1 hvis den ikke finner en subfolderID)
	public int findSubfolderID (String subfolderName, String folderName) {
		
		try {
			String query = "select subfolderID from Folder natural join Subfolder where subfolderName = ? AND folderName = ? ";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, subfolderName);
			ps.setString(2, folderName);
			ResultSet rs = ps.executeQuery();
			
			//hvisd en finner en subfolderID som stemmer med folderName og subfolderName, returneres subfolderID-en, ellers returneres -1
			if (rs.next()) {
				int returnInt = rs.getInt("subfolderID");
				return returnInt;
			}
			else {
				return -1;
			}
		
		}	catch (Exception e)
		{
			System.out.println("Noe er feil i findSubfolderID " + e);
			return -1;
		}
	
	
	}
	
	
	//metode som lager et nytt unikt postNr, returnerer ingenting men endrer variabelen postNr i klassen
	public void findNextPostNr() {
		
		try {
			//henter ut høyeste postNr og lager neste postNr et tall høyere, på denne måten får vi nye primary keys når vi oppretter en ny post
			String query = "select postNr from Post1 order by postNr DESC limit 1";
			PreparedStatement ps = conn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			//hvis det finnes minst en post allerede, sentter den postNr til en høyere enn hva høyeste postNr allerede er, hvis ikke finnes ikke en post, og da settes postNr = 1
			if (rs.next())  {
				this.postNr = rs.getInt("postNr");
				this.postNr++;
			}
			else 
				postNr = 1;
		} catch (Exception e) 
		{
			System.out.println("noe feil i regFullPost");
		}
	}
	
	
	
	
	
	//Metodene under er ikke påkrevd for usecases, men er metoder vi har uansett (for å legge inn data for statistikk):
	
	
	// oppdaterer hvor mange ganger en bruker har lagd en Post (hvis den ikke finner email i Student/Instructor i tabellen, blir bare 0 rows affected i SQL, og den prøver andre try-metoden)
	public void createdPost() {
		
		try {
			//hvis det er en student som har lagd en post finner vi email-en til brukeren her 
			String updateStatement = "UPDATE Student set postsCreated = (postsCreated + 1) where email = ? ";
			PreparedStatement ps1 = conn.prepareStatement(updateStatement);
			ps1.setString(1, UserCtrl.loggedInUser);
			ps1.executeUpdate();
		} catch (Exception e) 
		{
			System.out.println("Noe feil når studenttabell skulle oppdateres "+ e);
		}
		
		try {
			//hvis det er en instructor som har lagd en post finner vi email-en til brukeren her
			String updateStatement = "UPDATE Instructor set postsCreated = (postsCreated + 1) where email = ? ";
			PreparedStatement ps2 = conn.prepareStatement(updateStatement);
			ps2.setString(1, UserCtrl.loggedInUser);
			ps2.executeUpdate();
			} catch (Exception e) 
		{
			System.out.println("Noe feil når instructortabell skulle oppdateres");
		}
	}
	
	
	//oppdaterer hver Post med antall ganger den har blitt lest, som skal bli lagret i Post2 tabellen, metoden tar ikke hensyn til at en Post kan bli lest flere ganger av en person
	public void readPost(int postNr) {
		
		String updateStatement = "update Post2 set numberRead = (numberRead + 1) where postNr = ? ";
		
		try {
			PreparedStatement ps = conn.prepareStatement(updateStatement);
			ps.setInt(1, postNr);
			ps.executeUpdate();
		} catch (Exception e) 
		{
			System.out.println("Noe feil i readPost " + e);
		}
		
		//oppdaterer postsRead i enten student eller instructor avhengig av hvilken user som er logget inn
	    if (UserCtrl.loggedInPermission.equals("student"))
	    	this.studentReadPost();
	    else if (UserCtrl.loggedInPermission.equals("instructor"))
	    	this.instructorReadPost();
	}
	
	
	//oppdaterer antall Posts en bruker har lest (i Student-tabellen)
	public void studentReadPost() {
		
		//oppdaterer student tabell hvis han lesesr en post 
		String updateStatement = "UPDATE Student set postsRead = (postsRead + 1) where email = ? ";
		
		try {
			PreparedStatement ps1 = conn.prepareStatement(updateStatement);
			ps1.setString(1, UserCtrl.loggedInUser);	
			ps1.executeUpdate();
		} catch (Exception e)
		{
			System.out.println("Noe er feil i studenReadPost " + e);
		}
	}
	
	
	//oppdaterer antall Posts en bruker har lest (i instructor-tabellen)
	public void instructorReadPost() {
		
		String updateStatement = "UPDATE Instructor set postsRead = (postsRead + 1) where email = ? ";	
		
		try {
			PreparedStatement ps2 = conn.prepareStatement(updateStatement);
			ps2.setString(1, UserCtrl.loggedInUser);
			ps2.executeUpdate();
		} catch (Exception e) 
		{
			System.out.println("Noe er feil i instructorReadPost " +e);
		}	
	}
	
	
	//metode så vi kan "like" posts, effektivt sett oppdaterer den bare Post2 tabellen og legger til en like
	public void likePost(int postNr) {
		
		String updateStatement = "update Post2 set likes = (likes + 1) where postNr = ? ";
		
		try {
			PreparedStatement ps = conn.prepareStatement(updateStatement);
			ps.setInt(1, postNr);
			ps.executeUpdate();
		} catch (Exception e) 
		{
			System.out.println("Noe er feil i likePost " + e);
		}
	}
	
	
	//metode for å kunne printe ut verdier (så vi kan se en post i konsollen)
	public void viewPost(int postNr) {
		
		String query = "select * from Post1 natural join Post2 where postNr = ? ";
		
		try {
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setInt(1, postNr);
			ResultSet rs = ps.executeQuery();
			System.out.println("" + "POSTNR" + "\t" + "SUBFOLDERID" + "\t" + "USEREMAIL" + "\t" + "TOPOSTNR" + "\t" + "NUMBERREAD" + "\t" + "POSTTIME" + "\t\t" 
			                   + "TAG" + "\t\t" + "LIKES" + "\t" + "COLORCODE" + "\t" + "POSTTEXT");
			
			//printer ut alle attributtene til alle posts nedover
			while (rs.next()) {
				System.out.println("" + rs.getInt("postNr") + "\t" + rs.getInt("subfolderID") + "\t\t" + rs.getString("userEmail") +"\t\t" + rs.getInt("toPostNr") + "\t\t" +
				rs.getInt("numberRead") + "\t\t" + rs.getString("postTime") + "\t" + rs.getString("tag") + "\t" + rs.getInt("likes") + "\t" 
				+ rs.getString("colorCode") + "\t\t" + rs.getString("postText"));
			}
		} catch (Exception e)
		{
			System.out.println("Noe er feil i viewPost " + e);
		}  
		
	}
	
}
	
	

		

