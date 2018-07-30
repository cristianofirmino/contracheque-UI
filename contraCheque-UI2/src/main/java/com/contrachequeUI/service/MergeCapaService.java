package com.contrachequeUI.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.io.FileUtils;

import com.contrachequeUI.exception.BusinessException;
import com.contrachequeUI.util.LogUtil;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

public class MergeCapaService {

	private PdfWriter writer;
	private PdfDocument destPdf;
	private PdfDocument srcPdf;

	public MergeCapaService() {
	}

	public static void processarMerge(String pathCapa, String pathContraCheque, String pathMergeContraCheque)
			throws BusinessException {

		File folderCapa = new File(pathCapa);
		File folderContraCheque = new File(pathContraCheque);
		
		Arrays.asList(folderCapa.listFiles()).forEach(file -> {			
			String id = file.getName().split("_")[0];
			String periodo = file.getName().split("_")[1];
			String pagina = file.getName().split("_")[3];
			LogUtil.printLine("Processando contracheque individual com capa " + id + " pagina " + pagina);

			File[] listFiles = folderContraCheque.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.contains(id) && name.contains(pagina);
				}
			});

			try {
				if (listFiles.length > 0) {

					List<InputStream> filesIs = new ArrayList<>();
					filesIs.add((InputStream) new FileInputStream(file));
					
					Arrays.asList(listFiles).forEach(file2 -> {
						try {
							filesIs.add((InputStream) new FileInputStream(file2));
						} catch (FileNotFoundException e) {
							throw new RuntimeException(e);
						}
					});
					
					byte[] mergePdf = new MergeCapaService().mergePdf(filesIs);
					FileUtils.writeByteArrayToFile(new File(pathMergeContraCheque + File.separator + id + "_" + periodo + "_" + pagina),mergePdf);
				}
			} catch (BusinessException | IOException e) {
				LogUtil.printLine(e.getMessage());
				try {
					throw new BusinessException(e.getLocalizedMessage());
				} catch (BusinessException e1) {
					e1.printStackTrace();
				}
			}
		});

	}

	public byte[] mergePdf(List<InputStream> files) throws BusinessException {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		writer = new PdfWriter(bos);
		destPdf = new PdfDocument(writer);

		files.forEach(inputStream -> {			
			try {
				srcPdf = new PdfDocument(new PdfReader(inputStream));
			} catch (Exception e) {
				LogUtil.printLine(e.getMessage());
				throw new RuntimeException(e);
			}
			
			IntStream.rangeClosed(1, srcPdf.getNumberOfPages()).forEach(i -> {				
				PdfPage origPage = srcPdf.getPage(i);
				PdfPage page = destPdf.addNewPage(new PageSize(origPage.getPageSize()));
				page.setRotation(origPage.getRotation());
				PdfCanvas canvas = new PdfCanvas(page);
				PdfFormXObject pageCopy;

				try {
					pageCopy = origPage.copyAsFormXObject(destPdf);
				} catch (IOException e) {
					LogUtil.printLine(e.getMessage());
					throw new RuntimeException(e);
				}

				canvas.addXObject(pageCopy, 0, 0);
			});
		});
		
		try {
			writer.close();
			destPdf.close();
		} catch (Exception e) {
			LogUtil.printLine(e.getMessage());
			throw new BusinessException(e.getLocalizedMessage());
		}
		return bos.toByteArray();
	}

}
