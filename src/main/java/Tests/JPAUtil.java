//package Tests;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
//import java.sql.Statement;
//
//public class JPAUtil {
//	Statement st;
//
//	public JPAUtil() throws Exception {
//		Class.forName("org.postgresql.Driver");
//		System.out.println("Driver Loaded.");
//		String url = "jdbc:postgresql://localhost:5432/UserAccount";
//
//		Connection conn = DriverManager.getConnection(url, "postgres",
//				"Ksrmk12345");
//		System.out.println("Got Connection.");
//		st = conn.createStatement();
//	}
//
//	public void checkData(String sql) throws Exception {
//		ResultSet rs = st.executeQuery(sql);
//		ResultSetMetaData metadata = rs.getMetaData();
//
//		for (int i = 0; i < metadata.getColumnCount(); i++) {
//			System.out.print("\t" + metadata.getColumnLabel(i + 1));
//		}
//		System.out.println("\n----------------------------------");
//
//		while (rs.next()) {
//			for (int i = 0; i < metadata.getColumnCount(); i++) {
//				Object value = rs.getObject(i + 1);
//				if (value == null) {
//					System.out.print("\t       ");
//				} else {
//					System.out.print("\t" + value.toString().trim());
//				}
//			}
//			System.out.println("");
//		}
//	}
//
//	public void executeSQLCommand(String sql) throws Exception {
//		st.executeUpdate(sql);
//	}
// }
