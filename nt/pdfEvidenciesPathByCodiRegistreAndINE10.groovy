import java.security.MessageDigest
import groovy.sql.Sql

// db config...
def dbDev = [
    url : 'XXXXXXXX',
    username : 'XXXX',
    password : 'XXXX',
    driver : 'oracle.jdbc.driver.OracleDriver'
]

def dbPre = [
    url : 'XXXXXXXX',
    username : 'XXXX',
    password : 'XXXX',
    driver : 'oracle.jdbc.driver.OracleDriver'
]

def dbPro = [
     url : 'XXXXXXXX',
    username : 'XXXX',
    password : 'XXXX',
    driver : 'oracle.jdbc.driver.OracleDriver'
]

def db = dbDev

def referencia = 'S/001286-2017'
def ine10 = '9821920002'

// connexió fora com var de la closure...
def sql = Sql.newInstance("${db.url}", "${db.username}", "${db.password}","${db.driver}")

def idNotificacio = sql.firstRow("""select id_notificacio from aoc_nt_notificacions n
left join aoc_nt_tramesa t on t.id_tramesa = n.id_tramesa
left join aoc_nt_entitat e on t.id_entitat = e.id_entitat
left join aoc_nt_entitat ep on ep.id_entitat = e.id_entitat_pare
where n.numero_registre = ? and (e.ine10 = ? or ep.ine10 = ?)""",[referencia,ine10,ine10])?.id_notificacio

if(!idNotificacio) return "NO S'HA TROBAT RESULTAT PER AQUESTS PARAMS"

println idNotificacio

def prefix = 'ZC'
MessageDigest sha1 = MessageDigest.getInstance("SHA1")
def hash = sha1.digest("${prefix}_${idNotificacio}".getBytes('UTF-8')).encodeHex().toString()
def path = hash.take(2) + File.separator + hash.substring(2,4) + File.separator + prefix + '_' + hash

println "/apps/aoc/APP/NT30/repCiutada/${path}"
