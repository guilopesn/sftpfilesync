package com.github.guilopesn.sftpfilesync;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.guilopesn.sftpfilesync.model.ConfigurationFile;
import com.github.guilopesn.sftpfilesync.model.File;
import com.github.guilopesn.sftpfilesync.model.FileSyncJob;
import com.github.guilopesn.sftpfilesync.model.SFTPServer;

public class SFTPFileSync {

    private static Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws IOException {

	logger.info("Starting SFTPFileSync process");

	ConfigurationFile configurationFile = new ConfigurationFile();

	logger.info("Retrieving SFTP server parameters from configuration file");

	SFTPServer sftpServer = new SFTPServer(configurationFile.getConfiguration("sftpserver.host"),
		Integer.parseInt(configurationFile.getConfiguration("sftpserver.port")),
		configurationFile.getConfiguration("sftpserver.username"),
		configurationFile.getConfiguration("sftpserver.password"),
		Boolean.parseBoolean(configurationFile.getConfiguration("sftpserver.checkhostidentity")));

	logger.info("Listing FileSyncJob from configuration file");

	String filesyncjobs = configurationFile.getConfiguration("filesyncjobs");

	String[] filesyncjobsArray = filesyncjobs.split(",");

	logger.info("Filesyncjobs found: " + filesyncjobs);

	for (int i = 0; i < filesyncjobsArray.length; i++) {

	    String fileSyncJobIndex = filesyncjobsArray[i];

	    new FileSyncJob(configurationFile.getConfiguration("filesyncjob." + fileSyncJobIndex + ".name"),
		    new File(configurationFile.getConfiguration("filesyncjob." + fileSyncJobIndex + ".source")),
		    Boolean.parseBoolean(
			    configurationFile.getConfiguration("filesyncjob." + fileSyncJobIndex + ".isrecursively")),
		    configurationFile.getConfiguration("filesyncjob." + fileSyncJobIndex + ".filestoignoreregex"),
		    new File(configurationFile.getConfiguration("filesyncjob." + fileSyncJobIndex + ".destination")),
		    Boolean.parseBoolean(configurationFile
			    .getConfiguration("filesyncjob." + fileSyncJobIndex + ".overwriteondestination")),
		    Boolean.parseBoolean(
			    configurationFile.getConfiguration("filesyncjob." + fileSyncJobIndex + ".isdifferential")),
		    sftpServer).run();
	}

	logger.info("Stopping SFTPFileSync process");

	System.exit(0);
    }
}
