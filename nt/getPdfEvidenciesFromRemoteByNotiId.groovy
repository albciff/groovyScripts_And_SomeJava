/**
* @author albciff
* Genera el path a partir d'un id de noti i el recupera de la maquina en remote i el copia en local
**/
import java.security.MessageDigest

@Grab('org.hidetake:groovy-ssh:2.0.0')
@GrabExclude('org.codehaus.groovy:groovy-all')

// servei
def ssh = org.hidetake.groovy.ssh.Ssh.newService()

ssh.settings {
     knownHosts = allowAnyHosts
}

ssh.remotes {
    entorn {
      host = 'XXXXXXXXX'
      user = 'userName'
      password = 'password'
    }
}

def idNotificacio = 3318665 // id de la noti en questió

def prefix = 'ZC'
def lang = '' // buit català, '_ES' o '_OC' per castellà i aranés respectivament
prefix += lang
MessageDigest sha1 = MessageDigest.getInstance("SHA1")
def hash = sha1.digest("${prefix}_${idNotificacio}".getBytes('UTF-8')).encodeHex().toString()
def path = hash.take(2) + File.separator + hash.substring(2,4) + File.separator + prefix + '_' + hash
println path // sobre /NT30/repCiutada

ssh.run {
    session(ssh.remotes.entorn) {
      get from: "/apps/aoc/APP/NT30/repCiutada/${path}", into: "C:/temp/PDFs/"
    }
}
