/**
 * Carrega totes les properties d'un fitxer com a properties
 * del projecte SOAPUI. L'opció que proporciona la GUI sembla que per 
 * la versió 5.2.1 (no se si les altres) no va fina, i només actualitza 
 * el valor de les exhistents però no càrrega les noves.
 * @albciff
**/
Properties properties = new Properties()
File propertiesFile = new File('./Backend - SOAPUI Tests/properties/dev_properties')
propertiesFile.withInputStream {
    properties.load(it)
}

def prj = testCase.testSuite.project

properties.each{ k,v ->
	prj.setPropertyValue(k,v);
}
