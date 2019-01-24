/**
* @author albciff
* Exemple de POST passant com a request una petició JSON
* Aquest exemple concretament serveix per a enviar un post amb la configuració
* de la signatura desitjada al servei del signador
**/
@Grapes(
    @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1')
)

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

def serveiUrl = 'http://signador-pre.aoc.cat/signador/startSignProcess'
def http = new HTTPBuilder(serveiUrl)

http.request( POST, JSON ) { req ->

    def domini = 'http://ajuntament.cat'
    // set headers origin
    headers.origin = domini
    
    // montem el body de la petició
    body = '''{
                    "callbackUrl" : "/signador/demo/receiveSignature",
                    "token" : "622e7ab8-468d-47b9-b91c-559f7efb1af1",
                    "descripcio" : "Hash de proves",
                    "applet_cfg" :
                    {
                        "keystore_type":"0",
                        "signature_mode":"12",
                        "doc_type":"3",
                        "doc_name" : "Itaca.hash.bin",
                        "document_to_sign":"x/jw/6C/1DDqxF6BDi8mNiYEoc0=",
                        "hash_algorithm" : "SHA-1"
                }
            }'''

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
