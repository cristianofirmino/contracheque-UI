package com.contrachequeUI.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.contrachequeUI.model.ItemBean;
import com.contrachequeUI.util.Abstract2DPdfPageSplittingTool;
import com.contrachequeUI.util.CsvAndXlsxUtil;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;

public class SplitPDFService {

	private String ppdf;
	private String pdestino;
	private String pcsv;
	
	public SplitPDFService(String ppdf, String pdestino, String pcsv) {
		this.ppdf = ppdf;
		this.pcsv = pcsv;
		this.pdestino = pdestino;
	}

	Abstract2DPdfPageSplittingTool tool = new Abstract2DPdfPageSplittingTool() {
		@Override
		protected Iterable<Rectangle> determineSplitRectangles(PdfReader reader, int page) {
			Rectangle targetSize = PageSize.A4;
			List<Rectangle> rectangles = new ArrayList<>();
			Rectangle pageSize = reader.getPageSize(page);
			for (float y = pageSize.getTop(); y > pageSize.getBottom() + 5; y -= targetSize.getHeight()) {
				for (float x = pageSize.getLeft(); x < pageSize.getRight() - 5; x += targetSize.getWidth()) {
					rectangles.add(new Rectangle(x, y - targetSize.getHeight(), x + targetSize.getWidth(), y));
				}
			}
			return rectangles;
		}
	};

	public void splitPDF() throws Exception {
		if (ppdf == null)
			throw new Exception("Parâmentro pdf é requerido");
		if (pdestino == null)
			throw new Exception("Parâmentro destino é requerido");
		if (pcsv == null)
			throw new Exception("Parâmentro csv é requerido");

		File pdf = new File(ppdf);
		File destino = new File(pdestino);
		File csv = new File(pcsv);

		if (!pdf.isFile()) 
			throw new Exception("Parâmentro pdf não é um arquivo");
		if (!destino.isDirectory())
			throw new Exception("Parâmentro destino não é um diretório");
		if (!csv.isFile())
			throw new Exception("Parâmentro csv não é um arquivo");
		
		List<ItemBean> itens = new ArrayList<>();
		try {
			itens = CsvAndXlsxUtil.getListFromXlsxToCreateContraCheque(csv.getAbsolutePath());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			throw new Exception("Parâmentro csv de formato inválido");
		}

		PdfReader pdfReader = null;		
		try {
			pdfReader = new PdfReader((InputStream) new FileInputStream(pdf));
			for (ItemBean item : itens) {
				Integer pagina = Integer.valueOf(item.getPagina());				
				String nomeArquivo = destino.getAbsolutePath() + File.separator + item.getIDPeople() + "_" + item.getPeriodo() + "_" + item.getPagina() + ".pdf";
				FileOutputStream pdfMatricula;				
				pdfMatricula = new FileOutputStream(nomeArquivo);
				
				try { 					
					makePDF(pdfMatricula, pagina, pdfReader);
					System.out.println("Processando arquivo do Lote " + nomeArquivo);
				} catch (Exception e) {
					pdfMatricula.close();
					new File(nomeArquivo).delete();
					System.out.println(e.getMessage());
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new Exception("Arquivo pdf invalido");
		}

	}

	private void makePDF(OutputStream matricula, int pagina, PdfReader arquivo) throws Exception {
		tool.split(matricula, arquivo, pagina);
	}

}
