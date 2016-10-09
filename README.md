CS441 @ UIC: HOMEWORK2
====================
Developed by Marco Arnaboldi (marnab2@uic.edu)

#Description
--------------------
Your second homework assignment is to create a distributed software application for automatically analyzing the content of research papers in computer science using a map/reduce model. You will create and run your software application using [Apache Hadoop](http://hadoop.apache.org/), a framework for distributed processing of large data sets across multiple computers (or even on a single node) using the map/reduce model. Even though you can install and configure Hadoop on your computers, I recommend that you use a virtual machine of [Hortonworks Sandbox](http://hortonworks.com/products/sandbox/), a preconfigured Apache Hadoop installation with a comprehensive software stack . To run the VM, you can install vmWare or VirtualBox. As UIC students, you have access to [free vmWare licenses](http://go.uic.edu/csvmware). You can complete this homework using either Java (I prefer that you use Java for this assignment). You will use Simple Build Tools (SBT) for building the project and running automated tests. You can use the latest community version of IntelliJ IDE for this assignment.
To process research papers as the input to your application, you will use the [Apache Tika toolkit](http://tika.apache.org/) to detect and extract metadata and text from different file types (i.e., PPT and PDF). Next, once a plain text is extracted from these documents with Apache Tika, you will use the [Apache OpenNLP library](http://opennlp.apache.org/) to process the natural language text. Your ultimate goal is to use the Name Finder to detect named entities in the text using some pre-trained name finder models, many of which are obtained by training the Name Finder on available corpora. These models can be downloaded at the OpenNLP model download page. Details about sentence detector and tokenizer are given in many tutorials on the OpenNLP website along with examples how to find names in text by segmenting it into tokens and sentences. Finally, the output of your program is a list of referenced named in multiple documents with information about documents where these names are found.
Your implementation should include the following steps. You will provide a script to download a few papers from the command line. The script should take command-line parameters for the search keywords. If the user does not provide any command-line parameters, the script should search and retrieve some papers using default hard-coded values. After some papers are downloaded, they will be partitioned into shards, and then your Hadoop-based program will run to extract named references. Besides pure development, you will learn how to take a nontrivial task and partition it into mappers and reducers. 


#Development & Design choices
-----------------

######Script
The script to retrive pdf from the web was developed using python3, exploiting the [IEEE APIs](http://ieeexplore.ieee.org/gateway/) in order to retrieve the papers. It also exploits the "wget" CLI functionality provided by the OS, hence corresponding command and the python modules used by the script should be installed in the system before using it. 

######Application
The application was developed with with IntelliJIDEA IDE for a distributed environment where Hadoop and MapReduce are installed and configured. SBT was also exploited in order to manage the libraries and to create a fat jar containing all the needed jars and also the name finder pertained models. In particular it was developed using the following environment: the Hortonworks SandBox VM, hosted via VMWARE in a OS X 10 native environment.
The application also exploits the Tika and openNLP libraries in order to process PDFs and their content:

+ **Tika**: provides functionalities to process PDF. In this application it is used to retrieve metadata of the PDF documents and also their content in text format.
+ **openNLP**: provide functionalities to process text and retrieve word or name, based on the given name model. In this application some pre trained models are used to retrieve names from the parsed PDF document.

It has been designed in order to be as extendable as possible. In detail, it's composed by 4 modules composed by submodules and/or classes:

+ **Main**: this class is in charge to configure the job and run it. It passes the PDF files -in the given folder- to the mappers and it is also in charge to configure the mappers and reducers behavior. Furthermore it is also addressed to cache the NLP pre trained models, in order to let them available to all the mappers.
+ **MapReduce**: contains submodules and classes used by the MapReduce framework
    + **InputFormats**: this submodule contains the classes representing the InputFormat accepted by each mapper
        + *PDFInputFormat*: this class extends the basic FileInputFormat class. In particular it is in charge to instantiate a reader for the given file format, in this case a PDF file.
    + **Readers**: this submodules contains the reader classes representing the Readers instantiated by the different InputFormat classes.
        + *PDFRecordReader*: this class extends the basic RecordReader class. In particular it is in charge to process the PDF passed to it, and to split it in shards that will be send to the mapper. It also manage how the shards are passed to the mapper. 
    + **Mappers**: this submodule contains the mapper classes used by the MapReduce framework.
        + *nameRefMapper*: this class extends the basic Mapper class. It is fed with the input baked in the PDFRecordReader class. In particular is in charge to retrieve the cached NLP models, to apply some reflection hackery in order to workaround an Hadoop bugs, to parse its input value with the parser (using the NLP retrieved models) and to create the new <key,value> pairs. In this case a pair is composed by the name and the title of the file where it was found. 
    + **Reducers**: this submodule contains the reducers classes used by the MapReduce framework.
        + *nameRefReducer*: this class extends the basic Reducer class. It is fed with the output coming from the mappers. It is in charge to aggregate the <key,value> pairs over the key field. In particular the value (in this case the titles of the PDF) are concatenated and then send out.
+ **Models**: this modules contains useful classes to represent the entities used in the application in a more compact way.
    + *PDF*: this class represents a PDF. It is composed by two fields, the metadata field and the content field and exposes method to access and modify them.
+ **Utils**: this module contains util classes useful to work with and over the models.
    + *NameFinder*: this class provides methods to find names in a given string. It loads the given NLP models and based on them, the methods processed the passed string, returning a list of names. This class exploits openNLP library and it's exploited in the NameRefMapper class.
    + *PDFAnalizer*: this class provide methods to load PDF information. In particular provide methods to load a physical PDF file from URL to the PDF model class. This class exploits Tika library and it's exploited by the PDFRecordReader class.
  
Further information about classes and their methods can be find as comment into the code.

#Functionalities
----------------

The application runs in a distributed Hadoop MapReduce environment. Given a set of academic publication in PDF format, it retrieves -for each author name- a list of references representing the name of the PDFs where the author name is found. The list is not well formatted, some decoration function should be applied. Furthermore the pre trained NLP models used are not well performing returning sometime something that is not a personal name.

#Usage
----------------

######Script
To use the scripts check that python3 is installed and the required modules are also installed via pip, also "wget" should be installed as terminal command.
Then, via terminal, navigate till the folder where the script file is located in your machine and type the following:

`python3 pdfRetrival.py [-n <numberOfPDFs>] [-a <author>] [-y <year>] [-t <text>]`

If the parameters are not passed, the script runs with hardcoded values. The PDFs are saved in the same folder of the scripts. Since IEEE PDFs are download from [IEEEXplorer](http://ieeexplore.ieee.org/Xplore/home.jsp) an internet connection via a proxy server that provide access to the IEEEXplorer website is required (e.g. access trough the UIC network).

#####Application
To use the application, first of all create your own folder in the HDFS, by typing the following command in your Hadoop configured environment:

`hadoop fs -mkdir /dir/path/`

The folder should contain onlu PDF files. After that, load the files from local to the HDFS, in the created folder, by typing:

`hadoop fs -copyFromLocal /path/of/your/pdf/files/*.pdf /path/to/the/just/created/folder`

At this point launch the jar, by typing:

`hadoop jar nameFinder-Arnaboldi-assembly-final.jar /path/to/the/jar /path/to/the/hdfs/input/folder /path/to/output/folder`

The output of the application will be in the given output folder, that should not exist prior the computation. It will be created automatically. 

#Test
----------------
The tests were ran locally for what concerns utils and model classes, instead the test for the the MapReduce classes were ran in a Hortonworks Sandbox VM version 2.7.

##### JUnit
Automated tests with JUnit were made for utils and the model classes. Using a a test example PDF in resources. The classes in *Utils* and *Models* classes were tested in this way, in order to familiarize with the Tika and OpenNLP libraries and to check that chaining the provided functionalities will provide a corrected an expected behavior.


##### Other tests
The testing was made in a Hortonworks Sandbox VM version 2.7, with Hadoop and MapReduce already configured.
A bunch of pdfs was downloaded by internet, loaded into the HDFS and then the application were ran over those pdfs. The PDFs used for the test can be found in the root of these repository. And the expected output should be as in image. I've noticed that the sharding phase took a while before the mappers could start working, it depends on how many files are analyzed and how is performing the VM. 

![Alt text](https://bytebucket.org/MrArnab/marco_arnaboldi_cs441hw2/raw/60cd363c905e4bb599c1ea7b33d44850f17d9006/images/output.png?token=62f864d24a0b3ad59e5801a9d35538d3df8fe4eb)

#Acknowledgments
---------------
Some inspiration was taken by the [Hortonwork Hadoop Tutorial](http://it.hortonworks.com/hadoop-tutorial/hello-world-an-introduction-to-hadoop-hcatalog-hive-and-pig/), [Tika Tutorial](http://tika.apache.org/1.13/examples.html) and [OpenNLP Tutorial](http://www.programcreek.com/2012/05/opennlp-tutorial/). The code was rewritten and readapted in order to implement the described functionalities.