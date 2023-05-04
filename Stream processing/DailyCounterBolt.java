package com.stormpull;

import java.util.HashMap;
import java.util.Map;
import org.apache.storm.tuple.Values;

import org.apache.storm.tuple.Fields;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

public class DailyCounterBolt extends BaseBasicBolt {
    Map<String, Integer> counts = new HashMap<>();
    
    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        String date = input.getString(3).split(" ")[0];
        Integer count = counts.get(date);

        if (count == null)
            count = 0;
        count++;
        counts.put(date, count);
        collector.emit(new Values(date, count));
    }
    
    @Override
    public void cleanup() {
        System.out.println("Daily counter bolt: ");
        for(Map.Entry<String, Integer> entry:counts.entrySet())
            System.out.println(entry.getKey()+" : " + entry.getValue());     
    }
    
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("date", "count"));
    }
}
