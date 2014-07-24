package org.scripps.branch.service;

import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.User;

public interface DatasetService {

	boolean uploadFiles(Dataset dsObj, User userDetails);

}
