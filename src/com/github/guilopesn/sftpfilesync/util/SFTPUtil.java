package com.github.guilopesn.sftpfilesync.util;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

public class SFTPUtil {

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

	System.out.println("Verifying if directory tree " + destination + " is consistent");

	if (!verifyIfFolderExists(channelSftp, destination)) {

	    System.out.println("Directory tree structure is inconsistent! Correcting");

	    String[] completePath = destination.split("/");
	    String currentDirectory = "";

	    for (int i = 0; i < completePath.length; i++) {

		String directory = completePath[i];

		currentDirectory = currentDirectory + "/" + directory;

		System.out.println("Verifying if directory " + currentDirectory + " exists");

		if (!verifyIfFolderExists(channelSftp, currentDirectory)) {

		    System.out.println("Directory does not exists! Creating");

		    try {
			channelSftp.mkdir(currentDirectory);

			System.out.println("Directory creation succeed");

			channelSftp.cd(currentDirectory);
		    } catch (SftpException sftpException) {
			throw new Error("Could not create directory! Exception: " + sftpException.getClass().getName()
				+ " Message: " + sftpException.getMessage());
		    }
		} else {
		    System.out.println("Directory exists! Going under on tree");
		}
	    }

	    System.out.println("Directory tree structure correction succeed!");
	} else {
	    System.out.println("Directory tree structure is correct!");
	}
    }
}