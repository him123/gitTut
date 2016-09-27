package com.bigbang.superteam;

import android.content.Context;

import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.Util;

/**
 * Created by USER 3 on 04/08/2015.
 */
public class Privileges {
    public static Boolean APPROVE_LEAVE_REQUEST;
    public static Boolean TRACK_TEAMMEMBER;
    public static Boolean ENABLE_TRACKING;
    public static Boolean CLOSE_PROJECT;
    public static Boolean CLOSE_WORKITEM;
    public static Boolean REOPEN_PROJECT;
    public static Boolean REOPEN_WORKITEM;
    public static Boolean APPROVAL_REQ_FOR_NEW_PROJECT;
    public static Boolean APPROVAL_REQ_FOR_NEW_WORKITEM;
    public static Boolean MODIFY_PROJECT;
    public static Boolean MODIFY_WORKITEM;
    public static Boolean APPROVAL_REQ_FOR_WORKITEM_UPDATE;

    public static void Init(Context context) {
        int role = Integer.parseInt(Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID));
        switch (role) {
            case 3:
                APPROVE_LEAVE_REQUEST = Util.checkUserPrivilage(context, "2");
                TRACK_TEAMMEMBER = Util.checkUserPrivilage(context, "4");
                ENABLE_TRACKING = Util.checkUserPrivilage(context, "7");
                CLOSE_PROJECT = Util.checkUserPrivilage(context, "11");
                CLOSE_WORKITEM = Util.checkUserPrivilage(context, "12");
                REOPEN_PROJECT = Util.checkUserPrivilage(context, "13");
                REOPEN_WORKITEM = Util.checkUserPrivilage(context, "14");
                APPROVAL_REQ_FOR_NEW_PROJECT = Util.checkUserPrivilage(context, "15");
                APPROVAL_REQ_FOR_NEW_WORKITEM = Util.checkUserPrivilage(context, "16");
                MODIFY_PROJECT = Util.checkUserPrivilage(context, "25");
                MODIFY_WORKITEM = Util.checkUserPrivilage(context, "26");
                APPROVAL_REQ_FOR_WORKITEM_UPDATE = Util.checkUserPrivilage(context,"20");
                break;
            case 4:
                APPROVE_LEAVE_REQUEST = false;
                TRACK_TEAMMEMBER = false;
                ENABLE_TRACKING = Util.checkUserPrivilage(context, "23");
                APPROVAL_REQ_FOR_NEW_WORKITEM = Util.checkUserPrivilage(context, "19");
                MODIFY_PROJECT = Util.checkUserPrivilage(context, "27");
                MODIFY_WORKITEM = Util.checkUserPrivilage(context, "28");
                REOPEN_PROJECT = Util.checkUserPrivilage(context, "30");
                REOPEN_WORKITEM = Util.checkUserPrivilage(context, "31");
                CLOSE_PROJECT = Util.checkUserPrivilage(context, "32");
                CLOSE_WORKITEM = Util.checkUserPrivilage(context, "33");
                APPROVAL_REQ_FOR_NEW_PROJECT = true;
                APPROVAL_REQ_FOR_WORKITEM_UPDATE = Util.checkUserPrivilage(context,"20");
                break;
            default:
                APPROVE_LEAVE_REQUEST = true;
                TRACK_TEAMMEMBER = true;
                ENABLE_TRACKING = false;
                CLOSE_PROJECT = true;
                CLOSE_WORKITEM = true;
                REOPEN_PROJECT = true;
                REOPEN_WORKITEM = true;
                APPROVAL_REQ_FOR_NEW_PROJECT = false;
                APPROVAL_REQ_FOR_NEW_WORKITEM = false;
                MODIFY_PROJECT = true;
                MODIFY_WORKITEM = true;
                APPROVAL_REQ_FOR_WORKITEM_UPDATE = false;
                break;
        }
    }
}
