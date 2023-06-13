import com.sun.net.httpserver.HttpServer
/**
* Aixeca un server al PORT amb el context (/callback) i printa el atribut code en un json.
* TODO: Fer-lo més generic (printa un json amb el key : value de tots els parametres
**/
int PORT = 8080
HttpServer.create(new InetSocketAddress(PORT), 0).with {
    println "Server is listening on ${PORT}, hit Ctrl+C to exit."    
    createContext("/callback") { http ->	
		def query = http.getRequestURI().getRawQuery()
		
		Map<String, String> result = new HashMap<>();
		for (String param : query.split("&")) {
			
			param = java.net.URLDecoder.decode(param, "UTF-8")
			
			String[] entry = param.split("=",2);
			if (entry.length > 1) {
				println entry
				result.put(entry[0], entry[1]);
			}else{
				result.put(entry[0], "");
			}
		}
		
        http.responseHeaders.add("Content-type", "application/json")
        http.sendResponseHeaders(200, 0)
		http.responseBody.withWriter { out ->
            out << '{ "code" : "' + result.get('code') + '" }'
        }
		
        println "Peticio des de: ${http.remoteAddress.hostName} al port: ${http.remoteAddress.holder.port}"
    }
    start()
}
