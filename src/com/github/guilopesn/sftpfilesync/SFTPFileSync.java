package com.github.guilopesn.sftpfilesync;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.guilopesn.sftpfilesync.factory.FileSyncJobFactory;
import com.github.guilopesn.sftpfilesync.factory.SFTPServerFactory;
import com.github.guilopesn.sftpfilesync.model.ConfigurationFile;
import com.github.guilopesn.sftpfilesync.model.SFTPServer;

public class SFTPFileSync {

    private static Logger logger = LogManager.getLogger();

    private static SFTPServerFactory sftpServerFactory = new SFTPServerFactory();
    private static FileSyncJobFactory fileSyncJobFactory = new FileSyncJobFactory();

    public static void main(String[] args) throws IOException {

	SFTPFileSync.logger.info("Starting SFTPFileSync process");

	ConfigurationFile configurationFile = new ConfigurationFile();

	SFTPFileSync.logger.info("Retrieving SFTP server parameters from configuration file");

	SFTPServer sftpServer = SFTPFileSync.sftpServerFactory.getSFTPServer(configurationFile);

	SFTPFileSync.logger.info("Listing FileSyncJob from configuration file");

	String filesyncjobs = configurationFile.getConfiguration("filesyncjobs");

	String[] filesyncjobsArray = filesyncjobs.split(",");

	SFTPFileSync.logger.info("Filesyncjobs found: " + filesyncjobs);

	for (int i = 0; i < filesyncjobsArray.length; i++) {

	    SFTPFileSync.fileSyncJobFactory.getFileSyncJob(configurationFile, filesyncjobsArray[i], sftpServer).run();
	}

	SFTPFileSync.logger.info("Stopping SFTPFileSync process");

	System.exit(0);
    }
}