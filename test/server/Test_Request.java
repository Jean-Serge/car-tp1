package server;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import util.Tools;

/**
 * Classe de test pour les principales commandes gérées par le serveur.
 * (nécessite que le serveur soit lancé au préalable).
 * 
 * @author Jean-Serge Monbailly
 *
 */
public class Test_Request {

	private Socket con;
	private DataOutputStream bw;
	private BufferedReader br;
	
	@Before
	public void setUp() throws UnknownHostException, IOException {
		this.con = new Socket(InetAddress.getLocalHost(), Tools.FTP_PORT);
		this.bw = new DataOutputStream(this.con.getOutputStream());
		this.br = new BufferedReader(new InputStreamReader(this.con.getInputStream()));
		
		this.br.readLine();
	}

	@After
	public void terminate() throws IOException{
		this.con.close();
	}
	
	/* ====================  Tests Commande USER  ====================== */
	
	/**
	 * Vérifie le résultat de la commande USER pour un utilisateur existant.
	 * @throws IOException
	 */
	@Test
	public void testUSERExistant() throws IOException{
		this.bw.write((Tools.CMD_USER + " user\r\n").getBytes());
		String retour = this.br.readLine();
		assertEquals(Tools.USER_OK, retour.split(" ")[0]);
	}
	
	/**
	 * Vérifie le résultat de la commande USER pour un utilisateur inconnu.
	 * @throws IOException
	 */
	@Test
	public void testUSERNonExistant() throws IOException{
		this.bw.write((Tools.CMD_USER + " existePas\r\n").getBytes());
		String retour = this.br.readLine();
		assertEquals(Tools.UNKNOWN, retour.split(" ")[0]);
	}
	
	/**
	 * Vérifie le résultat de la commande USER sans paramètre.
	 * @throws IOException
	 */
	@Test
	public void testUSERNull() throws IOException{
		this.bw.write((Tools.CMD_USER + " \r\n").getBytes());
		String retour = this.br.readLine();
		assertEquals(Tools.SYNTAX_ERROR, retour.split(" ")[0]);
	}
	
	/* ====================  Tests Commande PASS  ====================== */
	
	/**
	 * Vérifie le résultat de la commande PASS lorsque le mot de passe est correct.
	 * @throws IOException
	 */
	@Test
	public void testPASSCorrect() throws IOException{
		this.bw.write((Tools.CMD_USER + " user\r\n").getBytes());
		this.br.readLine();
		this.bw.write((Tools.CMD_PASS + " mdp\r\n").getBytes());
		String retour = this.br.readLine();
		assertEquals(Tools.PASSWORD_OK, retour.split(" ")[0]);
	}
	
	/**
	 * Vérifie le résultat de la commande PASS lorsque le mot de passe est correct.
	 * @throws IOException
	 */
	@Test
	public void testPASSIncorrect() throws IOException{
		this.bw.write((Tools.CMD_USER + " user\r\n").getBytes());
		this.br.readLine();
		this.bw.write((Tools.CMD_PASS + " mdpIncorrect\r\n").getBytes());
		String retour = this.br.readLine();
		assertEquals(Tools.UNKNOWN, retour.split(" ")[0]);
	}
	
	/**
	 * Vérifie le résultat de la commande PASS lorsque le mot de passe est correct.
	 * @throws IOException
	 */
	@Test
	public void testPASSNull() throws IOException{
		this.bw.write((Tools.CMD_USER + " user\r\n").getBytes());
		this.br.readLine();
		this.bw.write((Tools.CMD_PASS + " \r\n").getBytes());
		String retour = this.br.readLine();
		assertEquals(Tools.SYNTAX_ERROR, retour.split(" ")[0]);
	}
	/* ====================  Tests Commande QUIT  ====================== */
	
	/**
	 * Ce test permet de vérifier que la commande QUIT est bien transmise et 
	 * traitée par le Serveur.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testQUIT() throws IOException {
		this.bw.write((Tools.CMD_QUIT + "\r\n").getBytes());
		String retour = this.br.readLine();
		assertEquals(Tools.USER_DECONNECTION, retour.split(" ")[0]);
	}

}
