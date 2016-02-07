package basicStats;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class BasicStatsMapper extends Mapper<LongWritable, Text, Text, StatsWritable>{
	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
	
		StatsWritable statsWritable = new StatsWritable();
		double rawData;
		
		StringTokenizer tokenizer = new StringTokenizer(value.toString(), "\n");
		
		while (tokenizer.hasMoreTokens()) {
			rawData = Double.parseDouble(tokenizer.nextToken());
			statsWritable.calculateStats(rawData);
		}
		System.out.println(statsWritable.count);
		context.write(new Text("Mapper"), statsWritable);
	}
}
