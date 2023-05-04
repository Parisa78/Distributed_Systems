package com.stormpull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Values;
import org.apache.storm.tuple.Fields;

public class InputSpout extends BaseRichSpout {
  BufferedReader file;
  SpoutOutputCollector collector;

  @Override
  public void open(Map<String, Object> conf, TopologyContext context, SpoutOutputCollector collector) {
    this.collector = collector;
      try {
          file = new BufferedReader(new FileReader("C:/Users/Mohaymen/Downloads/pullreq_events.csv"));
      } catch (FileNotFoundException ex) {
          Logger.getLogger(InputSpout.class.getName()).log(Level.SEVERE, null, ex);
      }

  }

  @Override
  public void nextTuple() {
    try {
      String line = null;
      if ((line = file.readLine()) != null) {
        String[] data = line.split(",");
        collector.emit(new Values(data[0], data[1], data[2], data[3]));
      }
    } catch (IOException e) {
        if (file!=null){
            try {
                file.close();
            }
            catch (IOException ex) {
                Logger.getLogger(InputSpout.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("id", "member", "event", "time"));
  }
}