//package Tests;
//
//import javax.sql.DataSource;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.transaction.annotation.Transactional;
//
//@Transactional
//class JDBCTemplate {
//
//	private static final Logger LOGGER = LoggerFactory
//			.getLogger(JDBCTemplate.class);
//
//	private DataSource dataSource;
//	private JdbcTemplate jdbcTemplate;
//
//	public void insert(Test customer) {
//
//		String Sql = "Insert into test values (2, ' Vyshakh')";
//
//		LOGGER.debug("JDBC TEMPLATE");
//
//		jdbcTemplate = new JdbcTemplate(dataSource);
//
//		jdbcTemplate.update(Sql);
//	}
//
//	public void setDataSource(DataSource dataSource) {
//
//		this.dataSource = dataSource;
//
//	}
//
// }
