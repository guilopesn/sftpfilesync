package com.github.guilopesn.sftpfilesync.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.github.guilopesn.sftpfilesync.util.SFTPUtil;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPServer {

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final boolean chekHostIdentity;
    private JSch jsch;
    private Session session;

    public SFTPServer(String host, int port, String username, String password, boolean chekHostIdentity) {
	super();
	this.host = host;
	this.port = port;
	this.username = username;
	this.password = password;
	this.chekHostIdentity = chekHostIdentity;
	this.jsch = new JSch();
    }

    public void openConnection() {

	try {

	    this.session = this.jsch.getSession(this.username, this.host, this.port);
	    this.session.setPassword(this.password);

	    if (!chekHostIdentity) {
		java.util.Properties configs = new java.util.Properties();
		configs.put("StrictHostKeyChecking", "no");
		this.session.setConfig(configs);
	    }

	    this.session.connect();

	} catch (JSchException jSchException) {
	    throw new Error("Could not open SSH connection! Exception: " + jSchException.getClass().getName()
		    + " Message: " + jSchException.getMessage());
	}
    }

    public void closeConnection() {
	this.session.disconnect();
    }

    public void uploadFile(File file, String destination, boolean overwriteondestination) {

	ChannelSftp channelSftp = null;

	try {

	    channelSftp = (ChannelSftp) this.session.openChannel("sftp");

	    channelSftp.connect();
	} catch (JSchException jSchException) {
	    throw new Error("Error! Could not open SFTP channel! Exception: " + jSchException.getClass().getName()
		    + " Message: " + jSchException.getMessage());
	}

	SFTPUtil.verifyDirectoryTreeConsistency(channelSftp, destination);

	try {

	    if (overwriteondestination) {
		channelSftp.put(new FileInputStream(file), destination + "/" + file.getName(), ChannelSftp.OVERWRITE);
	    } else {
		channelSftp.put(new FileInputStream(file), destination + "/" + file.getName());
	    }
	} catch (SftpException | IOException exception) {

	    if (channelSftp != null && channelSftp.isConnected()) {
		channelSftp.disconnect();
	    }

	    throw new Error("Error! Could not upload file! Exception: " + exception.getClass().getName() + " Message: "
		    + exception.getMessage());
	}
    }
}