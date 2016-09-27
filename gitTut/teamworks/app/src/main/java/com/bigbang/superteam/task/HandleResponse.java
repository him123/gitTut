package com.bigbang.superteam.task;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.bigbang.superteam.WorkItem_GCM;
import com.bigbang.superteam.fragment.ActiveTasksFragmentBuild;
import com.bigbang.superteam.model.Address;
import com.bigbang.superteam.model.User;
import com.bigbang.superteam.task.database.ExpenceDAO;
import com.bigbang.superteam.task.database.TaskAttachmentDAO;
import com.bigbang.superteam.task.database.TaskChatDAO;
import com.bigbang.superteam.task.database.TaskDAO;
import com.bigbang.superteam.task.database.TaskMemberDAO;
import com.bigbang.superteam.task.model.Expense;
import com.bigbang.superteam.task.model.TaskChat;
import com.bigbang.superteam.task.model.TaskMember;
import com.bigbang.superteam.task.model.TaskModel;
import com.bigbang.superteam.util.Constant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by User on 9/15/2016.
 */
public class HandleResponse {

    public static void handleAddTaskResponse(String response, Context context, String localId) {
        TaskDAO taskDAO = new TaskDAO(context);
        TaskMemberDAO taskMemberDAO = new TaskMemberDAO(context);
        TaskAttachmentDAO taskMemberDAO1 = new TaskAttachmentDAO(context);
        TaskChatDAO taskChatDAO = new TaskChatDAO(context);
        ExpenceDAO expenceDAO = new ExpenceDAO(context);

        TaskModel taskModel = null;
        int taskId = 0;

        try {
            JSONObject mainObj = new JSONObject(response);
            JSONObject json = new JSONObject(mainObj.getString("data"));

            //Update data in local
            taskModel = new TaskModel();

            taskModel.id = Integer.parseInt(localId);
            taskModel.taskID = json.optInt("taskID");
            taskModel.companyID = json.optInt("companyID");
            taskModel.name = json.optString("name");
            taskModel.description = json.optString("description");
            taskModel.taskType = json.optString("taskType");
            taskModel.address_id = json.getJSONObject("address").optInt("AddressID");
            taskModel.addressStr = json.getJSONObject("address").optString("AddressLine1") + ", " +
                    json.getJSONObject("address").optString("AddressLine2") + ", " +
                    json.getJSONObject("address").optString("City") + ", " +
                    json.getJSONObject("address").optString("State") + ", " +
                    json.getJSONObject("address").optString("Country");

            taskModel.invoiceAmount = json.optDouble("invoiceAmount");
            taskModel.invoiceDate = json.optString("invoiceDate");
            taskModel.outstandingAmount = json.optDouble("outstandingAmount");
            taskModel.pastHistory = json.optString("pastHistory");
            taskModel.customerType = json.optString("customerType");
            taskModel.advancePaid = json.optDouble("advancePaid");
            taskModel.vendorPreference = json.optString("vendorPreference");

            //Address
            Address address = new Address();
            address.setAddressID(json.getJSONObject("address").optInt("AddressID"));
            address.setCountry(json.getJSONObject("address").optString("Country"));
            address.setState(json.getJSONObject("address").optString("State"));
            address.setCity(json.getJSONObject("address").optString("City"));
            address.setAddressLine1(json.getJSONObject("address").optString("AddressLine1"));
            address.setAddressLine2(json.getJSONObject("address").optString("AddressLine1"));
            address.setPincode(json.getJSONObject("address").optString("Pincode"));
            address.setLattitude(json.getJSONObject("address").optString("Lattitude"));
            address.setLongitude(json.getJSONObject("address").optString("Longitude"));

            taskModel.address = address;
            taskModel.priority = json.optString("priority");
            taskModel.startTime = json.optString("startTime");
            taskModel.endTime = json.optString("endTime");
            taskModel.estimatedTime = json.optString("estimatedTime");

            //Task Member Json Object
            taskModel.taskRight = json.optString("taskRight");

            //Created_by JSON object
            taskModel.createdByName = json.getJSONObject("createdBy").optString("firstName") + " " +
                    json.getJSONObject("createdBy").optString("lastName");

            taskModel.status = json.optString("status");
            taskModel.active = json.getBoolean("active");
            taskModel.budget = json.optDouble("budget");
            taskModel.daycodes = json.optString("daycodes");
            taskModel.frequency = json.optString("frequency");

            //Approved_by JSON object
            User user2 = new User();
            user2.setUserID(json.getJSONObject("approvedBy").optInt("userId"));
            taskModel.approvedBy = user2;

            TaskMember taskMember;
            //Assigned_to list should be in task member table
            JSONArray arrAssignedUsers = json.getJSONArray("assignedToList");
            if (arrAssignedUsers.length() != 0)
                for (int j = 0; j < arrAssignedUsers.length(); j++) {
                    taskMember = new TaskMember();

                    taskMember.TaskID = json.optInt("taskID");
                    taskMember.userID = arrAssignedUsers.getJSONObject(j).optInt("userId");
                    taskMember.memberType = Constant.MemberTypeAssigned;
                    taskMember.userName = arrAssignedUsers.getJSONObject(j).optString("firstName");

                    taskMemberDAO.save(taskMember);
                }

            //CC to list should be in task member table
            JSONArray arrCCUsers = json.getJSONArray("ccList");
            if (arrCCUsers.length() != 0)
                for (int j = 0; j < arrCCUsers.length(); j++) {
                    taskMember = new TaskMember();

                    taskMember.TaskID = json.optInt("taskID");
                    taskMember.userID = arrCCUsers.getJSONObject(j).optInt("userId");
                    taskMember.memberType = Constant.MemberTypeCC;
                    taskMember.userName = arrCCUsers.getJSONObject(j).optString("firstName") + " " + arrCCUsers.getJSONObject(j).optString("lastName");

                    taskMemberDAO.save(taskMember);
                }

            //Attachment
//          JSONArray jArray = jsonArray.getJSONObject(i).getJSONArray("attachments");
//          if (jArray.length() != 0) {
//                for (int k = 0; k < jArray.length(); k++) {
////                                            arrAttachment.add(jArray.get(k).toString());
//
//                                            Attachment attachment= new Attachment();
//                                            attachment.
//                                        }
//                                    }

//                                    taskModel.attachments = arrAttachment;

            TaskChat taskChat;

            //CHAT list should be in task member table
            JSONArray arrChat = json.getJSONArray("taskChats");
            if (arrChat.length() != 0)
                for (int j = 0; j < arrChat.length(); j++) {
                    taskChat = new TaskChat();

                    taskChat.taskID = arrChat.getJSONObject(j).optInt("taskID");
                    taskChat.TaskEditID = arrChat.getJSONObject(j).optInt("TaskEditID");
                    taskChat.chatType = arrChat.getJSONObject(j).optString("chatType");
                    taskChat.chatStatus = arrChat.getJSONObject(j).optString("chatStatus");

                    taskChat.dataType = arrChat.getJSONObject(j).optString("dataType");
                    taskChat.transactionID = arrChat.getJSONObject(j).optString("transactionID");
                    taskChat.createdOn = arrChat.getJSONObject(j).optString("createdOn");
                    taskChat.createdById = arrChat.getJSONObject(j).getJSONObject("createdBy").optInt("userId");
                    taskChat.createdByName = "";//jsonArray.getJSONObject(i).optString("createdByName");
                    taskChat.approvedById = arrChat.getJSONObject(j).getJSONObject("approvedBy").optInt("userId");
                    taskChat.approvedByName = "";// jsonArray.getJSONObject(i).optInt("approvedByName");
                    taskChat.lastModifiedBy = arrChat.getJSONObject(j).optInt("lastModifiedBy");

                    if (taskChat.dataType.equals("Text")) {
                        taskChat.message = arrChat.getJSONObject(j).optString("message");
                        taskChat.attachmentPath = "";
                    } else {
                        taskChat.message = "";
                        taskChat.attachmentPath = arrChat.getJSONObject(j).optString("attachmentPath");
                    }


                    Expense expense = new Expense();

                    JSONObject chatExpJobj = arrChat.getJSONObject(j).getJSONObject("expense");
                    expense.expenseId = chatExpJobj.optInt("expenseId");
                    expense.taskID = chatExpJobj.optInt("taskID");
                    expense.taskChatID = chatExpJobj.optInt("taskChatID");
                    expense.expenseAmount = chatExpJobj.optDouble("expenseAmount");
                    expense.lastModifiedBy = chatExpJobj.optInt("lastModifiedBy");

//                                            taskChat.expense = expense;

                    expenceDAO.save(expense);

                    taskChatDAO.save(taskChat);
                }


            //Customer/Vendor list should be in task member table
            JSONArray arrCustVend = json.getJSONArray("taskCustVend");
            if (arrCustVend.length() != 0)
                for (int j = 0; j < arrCustVend.length(); j++) {
                    taskMember = new TaskMember();

                    taskMember.TaskID = arrCustVend.getJSONObject(j).optInt("taskID");
                    taskMember.userID = arrCustVend.getJSONObject(j).optInt("custVendorID");
                    taskMember.userName = arrCustVend.getJSONObject(j).optString("name");
                    taskMember.memberType = arrCustVend.getJSONObject(j).optString("custVendType");
                    taskMember.active = arrCustVend.getJSONObject(j).optBoolean("active");
                    taskMember.contact_num = arrCustVend.getJSONObject(j).optString("contact");

                    taskMemberDAO.save(taskMember);
                }

            taskDAO.update(taskModel);

            if (ActiveTasksFragmentBuild.Active) {
                Intent intent = new Intent("workitem_created");
                intent.putExtra("message", "data");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handleChatAddResponse(String response, Context context, String localId) {
        TaskChatDAO taskChatDAO = new TaskChatDAO(context);
        TaskChat taskChat = new TaskChat();

        try {
            JSONObject mainChatObj = new JSONObject(response);
            JSONObject jChatObj = new JSONObject(mainChatObj.getString("data"));

            taskChat.id = Integer.parseInt(localId);
            taskChat.taskID = jChatObj.optInt("taskID");
            taskChat.TaskEditID = jChatObj.optInt("TaskEditID");
            taskChat.chatType = jChatObj.optString("chatType");
            taskChat.chatStatus = jChatObj.optString("chatStatus");

            taskChat.dataType = jChatObj.optString("dataType");
            taskChat.transactionID = jChatObj.optString("transactionID");
            taskChat.createdOn = jChatObj.optString("createdOn");
            taskChat.createdById = jChatObj.getJSONObject("createdBy").optInt("userId");
            taskChat.createdByName = "";//jsonArray.getJSONObject(i).optString("createdByName");
            taskChat.approvedById = jChatObj.getJSONObject("approvedBy").optInt("userId");
            taskChat.approvedByName = "";// jsonArray.getJSONObject(i).optInt("approvedByName");
            taskChat.lastModifiedBy = jChatObj.optInt("lastModifiedBy");

            if (taskChat.dataType.equals("Text")) {
                taskChat.message = jChatObj.optString("message");
                taskChat.attachmentPath = "";
            } else {
                taskChat.message = "";
                taskChat.attachmentPath = jChatObj.optString("attachmentPath");
            }
            taskChatDAO.update(taskChat);

            Intent i = new Intent("android.intent.action.MAIN").putExtra("some_msg", "I will be sent!");
            context.sendBroadcast(i);

        } catch (Exception e) {
            e.printStackTrace();
        }


//        Expense expense = new Expense();
//
//        JSONObject chatExpJobj = jChatObj.getJSONObject("expense");
//        expense.expenseId = chatExpJobj.optInt("expenseId");
//        expense.taskID = chatExpJobj.optInt("taskID");
//        expense.taskChatID = chatExpJobj.optInt("taskChatID");
//        expense.expenseAmount = chatExpJobj.optDouble("expenseAmount");
//        expense.lastModifiedBy = chatExpJobj.optInt("lastModifiedBy");
//
////                                            taskChat.expense = expense;
//
//        expenceDAO.save(expense);


    }
}
