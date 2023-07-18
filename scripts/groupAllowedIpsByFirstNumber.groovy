/**
* En un fitxer on cada linia és una IP CDIR, torna un fitxer on cada linia és el llistat de IPs amb el mateix primer nombre precedit d'allowed from per
* a poder-ho afegir a un context de config d'apache httpd
**/
def ipList = new File("iplist.txt").text.readLines()
def ipListGroupByFirstNumber = new File("iplist_grouped.txt")

ipList.groupBy{ it.split('\\.')[0] }.each{ k,iplist ->
    ipListGroupByFirstNumber << "Allow from " + iplist.join(" ") + "\n"
}
