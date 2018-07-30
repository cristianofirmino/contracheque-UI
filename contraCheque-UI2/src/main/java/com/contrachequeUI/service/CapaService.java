package com.contrachequeUI.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import com.contrachequeUI.exception.BusinessException;
import com.contrachequeUI.model.ItemBean;
import com.contrachequeUI.util.CsvAndXlsxUtil;
import com.contrachequeUI.util.LogUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class CapaService {

	private PdfStamper stamper;
	private PdfContentByte over;

	private PdfReader reader;

	public CapaService(String xlsxPath, String pathCapa) throws IOException, BusinessException {
		List<ItemBean> mapFromXlsx = CsvAndXlsxUtil.getListFromXlsxToCreateContraCheque(xlsxPath);

		for (ItemBean itemBean : mapFromXlsx) {
			String payCheckPath = pathCapa + File.separator + itemBean.getIDPeople() + "_" + itemBean.getPeriodo() + "_"
					+ "capa_" + itemBean.getPagina() + ".pdf";
			boolean existsFile = new File(payCheckPath).exists();

			if (!existsFile) {
				byte[] employeeCardBytes = criarCapaContraCheque(itemBean.getNome(), itemBean.getIDPeople(),
						itemBean.getLideranca(), itemBean.getCargo(), itemBean.getPeriodo());
				try {
					LogUtil.printLine("Processando a capa " + payCheckPath);
					FileUtils.writeByteArrayToFile(new File(payCheckPath), employeeCardBytes);
				} catch (IOException e) {
					LogUtil.printLine(e.getMessage());
					throw new BusinessException(e.getLocalizedMessage());
				}
			}

		}
	}

	public byte[] criarCapaContraCheque(String nome, String idPeople, String lider, String cargo, String periodo)
			throws BusinessException {

		try {
			ClassPathResource classPathResource = new ClassPathResource("/template/capa.pdf");
			InputStream inputStream = classPathResource.getInputStream();
			reader = new PdfReader(inputStream);
		} catch (IOException e) {
			LogUtil.printLine(e.getMessage());
			throw new BusinessException(e.getLocalizedMessage());
		}

		int pages = reader.getNumberOfPages();
		ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();

		try {
			stamper = new PdfStamper(reader, fileOutputStream);
		} catch (DocumentException | IOException e) {
			LogUtil.printLine(e.getMessage());
			throw new BusinessException(e.getLocalizedMessage());
		}

		Font font = new Font(FontFamily.UNDEFINED, (float) 9.5);

		Phrase phraseNome = new Phrase(nome.toUpperCase(), font);
		Phrase phraseIdPeople = new Phrase(idPeople, font);
		Phrase phraseLider = new Phrase(lider.toUpperCase(), font);
		Phrase phraseCargo = new Phrase(cargo.toUpperCase(), font);
		Phrase phrasePeriodo = new Phrase(periodo.toUpperCase(), font);
		PdfGState gs1 = new PdfGState();

		IntStream.rangeClosed(1, pages).forEach(i -> {
			if (i == 1) {
				Rectangle pagesize = reader.getPageSize(i);
				float x = (pagesize.getLeft() + pagesize.getRight()) / 2;
				float y = (pagesize.getBottom() + pagesize.getTop()) / 2;

				this.over = stamper.getOverContent(i);
				this.over.saveState();
				this.over.setGState(gs1);

				ColumnText.showTextAligned(this.over, Element.ALIGN_LEFT, phraseNome, x - 149, (float) (y + 1.6), 0);
				ColumnText.showTextAligned(this.over, Element.ALIGN_LEFT, phraseIdPeople, x - 231, (float) (y - 24.1), 0);
				ColumnText.showTextAligned(this.over, Element.ALIGN_LEFT, phraseLider, x - 134, (float) (y - 24.1), 0);
				ColumnText.showTextAligned(this.over, Element.ALIGN_LEFT, phraseCargo, x - 213, (float) (y - 49.9), 0);
				ColumnText.showTextAligned(this.over, Element.ALIGN_LEFT, phrasePeriodo, x - 206, y - 75, 0);
			}
		});

		try {
			stamper.close();
		} catch (DocumentException | IOException e) {
			LogUtil.printLine(e.getMessage());
			throw new BusinessException(e.getLocalizedMessage());
		}
		reader.close();

		byte[] manipulatePdfByteArray = fileOutputStream.toByteArray();

		return manipulatePdfByteArray;
	}

}
