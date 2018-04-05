/**
* @albciff
* Recupera tots els certificats configurats a nivell d'entitat, descarta els caducats o els encara no valids.
* Sobre aquests genera dos fitxers amb info: 
*     1 amb informació bàsica nom ens/cert/serial
*     2 unicament els serials del certificats sense repeticions
**/
import groovy.sql.Sql
import java.security.cert.CertificateFactory
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import java.util.zip.GZIPInputStream

// connexió de la BDD de NT a revisar
def db = [
    url : 'jdbc:oracle:thin:@XXXXXXX',
    username : 'XXXX',
    password : 'XXXX',
    driver : 'oracle.jdbc.driver.OracleDriver'
]

// connexió fora com var de la closure...
def sql = Sql.newInstance("${db.url}", "${db.username}", "${db.password}","${db.driver}")

def log = new File("basicInfoFilePath")
log.text = ''
def serials = new File("UniqueSerialsFilePath")
serials.text = ''

def serialsList = []
def serialMap = [:]

sql.eachRow("select distinct(cert_digital),nom from aoc_nt_entitat where cert_digital is not null",{ 

    def ens = it.nom
    log.append("Certificat de l'ens ${ens}\n")
    def certificate = getCertificate(it.cert_digital,log)
    try{
        certificate.checkValidity()
    }catch(Exception e){
        log.append("El certificat ha expirat o encara no és valid... el descartem ${e.getMessage()}\n")
    }
    
    
    String serial = certificate.getSerialNumber().toString(16);    
    
    log.append("serial: ${serial}")
    log.append("-----------------------------\n")
    serialsList << serial
    serialMap << [serial:ens]
    
});

serialsList.unique()
serialsList.each{
    serials << "${it}\n"
}
return serialsList

// encara el certificat per obtenir el serial
// a BDD tenim certs DER en b64 i certs Zipats en base64
def getCertificate(certStr,log){
    // netegem els possibles salts de linea de la codificació en base64
    certStr = certStr.replaceAll("\r", "").replaceAll("\n", "");
    
    log.append("CERT: $certStr\n")
    
    InputStream is = new ByteArrayInputStream(certStr.decodeBase64())
    // a la BDD hi
    if(!certStr.startsWith('M')){
        // és un gzip
        log.append("És un gzip...\n")
        is = new GZIPInputStream(new ByteArrayInputStream(certStr.decodeBase64()))
    }
   
    CertificateFactory cf = CertificateFactory.getInstance('X.509')
    X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
    return cert
}
