package com.bigbang.superteam.rest;

import com.bigbang.superteam.expenses.ExpenseType;
import com.bigbang.superteam.model.Customer;
import com.bigbang.superteam.model.InvitedUser;
import com.bigbang.superteam.model.User;
import com.bigbang.superteam.payroll.UserPayslipDetail;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by dgohil on 6/17/15.
 */
public interface CommonService {

    @GET("/viewCustomerUpdates")
    void viewCustomerUpdates(@Query("TransactionID") String TransactionID,
                             @Query("MemberID") String MemberID,
                             @Query("UserID") String UserID,
                             @Query("TokenID") String TokenID,
                             Callback<Response> rc);

    @GET("/getCustomers")
    void getCustomers(@Query("CompanyID") String companyId,
                      @Query("UserID") String UserID,
                      @Query("TokenID") String TokenID,
                      Callback<ArrayList<Customer>> callback);


    @GET("/getVendors")
    void getVendors(@Query("CompanyID") String companyId,
                    @Query("UserID") String UserID,
                    @Query("TokenID") String TokenID,
                    Callback<ArrayList<Customer>> callback);

    @GET("/getCompany")
    void getCompany(@Query("CompanyID") String companyId,
                    @Query("UserID") String UserID,
                    @Query("TokenID") String TokenID,
                    Callback<Response> rc);

    @GET("/getCustomerVendorDetails")
    void getCustomerVendorDetails(@Query("CompanyID") String companyId,
                                  @Query("CustomerID") String CustomerID,
                                  Callback<Response> rc);

    @GET("/getUser")
    void getUser(@Query("UserID") String UserID,
                 @Query("CompanyID") String companyId,
                 @Query("Application") String Application,
                 Callback<Response> rc);

    @POST("/deleteCustomerVendor")
    void deleteCustomerVendor(@Query("CompanyID") String companyId,
                              @Query("ID") String ID,
                              @Query("UserID") String UserID,
                              @Query("TokenID") String TokenID,
                              Callback<Response> rc);

    @GET("/invitedMembers")
    void invitedMembers(@Query("CompanyID") String companyId,
                        @Query("UserID") String UserID,
                        @Query("TokenID") String TokenID,
                        @Query("Application") String Application,
                        Callback<List<InvitedUser>> callback);

    @GET("/getCompanyUsersList")
    void getCompanyUsersList(@Query("CompanyID") String companyId,
                             @Query("Application") String application,
                             @Query("UserID") String UserID,
                             @Query("TokenID") String TokenID,
                             Callback<List<User>> callback);

    @GET("/getReportingMembers")
    void getReportingMembers(@Query("UserID") String UserID,
                             @Query("Application") String application,
                             Callback<List<User>> callback);


    @GET("/getUserPrivileges")
    void getUserPrivileges(@Query("CompanyID") String CompanyID,
                           @Query("RoleID") String RoleID,
                           @Query("UserID") String UserID,
                           @Query("TokenID") String TokenID,
                           Callback<Response> callback);

    @GET("/getPrivileges")
    void getPrivileges(Callback<Response> callback);

    @GET("/getCompanyPrivileges")
    void getCompanyPrivileges(@Query("CompanyID") String CompanyID,
                              @Query("UserID") String UserID,
                              @Query("TokenID") String TokenID,
                              Callback<Response> callback);

    @POST("/addUser")
    void addUser(@Query("Application") String application,
                 @Query("EmailID") String emailID,
                 @Query("MobileNo") String mobileNo,
                 @Query("Name") String name,
                 @Query("RoleID") Integer roleID,
                 @Query("CompanyID") String companyID,
                 @Query("CreatorID") String id,
                 @Query("ManagerID") String ManagerID,
                 @Query("InviteID") String InviteID,
                 @Query("UserID") String UserID,
                 @Query("TokenID") String TokenID, Callback<Response> callback);

    @POST("/cancelInvite")
    void cancelInvite(@Query("InviteID") String ManagerID,
                      @Query("UserID") String UserID,
                      @Query("TokenID") String TokenID, Callback<Response> callback);

