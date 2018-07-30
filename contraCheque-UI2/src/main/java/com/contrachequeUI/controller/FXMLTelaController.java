package com.contrachequeUI.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;

import com.contrachequeUI.model.Integrante;
import com.contrachequeUI.service.CapaService;
import com.contrachequeUI.service.CriptografiaService;
import com.contrachequeUI.service.EmailService;
import com.contrachequeUI.service.ExtractPDFService;
import com.contrachequeUI.service.MergeCapaService;
import com.contrachequeUI.service.MergeContraChequeService;
import com.contrachequeUI.service.SplitPDFService;
import com.contrachequeUI.util.IntegrantesUtil;
import com.contrachequeUI.util.LogUtil;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;

public class FXMLTelaController implements Initializable {

	@FXML
	private Tab tab1;
	@FXML
	private AnchorPane mainPane;
	@FXML
	private Button btnSelectLote1;
	@FXML
	private Label labelLote1;
	@FXML
	private Label labelCSV;
	@FXML
	private Label labelFolderProc;
	@FXML
	private Button btnProcIndividual;
	@FXML
	private Button btnProcCompleto;
	@FXML
	private Button btnSelectCSV;
	@FXML
	private Button btnSelectFolderProc;
	@FXML
	private Button btnSelectLote2;
	@FXML
	private Button btnProcLotes;
	@FXML
	private Label labelLote2;
	@FXML
	private Button btnSelectLote3;
	@FXML
	private Label labelLote3;
	@FXML
	private Button btnSelectLote4;
	@FXML
	private Label labelLote4;
	@FXML
	private Label labelLote5;
	@FXML
	private Button btnSelectLote5;
	@FXML
	private Tab tab2;
	@FXML
	private Tab tab3;
	@FXML
	private TextArea txtAreaLog;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Button btnSair;
	@FXML
	private Button btnProcCriptografa;
	@FXML
	private Button btnEnviar;
	@FXML
	private Label labelProgress;
	@FXML
	private TableView<Integrante> tbVEmail;
	@FXML
	private Label labelQtdEmails;
	@FXML
	private Button btnProcExtract;
	@FXML
	private TextField txtPeriodo;

