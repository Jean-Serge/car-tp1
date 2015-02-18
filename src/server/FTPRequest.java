package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Socket;

import util.Tools;
import util.Identifier;

/**
 * Classe permettant représentant une connexion.
 * Cette classe permet de traiter les différentes commandes transmises
 * au serveur.
 * 
 * @author Jean-Serge Monbailly
 *
 */
public class FTPRequest extends Thread {

	private Socket con, transfert;
	private BufferedReader br;
	private DataOutputStream bw;
	private boolean termine, estIdentifie;
	private String user;
	private String wd = null;

	/*
	 * ==========================================================================
	 * Constructeurs 
	 * ================================
	 */

	/**
	 * Instancie un objet à partir de la connexion passée en paramètre et ouvre
	 * les différents flux de données nécessaires à la communication.
	 */
	public FTPRequest(Socket con) {
		super();
		this.con = con;
		try {
			this.br = new BufferedReader(new InputStreamReader(
					this.con.getInputStream()));
			this.bw = new DataOutputStream(this.con.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.termine = this.estIdentifie = false;
	}

	public void run() {
		String msg;

		try {
			// On envoi le code indiquant que tout va bien.
			envoyer(Tools.CONNECTION_OK);
			// On boucle pour lire le message envoyé par le client.
			while (!termine) {
				msg = this.br.readLine();

				// En cas de problème avec le Thread (TODO à améliorer)
				if (msg == null)
					return;

				System.out.println("Message reçu : " + msg);
				processRequest(msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * ==========================================================================
	 * Commandes 
	 * ================================
	 */

	/**
	 * Analyse la commande passée en paramètre et délègue son exécution à une
	 * autre fonction de processing si celle-ci est gérée par le système. Si ce
	 * n'est pas le cas, cette fonction transmet simplement un message
	 * l'indiquant à l'utilisateur.
	 * 
	 * @param cmd
	 *            la commande saisie par l'utilisateur
	 * @throws IOException
	 */
	public void processRequest(String cmd) throws IOException {
		String[] split = cmd.split(" ");

		switch (split[0]) {
		case Tools.CMD_QUIT:
			processQUIT();
			break;
		case Tools.CMD_USER:
			// On passe la 2e case (normalement ce message ne contient que 2
			// champs)
			processUSER(split.length == 1 ? null : split[1]);
			break;
		case Tools.CMD_PASS:
			processPASS(split.length == 1 ? null : split[1]);
			break;
		case Tools.CMD_SYST:
			processSYST();
			break;
		case Tools.CMD_STOR:
			// 	Si l'utilisateur à indiqué le nom distant, on le transmet à la commande
			processSTOR(split.length == 3 ? split[2] : split.length == 2 ? split[1] : null);
			break;
		case Tools.CMD_RETR:
			processRETR(split.length == 1 ? null : split[1]);
			break;
		case Tools.CMD_LIST:
			processLIST();
			break;
		case Tools.CMD_EPRT:
			processEPRT(split.length == 1 ? null : split[1]);
			break;
		case Tools.CMD_PORT:
			processPORT(split.length == 1 ? null : split[1]);
			break;
		default:
			envoyer(Tools.UNIMPLEMENTED_COMMAND + " Commande inconnue : "
					+ split[0]);
			break;
		}
	}

	/**
	 * Permet au client de saisir son mot de passe. Gère les cas suivant : -
	 * l'utilisateur est déjà identifié sur le serveur - l'utilisateur n'a pas
	 * encore saisi son login - le mot de passe est null - le mot de passe est
	 * invalide - le mot de passe correspond à l'utilisateur
	 * 
	 * @param pass
	 *            le mot de passe saisi par l'utilisateur
	 * @throws IOException
	 */
	public void processPASS(String pass) throws IOException {
		if (estIdentifie) {
			envoyer("Vous êtes déjà identifié sur le serveur.");
		} else if (null == this.user) { // L'utilisateur doit d'abord indiquer
										// son login
			envoyer("Erreur, veuillez indiquer votre nom d'utilisateur d'abord svp.");

		} else if (null == pass) {
			envoyer(Tools.SYNTAX_ERROR
					+ " Veuillez indiquer votre mot de passe.");
		} else if (Identifier.LOGGER.identify(user, pass)) { // On vérifie que le mot de passe
											// indiqué est le bon
			this.estIdentifie = true;
			this.wd = Tools.ROOT + user + "/";
			envoyer(Tools.PASSWORD_OK
					+ " Vous êtes maintenant identifié sur le serveur.");
		} else {
			// Sinon la connexion est refusée
			envoyer(Tools.UNKNOWN + " Mot de passe incorrect");
		}
	}

	/**
	 * Permet d'identifier le client indiqué comme un utilisateur du serveur
	 * FTP. Gère les cas suivant : - le login indiqué n'est pas valide - le
	 * login n'est pas inscrit dans la liste des utilisateurs valides - le login
	 * correspond à celui d'un utilisateur valide
	 * 
	 * @param username
	 *            le login de l'utilisateur souhaitant se connecter
	 * @throws IOException
	 */
	public void processUSER(String username) throws IOException {
		// Si l'utilisateur était déjà connecté on le "déconnecte"
		this.user = null;
		if (null == username) {
			envoyer(Tools.SYNTAX_ERROR
					+ " Veuillez saisir un nom d'utilisateur.");
		} else if (Identifier.LOGGER.estConnu(username)) {
			this.user = username;
			envoyer((Tools.USER_OK + " Veuillez indiquer votre mot de passe svp."));
		} else {
			envoyer(Tools.UNKNOWN
					+ " Désolé vous ne faites pas parti des utilisateurs autorisés.");
		}
	}

	/**
	 * Permet au client de quitter le serveur. Le serveur transmet une dernière
	 * réponse à l'utilisateur et ferme la connexion. Il met également à jour le
	 * booléen termine indiquant à la boucle principale de s'arrêter.
	 * 
	 * @throws IOException
	 */
	public void processQUIT() throws IOException {
		envoyer(Tools.USER_DECONNECTION + " Déconnecté.");
		this.con.close();
		System.out.println("Fermé : " + this.con.isClosed());
		this.termine = true;
	}
	
	/**
	 * Envoie au client la liste des fichiers du répertoire courant.
	 * 
	 * @throws IOException
	 */
	public void processLIST() throws IOException{
		File file = new File(wd);
		DataOutputStream dt = new DataOutputStream(this.transfert.getOutputStream());

		envoyer(Tools.TRANSFERT_BEGIN);
		for(String s : file.list()) 
			dt.writeBytes(s+"\r\n");
		
		dt.close();
		this.transfert.close();
		
		envoyer(Tools.TRANSFERT_OK + " Transfert terminé.");
	}
	
	/**
	 * Permet au client de télécharger le fichier indiqué (si il existe).
	 * Gère les cas :
	 * - filename null
	 * - filename n'existe pas
	 * - filename n'appartient pas à l'utilisateur
	 * - filename est un répertoire
	 * 
	 * @param filename le nom du fichier à télécharger
	 * @throws IOException
	 */
	public void processRETR(String filename) throws IOException{
		if(null == filename){
			envoyer(Tools.SYNTAX_ERROR + " Veuillez indiquer le fichier à transférer.");
			return;
		}
		File file = new File(wd+filename);
		
		//	On vérifie l'existence du fichier et les droits d'accès.
		if(!file.exists() || !isHomeSubDir(file)){
			envoyer(Tools.FILE_NOT_FOUND + " Le fichier "+ filename + " n'a pas été trouvé ou vous n'y avez pas accès.");
			return;
		}

		//	On ne transfert pas de répertoire
		if(file.isDirectory()){
			envoyer(Tools.UNAUTHORIZED_FILE + " Le fichier demandé est un répertoire.");
			return;
		}
		
		DataOutputStream dt = new DataOutputStream(this.transfert.getOutputStream());
		FileInputStream is = new FileInputStream(file);
		int c;
		
		envoyer(Tools.TRANSFERT_BEGIN);
		while((c = is.read()) != -1)
			dt.write(c);
		
		dt.close();
		is.close();
		this.transfert.close();

		envoyer(Tools.TRANSFERT_OK + " Transfert terminé.");
	}

	/**
	 * Permet au client de stocker des fichiers sur le serveur distant.
	 * Gère les cas :
	 * - pathname null
	 * - pathname n'appartient pas à l'utilisateur
	 *
	 * @param pathname
	 * @throws IOException
	 */
	public void processSTOR(String pathname) throws IOException {
		if(null == pathname){
			envoyer(Tools.SYNTAX_ERROR + " Veuillez indiquer le fichier à transférer.");
			return;
		}

		// 	Eviter les problèmes de chemin
		File file = new File(wd + pathname);
		
		if(!isHomeSubDir(file))
			envoyer(Tools.SYNTAX_ERROR + " Tentative d'accès à un répertoire non autorisé.");
		
		// Si le fichier n'existe pas on le créé
		if(!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		if (!file.exists())
			file.createNewFile();
		else if(file.isDirectory()){
			envoyer(Tools.INCORRECT_PATHNAME + " Chemin incorrect, vérifiez qu'il ne s'agisse pas d'un répertoire.");
			return;
		}
		// Le client peut commencer le transfert
		FileOutputStream fo = new FileOutputStream(file);
		envoyer(Tools.TRANSFERT_BEGIN);
		
		int car;
		BufferedReader f = new BufferedReader(new InputStreamReader(
				transfert.getInputStream()));
		while (-1 != (car = f.read()))
			fo.write((char)car);

		f.close();
		fo.close();

		envoyer(Tools.TRANSFERT_OK + " Transfert terminé.");
	}

	public void processSYST() throws IOException {
		envoyer("Serveur personnalisé par JS");
	}

	/**
	 * Permet au client de transmettre au serveur les informations pour ouvrir
	 * la connexion nécessaire au transfert de fichiers (adresse + port).
	 * 
	 * @param msg les informations transmises par le client
	 * @throws IOException
	 */
	public void processEPRT(String msg) throws IOException {
		if (null == msg) {
			envoyer(Tools.SYNTAX_ERROR
					+ " Veuillez indiquer les paramètres de EPRT.");
			return;
		}
		String[] split = msg.split("\\|");
		InetAddress adresse;

		if (Integer.parseInt(split[1]) == 1)
			adresse = Inet4Address.getByName(split[2]);
		else
			adresse = Inet6Address.getByName(split[2]);

		int port = Integer.parseInt(split[3]);
		this.transfert = new Socket(adresse, port);

		envoyer(Tools.CONNECTION_OPENED + " Connexion ouverte.");
	}

	/**
	 * Semblable à la commande EPRT mais plus spécifique au protocole IPv4.
	 * 
	 * @param msg
	 * @throws IOException
	 */
	public void processPORT(String msg) throws IOException {
		if(null == msg){
			envoyer(Tools.SYNTAX_ERROR + " Veuillez spécifiez les paramètres de la commande PORT.");
			return;
		}
		// 	Paramètres de la commandes
		String[] split = msg.split(",");
		if(split.length != 6){
			envoyer(Tools.SYNTAX_ERROR + " Arguments de la commande PORT incorrects.");
			return;
		}
		
		// 	Récupération de l'adress IP
		String ip = split[0];
		for(int i = 1 ; i <= 3 ; i++){
			ip += "." + split[i];
		}
		
		// 	Lecture du port
		int port = Integer.parseInt(split[4]);
		port *= 256;
		port += Integer.parseInt(split[5]);

		// 	Ouverture de la connexion
		this.transfert = new Socket(Inet4Address.getByName(ip), port);
		envoyer(Tools.CONNECTION_OPENED + " Connexion ouverte.");
	}
	
	/*
	 * ==========================================================================
	 * Fonctions utiles 
	 * ================================
	 */

	/**
	 * Permet d'envoyer un message au client. Transmet le message indiqué en le
	 * complétant d'un retour chariot.
	 * 
	 * @param msg
	 *            le message à transmettre
	 * @throws IOException
	 */
	private void envoyer(String msg) throws IOException {
		this.bw.write((msg + "\n").getBytes());
	}
	
	private boolean isHomeSubDir(File file) throws IOException{
		// 	Retourne le chemin absolu du fichier (sans . et ..)
		String path = file.getCanonicalPath();
		
		String home = Tools.ROOT + user + "/";
		File f = new File(home);
		home = f.getCanonicalPath();
		
//		System.out.println("Path : " + path);
//		System.out.println("Home : " + home);
		
		return path.startsWith(home);
		
	}
}
