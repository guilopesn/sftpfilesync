package com.github.guilopesn.sftpfilesync.util;

import java.util.ArrayList;
import java.util.List;

import com.github.guilopesn.sftpfilesync.model.File;

public class FileUtil {

    public static List<File> listFilesRecursively(File directory) {

	List<File> files = new ArrayList<>();

	if (directory == null || directory.listFiles() == null) {
	    return files;
	}

	for (java.io.File file : directory.listFiles()) {
	    if (file.isFile()) {
		files.add(new File(file.toURI()));
	    } else {
		files.addAll(FileUtil.listFilesRecursively(new File(file.toURI())));
	    }
	}

	return files;
    }
}