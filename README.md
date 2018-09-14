The JSL repository. 

This is a Gradle based project.

## JSL Documentation

If you are looking for the JSL documentation you can find it here:

https://rossetti.git-pages.uark.edu/JSL-Documentation/

The repository for the documentation is here:

https://git.uark.edu/rossetti/JSL-Documentation

## JAR files
The current jar files are found in the repos directory of the documentation site

## Cloning and Setting Up a Project

If you are using IntelliJ, you can use its clone repository functionality to 
setup a working version. Here are the steps:

Within IntelliJ

1) Check out from Version Control

2) Use this:  git@git.uark.edu:rossetti/JSL.git

3) Select directory where you want the clone

4) Answer yes to create Intellij project

5) Choose import project from external model, choose Gradle

6) Uncheck “Create separate module per source set”,  check create empty root directories

7) Check “Use grade wrapper task configuration”

8) Finish

9) Gradle import changes if asked

10) Build > Build Project

11) Try to run a class that has main(), e.g. ex.statistics.TestStatistic

You don't need to use version control. You can simply download the repository zip
file and unzip it. Then, open it up in IntelliJ in the same way as above by following 
steps 4-11.

If you are using NetBeans, then do the following:

1) Make sure that you have installed the Gradle plugin for NetBeans.  Follow 
NetBean's normal plugin install procedures

2) If you want to clone under Git, then Team > Git> Clone

    a) use git@git.uark.edu:rossetti/JSL.git
    
3) If you just want to download, then download the zip file from the repository and unzip it.

4) Once cloned or downloaded/unzipped, open the project in NetBeans using Open Project

5) Find an example that has a main method and run it for a simple test.

During either of these processes, Gradle will need to run to download and 
install any dependencies, etc.

You can use the Gradle build tasks to generate javadoc, generate jar files, etc.


