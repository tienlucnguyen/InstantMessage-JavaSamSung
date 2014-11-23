package samsung.sip.agent.agent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class UserLogIn extends Application {

	Button btnLogin;
	Button btnCreate;
	TextField txtUser;
	TextField txtPass;
	Connection connect;
	CheckBox checkPass;
	Statement st;

	@Override
	public void start(Stage primaryStage) {
		try {

			Parent root = FXMLLoader.load(getClass().getResource("/samsung/sip/giaodien/LogIn.fxml"));
			Scene scene = new Scene(root, 378, 296);
			scene.getStylesheets().add(
					getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
			primaryStage.setTitle("Welcome");
			btnCreate = (Button) scene.lookup("#btnCreate");
			btnLogin = (Button) scene.lookup("#btnLogIn");
			txtPass = (TextField) scene.lookup("#textPass");
			txtUser = (TextField) scene.lookup("#textName");
			checkPass = (CheckBox) scene.lookup("#checkPass");
			connectDatabase();

			txtPass.setOnMouseClicked(new EventHandler<Event>() {

				@Override
				public void handle(Event arg0) {

					String userName = txtUser.getText().toString().trim();
					String pass = "";
					int rememberPass = 0;

					try {

						st = connect.createStatement();
						ResultSet rs = st
								.executeQuery("Select * from account where name='"
										+ userName + "'");

						if (rs.next()) {
							pass = rs.getString(3);
							rememberPass = rs.getInt(4);

						}
						if (rememberPass == 1)
							txtPass.setText(pass);

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});

			btnLogin.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent arg0) {
					String userName = txtUser.getText().toString().trim();
					String pass = txtPass.getText().toString().trim();
					Statement st;
					try {
						st = connect.createStatement();
						ResultSet rs = st
								.executeQuery("Select password from account where name='"
										+ userName + "'");
						if (rs.next()) {
							if (rs.getString(1).equalsIgnoreCase(pass)) {

								JOptionPane.showMessageDialog(null,
										"Đăng nhập thành công");

								primaryStage.close();
								new SipAgent().start(new Stage());
								
		

							}

							else
								JOptionPane.showMessageDialog(null,
										"Sai mật khẩ xin mời nhập lại");

						} else
							JOptionPane.showMessageDialog(null,
									"Tài khoản này không tồn tại");

						if (checkPass.isSelected())

							st.executeUpdate("update account set remember=1 where name='"
									+ userName + "'");
						else
							st.executeUpdate("update account set remember=0 where name='"
									+ userName + "'");

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void connectDatabase() {

		connect = null;
		String url = "jdbc:mysql://localhost/agent";
		String user = "vankhang";
		String password = "150899";

		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Ket noi thanh cong toi co so du lieu");
			connect = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block

			System.out.println(e.toString());
			System.out.println("Khong the ket noi toi co so du lieu");
			return;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			return;

		}

		if (connect == null)
			System.out.println("Khong the ket noi");
		else
			System.out.println("Ket noi dc");

		// --------------------------------------------------
		try {
			Statement st = connect.createStatement();
			String userName = "vankhang";
			ResultSet rs = st.executeQuery("Select * from account where name='"
					+ userName + "'");

			if (rs.next()) {

				int re = rs.getInt(4);
				if (re == 1)
					checkPass.setSelected(true);
				else

					checkPass.setSelected(false);

			}
			rs.close();
			st.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		launch(args);

	}

}
