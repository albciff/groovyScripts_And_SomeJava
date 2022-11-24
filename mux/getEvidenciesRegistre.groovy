/**
	@albciff:
		A partir d'un id de transacció recupera les respostes dels registres i les desa a disc a: C:/tmp/idTransaccio (outputdir)
			* entrada - principal
			* entrada - auxiliar
			* sortida - principal
			* sortida - auxiliar
**/

import groovy.sql.Sql
import java.util.zip.*

// Id de transacció sobre el que es fara la cerca
def idTrans = '0e-8055-786-781c5f-1'
// format to muxv3 db identifier
idTrans = idTrans.toUpperCase().replace('-','')

// BDD MUXv3 PRO 
def dbPro = [
	url : 'jdbc:oracle:thin:@IP:PORT/SERVICENAME',
	username : 'MUXV3',
	password : 'XXXXXXXX',
	driver : 'oracle.jdbc.driver.OracleDriver'
]

def db = dbPro

// connexió fora com var de la closure...
def sql = Sql.newInstance("${db.url}", "${db.username}", "${db.password}","${db.driver}")

// query sobre MUXv3
def qry = "select * from mux_v3_transaccio where id_transaccio='${idTrans}'"

// metode per guardar la resposta a disc
def responseToFile(def list, def idTrans, def reg){
	
	// output dir on aniran les respostes
	def outputDir = "C:/tmp/"
	
	// revisem que existeixi resposta per aquell registre
	if(list){
		def resp = list[0][0]
		InputStream stream = new ByteArrayInputStream( resp.decodeBase64())
		
		def path = outputDir + File.separator + "${idTrans}"
		new File(path).mkdirs()
		
		unzipFromStream(stream, outputDir + File.separator + idTrans + File.separator + reg)

	}
}

// metode per a fer unzip de les respostes
def unzipFromStream(def stream, def fileOutputPath){
	
	GZIPInputStream zip = new GZIPInputStream(stream)
	FileOutputStream out = new FileOutputStream(fileOutputPath)
	byte[] buffer = new byte[8192];
	int len;
	while((len = zip.read(buffer)) != -1){
		out.write(buffer, 0, len);
	}

	zip.close();
	out.close();
}

// fem la query
sql.eachRow(qry,{ 
	
	// parsegem la resposta en json
	def slurper = new groovy.json.JsonSlurper()
	def j = slurper.parseText(it.dades?.asciiStream?.text)

	// recuperem cadascun dels assentaments (algun potser null per això evitem NPE amb el nullsafeoperator)
	def entPrin = j.assentaments?.entrada?.principal?.evidencies?.response
	def entSec = j.assentaments?.entrada?.auxiliar?.evidencies?.response
	def sortPrin = j.assentaments?.sortida?.principal?.evidencies?.response
	def sortSec = j.assentaments?.sortida?.auxiliar?.evidencies?.response

	// desem a disc les que toquin
	responseToFile(entPrin,idTrans,'entrada - principal.xml')
	responseToFile(entSec,idTrans,'entrada - auxiliar.xml')
	responseToFile(sortPrin,idTrans,'sortida - principal.xml')
	responseToFile(sortSec,idTrans,'sortida - auxiliar.xml')    

});
