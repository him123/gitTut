package com.bigbang.superteam.dataObjs;

/**
 * Created by USER 3 on 4/20/2015.
 */
public class CompanyInfo {
    int Company_ID;


    String Company_Name;
    String Mobile_No;
    String Landline_No;
    String Email_ID;
    String Address;
    String Segment;
    int Payroll;
    int Notification_Level;
    String Location;
    Double Latitude;
    Double Longitude;
    String Working_Days;
    String Working_StartTime;
    String Working_EndTime;
    String Holidays;
    int SL_Allowed;
    int OL_Allowed;
    int PL_Allowed;
    int CL_Allowed;

    public enum PayrollCycle {
        Monthly,
        Weekly
    }

    PayrollCycle Payeroll_Cycle;
    int Payroll_Period_Start;
    int Payroll_Period_End;
    int Late_Early_Regulization;
    String Deduction;
    int Deduction_Hour_1;
    int Deduction_Hour_2;
    int Deduction_Hour_3;
    int Deduction_Hour_4;
    int Deduction_Hour_After_4;

    public enum Ctc {
        Flat,
        Breakup
    }

    Ctc CTC;
    long Basic_Salary;
    long HRA;
    long Conveyance;
    long Medical;
    long Telephone;
    long LTA;
    long Special_Incentive;
    long Other_Allowance;
    long PF_Employee;
    long PF_Employer;
    long Professional_Tax;
    long TDS;
    long Other_Deductions;

    public int getCompany_ID() {
        return Company_ID;
    }

    public void setCompany_ID(int company_ID) {
        Company_ID = company_ID;
    }

    public String getCompany_Name() {
        return Company_Name;
    }

    public void setCompany_Name(String company_Name) {
        Company_Name = company_Name;
    }

    public String getMobile_No() {
        return Mobile_No;
    }

    public void setMobile_No(String mobile_No) {
        Mobile_No = mobile_No;
    }

    public String getLandline_No() {
        return Landline_No;
    }

    public void setLandline_No(String landline_No) {
        Landline_No = landline_No;
    }

    public String getEmail_ID() {
        return Email_ID;
    }

