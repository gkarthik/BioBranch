package org.scripps.branch.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletContext;

import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.Before;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.User;
import org.scripps.branch.repository.DatasetRepository;
import org.scripps.branch.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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

	@Autowired
	private Job job;
	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	ServletContext ctx;

	public String hashFileName(String name) {
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
		return sb.toString();
	}

	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public @ResponseBody String uploadFileHandler(
			@RequestParam("name") String name,
			@RequestParam("file") MultipartFile file) {

		// restrictor validate the file by the user with the file type using
		// .getContentType (appliation/pdf, text/plain...
		if (!file.isEmpty()) {
			try {
				byte[] bytes = file.getBytes();
				LOGGER.debug("File content type" + file.getContentType());
				// Creating the directory to store file
				String rootPath = System.getProperty("catalina.home");
				File dir = new File(
						"/home/bob/workspace/branch/src/main/resources/uploads");
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
	 * Upload single file using Spring Controller
	 */
	@RequestMapping(value = "/uploadFile", method = RequestMethod.GET)
	public String uploadFileHandler(WebRequest request, Model model) {
		LOGGER.debug("Rendering homepage.");
		return "user/upload";
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody String uploadMultipleFileHandler(@RequestParam("file") MultipartFile[] files, @RequestParam("description") String description,	@RequestParam("datasetName") String datasetName) {
		String message = "";
		UserDetails userDetails = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Dataset dsObj = new Dataset();
		User user = null;
		String[] fileType = new String[3];
		fileType[0] = "dataset";
		fileType[1] = "mapping";
		fileType[2] = "feature";
		String[] names = new String[3];
		if (!(auth instanceof AnonymousAuthenticationToken)){
			userDetails = (UserDetails) auth.getPrincipal();
			user = userRepo.findByEmail(userDetails.getUsername());
		}
		String[] md5FileName = new String[3];
		int i;
		File serverFile = null;
		for (i = 0; i < files.length; i++) {
			MultipartFile file = files[i];
			String name = file.getOriginalFilename();
			names[i] = file.getOriginalFilename();
			LOGGER.debug("File Name: " + name);
			try {
				byte[] bytes = file.getBytes();
				File dir = new File("/home/bob/uploads/");
				if (!dir.exists())
					dir.mkdirs();
				md5FileName[i] = hashFileName(name + user.getId() + System.currentTimeMillis() + fileType[i]);
				LOGGER.debug("MD5 File Name: " + md5FileName[i]);
				serverFile = new File(dir.getAbsolutePath() + File.separator + md5FileName[i]);
				LOGGER.debug("MD5 FileName with path" + serverFile);
				BufferedOutputStream stream = new BufferedOutputStream(	new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();
				message = message + "You successfully uploaded file=" + name + "<br />";
				if (i == 2) {
					message = message + runFeatureUpload(serverFile.toString());
				}
			} catch (Exception e) {
				return "You failed to upload " + name + " => " + e.getMessage();
			}
		}
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			dsObj.setDatasetname(names[0]);
			dsObj.setMappingname(names[1]);
			dsObj.setFeaturename(names[2]);
			dsObj.setDatasetfile(md5FileName[0]);
			dsObj.setMappingfile(md5FileName[1]);
			dsObj.setFeaturefile(md5FileName[2]);
			dsObj.setDescription(description);
			dsObj.setName(datasetName);
			dsObj.setUser(user);
			dsObj = dataRepo.saveAndFlush(dsObj);
		}
		return message;
	}

	private String runFeatureUpload(String filePath) {
		JobParameters jp = new JobParametersBuilder().addString("inputPath", filePath).toJobParameters();
		try {
			JobExecution jobExecution = jobLauncher.run(job, jp);
			return "Feature table added";
		} catch (JobExecutionAlreadyRunningException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JobRestartException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JobInstanceAlreadyCompleteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JobParametersInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Unable to add feature table";
	}

	/**
	 * Upload multiple file using Spring Controller
	 */

	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public String uploadMultipleFileHandler(WebRequest request, Model model) {
		LOGGER.debug("Rendering Multiple upload page");
		return "user/uploadMultiple";
	}
}
