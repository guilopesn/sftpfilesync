
# Getting Started with SFTPFileSync! [![Total alerts](https://img.shields.io/lgtm/alerts/g/guilopesn/sftpfilesync.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/guilopesn/sftpfilesync/alerts/)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/guilopesn/sftpfilesync.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/guilopesn/sftpfilesync/alerts/)

# Configuration


All SFTPFileSync synchronization jobs configuration parameters must be contained in a configuration file called SFTPFileSync.config. This configuration file must be in the same directory as the SFTPFileSync binary.

## Example Configuration File

	sftpserver.host=0.0.0.0
	sftpserver.port=22
	sftpserver.username=example
	sftpserver.password=example
	sftpserver.checkhostidentity=false

	filesyncjobs=1,2

	filesyncjob.1.name=Test Directory
	filesyncjob.1.source=C:\\Test
	filesyncjob.1.isrecursively=true
	filesyncjob.1.filestoignoreregex=.*.xml
	filesyncjob.1.destination=/test
	filesyncjob.1.sendtodestinationroot=false
	filesyncjob.1.overwriteondestination=false
	filesyncjob.1.isdifferential=true

	filesyncjob.2.name=Test CSV File
	filesyncjob.2.source=C:\\Test\\Test.csv
	filesyncjob.2.isrecursively=false
	filesyncjob.2.filestoignoreregex=
	filesyncjob.2.destination=/test
	filesyncjob.2.sendtodestinationroot=true
	filesyncjob.2.overwriteondestination=false
	filesyncjob.2.isdifferential=false

## Configuration Parameters

### SFTP Server Parameters

#### sftpserver.host
	Must especify the SFTP server address.
	Example: 127.0.0.1

#### sftpserver.port
	Must especify the SFTP server listen port.
	Example: 22

#### sftpserver.username
	Must especify the username to authenticate on SFTP server.
	Example: username

#### sftpserver.password
	Must especify the password to the username seted at sftpserver.username parameter to authenticate on SFTP server.
	Example: password

#### sftpserver.checkhostidentity
	Must be seted to true or false to verify or not the SFTP server identity.
	Example: false

### FileSync Jobs Parameters

The number of jobs that can be defined are unlimited. Each job must have a name and a numeric index that defines them.
Example:

- The first job on the example configuration file above it's called "Test Directory" and it's index will be 1.
- The second job on the example configuration file above it's called "Test CSV File" and it's index will be 2.

Each job is defined by a set of the 6 following parameters.

Note: Where the jobindex keyword exists in the parameter name, it must be replaced with the index number defined by you for this job.

#### filesyncjobs
	Must contains a comma-separated list of the jobs (Already defineds at configuration file) that must be executeds.
	Example: 1,2

#### filesyncjob.jobindex.name
	Must specify the name of the job.
	Example: Test Directory

#### filesyncjob.jobindex.source
	Must specify the file or directory that must be synchronized.
	Example: C:\\Test
	
#### filesyncjob.jobindex.isrecursively
	Must specify whether the source directory of job (Defined in the filesyncjob.jobindex.source parameter) should be recursively synchronized.
	Example: true
	Note: It is only considered in cases where the origin defined for the job is a directory

#### filesyncjob.jobindex.filestoignoreregex
	Must be seated with a regex to match files to be ignored on source.
	Example: .*.xml
	Note: Leave blank to do not ignore any files in source.

#### filesyncjob.jobindex.destination
	Must specify the remote path to where the source files should be synchronized.
	Example: /test
	
#### filesyncjob.jobindex.sendtodestinationroot
	Must be seted to true or false to specify if the source recursive path must be replicated to the destination.
	Example: false

#### filesyncjob.jobindex.overwriteondestination
	Must be seted to true or false to overwrite or not the file on destination in case of already exists.
	Example: false

#### filesyncjob.jobindex.isdifferential
	Must be seted to true or false. The behaviors for each one will be as follows:
	
		- false - Synchronize all files from source to destination, even when was already synchronized;
		- true - Synchronizes only source files that have not yet been synchronized with the destination.
	
	Note: If the type is set to true, a file whose name will be composed of job name (Defined in the filesyncjob.jobindex.name parameter) + the .syncjobcontrolfile extension will be created. This file is created on the first run of the job, or if it does not exist.
	If the file is not found by the job, or in case of first execution of this job, a complete job will be executed.

# Execution

To execute the SFTPFileSync binary, replace the pathtojar keyword in the command below with the path where the binary is found, and then run it:

	java -jar pathtojar\SFTPFileSync.jar
