package com.bigbang.superteam.payroll;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 6/17/2016.
 */
public class UserPayslipDetail implements Parcelable {
    int ID;
    int UserID;
    int CompanyID;
    int PayslipGenerationMonth;
    int PayslipGenerationYear;
    String PayslipGenerationDate;
    double TotalWorkingDays;
    double TotalPaidDays;
    double TotalLeavesTaken;
    double BasicSalary;
    double HRA;
    double SpecialAllowance;
    double ConveyanceAllowance;
    double MedicalExpenses;
    double VariableEarnings;
    double GrossEarnings;
    double PFEmployeeContribution;
    double PFEmployerContribution;
    double LeavesDeduction;
    double TDS;
    double ProfessionalTax;
    double ShortDeduction;
    double GrossDeduction;
    double NetSalary;
    boolean IsActive;
    int CreatedBy;
    String CreatedTime;
    int lastModifiedBy;

    protected UserPayslipDetail(Parcel in) {
        ID = in.readInt();
        UserID = in.readInt();
        CompanyID = in.readInt();
        PayslipGenerationMonth = in.readInt();
        PayslipGenerationYear = in.readInt();
        PayslipGenerationDate = in.readString();
        TotalWorkingDays = in.readDouble();
        TotalPaidDays = in.readDouble();
        TotalLeavesTaken = in.readDouble();
        BasicSalary = in.readDouble();
        HRA = in.readDouble();
        SpecialAllowance = in.readDouble();
        ConveyanceAllowance = in.readDouble();
        MedicalExpenses = in.readDouble();
        VariableEarnings = in.readDouble();
        GrossEarnings = in.readDouble();
        PFEmployeeContribution = in.readDouble();
        PFEmployerContribution = in.readDouble();
        LeavesDeduction = in.readDouble();
        TDS = in.readDouble();
        ProfessionalTax = in.readDouble();
        ShortDeduction = in.readDouble();
        GrossDeduction = in.readDouble();
        NetSalary = in.readDouble();
        IsActive = in.readByte() != 0x00;
        CreatedBy = in.readInt();
        CreatedTime = in.readString();
        lastModifiedBy = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeInt(UserID);
        dest.writeInt(CompanyID);
        dest.writeInt(PayslipGenerationMonth);
        dest.writeInt(PayslipGenerationYear);
        dest.writeString(PayslipGenerationDate);
        dest.writeDouble(TotalWorkingDays);
        dest.writeDouble(TotalPaidDays);
        dest.writeDouble(TotalLeavesTaken);
        dest.writeDouble(BasicSalary);
        dest.writeDouble(HRA);
        dest.writeDouble(SpecialAllowance);
        dest.writeDouble(ConveyanceAllowance);
        dest.writeDouble(MedicalExpenses);
        dest.writeDouble(VariableEarnings);
        dest.writeDouble(GrossEarnings);
        dest.writeDouble(PFEmployeeContribution);
        dest.writeDouble(PFEmployerContribution);
        dest.writeDouble(LeavesDeduction);
        dest.writeDouble(TDS);
        dest.writeDouble(ProfessionalTax);
        dest.writeDouble(ShortDeduction);
        dest.writeDouble(GrossDeduction);
        dest.writeDouble(NetSalary);
        dest.writeByte((byte) (IsActive ? 0x01 : 0x00));
        dest.writeInt(CreatedBy);
        dest.writeString(CreatedTime);
        dest.writeInt(lastModifiedBy);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserPayslipDetail> CREATOR = new Parcelable.Creator<UserPayslipDetail>() {
        @Override
        public UserPayslipDetail createFromParcel(Parcel in) {
            return new UserPayslipDetail(in);
        }

        @Override
        public UserPayslipDetail[] newArray(int size) {
            return new UserPayslipDetail[size];
        }
    };
}
