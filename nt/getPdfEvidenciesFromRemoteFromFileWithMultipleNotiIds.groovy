/**
* @author albciff
* Llegeix un fitxer amb id noti per linea (C:/temp/fileNotis.txt), i recupera l'evidencia pdf per cada id de noti, 
* copia el zip de remot a disc i descomprimeix el pdf
*
*  Per recuperar PDF d'estat final (ciutadà) prefix ZC
*  Per recuperar PDF de comunicacions en estat NO final (admin) prefix ZA
**/
import java.security.MessageDigest
import java.util.zip.*

@Grab('org.hidetake:groovy-ssh:2.0.0')
@GrabExclude('org.codehaus.groovy:groovy-all')

// file amb els ids de les notis per cada linea
def fileIdNotis = new File("C:/temp/fileNotis.txt")

def decompressGzip(File input, File output) {
	GZIPInputStream zip = new GZIPInputStream(new FileInputStream(input))
	FileOutputStream out = new FileOutputStream(output)
	byte[] buffer = new byte[8192];
	int len;
	while((len = zip.read(buffer)) != -1){
		out.write(buffer, 0, len);
	}
	
	zip.close();
	out.close();
}

// servei
def ssh = org.hidetake.groovy.ssh.Ssh.newService()

ssh.settings {
	 knownHosts = allowAnyHosts
}

ssh.remotes {
	entorn {
	  host = 'XXXXXXXXX'
	  user = 'hope'
	  password = 'no hate'
	}
}

def lines = fileIdNotis.readLines()
lines.each { String idNotificacio ->
	
	idNotificacio = idNotificacio.trim()
	def prefix = 'ZC' // use ZA per administració (pdf diposit p.e per comunicacions)
	def lang = '' // buit català, '_ES' o '_OC' per castellà i aranés respectivament
	prefix += lang
	MessageDigest sha1 = MessageDigest.getInstance("SHA1")
	def hash = sha1.digest("${prefix}_${idNotificacio}".getBytes('UTF-8')).encodeHex().toString()
	def path = hash.take(2) + '/' + hash.substring(2,4) + '/' + prefix + '_' + hash
	println "NOTI ${idNotificacio} > PATH: ${path}" // sobre /NT30/repCiutada o /NT30/repempleat
	

	def zipEvidencia = "C:/temp/PDFs/${idNotificacio}_evidencia.zip"
	def pdfEvidencia = new File("C:/temp/PDFs/${idNotificacio}_evidencia.pdf")
	
	def remotePathBase = '/apps/aoc20/APP/NT30/repCiutada/'
	if(prefix == 'ZA'){
	    remotePathBase = '/apps/aoc20/APP/NT30/repEmpleat/'
	}
	
	ssh.run {
		session(ssh.remotes.entorn) {
		  get from: "${remotePathBase}${path}", into: zipEvidencia
		}
	}
	
	decompressGzip(new File(zipEvidencia),pdfEvidencia)
}
