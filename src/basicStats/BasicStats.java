package basicStats;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;


public class BasicStats {

	public static void main(String args[]) throws Exception{
		Configuration config = new Configuration();
		String[] otherArgs = new GenericOptionsParser(config, args).getRemainingArgs(); // get all args
	    if (otherArgs.length != 2) {
	      System.err.println("Usage: WordCount <in> <out>");
	      System.exit(2);
	    }
	    config.setInt("LINES_TO_PROCESS", 100);
		Job job = new Job(config, "Basic Stats");	
		job.setJarByClass(BasicStats.class);
		job.setMapperClass(BasicStatsMapper.class);
		job.setReducerClass(BasicStatsReducer.class);
		
		job.setMapOutputKeyClass(Text.class);
	    job.setMapOutputValueClass(StatsWritable.class);
	    
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(DoubleWritable.class);
	    
	    job.setInputFormatClass(CustomInputFormat.class);
	    job.setOutputFormatClass(TextOutputFormat.class);
	    
	    FileInputFormat.setInputPaths(job, new Path(args[0])); 
	    FileOutputFormat.setOutputPath(job, new Path(args[1])); 
	      
	    System.exit(job.waitForCompletion(true) ? 0 : 1); 
	}
}
