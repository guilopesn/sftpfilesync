# Starting with SFTPFileSync!

# Configuration

All SFTPFileSync sync routine configuration parameters must be contained in a configuration file called SFTPFileSync.config.

## Example Configuration File

	sftpserver.host=0.0.0.0
	sftpserver.port=22
	sftpserver.username=example
	sftpserver.password=example
	sftpserver.checkhostidentity=false

	filesyncjobs=1,2

	filesyncjob.1.name=Test Directory
	filesyncjob.1.source=C:\\Test
	filesyncjob.1.destination=/test
	filesyncjob.1.overwriteondestination=false
	filesyncjob.1.type=DIFFERENTIAL

	filesyncjob.2.name=Test CSV File
	filesyncjob.2.source=C:\\Test\Test.csv
	filesyncjob.2.destination=/test
	filesyncjob.2.overwriteondestination=false
	filesyncjob.2.type=FULL

## Configuration Parameters

### SFTP Server Parameters

#### sftpserver.host
	Should contain the SFTP server address.
	Example: 127.0.0.1

#### sftpserver.port
	Should contain the SFTP server listen port.
	Example: 22

#### sftpserver.username
	Should contain the username to authenticate on SFTP server.
	Example: username

#### sftpserver.password
	Should contain the password to the username seted at sftpserver.username parameter to authenticate on SFTP server.
	Example: password

#### sftpserver.checkhostidentity
	Should to be seted to true or false to verify or not the SFTP server identity.
	Example: false

### FileSync Jobs Parameters

The number of jobs that can be defined are unlimited. Each job must have a name and a sequential index that defines them. Example:

- The first job on the example configuration fil above it's called "Test Directory" and its index will be 1.
- The second job on the example configuration fil above it's called "Test CSV File" and its index will be 2.

Each job is defined by a set of the following parameters.
Where the jobindex keyword exists in the parameter name, it must be replaced with the index number defined by you for this job.

#### filesyncjobs
	Must contains a comma-separated list of the jobs (Already defineds at configuration file) that must be executeds.
	Example: 1,2

#### filesyncjob.jobindex.name
	Must contains the name of the job.
	Example: Test Directory

#### filesyncjob.jobindex.source
	Must contain the file or directory that must be synchronized.
	Example: C:\\Test

#### filesyncjob.jobindex.destination
	Must specify the remote path to where the source files should be synchronized.
	Example: /test

#### filesyncjob.jobindex.overwriteondestination
	Must be seted to true or false to overwrite or not the file on destination in case of already exists.
	Example: false

#### filesyncjob.jobindex.type
	Must be set to FULL or DIFFERENTIAL. The behaviors will be as follows:
	
		- FULL - Synchronize all files from source to destination;
		- DIFFERENTIAL - Synchronizes only source files that have not yet been synchronized with the destination.
	
	Note: If the type is set to DIFFERENTIAL, a file whose name will be composed of "Job Name" (Defined in the filesyncjob.jobindex.name parameter) + the .syncjobcontrolfile extension will be created. This file is created on the first run of the job, or if it does not exist.

# Execution

To execute the SFTPFileSync binary, replace the pathtojar keyword in the command below with the path where the binary is found, and then run it:

	cd pathtojar
	java -jar pathtojar\SFTPFileSync.jar >> pathtojar\SFTPFileSync.log
