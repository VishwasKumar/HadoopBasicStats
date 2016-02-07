if [ ! -d classes ]; then
        mkdir classes;
fi

javac -classpath $HADOOP_HOME/hadoop-core-1.1.2.jar:$HADOOP_HOME/lib/commons-cli-1.2.jar:$HADOOP_HOME/log4j-1.2.17.jar -d ./classes src/*/*.java

jar -cvf basicStats.jar -C ./classes/ .

hadoop fs -rmr output

hadoop jar basicStats.jar basicStats.BasicStats input output
