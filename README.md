# Branch #
## Build Trees, Explore Data ##

## Requirements ##
1. Tomcat 7.0(<http://tomcat.apache.org/download-70.cgi>).
2. Eclipse Java EE IDE for Web Developers
3. Gradle Plugin for Eclipse(Can be installed directly from Eclipse market place).
4. Mercurial Plugin for Eclipse(Can be installed directly from Eclipse market place).
4. Java 7.0.

## Setup ##
### On Eclipse ###
* Install Spring Tool Suite(<http://spring.io/tools/sts/all>) in Eclipse to make handling Spring easier(Can be installed from Eclipse Marketplace). 
* Pull code from this repository using mercurial plugin. 
* Refresh all dependencies in gradle. 
* Set Up database details in src/main/resources/application.properties.
* To create database schema for the first time, set hibernate.hbm2ddl.auto=create in application.properties. To revert back to update on subsequent sessions set hibernate.hbm2ddl.auto=update.
* There are two configuration files, example_config_prod and example_config_dev for production and development environments respectively. Rename them as config_prod.properties and config_dev.properties. To configure the profile to run the application in, add a setenv.sh file to the bin folder of your tomcat installation. Add the following line to the file.
```
	JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=*profile_name*"
```
* Replace *profile_name* with prod or dev.
* To set up the uploads directory set uploads.dir=/absolute/path/to/folder/ in the appropriate properties file.
* Refresh dependencies using Gradle plugin.
* Run on Tomcat 7.0. 

### Db SetUp ###
* Setup an account wiht username "admin". 
* Log in using admin account and go to localhost:8080/branch/tutorials/.
* Click on Populate Pathway and choose appropriate source to add pathways to the database.
* Adding datasets... (Coming Soon)

### Tests: ###
Run using JUnit.

FAQs
If the tables for Spring batch(such as batch_job_instace) are not automatically created, copy and manually run the SQL script in src/main/webapp/WEB-INF/data.