package com.github.guilopesn.sftpfilesync.util;

import java.util.HashSet;
import java.util.Set;

import com.github.guilopesn.sftpfilesync.model.File;

public class FileUtil {

    public static Set<File> listFileTree(File directory) {

	Set<File> fileTree = new HashSet<File>();

	if (directory == null || directory.listFiles() == null) {
	    return fileTree;
	}

	for (java.io.File entry : directory.listFiles()) {
	    if (entry.isFile()) {
		fileTree.add(new File(entry.toURI()));
	    } else {
		fileTree.addAll(listFileTree(new File(entry.toURI())));
	    }
	}

	return fileTree;
    }
}
