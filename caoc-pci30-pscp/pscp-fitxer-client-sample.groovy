/**
* Script d'exemple per a fer un post contra CAOC-PCI30-PSCP
* 
* @albciff
**/
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1')
@Grab('org.apache.httpcomponents:httpmime:4.2.1')
import org.apache.http.entity.mime.content.* 
import org.apache.http.entity.mime.*
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.*
import groovy.transform.ToString

// POGO resposta JSON del servei CAOC-PSCP
@ToString
class RespostaPscp extends Expando implements Serializable {
    
    String data;
    String id;
    String notifications;
    
    def propertyMissing(String name, args) {
        // per si la resposta de valid té camps diferents als del
        // bean però el volem crear amb [map] as Bean.
     }
}    

// parametres de la crida
def urlBase = 'https://serveis3-pre.app.aoc.cat'
def md5 = new Date().toString().md5()
def path = "/CAOC-PCI30-PSCP/resources/rest/Fitxer/${md5}"
def fileadjunt = new File("C:/Users/aciffone/Desktop/Adjunt.pdf")

println path

// post
def http = new HTTPBuilder(urlBase)
def responsePscp = http.request(POST) { request ->

    uri.path = path

    requestContentType = ContentType.BINARY
    headers.Accept = 'application/json'
    headers.'Content-disposition' = 'attachment;filename=batman'
    
    /*
    Fer-ho així seria seguament el més correcte, però el servei ho espera al body...
    def content = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE)     
    content.addPart(fileadjunt.name, new InputStreamBody(fileadjunt.newInputStream(), fileadjunt.name))
    request.entity = content
    */
    body = fileadjunt.newInputStream()

    response.success = { resp, json ->
        if(resp.status != 200){
            println(" Error recuperant la resposta de PSCP: $resp")
            println("Resposta completa del servei: $json")
            return null
        }
        println("Recuperada resposta del servei de PSCP ${json}")
        return json
    }

    response.failure = { resp ->
        println "My response handler got response: ${resp.statusLine}"
        println("Error $resp")
        return null
    }
}

// parseig de la resposta
def resposta = responsePscp as RespostaPscp
println resposta
