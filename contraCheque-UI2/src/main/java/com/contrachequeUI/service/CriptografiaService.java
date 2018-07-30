package com.contrachequeUI.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import com.contrachequeUI.exception.BusinessException;
import com.contrachequeUI.model.Integrante;
import com.contrachequeUI.model.ItemBean;
import com.contrachequeUI.util.CsvAndXlsxUtil;
import com.contrachequeUI.util.IntegrantesUtil;
import com.contrachequeUI.util.LogUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class CriptografiaService {

	private List<Integrante> integrantesComEmail = new ArrayList<Integrante>();

	public List<Integrante> getIntegrantesComEmail() {
		return integrantesComEmail;
	}

	public void setIntegrantesComEmail(List<Integrante> integrantesComEmail) {
		this.integrantesComEmail = integrantesComEmail;
	}

	public void criptografaPdfs(String pastaOrigem, String pastaDestino, String xlsx, String urlIntegrantes,
			String senhaMestre) throws IOException, DocumentException, BusinessException {

		List<Integrante> integrantes;
		try {
			integrantes = IntegrantesUtil.getIntegrantesFromJSON(urlIntegrantes);
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}

		List<ItemBean> itensBean = CsvAndXlsxUtil.getListFromXlsxToCreateContraCheque(xlsx);

		List<Integrante> filtroIntegrantesComEmail = integrantes.stream()
				.filter(o1 -> itensBean.stream().anyMatch(o2 -> o2.equals(o1) && o1.getEmail() != null))
				.collect(Collectors.toList());

		List<File> listaArquivos = Arrays.asList(new File(pastaOrigem).listFiles());

		List<File> filtroArquivos = listaArquivos.stream()
				.filter(f -> filtroIntegrantesComEmail.stream().anyMatch(o -> f.getName().contains(o.getIdPeople())))
				.collect(Collectors.toList());

		filtroArquivos.forEach(f -> {
			filtroIntegrantesComEmail.forEach(i -> {
				try {
					if (f.getName().contains(i.getIdPeople())) {
						integrantesComEmail.add(i);
						String arquivoOrigem = f.getAbsolutePath();
						String arquivoDestino = pastaDestino + File.separator + f.getName();

						PdfReader reader = new PdfReader(arquivoOrigem);
						PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(arquivoDestino));
						stamper.setEncryption(i.getCpf().getBytes(), senhaMestre.getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.ALLOW_COPY |
								PdfWriter.ENCRYPTION_AES_128 | PdfWriter.DO_NOT_ENCRYPT_METADATA);
						stamper.close();
						reader.close();
						FileUtils.forceDelete(f);
						LogUtil.printLine("Processando arquivo criptografado: " + arquivoDestino + " CPF: " + i.getCpf());
					}
				} catch (IOException | DocumentException e) {
					try {
						throw new BusinessException(e.getMessage());
					} catch (BusinessException e1) {
						LogUtil.printLine(e.getMessage());
						e1.printStackTrace();
					}
				}
			});
		});

	}

}
