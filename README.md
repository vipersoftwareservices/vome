![VOME banner](doc/images/viper-wide-banner.jpg)

# VOME (In Progress) 

VOME is a database viewer tool. It is a standalone application written in Java using JavaFX. Databases are access via SQL using JDBC.


## Features

The VOME is standalone application and has the following features:

* Enter and preserve a collection of database connections
* Display the database schema and properties
* Display tables, views, triggers, stored procedures, indexes and constraints
* Enter SQL directly and display results in tabular form.
* Performs import/export functions  
* Allows editing of data in table.
* Add/Drop/Modify columns and rows with database table. 

## Additional Documentation

In progress: 

* [Authors Home Page](http://www.tnevin.com)

## Screenshot of main VOME window 
      
![VOME Main Window](doc/images/overview.jpg) 

## Getting Started

These instructions will get you a copy of the project up and running on your local machine.
 
### Prerequisites to running VOME

1. What things you need to install the software and how to install them

```
* java 1.6 or better. 
```

### Download VOME binary windows installer 

2. Download the VOME install jar file, and run the installation jar file.

* [VOME binary](http://www.tnevin.com/viper/software/downloads/vome-installer.jar) 
 
### Prerequisites to building VOME

1. What things you need to install the software and how to install them

```
* java 1.6 or better.
* ant 1.9 or better.
* For Windows, install CygWin, latest.
```

Note: ant commands have been run and tested using cygwin bash shell, dos shell, and other linux shells will probably work.

### Installing

2. Download the vome zip file, and unzip it.

```
https://github.com/vipersoftwareservices/vome
```

3. Run the build script if building sources is desired, runtime jars are available.

```
ant clean all
```


## Running the tests

Run the tests, by running ant command.

```
ant test
```

View the JUnit test results, by bringing the following file up in browser.
For windows, double click the file in the disk explorer, the location of the file is:

```
<install-directory>/build/reports/index.html
```

View the code coverage file in the browser..
For windows, double click the file in the disk explorer, the location of the file is:

```
<install-directory>/build/jacoco/index.html
```

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Check on coding style by running:

```
ant checkstyle
```

## Deployment

In progress

## Built With

In progress 

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/vipersoftwareservices/vome) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

In progress

## Authors

* **Tom Nevin** - *Initial work* - [NitroHammer](https://github.com/vipersoftwareservices/vome)

See also the list of [contributors](https://github.com/vipersoftwareservices/vome/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details

## Acknowledgments

In progress

