package com.github.guilopesn.sftpfilesync.model;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileSyncJob implements Runnable {

    private static Logger logger = LogManager.getLogger();

    private final String name;
    private final File source;
    private final String filesToIgnoreRegex;
    private final String destination;
    private final boolean overwriteondestination;
    private final String type;
    private final SFTPServer sftpServer;
    private FileSyncJobControlFile fileSyncJobControlFile;
    List<File> filesToSync;

    public FileSyncJob(String name, File source, String filesToIgnoreRegex, String destination,
	    boolean overwriteondestination, String type, SFTPServer sftpServer) {
	super();
	this.name = name;
	this.source = source;
	this.filesToIgnoreRegex = filesToIgnoreRegex;
	this.destination = destination;
	this.overwriteondestination = overwriteondestination;
	this.type = type;
	this.sftpServer = sftpServer;
	this.filesToSync = new ArrayList<>();

	if (this.type.equals("DIFFERENTIAL")) {
	    this.fileSyncJobControlFile = new FileSyncJobControlFile(this.name);
	}
    }

    @Override
    public void run() {

	logger.info("Starting FileSyncJob " + this.name);

	logger.info("Listing files to synchronize");

	logger.info("FileSyncJob type is set to " + this.type);

	this.listFilesToSync();

	logger.info("Listing proccess resulted in " + this.filesToSync.size() + " files to synchronize");

	if (!this.filesToSync.isEmpty()) {

	    logger.info("Starting synchronization with the server");

	    this.sftpServer.openConnection();

	    this.filesToSync.forEach((file) -> {

		logger.info("Synchronizing " + file.getName() + " with the server");

		if (this.type.equals("DIFFERENTIAL")) {

		    if (sftpServer.uploadFile(file, this.destination, this.overwriteondestination)) {
			this.fileSyncJobControlFile.add(file);
		    }
		} else {
		    sftpServer.uploadFile(file, this.destination, this.overwriteondestination);
		}

		logger.info(file.getName() + " synced with the server");
	    });
	}

	logger.info("FileSyncJob " + this.name + " finished!");
    }

    private void listFilesToSync() {

	if (this.source.isFile()) {
	    this.addFileToSync(this.source);
	} else {

	    try {

		Files.list(this.source.toPath()).forEach((path) -> {

		    File file = new File(path.toUri());

		    if (!file.isDirectory()) {
			this.addFileToSync(file);
		    }
		});
	    } catch (IOException ioException) {

		logger.fatal("Could not list files in directory! Exception: " + ioException.getClass().getName()
			+ " Message: " + ioException.getMessage());

		throw new Error("Could not list files in directory! Exception: " + ioException.getClass().getName()
			+ " Message: " + ioException.getMessage());
	    }
	}
    }

    private void addFileToSync(File file) {

	boolean isToSync;

	logger.info("Starting " + file.getName() + " file verifications");

	isToSync = verifyIfFileIsToIgnore(file);

	if (isToSync) {
	    logger.info("File does not match with ignore regex!");
	} else {
	    logger.info("File matches with ignore regex! Removing from list of files to synchronize");
	}

	if (isToSync) {

	    isToSync = verifyIfFileWasAlreadySynced(file);

	    if (isToSync) {
		logger.info("File not synced yet!");
	    } else {
		logger.info("File already synced! Removing from list of files to synchronize");
	    }
	}

	if (isToSync) {

	    logger.info("Adding " + file.getName() + " to the list of files to synchronize");

	    this.filesToSync.add(file);
	}
    }

    private boolean verifyIfFileIsToIgnore(File file) {

	if (this.filesToIgnoreRegex != null && !this.filesToIgnoreRegex.isEmpty()) {

	    logger.info("Verifying if file matches with ignore regex");

	    return !file.getName().matches(this.filesToIgnoreRegex);
	}

	return true;
    }

    private boolean verifyIfFileWasAlreadySynced(File file) {

	if (this.type.equals("DIFFERENTIAL")) {

	    logger.info("Verifying if file was already synced");

	    return !this.fileSyncJobControlFile.contains(file);

	}

	return true;
    }
}