/**
* @author albciff
* Exemple de GET amb capçaleres HTTP.
* Aquest exemple servei per a recuperar el token ID del servei del signador
**/
@Grapes(
    @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1')
)

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import java.text.SimpleDateFormat
import java.util.Date
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

def serveiUrl = 'http://signador-pre.aoc.cat/signador/initProcess'

def http = new HTTPBuilder(serveiUrl)

http.request( GET, JSON ) { req ->
        
   def secretKey = 'legalizeit'
   def domini = 'http://ajuntament.cat'
   def data = new SimpleDateFormat("dd/MM/yyyy HH:mm").format( new Date() )
    
   Mac mac = Mac.getInstance("HmacSHA256")   
   SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256")
   mac.init(secretKeySpec)
   def digest = mac.doFinal( ( "${domini}_${data}" ).getBytes())
   
    // set headers
    headers.origin = domini
    headers.date = data
    headers.Authoritzation = "SC_${digest.encodeBase64().toString()}"

    // handler de la resposta
    response.success = { resp, json ->
        // printem la resposta
        println json
    }
     
    // handlers dels possibles errors
    response.'404' = { resp ->
        println 'No trobat... :('
        // printem capçaleres
        response.getAllHeaders().each{
            println it
        }
    }
        
    response.'503' = { resp ->
        println 'Temporalment fora de servei... :_('
        // printem capçaleres
        response.getAllHeaders().each{
            println it
        }
    }
    
    response.'500' = { resp ->
        println 'Error intern del servidor... :___('
        // printem capçaleres
        response.getAllHeaders().each{
            println it
        }
    }

    // etc...
}