    @POST("/loginUser")
    void login(@Query("MobileNo") String id,
               @Query("Password") String password,
               @Query("DeviceID") String deviceId,
               @Query("Application") String application,
               @Query("WebLogin") String webLogin,
               @Query("GcmID") String GcmID,
               Callback<User> user);

    @POST("/changeMobileNo")
    void changeMobileNo(@Query("UserID") String id,
                        @Query("OldMobileNo") String oldMobile,
                        @Query("NewMobileNo") String newMobile,
                        @Query("TokenID") String TokenID,
                        Callback<Response> rc);

    @GET("/cancelChangeMobile")
    void cancelChangeMobile(@Query("MobileNo") String mobile,
                            @Query("UserID") String id,
                            @Query("TokenID") String TokenID,
                            Callback<Response> rc);

    @POST("/verifyChangeMobileNo")
    void verifyChangeMobileNo(@Query("UserID") String id,
                              @Query("MobileNo") String mobile,
                              @Query("Otp") String otp,
                              @Query("CompanyID") String CompanyID,
                              @Query("Application") String application,
                              @Query("TokenID") String TokenID,
                              Callback<Response> rc);

    @POST("/changePassword")
    void changePassword(@Query("UserID") String id,
                        @Query("OldPassword") String oldPassword,
                        @Query("NewPassword") String newPassword,
                        @Query("TokenID") String TokenID,
                        Callback<Response> rc);

    @GET("/resetPassword")
    void resetPassword(@Query("MobileNo") String id,
                       @Query("UserID") String UserID,
                       @Query("TokenID") String TokenID,
                       Callback<Response> rc);

    @POST("/resetPasswordForMember")
    void resetPasswordForMember(@Query("OwnerID") String OwnerID,
                                @Query("MemberID") String MemberID,
                                @Query("CompanyID") String CompanyID,
                                @Query("Password") String Password,
                                @Query("UserID") String UserID,
                                @Query("TokenID") String TokenID,
                                Callback<Response> rc);

    @POST("/resign")
    void resign(@Query("UserID") String UserID,
                @Query("CompanyID") String CompanyID,
                @Query("Description") String Reason,
                @Query("Application") String Application,
                @Query("TokenID") String TokenID,
                Callback<Response> rc);

    @POST("/updateUserRole")
    void updateUserRole(@Query("AdminID") String AdminID,
                        @Query("MemberID") String MemberID,
                        @Query("CompanyID") String CompanyID,
                        @Query("Application") String Application,
                        @Query("NewManagerID") String ManagerID,
                        @Query("OldRoleID") String OldRoleID,
                        @Query("NewRoleID") String NewRoleID,
                        @Query("UserID") String UserID,
                        @Query("TokenID") String TokenID,
                        Callback<Response> rc);

    @POST("/removeUser")
    void removeUser(@Query("UserID") String OwnerID,
                    @Query("MemberID") String UserID,
                    @Query("CompanyID") String CompanyID,
                    @Query("Application") String Application,
                    @Query("TokenID") String TokenID,
                    Callback<Response> rc);

    @POST("/updateUser")
    void updateUser(@Query("UserID") String id,
                    @Query("CompanyID") String CompanyID,
                    @Query("Email") String Email,
                    @Query("FirstName") String Fname,
                    @Query("LastName") String Lname,
                    @Query("PermAdd") String PermAdd,
                    @Query("TokenID") String TokenID,
                    Callback<Response> rc);

    @POST("/deleteCompany")
    void deleteCompany(@Query("UserID") String id,
                       @Query("CompanyID") String companyID,
                       @Query("TokenID") String TokenID,
                       Callback<Response> rc);

    @POST("/updateCompany")
    void updateCompany(@Query("CompanyDetails") String CompanyDetails,
                       @Query("AddressDetails") String AddressDetails,
                       @Query("UserID") String id,
                       @Query("TokenID") String TokenID,
                       Callback<Response> rc);

    @POST("/deletePicture")
    void deletePicture(@Query("UserID") String id,
                       @Query("CompanyID") String companyID,
                       @Query("TokenID") String TokenID,
                       Callback<Response> rc);

    @POST("/deleteLogo")
    void deleteLogo(@Query("UserID") String id,
                    @Query("CompanyID") String companyID,
                    @Query("TokenID") String TokenID,
                    @Query("Type") String Type,
                    Callback<Response> rc);

