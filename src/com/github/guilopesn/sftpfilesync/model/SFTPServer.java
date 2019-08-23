package com.github.guilopesn.sftpfilesync.model;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.guilopesn.sftpfilesync.util.SFTPUtil;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPServer {

    private static Logger logger = LogManager.getLogger();

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final boolean checkHostIdentity;
    private JSch jsch;
    private Session session;

    public SFTPServer(String host, int port, String username, String password, boolean checkHostIdentity) {
	super();
	this.host = host;
	this.port = port;
	this.username = username;
	this.password = password;
	this.checkHostIdentity = checkHostIdentity;
	this.jsch = new JSch();
    }

    public void openConnection() {

	try {

	    this.session = this.jsch.getSession(this.username, this.host, this.port);
	    this.session.setPassword(this.password);

	    if (!checkHostIdentity) {
		java.util.Properties configs = new java.util.Properties();
		configs.put("StrictHostKeyChecking", "no");
		this.session.setConfig(configs);
	    }

	    this.session.connect();

	} catch (JSchException jSchException) {

	    logger.fatal("Could not open SSH connection! Exception: " + jSchException.getClass().getName()
		    + " Message: " + jSchException.getMessage());

	    throw new Error("Could not open SSH connection! Exception: " + jSchException.getClass().getName()
		    + " Message: " + jSchException.getMessage());
	}
    }

    public void closeConnection() {
	this.session.disconnect();
    }

    public boolean uploadFile(File source, File destination, File file, boolean overwriteondestination,
	    boolean sendToDestinationRoot) {

	ChannelSftp channelSftp = null;
	Boolean uploadSuccess = false;

	try {

	    channelSftp = (ChannelSftp) this.session.openChannel("sftp");

	    channelSftp.connect();
	} catch (JSchException jSchException) {

	    logger.fatal("Error! Could not open SFTP channel! Exception: " + jSchException.getClass().getName()
		    + " Message: " + jSchException.getMessage());

	    throw new Error("Error! Could not open SFTP channel! Exception: " + jSchException.getClass().getName()
		    + " Message: " + jSchException.getMessage());
	}

	String fileDestination = SFTPUtil.convertPathToUnixPattern(source, destination, file, sendToDestinationRoot);

	SFTPUtil.validateDestinationPath(channelSftp, fileDestination);

	try {

	    if (overwriteondestination) {
		channelSftp.put(new FileInputStream(file), fileDestination + "/" + file.getName(),
			ChannelSftp.OVERWRITE);
	    } else {
		channelSftp.put(new FileInputStream(file), fileDestination + "/" + file.getName());
	    }

	    uploadSuccess = true;

	} catch (SftpException | IOException exception) {

	    if (channelSftp != null && channelSftp.isConnected()) {
		channelSftp.disconnect();
	    }

	    logger.fatal("Error! Could not upload file! Exception: " + exception.getClass().getName() + " Message: "
		    + exception.getMessage());

	    throw new Error("Error! Could not upload file! Exception: " + exception.getClass().getName() + " Message: "
		    + exception.getMessage());
	}

	return uploadSuccess;
    }
}