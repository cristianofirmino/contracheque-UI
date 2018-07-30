package com.contrachequeUI.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.contrachequeUI.exception.BusinessException;
import com.contrachequeUI.model.ItemBean;
import com.contrachequeUI.util.CsvAndXlsxUtil;
import com.contrachequeUI.util.LogUtil;
import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.FilteredTextRenderListener;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.RegionTextRenderFilter;
import com.itextpdf.text.pdf.parser.RenderFilter;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

public class ExtractPDFService {

	private final int colNome = 0;
	private final int colID = 1;
	private final int colCargo = 2;
	private final int colPeriodo = 3;
	private final int colLideranca = 4;
	private final int colPagina = 5;
	private final String pattern = "([0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9])\\sNúmero";
	
	private int contador = 0;
	private String idPeoplePDF;
	private String iDPeople;
	private String nome;
	private String cargo;
	private String lideranca;

	public void extract(String pathPDF, String xlsxPeople, String xlsxOutput, String periodo)
			throws IOException, BusinessException, InvalidFormatException {

		PdfReader pdfReader = new PdfReader(pathPDF);
		int numberOfPages = pdfReader.getNumberOfPages();

		List<ItemBean> beans = CsvAndXlsxUtil.getListFromXlsx(xlsxPeople);
		LogUtil.printLine("Quantidade de linhas na planilha: " + beans.size());

		FileOutputStream fos = new FileOutputStream(new File(xlsxOutput));
		XSSFWorkbook wb1 = new XSSFWorkbook();

		XSSFSheet sheet = wb1.createSheet("extractFromPeople");
		criarLinha(sheet, "PAGINA", "NOME", "ID PEOPLE", "CARGO", "PERIODO", "LIDER");

		IntStream.rangeClosed(1, numberOfPages).forEach(i -> {
			
			com.itextpdf.text.Rectangle pageSize = pdfReader.getPageSize(i);
			float width = pageSize.getWidth();
			float height = pageSize.getHeight();

			Rectangle rectangle = new Rectangle(0, 0, width, height);

			RenderFilter[] filter = { new RegionTextRenderFilter(rectangle) };
			TextExtractionStrategy strategy = new FilteredTextRenderListener(new LocationTextExtractionStrategy(), filter);
			String texto = null;
			try {
				texto = PdfTextExtractor.getTextFromPage(pdfReader, i, strategy);
			} catch (IOException e) {
				try {
					throw new BusinessException(e.getMessage());
				} catch (BusinessException e1) {
					e1.printStackTrace();
				}
			}

			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(texto);

			if (m.find()) {

				idPeoplePDF = texto.substring(m.start(), m.start() + 8);
				
				IntStream.range(0, beans.size()).forEach(j -> {
					
					if (idPeoplePDF.equals(beans.get(j).getIDPeople())) {
						
						iDPeople = beans.get(j).getIDPeople();
						nome = beans.get(j).getNome();
						cargo = beans.get(j).getCargo();
						lideranca = beans.get(j).getLideranca();

						criarLinha(sheet, i, nome, iDPeople, cargo, periodo, lideranca);
						LogUtil.printLine("Extraindo ID PDF: " + idPeoplePDF + " ID XLSX: " + iDPeople);
					}
				});
				
			} else {

				criarLinha(sheet, i, nome, iDPeople, cargo, periodo, lideranca);
				LogUtil.printLine("Extraindo ID PDF: " + idPeoplePDF + " ID XLSX: " + iDPeople);
			}
		});
		
		wb1.write(fos);
		fos.close();
		wb1.close();
		pdfReader.close();
	}

	public void criarLinha(XSSFSheet sheet, Object pagina, String nome, String iDPeople, String cargo, String periodo, String lideranca) {
		
		XSSFRow row = sheet.createRow(contador);
		
		XSSFCell cellNome = row.createCell(colNome);
		XSSFCell cellID = row.createCell(colID);
		XSSFCell cellCargo = row.createCell(colCargo);
		XSSFCell cellPeriodo = row.createCell(colPeriodo);
		XSSFCell cellLideranca = row.createCell(colLideranca);
		XSSFCell cellPagina = row.createCell(colPagina);

		cellNome.setCellValue(nome);
		cellID.setCellValue(iDPeople);
		cellCargo.setCellValue(cargo);
		cellPeriodo.setCellValue(periodo);
		cellLideranca.setCellValue(lideranca);
		
		if(pagina instanceof String){
			
			cellPagina.setCellValue((String)pagina);
			
		} else {
			
			cellPagina.setCellValue((int)pagina);
		}

		contador++;		
	}
}
