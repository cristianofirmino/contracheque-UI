package com.contrachequeUI.util;

import java.io.OutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;

public abstract class Abstract2DPdfPageSplittingTool {

	protected abstract Iterable<Rectangle> determineSplitRectangles(PdfReader reader, int page);

	Document document = null;
	PdfCopy writer = null;

	public void split(OutputStream outputStream, PdfReader input, int page) throws Exception {
		try {
			initDocument(outputStream);
			split(input, page);
		} finally {
			closeDocument();
		}
	}

	void initDocument(OutputStream outputStream) throws DocumentException {
		final Document document = new Document(PageSize.A4);
		final PdfCopy writer = new PdfCopy(document, outputStream);
		this.document = document;
		this.writer = writer;
	}

	void closeDocument() {
		try {
			document.close();
		} finally {
			this.document = null;
			this.writer = null;
		}
	}

	void newPage(Rectangle pageSize) {
		document.setPageSize(pageSize);
		if (!document.isOpen())
			document.open();
		else
			document.newPage();
	}

	void split(PdfReader reader, int ppage) throws Exception {
		document.open();
		PdfImportedPage page = writer.getImportedPage(reader, ppage);
		writer.addPage(page);
	}

}