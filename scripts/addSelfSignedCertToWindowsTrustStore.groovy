/**
* Prova conceptual en groovy per a carregar un CDS selfsigned al magatzem de certificats de Windows
* a través del sunMSCAPI
*
* Test to add in groovy a self signed server certificate to the Windows trust store
* @author albciff
* http://stackoverflow.com/users/1218618/albciff
**/

import java.security.KeyStore
import java.security.cert.Certificate

String aliasCds = 'cds'
KeyStore jks = KeyStore.getInstance("JKS")
jks.load(new FileInputStream('C:/temp/cds_nativa_certificate/keystore.jks'),'password'.toCharArray())
Certificate cds = jks.getCertificate(aliasCds)

// carreguem el windows trust store
KeyStore ks = KeyStore.getInstance("Windows-ROOT")
ks.load(null, null)

// afegim el CDS al magatzem
ks.setCertificateEntry("test",cds);

// print de tots els alias del magatzem
ks.aliases().each{ alias ->
  println alias
}
