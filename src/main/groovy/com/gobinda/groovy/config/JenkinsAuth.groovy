package com.gobinda.groovy.config
import org.apache.commons.httpclient.*

import org.apache.commons.httpclient.auth.*
import org.apache.commons.httpclient.methods.*

class JenkinsAuth {
	@Grab(group='commons-httpclient', module='commons-httpclient', version='3.1')
	static void createNewJenkinsProject() {
	
	  def server = "server"
	  def jenkinsHost = "http://localhost:8080"
	  def projectName = "TEST"
	  def configurationFile = "/home/gobinda/yaml/job.xml"
	
	  def username = "gobinda"
	  def apiToken = "test"
	
	  def client = new HttpClient()
	  client.state.setCredentials(
		new AuthScope( server, 443, "realm"),
		new UsernamePasswordCredentials( username, apiToken )
	  )
	
	  // Jenkins does not do any authentication negotiation,
	  // ie. it does not return a 401 (Unauthorized)
	  // but immediately a 403 (Forbidden)
	  client.params.authenticationPreemptive = true
	
	  def post = new PostMethod( "${jenkinsHost}/createItem?name=${projectName}" )
	  post.doAuthentication = true
	
	  File input = new File(configurationFile);
	  RequestEntity entity = new FileRequestEntity(input, "text/xml; charset=UTF-8");
	  post.setRequestEntity(entity);
	  try {
		int result = client.executeMethod(post)
		println "Return code: ${result}"
		post.responseHeaders.each{ println it.toString().trim() }
		println post.getResponseBodyAsString()
	  } finally {
		post.releaseConnection()
	  }
	}
	
	static void main(String[] args){
		createNewJenkinsProject();
	}

}
