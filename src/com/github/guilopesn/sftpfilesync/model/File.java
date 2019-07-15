package com.github.guilopesn.sftpfilesync.model;

import java.net.URI;

public class File extends java.io.File {

    private static final long serialVersionUID = 8145067064766478821L;

    public File(String pathname) {
	super(pathname);
    }

    public File(URI uri) {
	super(uri);
    }

    public File(String parent, String child) {
	super(parent, child);
    }

    public File(java.io.File parent, String child) {
	super(parent, child);
    }

    @Override
    public boolean equals(Object anObject) {

	if (anObject != null && anObject instanceof File) {

	    File fileToCompare = (File) anObject;

	    return this.getName().equals(fileToCompare.getName());
	} else {
	    return false;
	}
    }

    @Override
    public int hashCode() {
	return this.getName().hashCode();
    }
}