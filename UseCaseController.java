package src;
import java.util.Scanner;


//klasse som gir deg muligheten til å bruke funksjonalitet fra de andre klassen, til å se at du kan utføre usecase 1 til 5.
//kunne hatt en klasse for hvert usecase, men har alt samlet her så kan du teste de ulike funksjonalitetene sammen, f.eks at logget inn user sin email blir satt inn i 
//Post-tabellen (må ha login for å få det)
public class UseCaseController extends DBC {
	

	static Scanner scanner = new Scanner(System.in);
	static ReplyCtrl rc = new ReplyCtrl();
	static UserCtrl uc = new UserCtrl();
	static PostCtrl pc = new PostCtrl();
	static ReplyCtrl rct = new ReplyCtrl();
	static StatisticsCtrl stc = new StatisticsCtrl();


	public static void main(String[] args)  {
		
		rc.connect();
		uc.connect();
		pc.connect();
		rct.connect();
		stc.connect();
		
		//her sjekker vi om user kan logge inn ved å skrive inn brukernavn eller passord
		System.out.println("Enter email: ");
		String email = scanner.nextLine();
		System.out.println("Enter password: ");
		String password = scanner.nextLine();
		
		if (uc.checkLogin(email, password)) {    //kan kun bruke funksjonaliteten dersom du får logget inn	
			
			//Under kaller vi metoder som utfyller usecase 2-5. Du kan opprette post (så lenge du gjør det til en Folder og Subfolder som allerede finnes i systemet)
			//du kan lage en reply så lenge du replyer til en faktisk post, du kan søke etter en post og du kan se statistikk
			while (true) {  
				
				System.out.println("What do you want to do? [Make post (m)], [Make reply (r)], [Search for post (s)], [View statistics (v)], [Exit (e)]  ");
				String letter = scanner.nextLine();
				
				if (letter.equals("m")) 
						makePost();  //lage Post (se metoder lenger ned i klassen
				
				else if (letter.equals("r"))
					makeReply();  //lage Reply:

				else if (letter.equals("s"))
					searchForPost();  //search post:
	
				else if (letter.equals("v"))
					viewStatistics(); //se statistikk
				
				else if(letter.equals("e")) {
					System.out.println("Bye! See you later");
					break;
				}
	
				else {
					System.out.println("You did not type a valid letter. Try again: ");
				}
				
				
					
			}
			
			
		}
		scanner.close();
	
		
		
		
	}
	
	
	//under er metoder for å spørre etter nødvendig informasjon som skal inn i usecases, og så utføre de, er kun basic 
	//lager en ny post vha. brukerinput og regFullPost-metoden fra PostCtrl
	public static void makePost() {
		
		System.out.println("Make a post! Which folder do you want to post to? ");
		String folderName = scanner.nextLine();
		System.out.println("And which subfolder? ");
		String subfolderName = scanner.nextLine();
		System.out.println("What tag is the post? ");
		String tag = scanner.nextLine();
		System.out.println("What post do you want to refer to. If no post, write -1: ");
		int toPostNr = scanner.nextInt();
		scanner.nextLine();  //må bare ta next line så scanneren går faktisk går til neste linje (det gjør den ikke med nextInt)
		System.out.println("What do you want to write for your post? ");
		String postText = scanner.nextLine();
				
		pc.regFullPost(folderName, subfolderName, tag, toPostNr, postText);
	}

	
	//lager en reply vha. brukerinput og makeFullReply-metoden fra ReplyCtrl
	public static void makeReply() {
		
		System.out.println("Make a reply! Which Post do you want to reply to? ");
		int postNr = scanner.nextInt();
		scanner.nextLine();
		System.out.println("What type of reply is it? (comment/answer)");
		String replyType = scanner.nextLine();
		System.out.println("And what do you want to write in your reply");
		String replyText = scanner.nextLine();
		
		rc.makeFullReply(postNr, replyText, replyType);
	}
	
	
	//lar deg søke etter Post vha. brukerinput og searchForPost-funksjonen fra PostCtrl
	public static void searchForPost() {
		System.out.println("Write a keyword, and we will print every postID that contains the keyword!: ");
		String keyword = scanner.nextLine();
		
		pc.searchPost(keyword);
	}
	
	
	//printer ut statistikk for hver bruker med email, antall leste Posts, antall lagde Posts, sortert etter brukere som har totalt mest leste Posts (total av alle Posts brukeren
	//har lagd. Kan kun se statistikk hvis du er instruktør
	public static void viewStatistics() {
		
		stc.viewStatistics();	
	}
	
}
