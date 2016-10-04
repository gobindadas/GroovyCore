package com.gobinda.groovy.config

import javax.security.auth.login.Configuration;
import net.sf.json.JSONObject
import net.sf.json.xml.XMLSerializer;

import org.yaml.snakeyaml.Yaml

class YamlConfig {
	Configuration configuration;
	def readFileFromSource(){
		Yaml yaml = new Yaml();
		try{
			InputStream inputStream = new FileInputStream("/home/gobinda/yaml/d4d.yaml");
			if(inputStream){
				def data = yaml.load(inputStream);
				def xml = convertToJson(data);
				return xml;
			}else{
				println "File not loaded...";
			}
		}catch(Exception e){
			println "Encounter error: "+e;
		}
	}

	private static def convertToJson(Map<String,Object> map) {
		JSONObject jsonObject=new JSONObject(map);
		return convertToXML(jsonObject);
	}

	private static def convertToXML(def jsonObj) {
		XMLSerializer xmlSerializer = new XMLSerializer();
		xmlSerializer.setTypeHintsCompatibility(false);
		String xml = xmlSerializer.write(jsonObj);
		return xml.replace("<o>","").replace("</o>","");
	}
}
