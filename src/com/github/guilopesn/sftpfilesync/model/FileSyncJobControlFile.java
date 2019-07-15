package com.github.guilopesn.sftpfilesync.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileSyncJobControlFile extends File {

    private static final long serialVersionUID = -755824318193370704L;

    private static Logger logger = LogManager.getLogger();

    private Set<File> files = new HashSet<>();

    public FileSyncJobControlFile(String syncJobControlFileName) {

	super(syncJobControlFileName + ".syncjobcontrolfile");

	logger.info("Looking for an existing sync job control file");

	try (Reader controlFileReader = new FileReader(this);
		BufferedReader bufferedReader = new BufferedReader(controlFileReader)) {

	    logger.info("Sync job control file found! Parsing data");

	    String line = bufferedReader.readLine();

	    if (line != null && !line.isEmpty()) {
		this.files.add(new File(line));
	    }

	    while (line != null) {
		line = bufferedReader.readLine();
		if (line != null && !line.isEmpty()) {
		    this.files.add(new File(line));
		}
	    }
	} catch (FileNotFoundException fileNotFoundException) {

	    logger.error("Sync job control file not found! Creating a new one");

	    try {
		this.createNewFile();
		logger.info("Sync job control file creation succeed!");
	    } catch (IOException ioException) {

		logger.fatal("Could not create sync job control file! Exception: " + ioException.getClass().getName()
			+ " Message: " + ioException.getMessage());

		throw new Error("Could not create sync job control file! Exception: " + ioException.getClass().getName()
			+ " Message: " + ioException.getMessage());
	    }

	} catch (IOException ioException) {

	    logger.fatal("Could not read sync job control file! Exception: " + ioException.getClass().getName()
		    + " Message: " + ioException.getMessage());

	    throw new Error("Could not read sync job control file! Exception: " + ioException.getClass().getName()
		    + " Message: " + ioException.getMessage());
	}

    }

    public boolean contains(File file) {
	return this.files.contains(file);
    }

    public void add(File file) {

	logger.info("Adding file " + file.getName() + " to sync job control file");

	this.files.add(file);

	this.writeToFile();
    }

    private void writeToFile() {

	try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(this)))) {

	    for (File file : this.files) {

		String filePath = file.getCanonicalPath();

		if (filePath.contains("\\")) {
		    filePath = filePath.replace("\\", "\\\\");
		}

		writer.println(filePath);
	    }

	} catch (IOException ioException) {

	    logger.fatal("Could not write into sync job control file! Exception: " + ioException.getClass().getName()
		    + " Message: " + ioException.getMessage());

	    throw new Error("Could not write into sync job control file! Exception: " + ioException.getClass().getName()
		    + " Message: " + ioException.getMessage());
	}
    }
}