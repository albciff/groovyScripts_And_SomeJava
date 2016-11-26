/**
* Prova de concepte que aixeca un server http i un server https amb groovy
*
**/
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.SSLEngine
import javax.net.ssl.SSLParameters
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.cert.X509Certificate

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpsServer
import com.sun.net.httpserver.HttpsExchange
import com.sun.net.httpserver.HttpsConfigurator
import com.sun.net.httpserver.HttpsParameters
   
class MyHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {
        String response = '{"alive" : "true" }'
        t.sendResponseHeaders(200, response.length())
        OutputStream os = t.getResponseBody()
        os.with{
          write(response.getBytes())
          close()
        }
    }
}

/*  
HttpServer server = HttpServer.create(new InetSocketAddress(6065), 0);
server.with{
    createContext("/test", new MyHandler());
    setExecutor(null); // creates a default executor
    start();
}*/

HttpsServer server = HttpsServer.create(new InetSocketAddress(6069), 0);
    SSLContext sslContext = SSLContext.getInstance ( "TLS" );

    // initialise the keystore
    char[] password = "password".toCharArray ();
    KeyStore ks = KeyStore.getInstance ( "JKS" );
    FileInputStream fis = new FileInputStream ( "C:/temp/keystore.jks" );
    ks.load ( fis, password );

    // setup the key manager factory
    KeyManagerFactory kmf = KeyManagerFactory.getInstance ( "SunX509" );
    kmf.init ( ks, password );

    // setup the trust manager factory
    TrustManagerFactory tmf = TrustManagerFactory.getInstance ( "SunX509" );
    tmf.init ( ks );

    // setup the HTTPS context and parameters
    sslContext.init ( kmf.getKeyManagers (), tmf.getTrustManagers (), null );
    server.setHttpsConfigurator ( new HttpsConfigurator( sslContext )
    {
        public void configure ( HttpsParameters params )
        {
            try
            {
                // initialise the SSL context
                SSLContext c = SSLContext.getDefault ();
                SSLEngine engine = c.createSSLEngine ();
                params.setNeedClientAuth ( false );
                params.setCipherSuites ( engine.getEnabledCipherSuites () );
                params.setProtocols ( engine.getEnabledProtocols () );

                // get the default parameters
                SSLParameters defaultSSLParameters = c.getDefaultSSLParameters ();
                params.setSSLParameters ( defaultSSLParameters );
            }
            catch ( Exception ex ){}
        }
    } );
    
server.with{
    createContext("/prova", new MyHandler());
    setExecutor(null); // creates a default executor
    start();
}
