package cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class Client {

    public static void main(String[] args) {

        String url = "localhost";
        int port = 1234;

        TrustManager[] trustManagers;
        KeyManager[] keyManagers;

        try {

            
    // ============ CERTIFICADOS PROPIOS ===============
            // Cargamos nuestro certificado
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("keys/client/clientKey.jks"), "123456".toCharArray());

            // Preparamos nuestra lista de certificados
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, "123456".toCharArray());
            
            // Obtenemos nuestra lista de certificados
            keyManagers = kmf.getKeyManagers();

            
    // ============= CONFIANZA =====================
            // Cargamos nuestros certificados de confianza
            KeyStore trustedStore = KeyStore.getInstance("JKS");
            trustedStore.load(new FileInputStream("keys/client/clientTrustedCerts.jks"), "123456".toCharArray());

            // Preparamos nuestra lista de confianza
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustedStore);
            
            // Obtenemos nuestra lista de lugares seguros
            trustManagers = tmf.getTrustManagers();
            

    // ============= CONEXION ===============
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(keyManagers, trustManagers, null);
            
            SSLSocketFactory ssf = sc.getSocketFactory();
            SSLSocket client = (SSLSocket) ssf.createSocket(url, port);
            
            client.startHandshake();
            
            // Flujos
            DataOutputStream salida = new DataOutputStream(client.getOutputStream());
            DataInputStream entrada = new DataInputStream(client.getInputStream());
            
            // Recibir
            System.out.println("Recibir saludo");
            System.out.println(entrada.readUTF());
            
            // Enviar
            System.out.println("Enviar respuesta");
            salida.writeUTF("Hola server");
            
            
            client.close();
        } catch (IOException ex) {
            System.err.println("[ERROR-SOCKET]: " + ex);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | KeyManagementException ex) {
            System.out.println("[ERROR-SOCKET]: " + ex);
        }

    }

}