    @GET("/getInvites")
    void getInvites(@Query("MobileNo") String mobile,
                    @Query("UserID") String UserID,
                    @Query("TokenID") String TokenID,
                    Callback<Response> rc);

    @POST("/acceptCompanyInvite")
    void acceptCompanyInvite(@Query("Description") String Description,
                             @Query("Invite") String invite,
                             @Query("Application") String application,
                             @Query("CompanyID") String companyID,
                             @Query("RoleID") String roleID,
                             @Query("ManagerID") String ManagerID,
                             @Query("UserID") String UserID,
                             @Query("TokenID") String TokenID,
                             Callback<Response> rc);

    @POST("/declineCompanyInvite")
    void declineCompanyInvite(@Query("Invite") String invite,
                              @Query("Application") String application,
                              @Query("CompanyID") String companyID,
                              @Query("RoleID") String roleID,
                              @Query("UserID") String UserID,
                              @Query("TokenID") String TokenID,
                              Callback<Response> rc);

    @POST("/logout")
    void logout(@Query("UserID") String id,
                @Query("TokenID") String TokenID,
                @Query("Application") String Application,
                Callback<Response> rc);

    @GET("/getManagerHierarchy")
    void getManagerHierarchy(@Query("Application") String Application,
                             @Query("UserID") String UserID,
                             @Query("TokenID") String TokenID,
                             Callback<Response> rc);

    @POST("/changeRegDevice")
    void changeRegDevice(@Query("MobileNo") String Mobile,
                         @Query("Application") String Application,
                         @Query("DeviceDescription") String DeviceDescription,
                         @Query("DeviceID") String DeviceID,
                         Callback<Response> rc);

    @GET("/getDefaultHolidays")
    void getDefaultHolidays(Callback<Response> rc);

    @POST("/updateCompanyHolidays")
    void updateCompanyHolidays(@Query("JSON") String JSON,
                               @Query("UserID") String UserID,
                               @Query("CompanyID") String CompanyID,
                               @Query("TokenID") String TokenID, //no need in GET
                               Callback<Response> rc);

    @POST("/updateCompanyLeaves")
    void updateCompanyLeaves(@Query("CompanyLeaves") String CompanyLeaves,
                             @Query("UserID") String UserID,
                             @Query("CompanyID") String CompanyID,
                             @Query("TokenID") String TokenID, //no need in GET
                             @Query("AutoLeaveUpdate") String AutoLeaveUpdate,
                             Callback<Response> rc);

    @GET("/getCompanyLeaves")
    void getCompanyLeaves(@Query("CompanyID") String CompanyID,
                          Callback<Response> rc);

    @GET("/getCompanyPayrolls")
    void getCompanyPayrolls(Callback<Response> rc);

    @GET("/getUserPayrollDetails")
    void getUserPayrollDetails(@Query("getPayrollJson") String JSON,
                               Callback<Response> rc);

    @POST("/addUpdateCompanyPayroll")
    void addUpdateCompanyPayroll(@Query("addCompanyPayrolljson") String JSON,
                                 @Query("UserID") String UserID,
                                 @Query("TokenID") String TokenID, //no need in GET
                                 Callback<Response> rc);

    @POST("/addUpdateCompanyWorkingPolicy")
    void addUpdateCompanyWorkingPolicy(@Query("addCompanyWorkingPolicyjson") String JSON,
                                       @Query("UserID") String UserID,
                                       @Query("TokenID") String TokenID, //no need in GET
                                       Callback<Response> rc);

    @POST("/addUpdateUserPayroll")
    void addUpdateUserPayroll(@Query("addUserPayrolljson") String JSON,
                              @Query("UserID") String UserID,
                              @Query("TokenID") String TokenID, //no need in GET
                              Callback<Response> rc);

    @POST("/addUpdateUserPayslip")
    void addUpdateUserPayslip(@Query("addUserPayslipjson") String JSON,
                              @Query("paySlipUsersJson") String UserJSON,
                              @Query("UserID") String UserID,
                              @Query("TokenID") String TokenID, //no need in GET
                              Callback<Response> rc);

