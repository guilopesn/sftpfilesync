package com.github.guilopesn.sftpfilesync.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationFile {

    private final Properties configurations = new Properties();

    public ConfigurationFile() {

	File configurationsFile = new File("SFTPFileSync.config");

	System.out.println("Loading configurations from file");

	try (InputStream configurationsFileInputStream = new FileInputStream(configurationsFile)) {
	    this.configurations.load(configurationsFileInputStream);

	    System.out.println("Configurations loading succeed");
	} catch (IOException | IllegalArgumentException ex) {
	    throw new Error("Configurations could not be loaded! Check the configurations file. Exception: "
		    + ex.getClass().getName() + " Message: " + ex.getMessage());
	}
    }

    public String getConfiguration(String key) {

	String configuration = this.configurations.getProperty(key);

	if (configuration == null) {
	    throw new Error("Configuration not found! Key: " + key);
	}

	return configuration;
    }

}
