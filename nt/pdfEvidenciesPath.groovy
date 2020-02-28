/**
* Script path pdf d'evidències de enotum a disc...
**/
import java.security.MessageDigest

def prefix = 'ZC' // 'ZA' per al document d'empleat. El document 'ZA' només hi ha català per tant lang sempre buit
def lang = '' // buit català, '_ES' o '_OC' per castellà i aranés respectivament
def idNotificacio = 1397905 // id de la noti en questió
prefix += lang
MessageDigest sha1 = MessageDigest.getInstance("SHA1")
def hash = sha1.digest("${prefix}_${idNotificacio}".getBytes('UTF-8')).encodeHex().toString()
def path = hash.take(2) + File.separator + hash.substring(2,4) + File.separator + prefix + '_' + hash
if(prefix.startsWith('ZC')){
  println "NT30${File.separator}repCiutada${File.separator}${path}"
}else{
  println "NT30${File.separator}repEmpleat${File.separator}${path}"
}
