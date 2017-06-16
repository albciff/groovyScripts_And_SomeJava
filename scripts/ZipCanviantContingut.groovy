/**
* Pateja un directori i revisa tots els fitxers i canvi el contingut del *log* canviant debug per error.
* Exemple per modifcar continguts dels zips.
**/
import java.util.zip.*
import groovy.io.FileType

def earDir = new File("C:/temp/EARS/")
def subfolderResult = 'MODIFIED'

// mk dir per si de cas
new File("${earDir.absolutePath}/${subfolderResult}").mkdir()

// per cada fitxer en el directori
earDir.eachFileRecurse(FileType.FILES) { file ->
    
    println "ANEM A MODIFICAR ${file.name}"
    debugToError(file, "${file.parent}/${subfolderResult}/${file.name}")
    println '****************************'    
}

def debugToError(zipIn,pathOut){
    def zip = new ZipFile(zipIn)
    def zipTemp = new File(pathOut)
    zipTemp.deleteOnExit()
    def zos = new ZipOutputStream(new FileOutputStream(zipTemp))
    
    for(e in zip.entries()) {
        if(e.isDirectory()){
          // directoris res de res  
        }
        else if(!(e.name ==~ /.*log.*/)) {
            zos.putNextEntry(e)
            zos << zip.getInputStream(e).bytes
        } else {
            println "MODIFY: ${e.name}"
            zos.putNextEntry(new ZipEntry(e.name))
            zos << new String(zip.getInputStream(e).bytes,'UTF8').replaceAll('(?i)debug','ERROR').getBytes('UTF8')
        }
        zos.closeEntry()
    }
    
    zos.close()
    zipIn.delete()
    zipTemp.renameTo(zipIn)
}
