/**
* Pateja un fitxer zip i canvi el contingut del *log* canviant debug per error.
* Exemple per modifcar continguts dels zips
**/
import java.util.zip.*

def zipIn = new File('C:/Projectes/AOC/eNotum-v3/Backend/build_desenvolupament/prjNTCore.ear')
def zip = new ZipFile(zipIn)
def zipTemp = new File("C:/temp/prjNTCoreModified.ear")
zipTemp.deleteOnExit()
def zos = new ZipOutputStream(new FileOutputStream(zipTemp))

for(e in zip.entries()) {
    if(e.isDirectory()){
      // directoris res de res  
    }
    else if(!e.name ==~ /.*log.*/) {
        zos.putNextEntry(e)
        zos << zip.getInputStream(e).getBytes('UTF-8')
    } else {
        zos.putNextEntry(new ZipEntry(e.name))
        zos << new String(zip.getInputStream(e).bytes,'UTF8').replaceAll('(?i)debug','ERROR').getBytes('UTF8')
    }
    zos.closeEntry()
}

zos.close()
zipIn.delete()
zipTemp.renameTo(zipIn)
