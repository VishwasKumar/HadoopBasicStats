package basicStats;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.util.LineReader;

public class CustomRecordReader extends RecordReader<LongWritable, Text>{
	
	private int linesToProcess;
    private LineReader lineReader;
    private LongWritable key;
    private Text value = new Text();
    private long startBlock = 0;
    private long endBlock = 0;
    private long position = 0;
    private int maximumLineLength;
    
    @Override
    public void close() throws IOException {
        if (lineReader != null) {
            lineReader.close();
        }
    }
 
	@Override
    public LongWritable getCurrentKey() throws IOException,InterruptedException {
        return key;
    }
 
	@Override
    public Text getCurrentValue() throws IOException, InterruptedException {
        return value;
    }
 
	@Override
    public float getProgress() throws IOException, InterruptedException {
        if (startBlock == endBlock) {
            return 0.0f;
        }
        else {
            return Math.min(1.0f, (position - startBlock) / (float)(endBlock - startBlock));
        }
    }
 
	@Override
    public void initialize(InputSplit genericSplit, TaskAttemptContext context)throws IOException, InterruptedException {
		FileSplit split = (FileSplit) genericSplit;
        final Path file = split.getPath();
        Configuration conf = context.getConfiguration();
        this.maximumLineLength = conf.getInt("mapred.linerecordreader.maxlength",Integer.MAX_VALUE);
        linesToProcess = conf.getInt("LINES_TO_PROCESS", 1);
        FileSystem fs = file.getFileSystem(conf);
        startBlock = split.getStart();
        endBlock= startBlock + split.getLength();
        boolean skipFirstLine = false;
        FSDataInputStream filein = fs.open(split.getPath());
 
        if (startBlock != 0){
            skipFirstLine = true;
            --startBlock;
            filein.seek(startBlock);
        }
        lineReader = new LineReader(filein,conf);
        if(skipFirstLine){
            startBlock += lineReader.readLine(new Text(),0,(int)Math.min((long)Integer.MAX_VALUE, endBlock - startBlock));
        }
        this.position = startBlock;
    }
 
	@Override
    public boolean nextKeyValue() throws IOException, InterruptedException {
        if (key == null) {
            key = new LongWritable();
        }
        key.set(position);
        if (value == null) {
            value = new Text();
        }
        value.clear();
        final Text endline = new Text("\n");
        int newSize = 0;
        for(int i=0; i < linesToProcess; i++){
            Text v = new Text();
            while (position < endBlock) {
                newSize = lineReader.readLine(v, maximumLineLength,Math.max((int)Math.min(Integer.MAX_VALUE, endBlock-position),maximumLineLength));
                value.append(v.getBytes(),0, v.getLength());
                value.append(endline.getBytes(),0, endline.getLength());
                if (newSize == 0) {
                    break;
                }
                position += newSize;
                if (newSize < maximumLineLength) {
                    break;
                }
            }
        }
        if (newSize == 0) {
            key = null;
            value = null;
            return false;
        } else {
            return true;
        }
    }
}