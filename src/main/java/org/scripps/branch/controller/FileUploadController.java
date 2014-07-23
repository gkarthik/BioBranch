package  org.scripps.branch.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.internal.util.xml.ErrorLogger;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.User;
import org.scripps.branch.repository.DatasetRepository;
import org.scripps.branch.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handles requests for the application file upload requests
 */
@Controller
public class FileUploadController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FileUploadController.class);

	@Autowired
	UserRepository userRepo;

	@Autowired
	DatasetRepository dataRepo;

	/**
	 * Upload single file using Spring Controller
	 */
	@RequestMapping(value = "/uploadFile", method = RequestMethod.GET)
	public String uploadFileHandler(WebRequest request, Model model) {
		LOGGER.debug("Rendering homepage.");
		return "user/upload";
	}
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public @ResponseBody
	String uploadFileHandler(@RequestParam("name") String name,
			@RequestParam("file") MultipartFile file) {

		//restrictor validate the file by the user with the file type using .getContentType (appliation/pdf, text/plain...
		if (!file.isEmpty() ) {
			try {
				byte[] bytes = file.getBytes();
				LOGGER.debug("File content type"+file.getContentType());
				// Creating the directory to store file
				String rootPath = System.getProperty("catalina.home");
				File dir = new File("/home/bob/workspace/branch/src/main/resources/uploads");
				if (!dir.exists())	
					dir.mkdirs();

				// Create the file on server
				File serverFile = new File(dir.getAbsolutePath()
						+ File.separator + name);
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();

				LOGGER.info("Server File Location="
						+ serverFile.getAbsolutePath());

				return "You successfully uploaded file=" + name;
			} catch (Exception e) {
				return "You failed to upload " + name + " => " + e.getMessage();
			}
		} else {
			return "You failed to upload " + name
					+ " because the file was empty.";
		}
	}

	/**
	 * Upload multiple file using Spring Controller
	 */

	@RequestMapping(value = "/uploadMultipleFile", method = RequestMethod.GET)
	public String uploadMultipleFileHandler(WebRequest request, Model model) {
		LOGGER.debug("Rendering Multiple upload page");
		return "user/uploadMultiple";
	}
	
	@SuppressWarnings("null")
	@RequestMapping(value = "/uploadMultipleFile", method = RequestMethod.POST)
	public @ResponseBody
	String uploadMultipleFileHandler(@RequestParam("name") String[] names,
			@RequestParam("file") MultipartFile[] files,@RequestParam("description") String description,@RequestParam("datasetName")String datasetName) {

		//LOGGER.debug("description :  "+datasetName);
		LOGGER.debug("description :  "+description);
		
		if (files.length != names.length)
			return "Mandatory information missing";
		
		
		String message = "";
		UserDetails userDetails = null;
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		Dataset dsObj=new Dataset();
		User user = null;
		String[] fileType = new String[3];
		fileType[0]="dataset";
		fileType[1]="mapping";
		fileType[2]="feature";
		
		if (!(auth instanceof AnonymousAuthenticationToken)) {


			userDetails = (UserDetails) auth.getPrincipal();
			user = userRepo.findByEmail(userDetails.getUsername());
			LOGGER.debug("Login Checker "+ user);
		}
		String[] md5FileName = new String[3];

		for (int i = 0; i < files.length; i++) {
			MultipartFile file = files[i];
			String name = names[i];
			//String ext = FilenameUtils.getExtension(file.toString());
			//LOGGER.debug("check extention: "+ext);
			try {
				byte[] bytes = file.getBytes();
				
				// Creating the directory to store file
				String rootPath = System.getProperty("catalina.home");
				File dir = new File("/home/bob/workspace/branch/src/main/resources/uploads");
				if (!dir.exists())
					dir.mkdirs();

				md5FileName[i]= hashFileName((String) (name+user.getId()+ System.currentTimeMillis()+fileType[i]));
				
				LOGGER.debug(md5FileName[i]);
				// Create the file on server
				File serverFile = new File(dir.getAbsolutePath()
						+ File.separator + md5FileName[i]);//name
				
//				md5FileName[i]= hashFileName(name);
//				LOGGER.debug("MD5FileName"+md5FileName[i]);



				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(serverFile));
				LOGGER.debug("");
				stream.write(bytes);
				stream.close();

				LOGGER.info("Server File Location="
						+ serverFile.getAbsolutePath());

				message = message + "You successfully uploaded file=" + name
						+ "<br />";


			} catch (Exception e) {
				return "You failed to upload " + name + " => " + e.getMessage();
			}
		}


		LOGGER.debug("outside if"+ user);

		if (!(auth instanceof AnonymousAuthenticationToken)) {

			LOGGER.debug("Login Checker");
			//userDetails = (UserDetails) auth.getPrincipal();
			//user = userRepo.findByEmail(userDetails.getUsername());
			dsObj.setDatasetname(names[0]);
			dsObj.setMappingname(names[1]);
			dsObj.setFeaturename(names[2]);
			dsObj.setDatasetfile(md5FileName[0]);
			dsObj.setFeaturefile(md5FileName[1]);
			dsObj.setMappingfile(md5FileName[2]);
			dsObj.setDescription(description);
			dsObj.setName(datasetName);
			dsObj.setUser(user);
			dsObj= dataRepo.saveAndFlush(dsObj);

		}

		return message;
	}


	public String hashFileName(String name){

		//		if (name.length() != 1) {
		//			System.err.println("String to MD5 digest should be first and only parameter");
		//			return "error";
		//		}
		//		String original = args[0];
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		md.update(name.getBytes());
		byte[] digest = md.digest();
		StringBuffer sb = new StringBuffer();
		for (byte b : digest) {
			sb.append(String.format("%02x", b & 0xff));
		}

		//		System.out.println("original:" + original);
		LOGGER.debug("digested(hex):" + sb.toString());

		return sb.toString();
	}

}
