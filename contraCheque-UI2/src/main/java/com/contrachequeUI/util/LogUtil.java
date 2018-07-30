package com.contrachequeUI.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class LogUtil extends PrintStream {

	private static List<String> printList = new LinkedList<String>();
	private static String folderTEMP = System.getenv().get("TEMP");

	public LogUtil(PrintStream ps) {
		super(ps);
	}

	@Override
	public void println(String line) {
		printList.add(getInstantToLog() + " " + line);
		super.println(line);
	}
	
	public static void printLine(String line) {
		System.out.println(line);
	}

	public void println(int line) {
		this.println(String.valueOf(line));
	}

	public static void appendLog() {
		try {
			FileUtils.writeLines(new File(folderTEMP + File.separator + "log.txt"), LogUtil.printList, true);
			printList = new LinkedList<String>();
		} catch (IOException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText("Ocorreu um erro na gravação do arquivo de logs: " + folderTEMP + File.separator + "log.txt");
			alert.show();
		}
	}

	@SuppressWarnings("deprecation")
	public static String getLog() {

		String log = new String();
		try {
			log = FileUtils.readFileToString(new File(folderTEMP + File.separator + "log.txt"));
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return log;
	}
	
	public static void fileLogOperations() {
		File fileLogOld = new File(folderTEMP + File.separator + "log.txt"); 
		try {
			FileUtils.copyFile(fileLogOld, new File(folderTEMP + File.separator + File.separator + "log_ContraChequeUI_" + getInstantToFile() +".txt"));
			FileUtils.deleteQuietly(fileLogOld);
			fileLogOld.createNewFile();
		} catch (IOException e) {
			System.out.println("Erro ao processar os arquivos de log");
		}
	}

	public static String getInstantToFile() {
		long yourmilliseconds = System.currentTimeMillis();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");   
    	Date resultdate = new Date(yourmilliseconds);
    	String instant = sdf.format(resultdate);
		return instant;
	}
	
	public static String getInstantToLog() {
		long yourmilliseconds = System.currentTimeMillis();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
    	Date resultdate = new Date(yourmilliseconds);
    	String instant = sdf.format(resultdate);
		return instant;
	}

}