package com.bigbang.superteam.fragment;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.dataObjs.CompanyLeavesDAO;
import com.bigbang.superteam.login_register.CompanySetupActivity;
import com.bigbang.superteam.model.CompanyLeaves;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.DecimalDigitsInputFilter;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeavesSetupFragment extends Fragment {

    static int FROM = 1;

    @InjectView(R.id.tb_auto_leave)
    ToggleButton tb_auto_leave;
    @InjectView(R.id.edt_sick_leave)
    EditText edt_sick_leave;
    @InjectView(R.id.edt_causal_leave)
    EditText edt_causal_leave;
    @InjectView(R.id.edt_paid_leave)
    EditText edt_paid_leave;
    @InjectView(R.id.edt_optional_leave)
    EditText edt_optional_leave;
    @InjectView(R.id.sp_sick_lv)
    Spinner sp_sick_lv;
    @InjectView(R.id.sp_causal_lv)
    Spinner sp_causal_lv;
    @InjectView(R.id.sp_paid_lv)
    Spinner sp_paid_lv;
    @InjectView(R.id.sp_opt_lv)
    Spinner sp_opt_lv;
    @InjectView(R.id.ll_containter)
    LinearLayout ll_containter;
    @InjectView(R.id.btn_save)
    Button btn_save;

    boolean toggle_check = true;
    CompanyLeaves companyLeaves;
    CompanyLeavesDAO companyLeavesDAO;

    String[] holitypeArr;
    private TransparentProgressDialog pd;

    private String getText(EditText eTxt) {
        return eTxt == null ? "" : eTxt.getText().toString().trim();
    }

    public static LeavesSetupFragment newInstance(int from) {
        FROM = from;
        LeavesSetupFragment fragment = new LeavesSetupFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leave_setup, container, false);
        final Typeface mFont = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) view.getRootView();
        Util.setAppFont(mContainer, mFont);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();

        addItemsOnSpinners();

        tb_auto_leave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (Util.isOnline(getActivity())) {
                if (isChecked) {
                    toggle_check = true;
                    ll_containter.setVisibility(View.VISIBLE);
                } else {
                    toggle_check = false;
                    ll_containter.setVisibility(View.INVISIBLE);
                }
//                }else{
//                    Toast.makeText(getActivity(), Constant.network_error, Toast.LENGTH_SHORT).show();
//                }
            }
        });

        if (FROM == Constant.FROM_DASHBOARD) {

            if (companyLeavesDAO.getCompLeaves() != null) {
                setData(companyLeavesDAO.getCompLeaves());
            }
            if (Util.isOnline(getActivity())) {
                getLeaves();
            }
        }

        btn_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String arr;
                setBlank();

                if (Util.isOnline(getActivity())) {
                    if (toggle_check) {
                        arr = getJArrayForReq().toString();
                        Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_AUTO_LEAVE_UPDATE, String.valueOf(toggle_check));
                    } else {
                        arr = "";
                        Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_AUTO_LEAVE_UPDATE, String.valueOf(toggle_check));
                    }

                    if (pd != null) {
                        pd.show();
                    }
                    RestClient.getTeamWork().updateCompanyLeaves(arr,
                            Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID),
                            Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_COMPANY_ID),
                            Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_TOKEN),
                            String.valueOf(toggle_check),
                            new Callback<Response>() {
                                @Override
                                public void success(Response response, Response response2) {
                                    try {

                                        Map<String, String> map = Util.readStatus(response);
                                        boolean isSuccess = map.get("status").equals("Success");
                                        String json = Util.getString(response.getBody().in());
                                        JSONObject jObj = new JSONObject(json);

                                        String status = jObj.optString("status");
                                        if (status.equals(Constant.InvalidToken)) {
                                            TeamWorkApplication.LogOutClear(getActivity());
                                            return;
                                        }

                                        if (isSuccess) {
                                            if (FROM == Constant.FROM_DASHBOARD) {
                                                getActivity().finish();
                                                getActivity().overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
                                            } else {
                                                CompanySetupActivity.currentPosition++;
                                                CompanySetupActivity.pager.setCurrentItem(CompanySetupActivity.currentPosition);
                                            }
                                        }

                                        Toast.makeText(getActivity(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        Log.d("", "Exception: " + e);
                                        pd.dismiss();
                                    }
                                    pd.dismiss();
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    pd.dismiss();
                                }
                            });
                } else {
                    Toast.makeText(getActivity(), Constant.network_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {

        pd = new TransparentProgressDialog(getActivity(), R.drawable.progressdialog, false);

        companyLeavesDAO = new CompanyLeavesDAO(getActivity());
        holitypeArr = getResources().getStringArray(R.array.leaves_array);

        edt_sick_leave.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
        edt_causal_leave.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
        edt_paid_leave.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
        edt_optional_leave.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});

        inputValidation(edt_sick_leave);
        inputValidation(edt_causal_leave);
        inputValidation(edt_paid_leave);
        inputValidation(edt_optional_leave);

        int roleID = Integer.parseInt(read(Constant.SHRED_PR.KEY_ROLE_ID));

        switch (roleID) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                setEditableFalse();
                break;
            case 4:
                setEditableFalse();
                break;
            default:
                break;
        }
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(getActivity(), key);
    }

    private void inputValidation(final EditText edt) {
        edt.addTextChangedListener(new TextWatcher() {
            String oldText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldText = getText(edt);
                Log.e("oldText", ">>" + oldText);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = getText(edt);
                Log.e("newText", ">>" + newText);
                if (newText.contains(".")) {
                    if (newText.length() > 1) {
                        String[] temp = newText.split("\\.");
                        Log.e("temp", ">>" + temp[0]);
                        if (temp.length > 1) {
                            if (!(temp[1].equals("5") || temp[1].equals("0")) || temp[0].length() > 2) {
                                edt.setText(oldText);
                                edt.setSelection(getText(edt).length());
                                //toast(getResources().getString(R.string.mobile_not_zero));
                            }
                        }
                    } else {
                        edt.setText(oldText);
                        edt.setSelection(getText(edt).length());
                    }

                } else {
                    if (newText.length() > 2) {
                        edt.setText(oldText);
                        edt.setSelection(getText(edt).length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setEditableFalse() {
        edt_sick_leave.setEnabled(false);
        edt_causal_leave.setEnabled(false);
        edt_paid_leave.setEnabled(false);
        edt_optional_leave.setEnabled(false);

        edt_sick_leave.setFocusable(false);
        edt_causal_leave.setFocusable(false);
        edt_paid_leave.setFocusable(false);
        edt_optional_leave.setFocusable(false);

        sp_sick_lv.setEnabled(false);
        sp_causal_lv.setEnabled(false);
        sp_paid_lv.setEnabled(false);
        sp_opt_lv.setEnabled(false);

        btn_save.setVisibility(View.INVISIBLE);

        tb_auto_leave.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private JSONArray getJArrayForReq() {

        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObjectSL = new JSONObject();
            JSONObject jsonObjectOL = new JSONObject();
            JSONObject jsonObjectPL = new JSONObject();
            JSONObject jsonObjectCL = new JSONObject();

            jsonObjectSL.put("leaveType", "Sick Leave");
            jsonObjectSL.put("noOfLeaves", edt_sick_leave.getText().toString().trim());
            jsonObjectSL.put("leaveUpdateCyle", holitypeArr[sp_sick_lv.getSelectedItemPosition()].toString());

            jsonObjectOL.put("leaveType", "Optional Leave");
            jsonObjectOL.put("noOfLeaves", edt_optional_leave.getText().toString().trim());
            jsonObjectOL.put("leaveUpdateCyle", holitypeArr[sp_opt_lv.getSelectedItemPosition()].toString());

            jsonObjectPL.put("leaveType", "Paid Leave");
            jsonObjectPL.put("noOfLeaves", edt_paid_leave.getText().toString().trim());
            jsonObjectPL.put("leaveUpdateCyle", holitypeArr[sp_paid_lv.getSelectedItemPosition()].toString());

            jsonObjectCL.put("leaveType", "Casual Leave");
            jsonObjectCL.put("noOfLeaves", edt_causal_leave.getText().toString().trim());
            jsonObjectCL.put("leaveUpdateCyle", holitypeArr[sp_causal_lv.getSelectedItemPosition()].toString());


            jsonArray.put(jsonObjectSL);
            jsonArray.put(jsonObjectOL);
            jsonArray.put(jsonObjectPL);
            jsonArray.put(jsonObjectCL);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public void addItemsOnSpinners() {
        List<String> list = Arrays.asList(getResources().getStringArray(R.array.leaves_array));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_sick_lv.setAdapter(dataAdapter);
        sp_causal_lv.setAdapter(dataAdapter);
        sp_paid_lv.setAdapter(dataAdapter);
        sp_opt_lv.setAdapter(dataAdapter);
    }

    private void getLeaves() {
        RestClient.getTeamWork().getCompanyLeaves(Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_COMPANY_ID), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    String json = Util.getString(response.getBody().in());
                    JSONObject jObj = new JSONObject(json);
                    JSONObject DatajObj = jObj.getJSONObject("data");

                    String status = jObj.optString("status");
                    if (status.equals(Constant.InvalidToken)) {
                        TeamWorkApplication.LogOutClear(getActivity());
                        return;
                    }

                    boolean AutoLeaveUpdate = DatajObj.getBoolean("AutoLeaveUpdate");

                    Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_AUTO_LEAVE_UPDATE, String.valueOf(AutoLeaveUpdate));

                    if (AutoLeaveUpdate)
                        if (jObj.getString("status").equals("Success")) {

                            JSONArray compLeaveArr = jObj.getJSONObject("data").getJSONArray("CompanyLeaves");
                            if (compLeaveArr != null) {
                                companyLeavesDAO.deleteAll();
                                for (int cnt = 0; cnt < compLeaveArr.length(); cnt++) {
                                    companyLeaves = new CompanyLeaves();

                                    companyLeaves.companyId = compLeaveArr.getJSONObject(cnt).getString("companyId");
                                    companyLeaves.leaveType = compLeaveArr.getJSONObject(cnt).getString("leaveType");
                                    companyLeaves.noOfLeaves = compLeaveArr.getJSONObject(cnt).getString("noOfLeaves");
                                    companyLeaves.leaveUpdateCyle = compLeaveArr.getJSONObject(cnt).getString("leaveUpdateCyle");
                                    companyLeaves.active = String.valueOf(compLeaveArr.getJSONObject(cnt).getBoolean("active"));
                                    companyLeaves.modifiedBy = compLeaveArr.getJSONObject(cnt).getString("modifiedBy");

                                    companyLeavesDAO.save(companyLeaves);
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), jObj.getJSONObject("message").toString(), Toast.LENGTH_SHORT).show();
                        }
                    if (companyLeavesDAO.getCompLeaves() != null) {
                        setData(companyLeavesDAO.getCompLeaves());
                    }
                } catch (Exception e) {
                    Log.d("", "Exception : " + e);
                }
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    private void setData(ArrayList<CompanyLeaves> companyLeaves) {
        boolean AutoLeaveUpdate = Boolean.parseBoolean(Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_AUTO_LEAVE_UPDATE));
        if (AutoLeaveUpdate) {

            for (int i = 0; i < companyLeaves.size(); i++) {
                if (companyLeaves.get(i).leaveType.equals("Sick Leave")) {
                    edt_sick_leave.setText(companyLeaves.get(i).noOfLeaves);
                    sp_sick_lv.setSelection(Arrays.asList(holitypeArr).indexOf(companyLeaves.get(i).leaveUpdateCyle));
                } else if (companyLeaves.get(i).leaveType.equals("Optional Leave")) {
                    edt_optional_leave.setText(companyLeaves.get(i).noOfLeaves);
                    sp_opt_lv.setSelection(Arrays.asList(holitypeArr).indexOf(companyLeaves.get(i).leaveUpdateCyle));
                } else if (companyLeaves.get(i).leaveType.equals("Paid Leave")) {
                    edt_paid_leave.setText(companyLeaves.get(i).noOfLeaves);
                    sp_paid_lv.setSelection(Arrays.asList(holitypeArr).indexOf(companyLeaves.get(i).leaveUpdateCyle));
                } else if (companyLeaves.get(i).leaveType.equals("Casual Leave")) {
                    edt_causal_leave.setText(companyLeaves.get(i).noOfLeaves);
                    sp_causal_lv.setSelection(Arrays.asList(holitypeArr).indexOf(companyLeaves.get(i).leaveUpdateCyle));
                }
            }
        } else {
            tb_auto_leave.setChecked(false);
        }
    }

    private void setBlank() {
        if (edt_sick_leave.getText().toString().trim().equals("")) {
            edt_sick_leave.setText("0.0");
        }
        if (edt_causal_leave.getText().toString().trim().equals("")) {
            edt_causal_leave.setText("0.0");
        }
        if (edt_paid_leave.getText().toString().trim().equals("")) {
            edt_paid_leave.setText("0.0");
        }
        if (edt_optional_leave.getText().toString().trim().equals("")) {
            edt_optional_leave.setText("0.0");
        }
    }
}
