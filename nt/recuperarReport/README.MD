# Fallback excel canvis d'estat diari
Per a generar un excel de canvi d'estat diari. Es pot realitzar una petició de recuperarReport per a l'organisme i data desitjat (veure [petició](https://github.com/albciff/groovyScripts_And_SomeJava/blob/master/nt/recuperarReport/sampleRequestRecuperarReport.xml)), 
recuperar la resposta aplicant el xpath `//*:RespostaRecuperarReport` a aquesta, i convertint el XML resultant a XLS amb el [aquest script groovy](https://github.com/albciff/groovyScripts_And_SomeJava/blob/master/nt/recuperarReport/recuperarReportXmlToXls.groovy).
