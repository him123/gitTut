package com.bigbang.superteam.workitem;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 3 on 28/11/2015.
 */
public class SelectDaysActivity extends BaseActivity {

    @InjectView(R.id.tvTitle)
    TextView tvTitle;
    @InjectView(R.id.lvDays)
    ListView lvDays;

    ArrayList<HashMap<String,String>> listDays = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectdays);

        init();

        tvTitle.setText(getResources().getString(R.string.select_days));

        try {
            JSONArray jsonArray = new JSONArray(read(Constant.SHRED_PR.KEY_LIST));
            listDays.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.optJSONObject(i);

                HashMap<String,String> hashMap=new HashMap<>();
                hashMap.put("day", "" + jsonObject1.optString("day"));
                hashMap.put("selected", "" + jsonObject1.optString("selected"));
                hashMap.put("code", "" + jsonObject1.optString("code"));

                listDays.add(hashMap);

            }
            lvDays.setAdapter(new CustomAdapter(listDays));
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
            for (int i = 0; i < listDays.size(); i++) {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("day",""+listDays.get(i).get("day"));
                jsonObject.put("selected",""+listDays.get(i).get("selected"));
                jsonObject.put("code",""+listDays.get(i).get("code"));
                jsonArray.put(jsonObject);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        write(Constant.SHRED_PR.KEY_LIST, "" + jsonArray);
        write(Constant.SHRED_PR.KEY_DAYSLIST, "1");
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

        ArrayList<HashMap<String,String>> locallist;

        public CustomAdapter(ArrayList<HashMap<String,String>> locallist) {
            // TODO Auto-generated constructor stub
            this.locallist = locallist;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return locallist.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
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
            // TODO Auto-generated method stub
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

            final HashMap<String,String> hashMap=locallist.get(position);
            holder.name.setText("" + hashMap.get("day"));

            if (hashMap.get("selected").equals("1")) {
                holder.btnCheck.setBackgroundResource(R.drawable.chacked);
            } else {
                holder.btnCheck.setBackgroundResource(R.drawable.unchacked);
            }

            holder.relativeCheck.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (hashMap.get("selected").equals("1")) {
                        holder.btnCheck
                                .setBackgroundResource(R.drawable.unchacked);
                        locallist.get(position).put("selected","0");
                    } else {
                        holder.btnCheck
                                .setBackgroundResource(R.drawable.chacked);
                        locallist.get(position).put("selected", "1");
                    }

                }
            });


            return view;
        }
    }

}

