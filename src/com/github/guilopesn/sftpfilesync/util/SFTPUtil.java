package com.github.guilopesn.sftpfilesync.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.guilopesn.sftpfilesync.model.File;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

public class SFTPUtil {

    private static Logger logger = LogManager.getLogger();

    public static String getFullDestinationFilePath(File source, File destination, File file) {

	return destination.getPath().replace('\\', '/').concat(
		file.getParentFile().toPath().toString().replace(source.getPath(), "").replace('\\', '/').concat("/"));
    }

    public static boolean verifyIfFolderExists(ChannelSftp channelSftp, String folder) {

	try {
	    channelSftp.stat(folder);
	} catch (SftpException sftpException) {
	    if (sftpException.getMessage().equals("No such file")) {
		return false;
	    }
	}

	return true;
    }

    public static void verifyDirectoryTreeConsistency(ChannelSftp channelSftp, String destination) {

	logger.info("Verifying if directory tree " + destination + " is consistent");

	if (!verifyIfFolderExists(channelSftp, destination)) {

	    logger.warn("Directory tree structure is inconsistent! Correcting");

	    String[] completePath = destination.split("/");
	    String currentDirectory = "";

	    for (int i = 0; i < completePath.length; i++) {

		String directory = completePath[i];

		currentDirectory = currentDirectory + "/" + directory;

		logger.info("Verifying if directory " + currentDirectory + " exists");

		if (!verifyIfFolderExists(channelSftp, currentDirectory)) {

		    logger.warn("Directory does not exists! Creating");

		    try {
			channelSftp.mkdir(currentDirectory);

			logger.info("Directory creation succeed");

			channelSftp.cd(currentDirectory);
		    } catch (SftpException sftpException) {

			logger.fatal("Could not create directory! Exception: " + sftpException.getClass().getName()
				+ " Message: " + sftpException.getMessage());

			throw new Error("Could not create directory! Exception: " + sftpException.getClass().getName()
				+ " Message: " + sftpException.getMessage());
		    }
		} else {
		    logger.info("Directory exists! Going under on tree");
		}
	    }

	    logger.info("Directory tree structure correction succeed!");
	} else {
	    logger.info("Directory tree structure is correct!");

	    try {
		channelSftp.cd(destination);
	    } catch (SftpException sftpException) {

		logger.fatal("Could not get in on directory! Exception: " + sftpException.getClass().getName()
			+ " Message: " + sftpException.getMessage());

		throw new Error("Could not get in on directory! Exception: " + sftpException.getClass().getName()
			+ " Message: " + sftpException.getMessage());
	    }
	}
    }
}