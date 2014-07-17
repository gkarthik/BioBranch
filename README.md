# Branch #
## Build Trees, Explore Data ##

## Setup ##
### On Eclipse ###
* Setup Eclipse with the Spring Tool Suite(<http://spring.io/tools/sts/all>).
* Install the gradle plugin for eclipse.
* Refresh all dependencies.
* Set Up database name in src/main/resources/application.properties and in src/main/resources/luidbase.properties.
* To create database schema for the first time, set hibernate.hbm2ddl.auto=create in applciation.properties. To revert back to update set hibernate.hbm2ddl.auto=update.
* Run on Tomcat 7.0.

Tests:
Run using JUnit.