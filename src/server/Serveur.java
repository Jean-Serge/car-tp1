package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import util.Tools;

/**
 * Cette classe permet de lancer un serveur FTP.
 * Chaque connexion au serveur FTP génère un nouveau Thread
 * (cela permet les connexions multiples au serveur).
 *
 * @author Jean-Serge Monbailly
 * 
 */
public class Serveur {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerSocket sock = null;
		Socket con;
		FTPRequest rq;
		boolean estActif = true;

		try {
			sock = new ServerSocket(Tools.FTP_PORT);
		} catch (IOException e1) {
			System.out.println("Problème lors de l'ouverture de la socket.");
		}

		while (estActif) {
			System.out.println("Attente d'une connexion.");
			try {
				con = sock.accept();
				System.out.println("Personne connectée.");

				rq = new FTPRequest(con);
		
				rq.start();
			} catch (IOException e) {
				System.out.println("Problème lors de la connexion du client.");
			}

		}

		try {
			sock.close();
		} catch (IOException e) {
			System.out.println("Erreur lors de la fermeture de la connexion.");
		}
	}

}
