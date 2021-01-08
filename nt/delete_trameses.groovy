import groovy.sql.Sql

def dbPro = [
    url : 'jdbc:oracle:thin:@XXXXXX:XXX/XXX',
    username : 'X',
    password : 'X',
    driver : 'oracle.jdbc.driver.OracleDriver',
]

def db = dbPro

def nf = new File("C:/temp/a_notificacions/notificacions_${new Date().format('dd.MM.yyyy HH.mm.ss')}.txt")
def dpf = new File("C:/temp/a_notificacions/documents_path_${new Date().format('dd.MM.yyyy HH.mm.ss')}.txt")

// connexió fora com var de la closure...
def sql = Sql.newInstance("${db.url}", "${db.username}", "${db.password}","${db.driver}")

sql.eachRow 'select id_tramesa from aoc_nt_tramesa where id_entitat = 122 and ROWNUM  < 2000',{ tramesa ->
    println tramesa.id_tramesa
    sql.eachRow "select * from aoc_nt_notificacions where id_tramesa = ${tramesa.id_tramesa}",{ notificacio ->
        nf << "${notificacio.id_notificacio} \n"

        sql.execute "DELETE FROM AOC_NT_ACTOR where id in (${notificacio.id_visualitzador}, ${notificacio.id_signatari})";
        sql.eachRow "select * from nt30.aoc_nt_destinataris where id_notificacio = ${notificacio.id_notificacio}", { destinatari ->
            // 1st DELETE AOC_NT_DEST_TELEFON
            sql.execute "DELETE FROM AOC_NT_DEST_TELEFON where id_destinatari = ${destinatari.id_destinatari}"
			
            // 2nd DELETE AOC_NT_DEST_EMAIL
            sql.execute "DELETE FROM AOC_NT_DEST_EMAIL where id_destinatari = ${destinatari.id_destinatari}"
			
            // 3rd DELETE AOC_NT_PERSONA_AVIS
            sql.execute "DELETE FROM AOC_NT_PERSONA_AVIS where id_destinatari = ${destinatari.id_destinatari}"
			
            // 4th FINALMENT ESBORREM EL DESTINATARIS
            sql.execute "DELETE FROM AOC_NT_DESTINATARIS where id_destinatari = ${destinatari.id_destinatari}"
        }
        
        // 3rd println "DELETE AOC_NT_ACTOR_PAPER
        sql.execute "DELETE FROM AOC_NT_ACTOR_PAPER where id_notificacio = ${notificacio.id_notificacio}"
        
        // 4th println "DELETE AOC_NT_REGISTRE_CANVI_CANAL
        sql.execute "DELETE FROM AOC_NT_REGISTRE_CANVI_CANAL where id_notificacio = ${notificacio.id_notificacio}"
        
        // 5th println "DELETE AOC_NT_NOTIFICACIO_METADATA
        sql.execute "DELETE FROM AOC_NT_NOTIFICACIO_METADATA where id_notificacio = ${notificacio.id_notificacio}"
        
        // 6th println "DELETE AOC_NT_EVIDENCIES
        sql.execute "DELETE FROM AOC_NT_EVIDENCIES where id_notificacio = ${notificacio.id_notificacio}"
        
        // 7th println "DELETE AOC_NT_DIES_AVIS
        sql.execute "DELETE FROM AOC_NT_DIES_AVIS where id_notificacio = ${notificacio.id_notificacio}"
        
        // 8th println "DELETE AOC_NT_REL_ETIQ_NOTIF
        sql.execute "DELETE FROM AOC_NT_REL_ETIQ_NOTIF where id_notificacio = ${notificacio.id_notificacio}"
        
        // 9th FINALMENT ESBORREM LA NOTIFICACIO
        sql.execute "DELETE FROM AOC_NT_NOTIFICACIONS WHERE id_notificacio = ${notificacio.id_notificacio}"
    }

    // 2nd DELETE DOCUMENTS
    sql.eachRow "SELECT * FROM AOC_NT_DOCUMENTS WHERE ID_TRAMESA = ${tramesa.id_tramesa}",{ document ->
        dpf << "${document.path_document} \n"
    }
    
    sql.execute "DELETE AOC_NT_DOCUMENTS WHERE ID_TRAMESA = ${tramesa.id_tramesa}"
			
    // 3rd DELETE TRAMESA
    sql.execute "DELETE AOC_NT_TRAMESA WHERE ID_TRAMESA =  ${tramesa.id_tramesa}"
}
