import java.net.URL;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * Prova de concepte per a descarregar el server certificate
 * 
 * @author albciff
 *
 */
public class DownloadCDS {

	public static void main(String args[]){
		
		SSLSocket socket = null;
		
		try{
			
			X509TrustManager manager = new X509TrustManager() {
				
				private X509Certificate[] accepted;
				   
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					// TODO Auto-generated method stub
					return accepted;
				}
				
				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					  accepted = chain;
					
				}
				
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					// TODO Auto-generated method stub
					
				}
			};
			
			
			URL httpsURL = new URL("https://seuelectronica.antifrau.cat/");
			String host = httpsURL.getHost();
			int port = httpsURL.getPort();
			if(port == -1) {
				port = 443;
				if(httpsURL.getProtocol() == "http"){
					port = 90;
				}
			}
			
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new TrustManager[]{ manager }, null);
			socket = (SSLSocket) context.getSocketFactory().createSocket(host,port);
			socket.startHandshake();

			System.out.println(manager.getAcceptedIssuers()[0].getSubjectDN());
			
			// aqui tenim el certificat del servidor
			X509Certificate serverCertificate = manager.getAcceptedIssuers()[0];
			
		}catch(Exception e){
			
		}finally{
			try{ socket.close(); }catch(Exception e){};
		}
	}
	
}
