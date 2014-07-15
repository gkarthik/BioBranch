package org.scripps.branch.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class ImportPathwayfromMySQlToPostgres {

	public static void main(String args[]) throws Exception {
		System.out.println("-------- PostgreSQL "
				+ "JDBC Connection Testing ------------");
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("PostGreSql Driver not in Path!");
			e.printStackTrace();
			return;
		}
		System.out.println("PostgreSQL JDBC Driver Registered!");
		Connection conn = null;
		try {
			String url = "jdbc:postgresql://127.0.0.1:5432/branch_dev";
			Properties props = new Properties();
			props.setProperty("user", "postgres");
			props.setProperty("password", "prime");
			conn = DriverManager.getConnection(url, props);
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}
		if (conn != null) {
			System.out.println("Connected! Postgres");
		} else {
			System.out.println("Failed to make connection!");
		}
		Connection connmysql = null;
		try {
			String url = "jdbc:mysql://127.0.0.1:3306/cure";
			Properties props = new Properties();
			props.setProperty("user", "root");
			props.setProperty("password", "prime");
			connmysql = DriverManager.getConnection(url, props);
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}
		if (connmysql != null) {
			System.out.println("Connected!MYSQL");
		} else {
			System.out.println("Failed to make connection!");
		}
		PreparedStatement statement;
		// Import into pathway.
		// String queryMysql =
		// "select @rownum := @rownum + 1 as rownum, b.name,b.source_db from (select * from cpdb_pathway group by name,source_db order by id) b, (select @rownum:=0) v order by rownum";
		// PreparedStatement preparedStatement =
		// connmysql.prepareStatement(queryMysql);
		// ResultSet rs = preparedStatement.executeQuery(queryMysql);
		// int ctr = 0;
		// while(rs.next()){
		// statement = (PreparedStatement)
		// conn.prepareStatement("insert into pathway values(?,?,?)",
		// Statement.RETURN_GENERATED_KEYS);
		// statement.setInt(1, rs.getInt(1));
		// statement.setString(2, rs.getString(2));
		// statement.setString(3, rs.getString(3));
		// System.out.println(rs.getInt(1)+": "+rs.getString(2));
		// int affectedRows = statement.executeUpdate();
		// if (affectedRows == 0) {
		// throw new SQLException("db failed");
		// }
		// System.out.println("Inserted "+ctr);
		// ctr++;
		// }
		// Import into pathway_feature.
		String queryMysql = "select orig.rownum, feature.id, cpdb_pathway.name, cpdb_pathway.source_db from (select @rownum := @rownum + 1 as rownum, b.name,b.source_db from (select * from cpdb_pathway group by name,source_db order by id) b, (select @rownum:=0) v order by rownum) orig, cpdb_pathway, feature where orig.name=cpdb_pathway.name and orig.source_db=cpdb_pathway.source_db and cpdb_pathway.entrez_id = feature.unique_id";
		PreparedStatement preparedStatement = connmysql
				.prepareStatement(queryMysql);
		ResultSet rs = preparedStatement.executeQuery(queryMysql);
		int ctr = 0;
		while (rs.next()) {
			statement = conn.prepareStatement(
					"insert into pathway_feature values(?,?)",
					Statement.RETURN_GENERATED_KEYS);
			statement.setInt(1, rs.getInt(1));
			statement.setInt(2, rs.getInt(2));
			System.out.println(rs.getInt(1) + ": " + rs.getString(3)
					+ " - Source: " + rs.getString(4) + " -> " + rs.getInt(2));
			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0) {
				throw new SQLException("db failed");
			}
			System.out.println("Inserted " + ctr);
			ctr++;
		}
		conn.close();
	}
	// private static java.sql.Timestamp getCurrentTimeStamp() {
	// java.util.Date today = new java.util.Date();
	// return new java.sql.Timestamp(today.getTime());
	// }

}
