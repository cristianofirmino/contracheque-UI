package com.contrachequeUI.util;

import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

public class UtilPDF {

	public void mergeCapa(String scapa, String src, String dest) throws IOException, DocumentException{
		PdfReader cover = new PdfReader(scapa);
        PdfReader reader = new PdfReader(src);
        Document document = new Document();
        PdfCopy copy = new PdfCopy(document, new FileOutputStream(dest));
        document.open();
        copy.addDocument(cover);
        copy.addDocument(reader);
        document.close();
        cover.close();
        reader.close();
	}
}