    public void setEmail_ID(String email_ID) {
        Email_ID = email_ID;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getSegment() {
        return Segment;
    }

    public void setSegment(String segment) {
        Segment = segment;
    }

    public int getPayroll() {
        return Payroll;
    }

    public void setPayroll(int payroll) {
        Payroll = payroll;
    }

    public int getNotification_Level() {
        return Notification_Level;
    }

    public void setNotification_Level(int notification_Level) {
        Notification_Level = notification_Level;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public String getWorking_Days() {
        return Working_Days;
    }

    public void setWorking_Days(String working_Days) {
        Working_Days = working_Days;
    }

    public String getWorking_StartTime() {
        return Working_StartTime;
    }

    public void setWorking_StartTime(String working_StartTime) {
        Working_StartTime = working_StartTime;
    }

    public String getWorking_EndTime() {
        return Working_EndTime;
    }

    public void setWorking_EndTime(String working_EndTime) {
        Working_EndTime = working_EndTime;
    }

    public String getHolidays() {
        return Holidays;
    }

    public void setHolidays(String holidays) {
        Holidays = holidays;
    }

    public int getSL_Allowed() {
        return SL_Allowed;
    }

    public void setSL_Allowed(int SL_Allowed) {
        this.SL_Allowed = SL_Allowed;
    }

    public int getOL_Allowed() {
        return OL_Allowed;
    }

    public void setOL_Allowed(int OL_Allowed) {
        this.OL_Allowed = OL_Allowed;
    }

    public int getPL_Allowed() {
        return PL_Allowed;
    }

    public void setPL_Allowed(int PL_Allowed) {
        this.PL_Allowed = PL_Allowed;
    }

    public int getCL_Allowed() {
        return CL_Allowed;
    }

    public void setCL_Allowed(int CL_Allowed) {
        this.CL_Allowed = CL_Allowed;
    }

    public PayrollCycle getPayeroll_Cycle() {
        return Payeroll_Cycle;
    }

    public void setPayeroll_Cycle(PayrollCycle payeroll_Cycle) {
        Payeroll_Cycle = payeroll_Cycle;
    }

    public int getPayroll_Period_Start() {
        return Payroll_Period_Start;
    }

    public void setPayroll_Period_Start(int payroll_Period_Start) {
        Payroll_Period_Start = payroll_Period_Start;
    }

    public int getPayroll_Period_End() {
        return Payroll_Period_End;
    }

    public void setPayroll_Period_End(int payroll_Period_End) {
        Payroll_Period_End = payroll_Period_End;
    }

    public int getLate_Early_Regulization() {
        return Late_Early_Regulization;
    }

    public void setLate_Early_Regulization(int late_Early_Regulization) {
        Late_Early_Regulization = late_Early_Regulization;
    }

    public String getDeduction() {
        return Deduction;
    }

    public void setDeduction(String deduction) {
        Deduction = deduction;
    }

    public int getDeduction_Hour_1() {
        return Deduction_Hour_1;
    }

    public void setDeduction_Hour_1(int deduction_Hour_1) {
        Deduction_Hour_1 = deduction_Hour_1;
    }

    public int getDeduction_Hour_2() {
        return Deduction_Hour_2;
    }

    public void setDeduction_Hour_2(int deduction_Hour_2) {
        Deduction_Hour_2 = deduction_Hour_2;
    }

    public int getDeduction_Hour_3() {
        return Deduction_Hour_3;
    }

    public void setDeduction_Hour_3(int deduction_Hour_3) {
        Deduction_Hour_3 = deduction_Hour_3;
    }

    public int getDeduction_Hour_4() {
        return Deduction_Hour_4;
    }

    public void setDeduction_Hour_4(int deduction_Hour_4) {
        Deduction_Hour_4 = deduction_Hour_4;
    }

    public int getDeduction_Hour_After_4() {
        return Deduction_Hour_After_4;
    }

    public void setDeduction_Hour_After_4(int deduction_Hour_After_4) {
        Deduction_Hour_After_4 = deduction_Hour_After_4;
    }

    public Ctc getCTC() {
        return CTC;
    }

    public void setCTC(Ctc CTC) {
        this.CTC = CTC;
    }

    public long getBasic_Salary() {
        return Basic_Salary;
    }

    public void setBasic_Salary(long basic_Salary) {
        Basic_Salary = basic_Salary;
    }

    public long getHRA() {
        return HRA;
    }

    public void setHRA(long HRA) {
        this.HRA = HRA;
    }

    public long getConveyance() {
        return Conveyance;
    }

    public void setConveyance(long conveyance) {
        Conveyance = conveyance;
    }

    public long getMedical() {
        return Medical;
    }

    public void setMedical(long medical) {
        Medical = medical;
    }

    public long getTelephone() {
        return Telephone;
    }

    public void setTelephone(long telephone) {
        Telephone = telephone;
    }

    public long getLTA() {
        return LTA;
    }

    public void setLTA(long LTA) {
        this.LTA = LTA;
    }

    public long getSpecial_Incentive() {
        return Special_Incentive;
    }

    public void setSpecial_Incentive(long special_Incentive) {
        Special_Incentive = special_Incentive;
    }

    public long getOther_Allowance() {
        return Other_Allowance;
    }

    public void setOther_Allowance(long other_Allowance) {
        Other_Allowance = other_Allowance;
    }

    public long getPF_Employee() {
        return PF_Employee;
    }

    public void setPF_Employee(long PF_Employee) {
        this.PF_Employee = PF_Employee;
    }

    public long getPF_Employer() {
        return PF_Employer;
    }

    public void setPF_Employer(long PF_Employer) {
        this.PF_Employer = PF_Employer;
    }

    public long getProfessional_Tax() {
        return Professional_Tax;
    }

    public void setProfessional_Tax(long professional_Tax) {
        Professional_Tax = professional_Tax;
    }

    public long getTDS() {
        return TDS;
    }

    public void setTDS(long TDS) {
        this.TDS = TDS;
    }

    public long getOther_Deductions() {
        return Other_Deductions;
    }

    public void setOther_Deductions(long other_Deductions) {
        Other_Deductions = other_Deductions;
    }


}
