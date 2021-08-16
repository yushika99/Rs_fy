package com.example.rs_fy;

public class GoalData {

    String goalItem,goaldate,goalid,goalnotes;
    int goalamount,goalmonth;

    public GoalData() {
    }

    public GoalData(String goalItem, String goaldate, String goalid, String goalnotes, int goalamount, int goalmonth) {
        this.goalItem = goalItem;
        this.goaldate = goaldate;
        this.goalid = goalid;
        this.goalnotes = goalnotes;
        this.goalamount = goalamount;
        this.goalmonth = goalmonth;
    }

    public String getGoalItem() {
        return goalItem;
    }

    public void setGoalItem(String goalItem) {
        this.goalItem = goalItem;
    }

    public String getGoaldate() {
        return goaldate;
    }

    public void setGoaldate(String goaldate) {
        this.goaldate = goaldate;
    }

    public String getGoalid() {
        return goalid;
    }

    public void setGoalid(String goalid) {
        this.goalid = goalid;
    }

    public String getGoalnotes() {
        return goalnotes;
    }

    public void setGoalnotes(String goalnotes) {
        this.goalnotes = goalnotes;
    }

    public int getGoalamount() {
        return goalamount;
    }

    public void setGoalamount(int goalamount) {
        this.goalamount = goalamount;
    }

    public int getGoalmonth() {
        return goalmonth;
    }

    public void setGoalmonth(int goalmonth) {
        this.goalmonth = goalmonth;
    }
}
