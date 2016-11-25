/**
* Script d'exemple per a fer un post contra VALID per a recuperar el access token de valid a partir del authoritzation code.
* 
* @albciff
**/
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1')
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.*
import groovy.transform.ToString

// POGO resposta JSON del servei Valid amb el token d'autorització. 
@ToString
class AccessToken extends Expando implements Serializable {
    
    String access_token;
    String refresh_token;
    String expires_in;
    String token_type;
    String client_id;
    String error;
    String authorization_code;
    
    def propertyMissing(String name, args) {
        // per si la resposta de valid té camps diferents als del
        // bean però el volem crear amb [map] as Bean.
     }
}

// parametres de la crida
def authoritzationCode = 'XXXXXXXXXXXXXXXX'
def urlBase = 'https://identitats-pre.aoc.cat'
def pathAccessToken = '/o/oauth2/token'
def clientId = 'XXXXXXXXXXXXXXXX'
def clientSecret = 'XXXXXXXXXXXXXXXXXXXXXXXXXX'
def redirectUri = 'XXXXXXXXXXXXXXXXXXXXXX'

// post
def http = new HTTPBuilder(urlBase)
def responseValid = http.request(POST){

    uri.path = pathAccessToken
    def params = [code: authoritzationCode,
        client_id: clientId,
        client_secret : clientSecret,
        redirect_uri : redirectUri,
        grant_type : 'authorization_code']
    body = params.collect{k,v -> "$k=$v"}.join('&')
    requestContentType = ContentType.URLENC
    headers.Accept = 'application/json'

    response.success = { resp, json ->
        if(resp.status != 200){
            println("[getAccesToken] Error recuperant el authoritzation_code de valid per al token: $authoritzationCode")
            println("[getAccesToken] Resposta de valid: $json")
            return null
        }
        println("[getAccesToken] Recuperada resposta de valid ${json}")
        return json
    }

    response.failure = { resp ->
        println("[getAccesToken] Error recuperant el authoritzation_code de valid per al token: $authoritzationCode")
        return null
    }
}

// parseig de la resposta
def accessToken = responseValid as AccessToken
println accessToken
