/**
* Generate a union of truststore sets (A ∪ B), from A set truststore and B set truststotre
*
* @author albciff
* http://stackoverflow.com/users/1218618/albciff
*
**/
import java.security.KeyStore;

// A set truststore properties
def trustStoreAPath = 'path/to/setA'
def pwsTsA = 'changeit'
def tsAType = 'JKS'

// keystore where missing certs will be copied :P
def trustStoreBPath = 'path/to/setA'
def pwsTsB = 'changeit'
def tsBType = 'JKS'

def tsA = KeyStore.getInstance(tsAType)
tsA.load(new FileInputStream(trustStoreAPath),pwsTsA.toCharArray())

def tsB = KeyStore.getInstance(tsBType)
tsB.load(new FileInputStream(trustStoreBPath),pwsTsB.toCharArray())

// for each alias in set A
tsA.aliases().each{ alias ->
    // get the certificate
    def cert = tsA.getCertificate(alias)
    // if certificate not exists in the destination set B add it
    if(tsB.getCertificateAlias(cert) == null){
        tsB.setCertificateEntry(alias, cert)
    }
}

// save the union of set in a new path of truststore sets (A ∪ B)
tsB.store(new FileOutputStream('path/toSave/unionSets'),'changeit'.toCharArray())
