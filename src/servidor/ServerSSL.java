package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class ServerSSL {

    public static void main(String[] args) {

        int port = 1234;

        TrustManager[] trustManagers;
        KeyManager[] keyManagers;

        try {

            
    // ============ CERTIFICADOS PROPIOS ===============
            // Cargamos nuestro certificado
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("keys/server/serverKey.jks"), "123456".toCharArray());

            // Preparamos nuestra lista de certificados
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, "123456".toCharArray());
            
            // Obtenemos nuestra lista de certificados
            keyManagers = kmf.getKeyManagers();

            
    // ============= CONFIANZA =====================
            // Cargamos nuestros certificados de confianza
            KeyStore trustedStore = KeyStore.getInstance("JKS");
            trustedStore.load(new FileInputStream("keys/server/serverTrustedCerts.jks"), "123456".toCharArray());

            // Preparamos nuestra lista de confianza
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustedStore);
            
            // Obtenemos nuestra lista de lugares seguros
            trustManagers = tmf.getTrustManagers();

            
    // =============== CONEXION =================
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(keyManagers, trustManagers, null);

            SSLServerSocketFactory ssf = sc.getServerSocketFactory();
            ServerSocket serverSocket = ssf.createServerSocket(port);
            
            

            System.out.println("Esperando");
            Socket conexion = serverSocket.accept();
            System.out.println("Conexión establecida");

            
            
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            
            // Enviar
            System.out.println("Enviar salido");
            salida.writeUTF("Hola cliente");
            salida.flush();
            
            // Recibir
            System.out.println("Recibir respuesta");
            System.out.println(entrada.readUTF());
            
            conexion.close();

        } catch (IOException ex) {
            System.err.println("[ERROR-SOCKET]: " + ex);
        } catch (NoSuchAlgorithmException | CertificateException | KeyStoreException | UnrecoverableKeyException | KeyManagementException ex) {
            System.err.println("[ERROR]: " + ex);
        }

    }

}
