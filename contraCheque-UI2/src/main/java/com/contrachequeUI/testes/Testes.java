package com.contrachequeUI.testes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.IntStream.Builder;

import org.springframework.core.io.ClassPathResource;

import com.itextpdf.text.log.SysoCounter;

public class Testes {

	public static void main(String[] args) throws IOException {

		/*ClassPathResource classPathResource = new ClassPathResource("/template/capa.pdf");
		InputStream inputStream = classPathResource.getInputStream();
		System.out.println(classPathResource.getFile().getName());*/
		
		List<String> test = new ArrayList<>();
		test.add("Teste1");
		test.add("Teste2");
		test.add("Teste3");
		
		IntStream.rangeClosed(1, 100).forEach(System.out::println);
	}
}
