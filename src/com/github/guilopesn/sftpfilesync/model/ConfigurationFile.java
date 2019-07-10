package com.github.guilopesn.sftpfilesync.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigurationFile {

    private static Logger logger = LogManager.getLogger();

    private final Properties configurations = new Properties();

    public ConfigurationFile() {

	File configurationsFile = new File("SFTPFileSync.config");

	logger.info("Loading configurations from file");

	try (InputStream configurationsFileInputStream = new FileInputStream(configurationsFile)) {
	    this.configurations.load(configurationsFileInputStream);

	    logger.info("Configurations loading succeed");
	} catch (IOException | IllegalArgumentException ex) {

	    logger.fatal("Configurations could not be loaded! Check the configurations file. Exception: "
		    + ex.getClass().getName() + " Message: " + ex.getMessage());

	    throw new Error("Configurations could not be loaded! Check the configurations file. Exception: "
		    + ex.getClass().getName() + " Message: " + ex.getMessage());
	}
    }

    public String getConfiguration(String key) {

	String configuration = this.configurations.getProperty(key);

	if (configuration == null) {

	    logger.fatal("Configuration not found! Key: " + key);

	    throw new Error("Configuration not found! Key: " + key);
	}

	return configuration;
    }

}
