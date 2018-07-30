package com.contrachequeUI.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.io.FileUtils;

import com.contrachequeUI.exception.BusinessException;
import com.contrachequeUI.model.ItemBean;
import com.contrachequeUI.util.CsvAndXlsxUtil;
import com.contrachequeUI.util.LogUtil;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

public class MergeContraChequeService {

	private PdfWriter writer;
	private PdfDocument destPdf;
	private PdfDocument srcPdf;

	public MergeContraChequeService() {
	}

	public static void processaMerge(String pathComCapa, String pathMergeContraCheque, String xslxPath, String periodo)
			throws BusinessException, IOException {

		List<ItemBean> mapFromXlsx = CsvAndXlsxUtil.getListFromXlsxToCreateContraCheque(xslxPath);

		mapFromXlsx.sort((itemBean1, itemBean2) -> {
			if ((itemBean1.getLideranca().compareTo(itemBean2.getLideranca())) != 0) {
				return itemBean1.getLideranca().compareTo(itemBean2.getLideranca());
			} else {
				return itemBean1.getNome().compareTo(itemBean2.getNome());
			}
		});

		List<InputStream> filesIs = new ArrayList<>();
		byte[] mergePdf = null;

		mapFromXlsx.forEach(itemBean -> {
			String fileComCapa = pathComCapa + File.separator + itemBean.getIDPeople() + "_" + itemBean.getPeriodo()
					+ "_" + itemBean.getPagina() + ".pdf";
			boolean existsFile = new File(fileComCapa).exists();

			if (existsFile) {
				try {
					filesIs.add((InputStream) new FileInputStream(new File(fileComCapa)));
				} catch (FileNotFoundException e) {
					throw new RuntimeException(e.getMessage());
				}

				LogUtil.printLine("Fazendo merge do arquivo: " + fileComCapa);
			}
		});

		try {
			mergePdf = new MergeContraChequeService().mergePdf(filesIs);
			FileUtils.writeByteArrayToFile(
					new File(pathMergeContraCheque + File.separator + "ContraCheques_" + periodo + ".pdf"),
					mergePdf);
		} catch (IOException e) {
			throw new BusinessException(e.getLocalizedMessage());
		}
	}

	public static void processaMergeLotes(List<InputStream> files, String pathMergeLotes, String periodo)
			throws BusinessException {
		byte[] mergePdf = null;

		try {
			mergePdf = new MergeContraChequeService().mergePdf(files);
			FileUtils.writeByteArrayToFile(new File(pathMergeLotes + File.separator + "Lotes_" + periodo + ".pdf"),
					mergePdf);
		} catch (IOException | BusinessException e) {
			throw new BusinessException(e.getLocalizedMessage());
		}
	}

	public byte[] mergePdf(List<InputStream> files) throws BusinessException {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		writer = new PdfWriter(bos);
		destPdf = new PdfDocument(writer);

		files.forEach(inputStream -> {
			try {
				srcPdf = new PdfDocument((com.itextpdf.kernel.pdf.PdfReader) new PdfReader(inputStream));

				IntStream.rangeClosed(1, srcPdf.getNumberOfPages()).forEach(i -> {
					PdfPage origPage = srcPdf.getPage(i);
					PdfPage page = destPdf.addNewPage(new PageSize(origPage.getPageSize()));
					page.setRotation(origPage.getRotation());
					PdfCanvas canvas = new PdfCanvas(page);
					PdfFormXObject pageCopy = null;

					try {
						pageCopy = origPage.copyAsFormXObject(destPdf);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}

					canvas.addXObject(pageCopy, 0, 0);
				});

			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		});

		try {
			writer.close();
			destPdf.close();
		} catch (Exception e) {
			throw new BusinessException(e.getLocalizedMessage());
		}
		return bos.toByteArray();
	}
}
