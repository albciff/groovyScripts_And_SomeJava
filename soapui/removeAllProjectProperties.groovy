/** 
 *  Elimina totes les properties del projecte d'una vegada.
 *  L'opció d'eliminar properties va una per una i demana confirmació amb un prompt
 *  cada vegada, cosa que és terriblement disfuncional.... scripts to the rescue!
 *   @albciff
**/
def prj = testCase.testSuite.project
prj.properties.each{ n,v ->
	prj.removeProperty(n)
}
