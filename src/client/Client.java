package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import util.Tools;

/**
 * (obsolète)
 * Classe proposant un client pour un serveur FTP.
 * 
 * @author Jean-Serge Monbailly
 *
 */
public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			/* =============== Ouverture de la connexion et des différents flux ============ */
			
			Socket s = new Socket(InetAddress.getLocalHost(), Tools.FTP_PORT);
			DataOutputStream bw = new DataOutputStream(s.getOutputStream());
			InputStream is = s.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));


			/* ================= Boucle de communication avec le serveur =================== */
			
			Scanner sc = new Scanner(System.in);
			boolean fini = false;
			String msg;
			
			while(true){
				msg = br.readLine();
				System.out.println(msg);
				
				// Dernière réponse du serveur
				if(fini)
					break;

				// Lecture et envoi de la commande voulue
				System.out.print(">");
				msg = sc.nextLine();
				msg += "\n";
				bw.write(msg.getBytes());

				// La commande QUIT permet de quitter la boucle
				if(msg.startsWith(Tools.CMD_QUIT))
					fini = true;
			}
			
			/* ======================= Fermeture de la connexion ============================ */
			
			System.out.println("Fini");
			sc.close();
			s.close();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

}
