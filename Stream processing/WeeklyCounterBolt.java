package com.stormpull;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;

import org.apache.storm.tuple.Values;

import org.apache.storm.tuple.Fields;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

public class WeeklyCounterBolt extends BaseBasicBolt {
    Map<String, Integer> counterMap=new HashMap<>();
    Map<String, Integer> weekcount=new HashMap<>();
    Map<String, String> weekCountWithID=new HashMap<>();
    ArrayList<String> arrayList1 = new ArrayList<>();
    ArrayList<String> arrayList2 = new ArrayList<>();
    ArrayList<Integer>arrayList3=new ArrayList<>();

    @Override
    public void execute(Tuple tuple,BasicOutputCollector collector) {
        try {
            String id=tuple.getString(1);
            String date=tuple.getString(3);
            String[] temp_date=date.split(" ");
            Date dt = new SimpleDateFormat("yyyy-MM-dd").parse(temp_date[0]);
            Calendar cal=Calendar.getInstance();
            cal.setTime(dt);
            int weekNum=cal.get(Calendar.WEEK_OF_YEAR);
            int yearNum=cal.get(Calendar.YEAR);
            String fieldDate = Integer.toString(yearNum) + "-" + Integer.toString(weekNum);
            String key = fieldDate + "-" + id;
            
            Integer count = counterMap.get(key);
            if (count == null)
                count = 0;
            count++;
            counterMap.put(key, count);
            
            Integer countWeek = weekcount.get(fieldDate);
            if (countWeek == null)
                countWeek = 0;
            
            if (count > countWeek) {
                weekcount.put(fieldDate, count);
                weekCountWithID.put(fieldDate, id);
                collector.emit(new Values(fieldDate, id, count));
            }
        } 
        catch (ParseException ex) {
            Logger.getLogger(WeeklyCounterBolt.class.getName()).log(Level.SEVERE, null, ex);
        }		
   }

    @Override
    public void cleanup() { 
        System.out.println("Weekly counter bolt: ");
        for(Map.Entry<String, Integer> entry:weekcount.entrySet()){
         arrayList1.add(entry.getKey());
         arrayList3.add(entry.getValue());  
        }
        
        for (Map.Entry<String, String> entry : weekCountWithID.entrySet()) 
            arrayList2.add(entry.getValue());
        
        for (int i=0;i<arrayList1.size();i++)
            System.out.println(arrayList1.get(i)+" : " + arrayList2.get(i)+" : "+arrayList3.get(i));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("date","pull_request_id","number_of_events"));
    }	
}