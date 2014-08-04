package org.scripps.branch.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.scripps.branch.entity.Collection;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.User;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.globalentity.DatasetMap;
import org.scripps.branch.repository.CollectionRepository;
import org.scripps.branch.repository.DatasetRepository;
import org.scripps.branch.repository.UserRepository;
import org.scripps.branch.service.AttributeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileUploadController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FileUploadController.class);

	private static final String UPLOAD = "user/uploadMultiple";

	@Autowired
	AttributeService attrSer;

	@Autowired
	CollectionRepository colRepo;

	@Autowired
	DatasetRepository dataRepo;

	@Autowired
	private Job job;

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	UserRepository userRepo;

	@Autowired
	DatasetMap wekaobj;

	@Autowired
	ApplicationContext ctx;
	
	@Autowired 
	String uploadPath; 

	public String hashFileName(String name) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
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

	private String runFeatureUpload(String filePath) {
		JobParameters jp = new JobParametersBuilder().addString("inputPath", filePath).toJobParameters();
		try {
			JobExecution jobExecution = jobLauncher.run(job, jp);
			return "Feature table added";
		} catch (JobExecutionAlreadyRunningException | JobRestartException
				| JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "Unable to add feature table";
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public String uploadMultipleFileHandler(
			@RequestParam("file") MultipartFile[] files,
			@RequestParam("description") String description,
			@RequestParam("datasetName") String datasetName,
			@RequestParam("collectionId") long collectionId, WebRequest req) {
		String privateSet = "";
		if (req.getParameter("private") != null) {
			privateSet = req.getParameter("private");
		}
		 Weka weka = new Weka();
		Collection col = colRepo.findById(collectionId);
		String message = "";
		UserDetails userDetails = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Dataset ds = new Dataset();
		User user = null;
		String[] fileType = new String[3];
		fileType[0] = "dataset";
		fileType[1] = "mapping";
		fileType[2] = "feature";
		String[] names = new String[3];
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			userDetails = (UserDetails) auth.getPrincipal();
			user = userRepo.findByEmail(userDetails.getUsername());
		}
		for (int i = 0; i < files.length; i++) {
			MultipartFile file = files[i];
			names[i] = file.getOriginalFilename();
		}
		ds.setDatasetname(names[0]);
		ds.setMappingname(names[1]);
		ds.setFeaturename(names[2]);
		ds.setDescription(description);
		ds.setName(datasetName);
		if (privateSet.equals("1"))
			ds.setPrivateset(true);
		else
			ds.setPrivateset(false);
		ds.setCollection(col);
		ds = dataRepo.saveAndFlush(ds);
		String[] md5FileName = new String[3];
		int i;
		File serverFile = null;
		Boolean check = false;
		for (i = 0; i < files.length; i++) {
			MultipartFile file = files[i];
			String name = file.getOriginalFilename();
			names[i] = file.getOriginalFilename();
			LOGGER.debug("File Name: " + name);
			try {
				byte[] bytes = file.getBytes();
				File dir = new File(uploadPath);
				if (!dir.exists()){
					dir.mkdirs();
				}
				md5FileName[i] = hashFileName(name + user.getId() + System.currentTimeMillis() + fileType[i]);
				LOGGER.debug("MD5 File Name: " + md5FileName[i]);
				serverFile = new File(dir.getAbsolutePath() + File.separator + md5FileName[i]);
				LOGGER.debug("MD5 FileName with path" + serverFile);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();
				message = message + "You successfully uploaded file, " + name;
				if (i == 0) {
					InputStream path1 = ctx.getResource("file:" + serverFile).getInputStream();
					InputStream path2 = null;
					if(col.getDatasets().size()>1){
						path2 = new FileInputStream(uploadPath+col.getDatasets().get(0).getDatasetfile());
					} else {
						check = true;
					}
					if (path2 != null && path1 != null && check==false) {
						check = weka.checkDataset(path1, path2);
					}
					if (check == false) {
						serverFile.delete();
						return "Dataset header does not match any other in collection";
					}
				}
				 if (i == 2) {
				 System.out.println("file:" + serverFile.toString());
				 weka.buildWeka(ctx.getResource("file:"+ uploadPath + md5FileName[0]).getInputStream(), null, "");
				 attrSer.generateAttributesFromDataset(weka.getTrain(), ds, serverFile.toString());
				 message = message + "attribute file added";
				 }
				
				 if (i == 1) {
					  message = message +
					  runFeatureUpload(serverFile.toString());
				 }
			} catch (Exception e) {
				LOGGER.error("Exception",e);
			}
		}
		if (!(auth instanceof AnonymousAuthenticationToken && check == true)) {
			ds.setDatasetfile(md5FileName[0]);
			ds.setMappingfile(md5FileName[1]);
			ds.setFeaturefile(md5FileName[2]);
			ds = dataRepo.saveAndFlush(ds);
			LOGGER.debug("Success");
		} else {
			LOGGER.debug("Deleted");
			dataRepo.delete(ds);
		}
		return "redirect:/";
	}

	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public String uploadMultipleFileHandler(WebRequest request, Model model) {
		LOGGER.debug("Rendering Multiple upload page");
		return UPLOAD;
	}
}
