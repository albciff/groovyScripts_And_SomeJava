/**
* @albciff: Gradle task to deploy on tomcats... traduit a groovy.
* No faig servir el plugin de cargo per evitar els OOM per el PermGen.
* A la brava atura els tomcats, esborra work, i temp, copia el nou war esborra l'antic
* i arrenca de nou.
**/
@Grab('org.hidetake:groovy-ssh:2.0.0')
@GrabExclude('org.codehaus.groovy:groovy-all')

// servei
def ssh = org.hidetake.groovy.ssh.Ssh.newService()

// any host...
ssh.settings {
     knownHosts = allowAnyHosts
}

// conf...
ssh.remotes {
    frontaldev {
      host = 'XXXXXXXXX'
      user = 'userName'
      password = 'password'
    }
}

// groovy dsl style... això ja t'ho explicaré
def tomcat6baseHome = '/apps/tomcat6'
def tomcat6bisbaseHome = '/apps/tomcat6-bis'
def tomcat6DEVHome = "${tomcat6baseHome}/webapps"
def tomcat6bisDEVHome = "${tomcat6bisbaseHome}/webapps"
def warName = 'myApp'
def warOriginPath = '/path/to/myApp.war'

ssh.run {
	session(remotes.frontaldev) {
	
		println 'STOP TOMCAT6 DEV'
		execute('sudo /etc/init.d/tomcat6 stop')
		
		println 'STOP TOMCAT6-BIS DEV'
		execute('sudo /etc/init.d/tomcat6-bis stop')
		
		println 'Copiar el war en el tomcat6'
		put(warOriginPath,"${tomcat6DEVHome}/${warName}.war.new")
		
		println 'Copiar el war en el tomcat6-bis'
		put(warOriginPath,"${tomcat6bisDEVHome}/${warName}.war.new")
		
		println 'Eliminem el war antic del tomcat6'
		execute("rm ${tomcat6DEVHome}/${warName}.war")
		
		println 'Eliminem el war antic del tomcat6-bis'
		execute("rm ${tomcat6bisDEVHome}/${warName}.war")
		
		println 'Eliminem la carpeta deployada del tomcat6'
		execute("rm -r ${tomcat6DEVHome}/${warName}")
		
		println 'Eliminem la carpeta deployada del tomcat6-bis'
		execute("rm -r ${tomcat6bisDEVHome}/${warName}")
		
		println 'Eliminem la carpeta deployada del work'
		execute("rm -r ${tomcat6baseHome}/work/Catalina/localhost/${warName}")

		println 'Eliminem la carpeta deployada del work'
		execute("rm -r ${tomcat6bisbaseHome}/work/Catalina/localhost/${warName}")
		
		println 'Activem el nou war... al tomcat6'
		execute("mv ${tomcat6DEVHome}/${warName}.war{.new,}")
		
		println 'Activem el nou war... al tomcat6-bis'
		execute("mv ${tomcat6bisDEVHome}/${warName}.war{.new,}")
		
		println 'AIXEQUEM TOMCAT6 DEV'
		execute('sudo /etc/init.d/tomcat6 start')
		
		println 'AIXEQUEM TOMCAT6-BIS DEV'
		execute('sudo /etc/init.d/tomcat6-bis start')
		
		println 'FET!!!! Yeaaah ;)'
	}
}