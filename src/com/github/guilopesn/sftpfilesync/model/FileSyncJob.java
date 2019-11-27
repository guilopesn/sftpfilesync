package com.github.guilopesn.sftpfilesync.model;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.github.guilopesn.sftpfilesync.util.FileUtil;

public class FileSyncJob implements Runnable {

    private static Logger logger = LogManager.getLogger();

    private final String name;
    private final File source;
    private final boolean isRrecursively;
    private final String filesToIgnoreRegex;
    private final File destination;
    private final boolean sendToDestinationRoot;
    private final boolean overwriteOnDestination;
    private final boolean isDifferential;
    private final SFTPServer sftpServer;
    private FileSyncJobControlFile fileSyncJobControlFile;
    private List<File> filesToSync;

    public FileSyncJob(String name, File source, boolean isRrecursively, String filesToIgnoreRegex, File destination,
	    boolean sendToDestinationRoot, boolean overwriteOnDestination, boolean isDifferential,
	    SFTPServer sftpServer) {

	this.name = name;
	this.source = source;
	this.isRrecursively = isRrecursively;
	this.filesToIgnoreRegex = filesToIgnoreRegex;
	this.sendToDestinationRoot = sendToDestinationRoot;
	this.destination = destination;
	this.overwriteOnDestination = overwriteOnDestination;
	this.isDifferential = isDifferential;
	this.sftpServer = sftpServer;
	this.filesToSync = new ArrayList<>();

	if (this.isDifferential) {
	    this.fileSyncJobControlFile = new FileSyncJobControlFile(this.name);
	}
    }

    @Override
    public void run() {

	logger.info("Starting FileSyncJob " + this.name);

	logger.info("Listing files to synchronize");

	logger.info("FileSyncJob is differential: " + this.isDifferential);

	this.listFilesToSync();

	logger.info("Listing proccess resulted in " + this.filesToSync.size() + " files to synchronize");

	if (!this.filesToSync.isEmpty()) {

	    logger.info("Starting synchronization with the server");

	    this.sftpServer.openConnection();

	    this.filesToSync.forEach((file) -> {

		logger.info("Synchronizing " + file.getName() + " with the server");

		if (this.isDifferential) {

		    if (sftpServer.uploadFile(this.source, this.destination, file, this.overwriteOnDestination,
			    this.sendToDestinationRoot)) {
			this.fileSyncJobControlFile.add(file);
		    }
		} else {
		    sftpServer.uploadFile(this.source, this.destination, file, this.overwriteOnDestination,
			    this.sendToDestinationRoot);
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

	    if (this.isRrecursively) {

		for (File file : FileUtil.listFilesRecursively(this.source)) {
		    this.addFileToSync(file);
		}
	    } else {
		for (File file : FileUtil.listFiles(this.source)) {
		    this.addFileToSync(file);
		}
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

	if (this.isDifferential) {

	    logger.info("Verifying if file was already synced");

	    return !this.fileSyncJobControlFile.contains(file);

	}

	return true;
    }
}
