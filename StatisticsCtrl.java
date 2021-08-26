package src;

import java.sql.PreparedStatement;
import java.sql.*;


//klasse som fikser statistikk for usecase5
public class StatisticsCtrl extends DBC {


	
	//vi printer ut statistikken til konsoll (i stedet for � returnere en liste med statistikk eller lignende)
	//metoden returner false hvis en student pr�ver � se statistikk (noe studenten ikke har rettighet til), og true hvis en instrukt�r pr�ver � se statistikk
	public boolean viewStatistics () {
		
		if (UserCtrl.loggedInPermission.equals("student")) {
			System.out.println("You are not an instructor and can not view statistics");
			return false;
				}
		
		// finner riktig informasjon for statistikken og sorterer etter brukeren hvor summen av posts brukeren har lagd, er lest mest
		// bruker left outer join for � f� med alle users, selv om de ikke har lagd en post allerede (m� ha \r\n s� vi ikke f�r altfor lange linjer)
		//bruker union for � legge til informasjon vi finner fra b�de Student og Instructor-tabellene
		// selecter de 3 etterspurte attributtene email, postsRead og postsCreated, grouper etter email (for � samle alle totalt antall ganger en bruker sine posts er blitt lest)
		// Totalt antall visninger en bruker har p� postene sine blir lagret i "nr", og sorteres da p� det til slutt
		String query = "select email, postsRead, postsCreated from\r\n" + 
				"(select Student.email, Student.postsRead, Student.postsCreated, sum(numberRead) as nrReads\r\n" + 
				"from Student left outer join Post1 on (Student.email = Post1.userEmail) left outer join Post2\r\n" + 
				"on (Post1.postNr = Post2.postNr)\r\n"+
				"group by Student.email \r\n" + 
				"UNION \r\n" + 
				"select Instructor.email, Instructor.postsRead, Instructor.postsCreated, SUM(numberRead) as nrReads\r\n" + 
				"from Instructor left outer join Post1 on (Instructor.email = Post1.userEmail) left outer join Post2 \r\n" + 
				"on (Post1.postNr = Post2.postNr)\r\n"+
				"group by Instructor.email\r\n" + 
				"order by nrReads DESC) as statistikkTable";
		
		try {
			PreparedStatement ps = conn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			System.out.println("EMAIL" + "\t\t" + "POSTSREAD" + "\t" + "POSTSCREATED");
			//printer ut statistikken vi finner til konsoll
			while (rs.next()) {
				System.out.println(""+rs.getString("email") +"\t\t"+ rs.getInt("postsRead")+ "\t\t" +rs.getInt("postsCreated") + "\n");
			}
			
		} catch (Exception e)
		{
			System.out.println("Statistikk funker ikke " + e);
		}
		
		return true;
	}
	
}
