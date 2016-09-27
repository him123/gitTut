package com.bigbang.superteam.dataObjs;

import com.bigbang.superteam.util.Util;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by USER 3 on 23/04/2015.
 */
public class GeneralObj implements Comparable {
    public String Name;
    public String Description;
    public String Url;
    public int alertCounter;
    public int taskCode;
    public String Status;
    public String endDate;

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public int getAlertCounter() {
        return alertCounter;
    }

    public void setAlertCounter(int alertCounter) {
        this.alertCounter = alertCounter;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public int getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(int taskCode) {
        this.taskCode = taskCode;
    }

    @Override
    public int compareTo(Object o) {
        int alert = ((GeneralObj) o).getAlertCounter();
        if (alert > 0 || this.getAlertCounter() > 0) {
            return alert - this.getAlertCounter();
        }
        try {
            Date d1 = Util.sdf.parse(((GeneralObj) o).getEndDate());
            Date d2 = Util.sdf.parse(this.getEndDate());
            if (d1.before(d2)) return 1;
            if (d2.before(d1)) return -1;
            return 0;
        } catch (ParseException e) {
            e.printStackTrace();
            return alert - this.getAlertCounter();
        }
    }
}