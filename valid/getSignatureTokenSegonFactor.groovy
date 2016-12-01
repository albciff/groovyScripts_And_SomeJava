/**
* Script d'exemple per a fer un post contra VALID per a recuperar un codi de signatura per tal de poder
* realitzar la signatura amb segon factor per part del usuari.
* 
* @albciff
**/
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1')
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.*
import groovy.transform.ToString

// POGO resposta JSON del servei per a recuperar un codi de signatura per a valid
@ToString
class SignatureResponse extends Expando implements Serializable {
    
    String status
    String signature_code
    String error
    
    def propertyMissing(String name, args) {
        // per si la resposta de valid té camps diferents als del
        // bean però el volem crear amb [map] as Bean.
     }
}


// util per a fer hashos en b64
def hashFn = { text,algorithm ->  
  java.security.MessageDigest.getInstance(algorithm)   
    .digest(text.getBytes("UTF-8")).encodeBase64().toString()  
}  

// parametres de la crida
def accessToken = 'XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX' 
def urlBase = 'https://identitats-pre.aoc.cat'
def pathSignature = '/serveis-rest/initBasicSignature'
def redirectUri = 'XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX'

// definim uns documents d'exemple per a signar
def documents = [[name:'test.txt',algorithm:'SHA-256',hash : hashFn('desobediencia','SHA-256')],
[name:'test2.txt',algorithm:'SHA-256',hash : hashFn('cultura','SHA-256')]]

//post
def http = new HTTPBuilder(urlBase)
def responseValid = http.request(POST){

            uri.path = pathSignature
            body = [accessToken: accessToken,
                redirectUri: redirectUri,
                documents : documents
                ]
            println body
            requestContentType = ContentType.JSON
            headers.Accept = 'application/json'

    response.success = { resp, json ->
        if(resp.status != 200){
            println("[getSignatureCode] Error recuperant el signatureCode de valid per al token: $accessToken")
            println("[getSignatureCode] Resposta de valid: $json")
            return null
        }
        println("[getSignatureCode] Recuperada resposta de valid ${json}")
        return json
    }

    response.failure = { resp ->
        println("[getSignatureCode] Error recuperant el signatureCode de valid per al token: $accessToken")
        return null
    }
}

// parseig de la resposta
def signatureResponse = responseValid as SignatureResponse
println signatureResponse
