# airports-renue-test

The program allows you to search airports from a csv file by a search word in a given column.<br>
The column is recorded in the ``application.xml`` file. User can add column number as parameter to ``java -jar`` command.

### Create the executable JAR file
    mvn clean package assembly:single

### Run the JAR file
    java -jar ./target/airports-renue-1.0-SNAPSHOT.jar [<column number>]