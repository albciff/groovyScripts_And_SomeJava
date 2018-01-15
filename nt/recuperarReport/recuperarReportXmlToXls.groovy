/**
/* author @albciff
/* Script per convertir els xmls de resposta de recuperarReport en un XLS en un format concret.
**/
@Grab('com.jameskleeh:excel-builder:0.4.2')

import com.jameskleeh.excel.ExcelBuilder
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.awt.Color
import groovy.io.FileType

def pathToReportsDiarisFormatXML = "C:/temp/reportPerData/"

File dir = new File(pathToReportsDiarisFormatXML)

dir.eachFileRecurse (FileType.FILES) { fileXml ->
    if(fileXml.name.endsWith('.xml')){
        convertXmlToXls(fileXml, new FileOutputStream(fileXml.absolutePath + ".xls"))
    }
}


def convertXmlToXls(def fileIn, def fileOut){

    def slurper = new XmlSlurper().parseText(fileIn.getText('Cp1252'))
    
      slurper.Report.Capcalera.children().each{
                println it.name()
                println it
      }
    
    XSSFWorkbook workbook = ExcelBuilder.build {
     sheet {
            columns {
                slurper.Report.Capcalera.children().each{
                    column(it.name(), it.name())//,[wrapped: true])
                }
            }
    
            row{        
                slurper.Report.Capcalera.children().each{
                    cell(it)
                }
            }
            
            skipRows(1)
            
            row{
                cell("IdNotificacio")
                cell("Referencia")
                cell("TipusObjecte")
                cell("DiesExpiracio")
                cell("Estat")
                cell("DescripcioEstat")
                cell("DataActualitzacio")
                cell("DataRegistre")
                cell("NumeroRegistreSortida")
                cell("NIF/PASSAPORT")
                cell("NomComplet")
                cell("CIF/VAT")
                cell("RaoSocial")
                cell("CodiBackOffice")
                [backgroundColor: Color.BLUE]
            }
    
            slurper.Report.Dades.each{        
                row(it.DadesNotificacio.IdNotificacio,
                    it.DadesNotificacio.Referencia,
                    it.DadesNotificacio.TipusObjecte,
                    it.DadesNotificacio.DiesExpiracio,
                    it.DadesEstat.Estat,
                    it.DadesEstat.DescripcioEstat, 
                    it.DadesEstat.DataActualitzacio,
                    it.DadesRegistre.DataRegistre,
                    it.DadesRegistre.NumeroRegistreSortida,
                    it.DadesActor.PersonaFisica?.DocumentIdentificatiu?.NIF !='' ? it.DadesActor.PersonaFisica?.DocumentIdentificatiu?.NIF : it.DadesActor.PersonaFisica?.DocumentIdentificatiu?.PASSAPORT,
                    it.DadesActor.PersonaFisica?.NomComplet,
                    it.DadesActor.PersonaJuridica?.DocumentIdentificatiu?.CIF != '' ? it.DadesActor.PersonaJuridica?.DocumentIdentificatiu?.CIF : it.DadesActor.PersonaJuridica?.DocumentIdentificatiu?.VAT,
                    it.DadesActor.PersonaJuridica?.RaoSocial,
                    it.CodiBackOffice)
            }
        }
    }
    
    def sheet = workbook.getSheetAt(0)
    
    // numero de columens
    def columns = 14
    (0..columns).each {
        sheet.autoSizeColumn(it)
    }
    
    workbook.write(fileOut)
}
