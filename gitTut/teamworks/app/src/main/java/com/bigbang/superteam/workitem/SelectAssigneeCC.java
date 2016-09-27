package com.bigbang.superteam.workitem;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.dataObjs.User;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SelectAssigneeCC extends BaseActivity {

    @InjectView(R.id.tvTitle)
    TextView tvTitle;
    @InjectView(R.id.lvAssigneeCC)
    ListView lvAssigneeCC;

    ArrayList<User> listUsers = new ArrayList<>();
    String title,key_assignee_cc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_assignee_cc);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            title=extras.getString("title");
            key_assignee_cc=extras.getString("key_assignee_cc");
        }

        init();

        tvTitle.setText(""+title);

        try {
            JSONArray jsonArray = new JSONArray(read(Constant.SHRED_PR.KEY_LIST));
            listUsers.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.optJSONObject(i);

                User user = new User();
                user.setFirstName("" + jsonObject1.optString(User.FIRST_NAME));
                user.setLastName("" + jsonObject1.optString(User.LAST_NAME));
                user.setUser_Image("" + jsonObject1.optString(User.USER_IMAGE));
                user.setUser_Id(jsonObject1.optInt(User.USER_ID));
                user.setSelected(jsonObject1.optBoolean(User.SELECTED));
                user.setDisabled(jsonObject1.optBoolean(User.DISABLED));
                listUsers.add(user);

            }
            lvAssigneeCC.setAdapter(new CustomAdapter(listUsers));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
    }


    @OnClick(R.id.rlBack)
    @SuppressWarnings("unused")
    public void Back(View view) {

        hideKeyboard();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
    }

    @OnClick(R.id.rlSave)
    @SuppressWarnings("unused")
    public void Save(View view) {

        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < listUsers.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(User.FIRST_NAME, "" + listUsers.get(i).getFirstName());
                jsonObject.put(User.LAST_NAME, "" + listUsers.get(i).getLastName());
                jsonObject.put(User.USER_ID, "" + listUsers.get(i).getUser_Id());
                jsonObject.put(User.USER_IMAGE, "" + listUsers.get(i).getUser_Image());
                jsonObject.put(User.SELECTED, "" + listUsers.get(i).isSelected());
                jsonObject.put(User.DISABLED, "" + listUsers.get(i).isDisabled());
                jsonArray.put(jsonObject);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        write(Constant.SHRED_PR.KEY_LIST, "" + jsonArray);
        write(Constant.SHRED_PR.KEY_ASSIGNEE_CC, ""+key_assignee_cc);
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);

    }

    private void init() {
        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();

        Util.setAppFont(mContainer, mFont);
    }

    class ViewHolder {
        TextView name;
        RelativeLayout relativeCheck;
        ImageButton btnCheck;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }

    class CustomAdapter extends BaseAdapter {

        ArrayList<User> locallist;

        public CustomAdapter(ArrayList<User> locallist) {
            this.locallist = locallist;
        }

        @Override
        public int getCount() {
            return locallist.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            final ViewHolder holder;
            View view = convertView;

            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.listrow_assignee_cc,
                        parent, false);
                holder = new ViewHolder(view);
                assert view != null;
                holder.name = (TextView) view.findViewById(R.id.name);
                holder.relativeCheck = (RelativeLayout) view
                        .findViewById(R.id.relativeCheck);
                holder.btnCheck = (ImageButton) view.findViewById(R.id.check);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            final User user = locallist.get(position);
            holder.name.setText("" + user.getFirstName() + " " + user.getLastName());

            if (user.isSelected()) {
                holder.btnCheck.setBackgroundResource(R.drawable.chacked);
            } else {
                holder.btnCheck.setBackgroundResource(R.drawable.unchacked);
            }

            Log.e("User Enable:",position+">>"+user.isDisabled());
            if(user.isDisabled()){
                holder.relativeCheck.setEnabled(false);
                holder.name.setTextColor(getResources().getColor(R.color.gray));
            }else{
                holder.relativeCheck.setEnabled(true);
                holder.name.setTextColor(getResources().getColor(R.color.black));
            }

            holder.relativeCheck.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (user.isSelected()) {
                        holder.btnCheck
                                .setBackgroundResource(R.drawable.unchacked);
                        user.setSelected(false);
                        locallist.get(position).setSelected(false);
                    } else {
                        holder.btnCheck
                                .setBackgroundResource(R.drawable.chacked);
                        locallist.get(position).setSelected(true);
                    }

                }
            });


            return view;
        }
    }

}
