package com.stormpull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;


public class DayMerger extends BaseBasicBolt  {
    
    Map<String,Date> counter=new HashMap<String,Date>();
    Map<String,Integer> c=new HashMap<String,Integer>();
    
    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofd) {
        ofd.declare(new Fields("id"));    
    }
    
    @Override
    public void cleanup(){
        System.out.println("One day merger bolt: ");
        for(Map.Entry<String, Integer> entry:c.entrySet())
            System.out.println(entry.getKey());
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector boc) {
        
        try {
            String id=tuple.getString(1);
            String status=tuple.getString(2);
            String date=tuple.getString(3);
            String[] DT=date.split(" ");
            Date dt = new SimpleDateFormat("yyyy-MM-dd").parse(DT[0]);
            Calendar cal=Calendar.getInstance();
            cal.setTime(dt);
            switch (status) {
                case "opened":
                    cal.add(Calendar.DAY_OF_YEAR, 1);
                    counter.put(id,cal.getTime());
                    break;
                case "discussed":
                    discussed(id, cal);
                    break;
                case "merged":
                    merged(id, cal, boc);
                    break;
            }
        } 
        catch (ParseException ex) {
            Logger.getLogger(DayMerger.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    void discussed(String id,Calendar cal){  
        Date temp = counter.get(id);
        if (temp!=null){
            if(temp.after(cal.getTime())){
                Integer count=c.get(id);
                if(count ==null){
                    count=0;
                }
                count++;
                c.put(id,count);
            }
        }
    }
    
    void merged(String id,Calendar cal,BasicOutputCollector boc){
        Date temp1 = counter.get(id);
        if (temp1!=null){
            if(temp1.after(cal.getTime())){
                Integer count=c.get(id);
                if(count==null) {
                    count=0;
                } 
                if(count>=10) {
                    boc.emit(new Values(id));
                }
            }
        }    
    } 
}
