package com.contrachequeUI.util;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.client.RestTemplate;

import com.contrachequeUI.model.Integrante;

public class IntegrantesUtil {

	public static List<Integrante> getIntegrantesFromJSON(String url) {
		url = "http://api-baseunica.net/api/pessoas?fields=cpf,idPeople,nome,email&filter=.&limit=0";
		LogUtil.printLine(url);
		RestTemplate restTemplate = new RestTemplate();
		Integrante[] integrantes = restTemplate.getForObject(url, Integrante[].class);
		List<Integrante> integrantesList = Arrays.asList(integrantes);

		return integrantesList;
	}
	
	public static String getEmail(String username) throws Exception{
		
		String url = "http://api-baseunica.net/api/pessoas?fields=cpf,idPeople,nome,email&filter="+ username + "&limit=0";
		LogUtil.printLine(url);
		RestTemplate restTemplate = new RestTemplate();
		
		Integrante[] integrantes = restTemplate.getForObject(url, Integrante[].class);
		
		if (integrantes[0].getEmail() != null && !integrantes[0].getEmail().equals("")){
			return integrantes[0].getEmail();
		}
		
		return "";
	}
}
