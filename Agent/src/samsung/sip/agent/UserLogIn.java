package samsung.sip.agent;
	
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class UserLogIn extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			
			connectDatabase();
			Parent root=FXMLLoader.load(getClass().getResource("LogIn.fxml"));
			Scene scene = new Scene(root,378,267);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("Welcome");
		
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void connectDatabase(){
		
		
		Connection connect = null;
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
			ResultSet rs = st.executeQuery("Select * from account");
			while (rs.next()) {

				System.out.println(rs.getInt(1) + ": " + rs.getString(2));

				// System.out.println(rs.getString(1));

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
