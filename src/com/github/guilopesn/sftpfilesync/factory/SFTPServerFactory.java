package com.github.guilopesn.sftpfilesync.factory;

import com.github.guilopesn.sftpfilesync.model.ConfigurationFile;
import com.github.guilopesn.sftpfilesync.model.SFTPServer;

public class SFTPServerFactory {

    public SFTPServer getSFTPServer(ConfigurationFile configurationFile) {

	return new SFTPServer(configurationFile.getConfiguration("sftpserver.host"),
		Integer.parseInt(configurationFile.getConfiguration("sftpserver.port")),
		configurationFile.getConfiguration("sftpserver.username"),
		configurationFile.getConfiguration("sftpserver.password"),
		Boolean.parseBoolean(configurationFile.getConfiguration("sftpserver.checkhostidentity")));
    }
}