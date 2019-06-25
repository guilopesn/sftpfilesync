package com.github.guilopesn.sftpfilesync.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileSyncJob implements Runnable {

    private final String name;
    private final File source;
    private final String destination;
    private final boolean overwriteondestination;
    private final String type;
    private final SFTPServer sftpServer;
    private FileSyncJobControlFile fileSyncJobControlFile;
    List<File> filesToSync;

    public FileSyncJob(String name, File source, String destination, boolean overwriteondestination, String type,
	    SFTPServer sftpServer) {
	super();
	this.name = name;
	this.source = source;
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

	System.out.println("Starting FileSyncJob " + this.name);

	System.out.println("Listing files to synchronize");

	System.out.println("FileSyncJob type is set to " + this.type);

	this.listFilesToSync();

	System.out.println("Listing proccess resulted in " + this.filesToSync.size() + " files to sync");

	if (!this.filesToSync.isEmpty()) {

	    System.out.println("Starting synchronization with the server");

	    this.sftpServer.openConnection();

	    this.filesToSync.forEach((file) -> {

		System.out.println("Synchronizing " + file.getName() + " with the server");

		if (this.type.equals("DIFFERENTIAL")) {
		    if (sftpServer.uploadFile(file, this.destination, this.overwriteondestination)) {
			this.fileSyncJobControlFile.add(file);
		    }
		} else {
		    sftpServer.uploadFile(file, this.destination, this.overwriteondestination);
		}

		System.out.println(file.getName() + " synced with the server");
	    });
	}

	System.out.println("FileSyncJob " + this.name + " finished!");
    }

    private void listFilesToSync() {

	if (this.source.isFile()) {
	    this.addFileToSync(this.source);
	} else {

	    try {

		Files.list(this.source.toPath()).forEach((path) -> {
		    if (!path.toFile().isDirectory()) {
			this.addFileToSync(path.toFile());
		    }
		});
	    } catch (IOException ioException) {
		throw new Error("Could not list files in directory! Exception: " + ioException.getClass().getName()
			+ " Message: " + ioException.getMessage());
	    }
	}
    }

    private void addFileToSync(File file) {

	if (this.type.equals("DIFFERENTIAL")) {

	    System.out.println("Verifying if file " + file.getName() + " was already synced");

	    if (!this.fileSyncJobControlFile.contains(file)) {

		System.out
			.println("File not synced yet! Adding " + file.getName() + " to list of files to synchronize");

		this.filesToSync.add(file);
	    } else {
		System.out.println("File already synced! Removing from list of files to synchronize");
	    }
	} else {

	    System.out.println("Adding " + file.getName() + " to the list of files to synchronize");

	    this.filesToSync.add(file);
	}
    }

}
