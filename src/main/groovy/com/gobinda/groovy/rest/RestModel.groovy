package com.gobinda.groovy.rest

import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import groovyx.net.http.RESTClient

import com.gobinda.groovy.config.YamlConfig

class RestModel {

	// Access catalyst api
	/*private static void callRestApi(String callbackUrl){
	 def client = new RESTClient(callbackUrl);
	 client.setHeaders(Accept: 'application/json')
	 try{
	 def res = client.get(path : '/instances');
	 assert res.status == 200;  // HTTP response code; 404 means not found, etc.
	 println res.getData().instances.name;
	 }catch(HttpHostConnectException e){
	 println "Encountered Error: "+e;
	 }
	 }*/

	private static def createJenkinsJob(def properties,def subURL,def xml){
		def client = new RESTClient(properties.host);
		def token = (properties.userName+":"+properties.password).getBytes('iso-8859-1').encodeBase64();
		client.setHeaders([Authorization: "Basic "+token])
		try{
			def res = client.get(path : '/crumbIssuer/api/json');
			assert res.status == 200;
			// POST
			def client1 = new RESTClient(properties.host+""+subURL);
			client1.setHeaders([Authorization: "Basic "+token,"Jenkins-Crumb":res.getData().crumb,"Content-Type": "text/xml"])
			client1.request(Method.POST, ContentType.XML){ req ->
				body = xml.toString()
				response.success = { resp, resXml ->
					assert resp.status == 200
					return 200;
				}
				// not logged in response
				response.'302' = { resp ->
					throw new Exception("Stopping at item POST: uri: " + baseUrl + "\n" +
					" Item will not be created.");
					return 302;
				}
				response.failure = { resp, resXml ->
					throw new Exception("Failed to create job.")
				}
			}

		}catch(Exception e){
			println "Encountered Error while creating jenkins job: "+e;
			return 500;
		}

	}

	static def readProperties(){
		Properties properties = new Properties();
		File pFile = new File("config.properties");
		pFile.withInputStream {
			properties.load(it);
		}
		println "Jenkins Server: "+properties.host;
		return properties;
	}

	static void main(String[] args){
		YamlConfig yConfig = new YamlConfig();
		def jsonObj = yConfig.readFileFromSource();
		Scanner sc = new Scanner(System.in);
		println "Please enter job name:";
		def jobName = sc.nextLine();
		def prop = readProperties();
		def status = createJenkinsJob(prop,'/createItem?name='+jobName,jsonObj);
		if(status == 200){
			println "Jenkins job created successfully...";
		}else{
			println "Jenkins job creation failed...";
		}
	}
}
