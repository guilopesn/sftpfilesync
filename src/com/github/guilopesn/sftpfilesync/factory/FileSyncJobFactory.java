package com.github.guilopesn.sftpfilesync.factory;

import com.github.guilopesn.sftpfilesync.model.ConfigurationFile;
import com.github.guilopesn.sftpfilesync.model.File;
import com.github.guilopesn.sftpfilesync.model.FileSyncJob;
import com.github.guilopesn.sftpfilesync.model.SFTPServer;

public class FileSyncJobFactory {

    public FileSyncJob getFileSyncJob(ConfigurationFile configurationFile, String fileSyncJobIndex,
	    SFTPServer sftpServer) {

	return new FileSyncJob(configurationFile.getConfiguration("filesyncjob." + fileSyncJobIndex + ".name"),
		new File(configurationFile.getConfiguration("filesyncjob." + fileSyncJobIndex + ".source")),
		Boolean.parseBoolean(configurationFile.getConfiguration("filesyncjob." + fileSyncJobIndex + ".isrecursively")),
		configurationFile.getConfiguration("filesyncjob." + fileSyncJobIndex + ".filestoignoreregex"),
		new File(configurationFile.getConfiguration("filesyncjob." + fileSyncJobIndex + ".destination")),
		Boolean.parseBoolean(configurationFile.getConfiguration("filesyncjob." + fileSyncJobIndex + ".overwriteondestination")),
		Boolean.parseBoolean(configurationFile.getConfiguration("filesyncjob." + fileSyncJobIndex + ".isdifferential")),
		sftpServer);
    }
}