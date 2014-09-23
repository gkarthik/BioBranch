# Branch #
## Build Trees, Explore Data ##

## Requirements ##
1. Tomcat 7.0(<http://tomcat.apache.org/download-70.cgi>).
2. Eclipse Java EE IDE for Web Developers
3. Gradle Plugin for Eclipse(Can be installed directly form Eclipse market place).
4. Mercurial Plugin for Eclipse(Can be installed directly form Eclipse market place).
4. Java 7.0.

## Setup ##
### On Eclipse ###
* Install Spring Tool Suite(<http://spring.io/tools/sts/all>) in Eclipse to make handling Spring easier(Can be installed from Eclipse Marketplace). 
* Pull code from this repository using mercurial plugin. 
* Refresh all dependencies in gradle. 
* Set Up database details in src/main/resources/application.properties.
* To create database schema for the first time, set hibernate.hbm2ddl.auto=create in application.properties. To revert back to update set hibernate.hbm2ddl.auto=update.
* To set up the uploads directory change uploads.dir=/absolute/path/to/folder/
* Run on Tomcat 7.0. 

### Db SetUp ###
* Download db_dump.sql from <coming soon>
* Login to postgresql command line.
* Run following commands
```
    DROP TABLE attribute;
    DROP TABLE feature CASCADE;
    DROP TABLE pathway CASCADE;
    DROP TABLE pathway_feature;
```
* Exit postgresql command line and execute this command on the shell/cmd.
```
    pg_dump -U <username> -h localhost <database-name> < db_dump.sql
``` 

### Tests: ###
Run using JUnit.