	private File lastDirectory;
	private File folderSemCapa;
	private File folderIndividual;
	private File folderCompleto;
	private File folderCapa;
	private File folderLoteMerge;
	private File folderCriptografado;
	private String folderProc;
	private String fileNameXlsx;
	private String fileNameXlsxOut;
	private String periodo;
	private String alertaMsg;
	private String fileNameLote1;
	private String fileNameLote2;
	private String fileNameLote3;
	private String fileNameLote4;
	private String fileNameLote5;
	private String fileNameLoteMerge;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.setOut(new LogUtil(System.out));
		LogUtil.printLine("O usuário " + System.getProperty("user.name") + " abriu a aplicação");
		LogUtil.fileLogOperations();
		LogUtil.appendLog();
		printLogTxtArea();
		desabilitaBotoes(true);
		labelProgress.setVisible(false);

	}

	@FXML
	public void selecionarArquivo(ActionEvent event) {
		String btnId = ((Button) event.getSource()).getId();

		try {
			if (btnId.equals("btnSelectFolderProc")) {
				DirectoryChooser dc = new DirectoryChooser();
				dc.setTitle("Selecione a pasta de Processamento");
				File file = dc.showDialog(null);
				lastDirectory = file;
				folderProc = file.getAbsolutePath();
				labelFolderProc.setText(folderProc);
				LogUtil.printLine("Pasta de Processamento selecionada: " + folderProc);
				criarPastas();
			}

			if (btnId.equals("btnSelectCSV")) {
				FileChooser fc = new FileChooser();
				fc.setTitle("Selecione o arquivo Excel (.xlsx)");
				fc.setInitialDirectory(lastDirectory);
				File file = fc.showOpenDialog(null);
				lastDirectory = file.getParentFile();
				fileNameXlsx = file.getAbsolutePath();
				labelCSV.setText(fileNameXlsx);
				LogUtil.printLine("Arquivo Excel selecionado: " + fileNameXlsx);

				if (!fileNameXlsx.contains(".xlsx") && !fileNameXlsx.contains(".XLSX")) {
					Alert alerta = new Alert(AlertType.ERROR);
					alerta.setContentText("O arquivo selecionado não é um Excel (.xlsx)");
					showAlerta(alerta);
					labelCSV.setText("");
					LogUtil.printLine(alerta.getContentText());
				}
			}

			if (btnId.equals("btnSelectLote1")) {
				FileChooser fc = new FileChooser();
				fc.setTitle("Selecione o arquivo PDF do Lote 1");
				fc.setInitialDirectory(lastDirectory);
				File file = fc.showOpenDialog(null);
				lastDirectory = file.getParentFile();
				fileNameLote1 = file.getAbsolutePath();
				labelLote1.setText(fileNameLote1);
				LogUtil.printLine("Arquivo de Lote selecionado: " + fileNameLote1);

				if (!fileNameLote1.contains(".pdf") && !fileNameLote1.contains(".PDF")) {
					Alert alerta = new Alert(AlertType.ERROR);
					alerta.setContentText("O arquivo selecionado não é um PDF");
					showAlerta(alerta);
					labelLote1.setText("");
					LogUtil.printLine(alerta.getContentText());
				}
			}

			if (btnId.equals("btnSelectLote2")) {
				FileChooser fc = new FileChooser();
				fc.setTitle("Selecione o arquivo PDF do Lote 2");
				fc.setInitialDirectory(lastDirectory);
				File file = fc.showOpenDialog(null);
				lastDirectory = file.getParentFile();
				fileNameLote2 = file.getAbsolutePath();
				labelLote2.setText(fileNameLote2);
				LogUtil.printLine("Arquivo de Lote selecionado: " + fileNameLote2);

				if (!fileNameLote2.contains(".pdf") && !fileNameLote2.contains(".PDF")) {
					Alert alerta = new Alert(AlertType.ERROR);
					alerta.setContentText("O arquivo selecionado não é um PDF");
					showAlerta(alerta);
					labelLote2.setText("");
					LogUtil.printLine(alerta.getContentText());
				}
			}

			if (btnId.equals("btnSelectLote3")) {
				FileChooser fc = new FileChooser();
				fc.setTitle("Selecione o arquivo PDF do Lote 3");
				fc.setInitialDirectory(lastDirectory);
				File file = fc.showOpenDialog(null);
				lastDirectory = file.getParentFile();
				fileNameLote3 = file.getAbsolutePath();
				labelLote3.setText(fileNameLote3);
				LogUtil.printLine("Arquivo de Lote selecionado: " + fileNameLote3);

				if (!fileNameLote3.contains(".pdf") && !fileNameLote3.contains(".PDF")) {
					Alert alerta = new Alert(AlertType.ERROR);
					alerta.setContentText("O arquivo selecionado não é um PDF");
					showAlerta(alerta);
					labelLote3.setText("");
					LogUtil.printLine(alerta.getContentText());
				}
			}

			if (btnId.equals("btnSelectLote4")) {
				FileChooser fc = new FileChooser();
				fc.setTitle("Selecione o arquivo PDF do Lote 4");
				fc.setInitialDirectory(lastDirectory);
				File file = fc.showOpenDialog(null);
				lastDirectory = file.getParentFile();
				fileNameLote4 = file.getAbsolutePath();
				labelLote4.setText(fileNameLote4);
				LogUtil.printLine("Arquivo de Lote selecionado: " + fileNameLote4);

				if (!fileNameLote4.contains(".pdf") && !fileNameLote4.contains(".PDF")) {
					Alert alerta = new Alert(AlertType.ERROR);
					alerta.setContentText("O arquivo selecionado não é um PDF");
					showAlerta(alerta);
					labelLote4.setText("");
					LogUtil.printLine(alerta.getContentText());
				}
			}

			if (btnId.equals("btnSelectLote5")) {
				FileChooser fc = new FileChooser();
				fc.setTitle("Selecione o arquivo PDF do Lote 5");
				fc.setInitialDirectory(lastDirectory);
				File file = fc.showOpenDialog(null);
				lastDirectory = file.getParentFile();
				fileNameLote5 = file.getAbsolutePath();
				labelLote5.setText(fileNameLote5);
				LogUtil.printLine("Arquivo de Lote selecionado: " + fileNameLote5);

				if (!fileNameLote5.contains(".pdf") && !fileNameLote5.contains(".PDF")) {
					Alert alerta = new Alert(AlertType.ERROR);
					alerta.setContentText("O arquivo selecionado não é um PDF");
					showAlerta(alerta);
					labelLote5.setText("");
					LogUtil.printLine(alerta.getContentText());
				}
			}
		} catch (Exception e) {
			Alert alerta = new Alert(AlertType.WARNING);
			alerta.setContentText("Você não selecionou nenhum arquivo ou pasta!");
			showAlerta(alerta);
			LogUtil.printLine(alerta.getContentText());
			LogUtil.printLine(e.getLocalizedMessage());
		}

		LogUtil.appendLog();
		printLogTxtArea();
	}

	@FXML
	public void processaExtracao() {

		desabilitaTab(true);
		alertaMsg = new String("");

		if (itensSelecionados()) {
			Task<Object> task = taskProcessaExtracao();
			updateMensagemProgresso("Extraindo os números das páginas do Lote Único...", true);
			progressBar.progressProperty().bind(task.progressProperty());
			progressBar.setVisible(true);
			new Thread(task).start();

		} else {
			Alert alerta = new Alert(AlertType.ERROR);
			alerta.setContentText(alertaMsg);
			showAlerta(alerta);
			desabilitaTab(false);
			LogUtil.printLine(alertaMsg);
			LogUtil.appendLog();
		}
		printLogTxtArea();
	}

	@FXML
	public void processaLote() {

		desabilitaTab(true);
		alertaMsg = new String("");

		if (itensSelecionados()) {
			Task<Object> task = taskProcessaLote();
			updateMensagemProgresso("Processando os contracheques individuais sem capa", true);
			progressBar.progressProperty().bind(task.progressProperty());
			progressBar.setVisible(true);
			new Thread(task).start();

		} else {
			Alert alerta = new Alert(AlertType.ERROR);
			alerta.setContentText(alertaMsg);
			showAlerta(alerta);
			desabilitaTab(false);
			LogUtil.printLine(alertaMsg);
			LogUtil.appendLog();
		}
		printLogTxtArea();

	}

	@FXML
	public void processaIndividual() {

		desabilitaTab(true);
		alertaMsg = new String("");
		updateMensagemProgresso("Processando os contracheques individuais com capa", true);
		Task<Object> task = taskProcessaIndividual();
		progressBar.progressProperty().bind(task.progressProperty());
		progressBar.setVisible(true);
		new Thread(task).start();
		printLogTxtArea();

	}

	@FXML
	public void processaCompleto() {

		desabilitaTab(true);
		alertaMsg = new String("");
		updateMensagemProgresso("Processando o arquivo completo dos contracheques ordenados pelo Líder", true);
		Task<Object> task = taskProcessaCompleto();
		progressBar.setVisible(true);
		progressBar.progressProperty().bind(task.progressProperty());
		Thread thread = new Thread(task);
		thread.start();

		printLogTxtArea();
	}

	@FXML
	public void processaCriptografia() {

		desabilitaTab(true);
		alertaMsg = new String("");
		updateMensagemProgresso("Criptografando os contracheques sem capa", true);
		Task<Object> task = taskProcessaCriptografia();
		progressBar.setVisible(true);
		progressBar.progressProperty().bind(task.progressProperty());
		Thread thread = new Thread(task);
		thread.start();

		printLogTxtArea();
	}

	@FXML
	public void processaEnvio() {

		desabilitaTab(true);
		alertaMsg = new String("");
		updateMensagemProgresso("Enviando contracheques por e-mail para os integrantes...", true);
		Task<Object> task = taskProcessaEnvio();
		progressBar.setVisible(true);
		progressBar.progressProperty().bind(task.progressProperty());
		Thread thread = new Thread(task);
		thread.start();

		printLogTxtArea();

	}

	public Task<Object> taskProcessaExtracao() {

		return new Task<Object>() {

			@Override
			protected Object call() {

				try {

					criarPastas();

					periodo = txtPeriodo.getText().replaceAll("[^a-zA-Z0-9\\.\\-]", "-");

					MergeContraChequeService.processaMergeLotes(getFilesLotes(), folderLoteMerge.getAbsolutePath(),
							periodo);

					fileNameLoteMerge = folderLoteMerge.getAbsolutePath() + File.separator + "Lotes_" + periodo
							+ ".pdf";
					LogUtil.printLine("Arquivo dos Lotes criados com sucesso: " + fileNameLoteMerge);

					ExtractPDFService extractPDFS = new ExtractPDFService();
					fileNameXlsxOut = folderProc + File.separator + "extractFromPeople_" + periodo + ".xlsx";
					extractPDFS.extract(fileNameLoteMerge, fileNameXlsx, fileNameXlsxOut, periodo);

					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							showMensagem("Números de páginas extraídos com sucesso!", AlertType.INFORMATION);
							btnProcExtract.setDisable(true);
							btnProcLotes.setDisable(false);
						}
					});

				} catch (Exception e) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							showMensagem("Ocorreu um erro ao extrair os números das páginas: \n\n" + e.getMessage(),
									AlertType.ERROR);
						}
					});
				}

				return null;
			}
		};
	}

	public Task<Object> taskProcessaLote() {

		return new Task<Object>() {

			@Override
			protected Object call() {
				try {
					criarPastas();
					SplitPDFService service = new SplitPDFService(fileNameLoteMerge, folderSemCapa.getAbsolutePath(),
							fileNameXlsxOut);
					service.splitPDF();

					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							showMensagem("Contracheques individuais sem capa processados com sucesso",
									AlertType.INFORMATION);
							btnProcLotes.setDisable(true);
							btnProcCriptografa.setDisable(false);
						}
					});

				} catch (Exception e) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							showMensagem("Ocorreu um erro processando os lotes: \n\n" + e.getMessage(),
									AlertType.ERROR);
						}
					});
				}

				return null;
			}
		};
	}

	public Task<Object> taskProcessaIndividual() {

		return new Task<Object>() {

			@Override
			protected Object call() {
				try {
					new CapaService(fileNameXlsxOut, folderCapa.getAbsolutePath());

					try {
						MergeCapaService.processarMerge(folderCapa.getAbsolutePath(), folderSemCapa.getAbsolutePath(),
								folderIndividual.getAbsolutePath());

						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								showMensagem("Contracheques individuais com capa processados com sucesso",
										AlertType.INFORMATION);
								btnProcIndividual.setDisable(true);
								btnProcCompleto.setDisable(false);
							}
						});

					} catch (Exception e) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								showMensagem("Ocorreu um erro ao fazer o merge das capas + contracheques: \n\n"
										+ e.getMessage(), AlertType.ERROR);
							}
						});
					}

				} catch (Exception e) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							showMensagem("Ocorreu um erro ao processar as capas: \n\n" + e.getMessage(),
									AlertType.ERROR);
						}
					});
				}

				return null;
			}
		};
	}

	public Task<Object> taskProcessaCompleto() {

		return new Task<Object>() {

			@Override
			protected Object call() {

				try {
					MergeContraChequeService.processaMerge(folderIndividual.getAbsolutePath(),
							folderCompleto.getAbsolutePath(), fileNameXlsxOut, periodo);

					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							showMensagem("Arquivo completo dos contracheques processados com sucesso!",
									AlertType.INFORMATION);
							btnProcCompleto.setDisable(true);
						}
					});

				} catch (Exception e) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							showMensagem("Ocorreu um erro ao gerar o Contracheque completo: \n\n" + e.getMessage(),
									AlertType.ERROR);
						}
					});
				}
				return null;
			}
		};
	}

	public Task<Object> taskProcessaCriptografia() {

		return new Task<Object>() {

			@Override
			protected Object call() {

				String urlIntegrantes = "http://api-baseunica.net/api/pessoas?fields=cpf,idPeople,email&filter=.&limit=0";
				try {

					CriptografiaService cps = new CriptografiaService();

					cps.criptografaPdfs(folderSemCapa.getAbsolutePath(), folderCriptografado.getAbsolutePath(),
							fileNameXlsxOut, urlIntegrantes, "Pass@2018cc");

					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							setTableView(cps.getIntegrantesComEmail());
							labelQtdEmails.setText("Quantidade de Integrantes: " + cps.getIntegrantesComEmail().size());
							showMensagem(
									"Contracheques criptografados processados com sucesso! \n\n\n Vá para a aba Enviar Contracheques, para enviar por email para os Integrantes",
									AlertType.INFORMATION);
							btnEnviar.setDisable(false);
							btnProcCriptografa.setDisable(true);
							btnProcIndividual.setDisable(false);
						}
					});

				} catch (Exception e) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							showMensagem("Ocorreu um erro ao criptografar os contracheques: \n\n" + e.getMessage(),
									AlertType.ERROR);
						}
					});
				}
				return null;
			}
		};
	}

	public Task<Object> taskProcessaEnvio() {

		return new Task<Object>() {

			@Override
			protected Object call() {

				String src = folderCriptografado.getAbsolutePath();
				String urlIntegrantes = "http://api-baseunica.net/api/pessoas?fields=cpf,idPeople,email&filter=.&limit=0";
				try {
					String emailRemetente = IntegrantesUtil.getEmail(System.getProperty("user.name"));
					EmailService.enviaEmail(urlIntegrantes, src, emailRemetente);

					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							showMensagem("E-mails enviados com sucesso!", AlertType.INFORMATION);
						}
					});

				} catch (Exception e) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							showMensagem("Ocorreu ao enviar os emails: \n\n" + e.getMessage(), AlertType.ERROR);
						}
					});
				}

				return null;
			}
		};
	}

	public List<InputStream> getFilesLotes() {
		List<InputStream> filesLotes = new ArrayList<InputStream>();

		try {
			filesLotes.add(new FileInputStream(new File(fileNameLote1)));
			filesLotes.add(new FileInputStream(new File(fileNameLote2)));
			filesLotes.add(new FileInputStream(new File(fileNameLote3)));
			filesLotes.add(new FileInputStream(new File(fileNameLote4)));
			filesLotes.add(new FileInputStream(new File(fileNameLote5)));
		} catch (FileNotFoundException e) {
			LogUtil.printLine(e.getLocalizedMessage());
		}
		return filesLotes;
	}

	public void desabilitaBotoes(boolean bool) {
		btnProcIndividual.setDisable(bool);
		btnProcCompleto.setDisable(bool);
		btnProcCriptografa.setDisable(bool);
		btnProcLotes.setDisable(bool);
	}

	public void desabilitaTab(boolean bool) {
		tab1.setDisable(bool);
		tab2.setDisable(bool);
		tab3.setDisable(bool);
	}

	public void criarPastas() throws Exception {

		folderSemCapa = new File(folderProc + File.separator + "sem_capa");
		folderCapa = new File(folderProc + File.separator + "capa");
		folderIndividual = new File(folderProc + File.separator + "individual");
		folderCompleto = new File(folderProc + File.separator + "completo");
		folderLoteMerge = new File(folderProc + File.separator + "lote");
		folderCriptografado = new File(folderProc + File.separator + "criptografado");

		folderSemCapa.mkdirs();
		folderCapa.mkdirs();
		folderIndividual.mkdirs();
		folderCompleto.mkdirs();
		folderLoteMerge.mkdirs();
		folderCriptografado.mkdirs();

		LogUtil.printLine("Pasta criada: " + folderSemCapa.getAbsolutePath());
		LogUtil.printLine("Pasta criada: " + folderCapa.getAbsolutePath());
		LogUtil.printLine("Pasta criada: " + folderIndividual.getAbsolutePath());
		LogUtil.printLine("Pasta criada: " + folderCompleto.getAbsolutePath());
		LogUtil.printLine("Pasta criada: " + folderLoteMerge.getAbsolutePath());
		LogUtil.printLine("Pasta criada: " + folderCriptografado.getAbsolutePath());
		LogUtil.appendLog();

		printLogTxtArea();

	}

	public void limparPastas() throws IOException {
		FileUtils.deleteDirectory(folderSemCapa);
		FileUtils.deleteDirectory(folderCapa);
		FileUtils.deleteDirectory(folderIndividual);
		FileUtils.deleteDirectory(folderCompleto);
		FileUtils.deleteDirectory(folderLoteMerge);
	}

	public void printLogTxtArea() {
		txtAreaLog.setText(LogUtil.getLog());
	}

	public boolean itensSelecionados() {

		boolean validador = true;

		if (txtPeriodo.getText() == null || txtPeriodo.getText().equals("")) {
			alertaMsg += "\n ...informe o período de processamento";
			validador = false;
		}

		if (labelFolderProc.getText() == null || labelFolderProc.getText().equals("")) {
			alertaMsg += "\n ...selecione a pasta de Processamento";
			validador = false;
		}

		if (labelCSV.getText() == null || labelCSV.getText().equals("")) {
			alertaMsg += "\n ...selecione o arquivo Excel (.xlsx)";
			validador = false;
		}

		if (labelLote1.getText() == null || labelLote1.getText().equals("")) {
			alertaMsg += "\n ...selecione o arquivo PDF do Lote 1";
			validador = false;
		}

		if (labelLote2.getText() == null || labelLote2.getText().equals("")) {
			alertaMsg += "\n ...selecione o arquivo PDF do Lote 2";
			validador = false;
		}

		if (labelLote3.getText() == null || labelLote3.getText().equals("")) {
			alertaMsg += "\n ...selecione o arquivo PDF do Lote 3";
			validador = false;
		}

		if (labelLote4.getText() == null || labelLote4.getText().equals("")) {
			alertaMsg += "\n ...selecione o arquivo PDF do Lote 4";
			validador = false;
		}

		if (labelLote5.getText() == null || labelLote5.getText().equals("")) {
			alertaMsg += "\n ...selecione o arquivo PDF do Lote 5";
			validador = false;
		}

		return validador;
	}

	public void sair() throws Exception {
		System.out.println("O usuário " + System.getProperty("user.name") + " fechou a aplicação");
		LogUtil.appendLog();
		System.exit(0);
	}

	public void showAlerta(Alert alerta) {
		alerta.getDialogPane().getStylesheets()
				.add(getClass().getResource("/com/contrachequeUI/view/application.css").toExternalForm());
		alerta.getDialogPane().setHeaderText(null);
		alerta.initStyle(StageStyle.UNDECORATED);
		alerta.show();
	}

	public void updateMensagemProgresso(String mensagem, boolean bool) {
		labelProgress.setVisible(bool);
		labelProgress.setText(mensagem);
	}

	public void showMensagem(String mensagem, AlertType tipo) {

		updateMensagemProgresso(null, false);
		desabilitaTab(false);
		progressBar.setVisible(false);
		progressBar.progressProperty().unbind();
		alertaMsg = mensagem;
		LogUtil.printLine(mensagem);
		LogUtil.appendLog();
		printLogTxtArea();
		Alert alerta = new Alert(tipo);
		alerta.setContentText(alertaMsg);
		showAlerta(alerta);
	}

	@SuppressWarnings("unchecked")
	public void setTableView(List<Integrante> integrantes) {

		tbVEmail.setPrefWidth(561);
		TableColumn<Integrante, String> col1 = new TableColumn<>("ID");
		col1.setPrefWidth(70);
		col1.setCellValueFactory(new PropertyValueFactory<>("idPeople"));

		TableColumn<Integrante, String> col2 = new TableColumn<>("Nome");
		col2.setPrefWidth(291);
		col2.setCellValueFactory(new PropertyValueFactory<>("nome"));

		TableColumn<Integrante, String> col3 = new TableColumn<>("E-mail");
		col3.setPrefWidth(200);
		col3.setCellValueFactory(new PropertyValueFactory<>("email"));

		tbVEmail.getColumns().addAll(col1, col2, col3);

		ObservableList<Integrante> items = FXCollections.observableArrayList(integrantes);

		tbVEmail.setItems(items);
	}

}
