package com.contrachequeUI.application;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.contrachequeUI.util.LogUtil;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@SpringBootApplication
public class Main extends Application {

	private Parent rootNode;
	

	@Override
	public void init() throws Exception {
		FXMLLoader fxmlloader = new FXMLLoader(
				getClass().getResource("/com/contrachequeUI/view/FXMLTela.fxml"));
		rootNode = fxmlloader.load();
	}

	@Override
	public void start(Stage primaryStage) {
		Scene scene = new Scene(rootNode);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Contracheque UI");
		primaryStage.initStyle(StageStyle.UNDECORATED);
		rootNode.setStyle("-fx-border: 1px;");
		primaryStage.show();

		String user = System.getProperty("user.name");
		validaUsuario(user);
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void stop() throws Exception {
		System.out.println("O usuário " + System.getProperty("user.name") + " fechou a aplicação");
		LogUtil.appendLog();
		System.exit(0);
	}

	public void validaUsuario(String user) {

		String user1 = "00049493";
		String user2 = "00015543";

		if (!user.equals(user1) && !user.equals(user2)) {
			Alert alerta = new Alert(AlertType.WARNING);
			alerta.setContentText("Acesso negado, você não tem permissão para usar essa aplicação!");
			alerta.getDialogPane().getStylesheets()
					.add(getClass().getResource("/com/contrachequeUI/view/application.css").toExternalForm());
			alerta.getDialogPane().setHeaderText(null);
			alerta.initStyle(StageStyle.UNDECORATED);
			alerta.showAndWait();
			LogUtil.printLine(alerta.getContentText());
			System.exit(0);
		}

	}
}
