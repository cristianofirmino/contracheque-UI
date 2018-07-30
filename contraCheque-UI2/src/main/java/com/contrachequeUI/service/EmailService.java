package com.contrachequeUI.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import com.contrachequeUI.exception.BusinessException;
import com.contrachequeUI.model.Integrante;
import com.contrachequeUI.util.IntegrantesUtil;
import com.contrachequeUI.util.LogUtil;

@SuppressWarnings("deprecation")
public class EmailService {

	public static void enviaEmail(String urlIntegrantes, String pastaOrigem, String emailRemetente) throws EmailException, IOException {

		List<Integrante> integrantes = IntegrantesUtil.getIntegrantesFromJSON(urlIntegrantes);
		List<File> listaArquivos = Arrays.asList(new File(pastaOrigem).listFiles());

		listaArquivos.forEach(file -> {

			integrantes.forEach(i -> {

				if (i.getEmail() != null && i.getIdPeople() != null && file.getName().contains(i.getIdPeople())) {

					try {
						FileInputStream fis = new FileInputStream(file.getAbsolutePath());
						String periodo = file.getName().split("_")[0] + "_" + file.getName().split("_")[1];

						HtmlEmail email = new HtmlEmail();
						email.setHostName("webmail.com");
						email.addTo(i.getEmail());
						email.setFrom(emailRemetente, "Pessoas e Organização");
						email.setSubject("DEMONSTRATIVO DE PAGAMENTO - " + periodo);
						email.setCharset("utf-8");
						email.setHtmlMsg("<div style='font-family: Helvetica, Arial, sans-serif;font-size: 13px;'>"
								+ "Visualize seu Demonstrativo de Pagamento em anexo.<br>A senha para "
								+ "abrir o arquivo é o número do seu CPF sem pontos e traço."
								+ "<br><br> ID: " + i.getIdPeople() 
								+ "<br>Nome:" + i.getNome()
								+ "<br>E-mail: " + i.getEmail()
								+ "</div>");

						EmailAttachment attachment = new EmailAttachment();
						attachment.setDisposition(EmailAttachment.ATTACHMENT);
						email.attach(new ByteArrayDataSource(fis, "application/pdf"), file.getName(),
								EmailAttachment.ATTACHMENT);

						LogUtil.printLine("Enviando contracheque para: " + i.getEmail() + " - " + i.getIdPeople());

						email.send();

					} catch (EmailException | IOException e) {
						try {
							throw new BusinessException(e.getMessage());
						} catch (BusinessException e1) {
							e1.printStackTrace();
						}
					}
				}
			});
		});

	}
}
