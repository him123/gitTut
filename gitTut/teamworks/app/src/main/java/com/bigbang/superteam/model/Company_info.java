package com.bigbang.superteam.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by USER 8 on 27-Apr-16.
 */

public class Company_info implements Parcelable {

    public Company_info() {
    }

    public String companyid;
    public String deducution;
    public String deducutionHour1;
    public String deducutionHour2;
    public String deducutionHour3;
    public String deducutionHour4;
    public String deducutionHour4On;
    public String userid;
    public String basicSalary;
    public String hra;
    public String conveyance;
    public String medical;
    public String telephone;
    public String lta;
    public String specialIncentive;
    public String otherAllownace;
    public String pfEmployee;
    public String pfEmployer;
    public String profTax;
    public String tds;
    public String otherDeduction;
    public String notificationLevel;
    public String name;
    public String workingDays;
    public String startTime;
    public String endTime;
    public String payrollCycle;
    public String payrollStart;
    public String payrollEnd;
    public String salaryBreakupType;
    public String isPayrollEnabled;
    public String isRegularizationAllowed;
    public String isLateDeductionOn;
    public String autoLeaveUpdate;

    ArrayList<Holidays> holiday_array = new ArrayList<>();

    protected Company_info(Parcel in) {
        companyid = in.readString();
        deducution = in.readString();
        deducutionHour1 = in.readString();
        deducutionHour2 = in.readString();
        deducutionHour3 = in.readString();
        deducutionHour4 = in.readString();
        deducutionHour4On = in.readString();
        userid = in.readString();
        basicSalary = in.readString();
        hra = in.readString();
        conveyance = in.readString();
        medical = in.readString();
        telephone = in.readString();
        lta = in.readString();
        specialIncentive = in.readString();
        otherAllownace = in.readString();
        pfEmployee = in.readString();
        pfEmployer = in.readString();
        profTax = in.readString();
        tds = in.readString();
        otherDeduction = in.readString();
        notificationLevel = in.readString();
        name = in.readString();
        workingDays = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        payrollCycle = in.readString();
        payrollStart = in.readString();
        payrollEnd = in.readString();
        salaryBreakupType = in.readString();
        isPayrollEnabled = in.readString();
        isRegularizationAllowed = in.readString();
        isLateDeductionOn = in.readString();
        autoLeaveUpdate = in.readString();
        if (in.readByte() == 0x01) {
            holiday_array = new ArrayList<Holidays>();
            in.readList(holiday_array, Holidays.class.getClassLoader());
        } else {
            holiday_array = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(companyid);
        dest.writeString(deducution);
        dest.writeString(deducutionHour1);
        dest.writeString(deducutionHour2);
        dest.writeString(deducutionHour3);
        dest.writeString(deducutionHour4);
        dest.writeString(deducutionHour4On);
        dest.writeString(userid);
        dest.writeString(basicSalary);
        dest.writeString(hra);
        dest.writeString(conveyance);
        dest.writeString(medical);
        dest.writeString(telephone);
        dest.writeString(lta);
        dest.writeString(specialIncentive);
        dest.writeString(otherAllownace);
        dest.writeString(pfEmployee);
        dest.writeString(pfEmployer);
        dest.writeString(profTax);
        dest.writeString(tds);
        dest.writeString(otherDeduction);
        dest.writeString(notificationLevel);
        dest.writeString(name);
        dest.writeString(workingDays);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(payrollCycle);
        dest.writeString(payrollStart);
        dest.writeString(payrollEnd);
        dest.writeString(salaryBreakupType);
        dest.writeString(isPayrollEnabled);
        dest.writeString(isRegularizationAllowed);
        dest.writeString(isLateDeductionOn);
        dest.writeString(autoLeaveUpdate);
        if (holiday_array == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(holiday_array);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Company_info> CREATOR = new Parcelable.Creator<Company_info>() {
        @Override
        public Company_info createFromParcel(Parcel in) {
            return new Company_info(in);
        }

        @Override
        public Company_info[] newArray(int size) {
            return new Company_info[size];
        }
    };
}