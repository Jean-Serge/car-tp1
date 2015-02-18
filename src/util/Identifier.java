package util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Cette classe permet de traiter l'identification d'un utilisateur.
 * Elle sert d'interface au système de stockage des informations utilisateur.
 * 
 * L'utilisation du pattern singleton permet de n'avoir qu'une seule instance de
 * cette classe utilisée par les Threads
 * 
 * @author Jean-Serge Monbailly
 *
 */
public class Identifier {
	
	public static final Identifier LOGGER = new Identifier(); 
	
	
	private Map<String, String>login; 	// Liste des couples user/password
	
	private Identifier(){
		this.login = new HashMap<String, String>();
		this.login.put("user", "mdp");
		this.login.put("user2", "azerty");

		String path;
		for (String username : login.keySet()){
			path = Tools.ROOT + username;
			new File(path).mkdirs();
		}
	}
	
	/**
	 * Permet d'identifier l'utilisateur grâce au couple login/mdp fournit. Lors
	 * de l'appel à cette fonction, l'utilisateur courant doit être connu.
	 * 
	 * @param user
	 *            le nom d'utilisateur
	 * @param pass
	 *            le mot de passe
	 * @return true si le mot de passe correspond à celui de l'utilisateur
	 */
	public boolean identify(String user, String pass) {
		return pass.equals(this.login.get(user));
	}
	
	/**
	 * Permet de savoir si l'utilisateur indiqué fait parti des clients
	 * autorisés de la base.
	 * 
	 * @param name
	 *            le nom du client à chercher
	 * @return true si le client est connu
	 */
	public boolean estConnu(String name) {
		return this.login.containsKey(name);
	}
}
