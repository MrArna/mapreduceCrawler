CS441 @ UIC: HOMEWORK2
====================
Developed by Marco Arnaboldi (marnab2@uic.edu)

#Description
--------------------
Your second homework assignment is to create a distributed software application for automatically analyzing the content of research papers in computer science using a map/reduce model. You will create and run your software application using Apache Hadoop (http://hadoop.apache.org/), a framework for distributed processing of large data sets across multiple computers (or even on a single node) using the map/reduce model. Even though you can install and configure Hadoop on your computers, I recommend that you use a virtual machine of Hortonworks Sandbox, a preconfigured Apache Hadoop installation with a comprehensive software stack (http://hortonworks.com/products/sandbox/). To run the VM, you can install vmWare or VirtualBox. As UIC students, you have access to free vmWare licenses, go to http://go.uic.edu/csvmware to obtain your license. You can complete this homework using either Java (I prefer that you use Java for this assignment). You will use Simple Build Tools (SBT) for building the project and running automated tests. You can use the latest community version of IntelliJ IDE for this assignment.
To process research papers as the input to your application, you will use the Apache Tika toolkit (http://tika.apache.org/) to detect and extract metadata and text from different file types (i.e., PPT and PDF). Next, once a plain text is extracted from these documents with Apache Tika, you will use the Apache OpenNLP library (http://opennlp.apache.org/) to process the natural language text. Your ultimate goal is to use the Name Finder to detect named entities in the text using some pre-trained name finder models, many of which are obtained by training the Name Finder on available corpora. These models can be downloaded at the OpenNLP model download page. Details about sentence detector and tokenizer are given in many tutorials on the OpenNLP website along with examples how to find names in text by segmenting it into tokens and sentences. Finally, the output of your program is a list of referenced named in multiple documents with information about documents where these names are found.
Your implementation should include the following steps. You will provide a script to download a few papers from the command line. The script should take command-line parameters for the search keywords. If the user does not provide any command-line parameters, the script should search and retrieve some papers using default hard-coded values. After some papers are downloaded, they will be partitioned into shards, and then your Hadoop-based program will run to extract named references. Besides pure development, you will learn how to take a nontrivial task and partition it into mappers and reducers. 


#Development & Design choices
-----------------

######Script
The script to retrive pdf from the web was developed using python3, exploiting the IEEE APIs in order to retrieve the papers. It also exploits the "wget" functionality provided by the OS, hence corresponding command and the python module used by the script should be installed in the system before using it. 

######Application
The application was developed with with IntelliJIDEA IDE for a distributed environment where Hadoop and MapReduce are installed and configured. SBT was also exploited in order to manage the libraries and to create a fat jar containing all the needed jars and also the name finder pertained models. In particular it was developed using the following environment: the Hortonworks SandBox VM, hosted via VMWARE in a OS X 10 native environment.
The application also exploits the Tika and openNLP libraries in order to process PDFs and their content:

+ **Tika**: provides functionalities to process PDF. In this application it is used to retrieve metadata of the PDF documents and also their content in text format.
+ **openNLP**: provide functionalities to process text and retrieve word or name, based on the given name model. In this application some pre trained models are used to retrieve names from the parsed PDF document.

It has been designed in order to be as extendable as possible. In detail, it's composed by 4 modules composed by submodules and/or classes:

+ **MapReduce**: contains submodules and classes used by the MapReduce framework
    + **InputFormats**: this submodule contains the classes representing the InputFormat accepted by each mapper
        + *PDFInputFormat*: this class extends the basic FileInputFormat class. In particular it is in charge to instantiate a reader for the given file format, in this case a PDF file.
    + **Readers**: this submodules contains the reader classes representing the Readers instantiated by the different InputFormat classes.
        + *PDFRecordReader*: this class extends the basic RecordReader class. In particular it is in charge to process the PDF passed to it, and to split it in shards that will be send to the mapper. It also manage how the shards are passed to the mapper. 
    + **Mappers**: this submodule contains the mapper classes used by the MapReduce framework.
        + *nameRefMapper*: this class extends the basic Mapper class. It is fed with the input baked in the PDFRecordReader class. In particular is in charge to retrieve the cached NLP models, to apply some reflection hackery in order to workaround an Hadoop bugs, to parse its input value with the parser (using the NLP retrieved models) and to create the new <key,value> pairs. In this case a pair is composed by the name and the title of the file where it was found. 
    + *DriveService*: this class is in charge to manage Google Drive RPC. It creates a connection and exposes methods to retrive the list of the spreadsheet in the accredited account and to create a new one if necessary.
    + *MailService*: this class is in charge to manage Google Gmail RPC. It creates a connection and exposes methods to retrive unread messages by today, to retrive a message from a given history information, to start a watcher over the accredited user Gmail account via a pre-settled Google topic
    + *PubSubService*: this class is in charge to manage Google PubSub RPC. It creates a connection and exposes a method to watch over a subscription via PULL request and if a new publication is available it retrieves it and call the correct service to handle it (i.e. in this application the retrieved publication is a message containing the history changeLog  - when a mail comes- of the Gmail service, so the MailService is called in order to retrieve the just income mail)
    + *SpreadsheetService*: this class is in charge to manage Google PubSub RPC. It creates a connection and exposes a method to modify its content

Further information about classes and their methods can be find as comment into the code.

#Functionalities
----------------

The application run into a client (e.g. your personal computer). At its start the application, if not exists, creates a file named with the current date DDMMYYYY, then fill it with the unread mails till the current day. After that if a new mail incomes, the application appends its information to the file. There's no checking if an unread message was already appended, so duplication is allowed in the spreadsheet.

#Usage
----------------

To use the application open the terminal and type as the following snippet of code, from the folder where the executable is located:

`java -jar hw1-1.0.jar`

Press any key to stop it.
At the very first run the application will open a dialog in the browser asking to allow it to access with the specified privileges to the marco.arnaboldi91@gmail.com account. You need to provide those privileges being logged in Google as the account above. See the "Test" section to find out the credential needed to access with that account.

#Test
----------------
The tests and the application were developed in a OS X environment.

##### JUnit
Automated tests with JUnit were made for the services. To run them an internet connection is needed.
The services tested are:

+ **Drive**: creates a Test spreadsheet and deletes it
+ **GMail**: reads the unread mails
+ **Spreadsheet**: opens the Test file and append into it some values


##### Other tests
The testing was made using a python script that sends mail to the defined mail account. Usage of the scripts is the following, from terminal:

`python sendMail.py [-t <toaddr>] [-s <subject>] [-b <body>]`

If not specified, the parameters will assume the default values. The mail will be send out via the Gmail account specified later. If you want to change the sender, edit the script with your own sender and its password.

Steps for the test:

1. Launch the application
1. Send a mail to the account using the python script
1. Check if the mail was added to the current spreadsheet

To check the result the credential to access the accounts are (please don't change any account settings):

| Property       | Value                         |
| ------------- | ----------------------------- |
| Google Account| marco.arnaboldi91@gmail.com   |
| Password      | hh7-JAL-mJX-wBB               |


#Acknowledgments
---------------
Some inspiration was taken by the [Google API Tutorial](https://developers.google.com). The code was rewritten and readapted in order to implement the described functionalities.