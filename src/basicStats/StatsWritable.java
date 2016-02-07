package basicStats;

import java.io.DataInput; 
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class StatsWritable implements Writable {
	
	protected Double min, max, sum, count, squareSum; 
	
	public StatsWritable() {
		this.min = new Double(Double.MAX_VALUE);
		this.max = new Double(Double.MIN_VALUE);
		this.count = new Double(0.00);
		this.sum = new Double(0.00);
		this.squareSum = new Double(0.00);
	}

	@Override
	public void readFields(DataInput input) throws IOException {
		min = input.readDouble();
		max = input.readDouble();
		count = input.readDouble();
		sum = input.readDouble();
		squareSum = input.readDouble();

	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeDouble(min);
		output.writeDouble(max);
		output.writeDouble(count);
		output.writeDouble(sum);
		output.writeDouble(squareSum);
	}
	
	public void calculateStats(Double statsData){
		this.min = Math.min(this.min, statsData);
		this.max = Math.max(this.max, statsData);
		this.count++;
		this.sum = this.sum + statsData;
		this.squareSum = this.squareSum + Math.pow(statsData, 2.0);
	}
	
	public void mergeStats(StatsWritable statsData){
		this.min = Math.min(this.min, statsData.min);
		this.max = Math.max(this.max, statsData.max);
		this.count += statsData.count;
		this.sum = this.sum + statsData.sum;
		this.squareSum = this.squareSum + statsData.squareSum;
	}
}
