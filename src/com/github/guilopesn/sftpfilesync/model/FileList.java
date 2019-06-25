package com.github.guilopesn.sftpfilesync.model;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

public class FileList extends HashSet<File> {

    private static final long serialVersionUID = 8983080762962416917L;

    public FileList() {
	super();
    }

    public FileList(Collection<File> arg0) {
	super(arg0);
    }

    public FileList(int arg0) {
	super(arg0);
    }

    @Override
    public boolean contains(Object anObject) {

	if (anObject != null && anObject instanceof File) {

	    File fileToCompare = (File) anObject;

	    Boolean result = false;

	    for (File file : this) {
		if (!result) {
		    result = file.getPath().equals(fileToCompare.getPath());
		}
	    }

	    return result;
	}

	return false;
    }
}
