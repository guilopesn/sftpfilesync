package com.github.guilopesn.sftpfilesync;

import java.io.File;
import java.io.IOException;

import com.github.guilopesn.sftpfilesync.model.ConfigurationFile;
import com.github.guilopesn.sftpfilesync.model.FileSyncJob;
import com.github.guilopesn.sftpfilesync.model.SFTPServer;

public class SFTPFileSync {

    public static void main(String[] args) throws IOException {

	System.out.println("Starting SFTPFileSync proccess");

	ConfigurationFile configurationFile = new ConfigurationFile();

	System.out.println("Retrieving SFTP server parameters from configuration file");

	SFTPServer sftpServer = new SFTPServer(configurationFile.getConfiguration("sftpserver.host"),
		Integer.parseInt(configurationFile.getConfiguration("sftpserver.port")),
		configurationFile.getConfiguration("sftpserver.username"),
		configurationFile.getConfiguration("sftpserver.password"),
		Boolean.parseBoolean(configurationFile.getConfiguration("sftpserver.checkhostidentity")));

	System.out.println("Listing FileSyncJob from configuration file");

	String filesyncjobs = configurationFile.getConfiguration("filesyncjobs");

	String[] filesyncjobsArray = configurationFile.getConfiguration("filesyncjobs").split(",");

	System.out.println("Filesyncjobs found: " + filesyncjobs);

	for (int i = 0; i < filesyncjobsArray.length; i++) {

	    String fileSyncJobIndex = filesyncjobsArray[i];

	    new FileSyncJob(configurationFile.getConfiguration("filesyncjob." + fileSyncJobIndex + ".name"),
		    new File(configurationFile.getConfiguration("filesyncjob." + fileSyncJobIndex + ".source")),
		    configurationFile.getConfiguration("filesyncjob." + fileSyncJobIndex + ".destination"),
		    Boolean.parseBoolean(configurationFile
			    .getConfiguration("filesyncjob." + fileSyncJobIndex + ".overwriteondestination")),
		    configurationFile.getConfiguration("filesyncjob." + fileSyncJobIndex + ".type"), sftpServer).run();
	}

	System.out.println("Stopping SFTPFileSync proccess");

	System.exit(0);
    }
}