    @POST("/publishPayslip")
    void publishPayslip(@Query("UserID") String userID,
                        @Query("CompanyID") String CompanyID,
                        @Query("UserIDList") String UserIDList,
                        @Query("Month") String Month,
                        @Query("Year") String Year,
                        @Query("TokenID") String TokenID, //no need in GET
                        Callback<Response> rc);

    @GET("/getUserPayslip")
    void getUserPayslip(@Query("UserID") String userID,
                        @Query("CompanyID") String companyID,
                        @Query("Month") String month,
                        @Query("Year") String year,
                        @Query("isPublished") String isPublished,
                        Callback<UserPayslipDetail> rc);

    @POST("/updateUserPayslip")
    void updateUserPayslip(@Query("paySlipUsersJson") String JSON,
                           @Query("UserID") String UserID,
                           @Query("TokenID") String TokenID, //no need in GET
                           Callback<UserPayslipDetail> rc);

    @GET("/getPayslipGeneratedUsers")
    void getPayslipGeneratedUsers(@Query("UserID") String UserID,
                                  @Query("CompanyID") String CompanyID,
                                  @Query("Month") String month,
                                  @Query("Year") String year,
                                  Callback<Response> callback);


    @POST("/emailPayslip")
    void emailPayslip(@Query("UserID") String UserID,
                      @Query("CompanyID") String CompanyID,
                      @Query("Month") String Month,
                      @Query("Year") String Year,
                      @Query("EmailID") String EmailID,
                      @Query("TokenID") String TokenID, //no need in GET
                      Callback<Response> rc);

    @GET("/getCompanyInfo")
    void getCompanyInfo(@Query("UserID") String UserID,
                        @Query("CompanyID") String CompanyID,
                        @Query("TokenID") String TokenID, //no need in GET
                        Callback<Response> rc);

    @POST("/addExpense")
    void addExpense(@Query("ExpenseJSON") String JSON,
                    @Query("Application") String application,
                    @Query("CompanyID") String CompanyID,
                    @Query("UserID") String UserID,
                    @Query("TokenID") String TokenID,
                    Callback<Response> rc);

    @GET("/getExpenseType")
    void getExpenseType(Callback<String[]> rc);

    @GET("/getUserExpenses")
    void getUserExpenses(@Query("UserID") String UserID,
                         @Query("CompanyID") String CompanyID,
                         @Query("Application") String application,
                         @Query("Month") String month,
                         @Query("Year") String year,
                         Callback<ArrayList<ExpenseType>> expenseList);

    @GET("/getMemberExpenses")
    void getMemberExpenses(@Query("UserID") String UserID,
                           @Query("CompanyID") String CompanyID,
                           @Query("Application") String application,
                           @Query("Month") String month,
                           @Query("Year") String year,
                           Callback<ArrayList<ExpenseType>> expenseList);

    @POST("/withdrawExpense")
    void withdrawExpense(@Query("UserID") String UserID,
                         @Query("ExpenseID") String ExpenseID,
                         @Query("Application") String application,
                         @Query("TokenID") String TokenID,
                         Callback<Response> rc);

    @POST("/updateExpense")
    void updateExpense(@Query("ExpenseJSON") String JSON,
                    @Query("Application") String application,
                    @Query("CompanyID") String CompanyID,
                    @Query("UserID") String UserID,
                    @Query("TokenID") String TokenID,
                    Callback<Response> rc);

    @POST("/approveRejectExpense")
    void approveRejectExpense(@Query("ExpenseJSON") String JSON,
                       @Query("Application") String application,
                       @Query("TransactionID") String TransactionID,
                       @Query("UserID") String UserID,
                       @Query("TokenID") String TokenID,
                       @Query("isApproved") String isApproved,
                       Callback<Response> rc);

    @GET("/getExpenseForTransaction")
    void getExpenseForTransaction(@Query("Application") String application,
                         @Query("TransactionID") String TransactionID,
                         Callback<ExpenseType> expenseType);

    @GET("/generateExpenseReport")
    void generateExpenseReport(@Query("UserID") String UserID,
                           @Query("CompanyID") String CompanyID,
                           @Query("Application") String application,
                           @Query("EmailID") String EmailID,
                           @Query("Month") String month,
                           @Query("Year") String year,
                           Callback<Response> cl);
}

