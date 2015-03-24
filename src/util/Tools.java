package util;

/**
 * Classe contenantconstantes utiles pour le projet.
 * 3 catégories :
 * - paramètrage du serveur 
 * - commandes traitées par le serveur
 * - code de réponse du serveur au client
 * 
 * @author Jean-Serge Monbailly
 *
 */
public class Tools {

	//	=================  Paramètres Serveur  =============== 
	public static final int FTP_PORT = 2000;
	
	public static final String ROOT = "\\/";
	
// 	================  Commandes Reconnues ================
	public static final String CMD_QUIT = "QUIT";

	public static final String CMD_USER = "USER";
	
	public static final String CMD_PASS = "PASS";

	public static final String CMD_STOR = "STOR";
	
	public static final String CMD_RETR = "RETR";
	
	public static final String CMD_SYST = "SYST";

	public static final String CMD_EPRT = "EPRT";

	public static final String CMD_PORT = "PORT";
	
	public static final String CMD_LIST = "LIST";

	public static final String CMD_RMD = "RMD";
	
	// 	=====================  Réponses  =====================
	public static final String CONNECTION_OK = "220";
	
	public static final String USER_OK = "331";
	
	public static final String UNKNOWN = "430";
	
	public static final String PASSWORD_OK = "230";
	
	public static final String SYNTAX_ERROR = "501";
	
	public static final String USER_DECONNECTION = "231";

	public static final String UNIMPLEMENTED_COMMAND = "202";
	
	public static final String COMMAND_OK = "200";

	public static final String TRANSFERT_OK = "226";
	
	public static final String TRANSFERT_BEGIN = "125";
	
	public static final String CONNECTION_OPENED = "225";
	
	public static final String INCORRECT_PATHNAME = "553";
	
	public static final String FILE_NOT_FOUND = "550";
	
	public static final String UNAUTHORIZED_FILE = "553";


//	public static final String ANSI_RED = "\u001B[31m";
}

