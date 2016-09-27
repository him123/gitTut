package com.bigbang.superteam.Application;

import java.util.HashMap;

/**
 * Created by USER 3 on 28/05/2015.
 */
public interface OnTaskCompleted {
    void onTaskCompleted(HashMap<String, String> toSendHashMap, HashMap<String, String> localHshMap, String resp);
}
