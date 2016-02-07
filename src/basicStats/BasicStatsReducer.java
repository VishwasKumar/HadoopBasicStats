package basicStats;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class BasicStatsReducer extends Reducer<Text, StatsWritable, Text, DoubleWritable>{
	public void reduce(Text key, Iterable<StatsWritable> partialValues,
			Context context)
			throws IOException, InterruptedException {
		Iterator<StatsWritable> partialSeperates = partialValues.iterator();
		StatsWritable partials = new StatsWritable();
		double standardDeviation = 0;

		while (partialSeperates.hasNext()){
			partials.mergeStats(partialSeperates.next());
		}
		standardDeviation = Math.sqrt((partials.squareSum/partials.count) - (Math.pow((partials.sum/partials.count), 2.0)));
		
		context.write(new Text("AVG: "), new DoubleWritable((Math.round((partials.sum/partials.count) * 100.0)/100.0)));
		context.write(new Text("MIN: "), new DoubleWritable(partials.min));
		context.write(new Text("MAX: "), new DoubleWritable(partials.max));
		context.write(new Text("STD DEV ROUND: "), new DoubleWritable((Math.floor(standardDeviation * 10000.0)/10000.0)));
	}
}
