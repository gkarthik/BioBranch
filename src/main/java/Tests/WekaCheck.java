package Tests;
import java.io.FileInputStream;
import java.io.InputStream;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WekaCheck {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(WekaCheck.class);

	public static void main(String args[]) {

		// Read all the instances in the file (ARFF, CSV, XRFF, ...)
		System.out.println("checking");
//		
//		try {
//			DataSource source1 = new DataSource("/home/bob/Metabric_clinical_expression_DSS_sample_filtered.arff");
//			DataSource source2 = new DataSource("/home/bob/Oslo_clinical_expression_OS_sample_filt.arff");
//			Attribute attribute;
//			Instances instances = source1.getDataSet();
//			instances=source2.getDataSet();
//			//System.out.println("\nDataset:\n"+instances);
//			// Make the last attribute be the class
//			//instances.setClassIndex(instances.numAttributes() - 1);
//			System.out.println("\nDataset:\n");
//			System.out.println(source1.getStructure().equalHeaders(instances));
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		Weka wekaObj1;
		Weka wekaObj2;
		try {
			wekaObj1 = new Weka();
			InputStream source1 = new FileInputStream(
					"/home/bob/Metabric_clinical_expression_DSS_sample_filtered.arff");
			wekaObj1.buildWeka(source1, null, "set1", "metabric_with_clinical");
			wekaObj2 = new Weka();
			InputStream source2 = new FileInputStream(
					"/home/bob/Oslo_clinical_expression_OS_sample_filt.arff");
			wekaObj2.buildWeka(source2, null, "set2",
					"Oslo_clinical_expression");
			boolean value=wekaObj1.getTrain().equalHeaders(wekaObj2.getTrain());
			
			
			LOGGER.debug("value="+value);
			System.out.println("checking");

		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error("Error!",e);
		}
	}





}
