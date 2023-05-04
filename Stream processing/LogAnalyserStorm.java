package com.stormpull;
//import storm configuration packages
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;

//Create main class LogAnalyserStorm submit topology.
public class LogAnalyserStorm {
   public static void main(String[] args) throws Exception{
      //Create Config instance for cluster configuration
      Config config = new Config();
      config.setDebug(true);

      TopologyBuilder builder = new TopologyBuilder();
      InputSpout inputSpout=new InputSpout();
      DailyCounterBolt dailyBolt=new DailyCounterBolt();
      WeeklyCounterBolt WeeklyBolt=new WeeklyCounterBolt();
      DayMerger daymrg=new DayMerger();

      builder.setSpout("log-reader-spout", inputSpout,1);

      builder.setBolt("daily-counter-bolt", dailyBolt,1)
              .shuffleGrouping("log-reader-spout");
      builder.setBolt("weekly-counter-bolt", WeeklyBolt,1)
              .shuffleGrouping("log-reader-spout");
      builder.setBolt("one-day-merger", daymrg,1)
              .shuffleGrouping("log-reader-spout");
			
      LocalCluster cluster = new LocalCluster();
      cluster.submitTopology("LogAnalyserStorm", config, builder.createTopology());
      Thread.sleep(100000);
		
      cluster.shutdown();
   }
}