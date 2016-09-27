package com.bigbang.superteam.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.dataObjs.ClientVendorAddressModel;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 7 on 8/22/2015.
 */
public class ClientVendorAddressAdapter extends BaseAdapter {
    private ArrayList<ClientVendorAddressModel> data;
    private Context mContext;
    private static LayoutInflater inflater = null;

    static SQLiteHelper helper;
    public static SQLiteDatabase db = null;

    DisplayImageOptions options;
    String TAG = "ClientVendorAddressAdapter";
    ImageLoader
            imageLoader = ImageLoader.getInstance();

    private TransparentProgressDialog pd;

    public ClientVendorAddressAdapter(Context ctx, ArrayList<ClientVendorAddressModel> d) {
        mContext = ctx;
        data = d;
        /***********  Layout inflator to call external xml layout () ***********/
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(150))
                .showImageOnLoading(0).resetViewBeforeLoading(true)
                .showImageForEmptyUri(0).showImageOnFail(0).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(ctx));

        pd = new TransparentProgressDialog(ctx, R.drawable.progressdialog,false);

    }

    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int index, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.listrow_client_vendor_address, null);
            holder = new ViewHolder(view);

            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();
        holder.tvClientName.setText(data.get(index).getClientName());
        String addressData = ""+data.get(index).getAddressData();
        try{
            JSONObject jsonObject = new JSONObject(addressData);
            holder.tvAddress.setText(""+jsonObject.optString("AddressLine1")+","+"\n"+jsonObject.optString("AddressLine2")+","+"\n"+jsonObject.optString("City")+","+"\n"+jsonObject.optString("State")+","+jsonObject.optString("Country"));
        }catch (Exception e){
            e.printStackTrace();
        }


       /* String strImage = "" + data.get(index).getImageUrl();
        imageLoader.displayImage(strImage, holder.imgUser, options);*/


        holder.relativeDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Util.isOnline(mContext)) {

                        new deleteClientVendor(index).execute();

                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }

            }
        });
        return view;
    }


    public class ViewHolder {
        @InjectView(R.id.rlDelete)
        RelativeLayout relativeDelete;
        @InjectView(R.id.title)
        TextView tvClientName;
        @InjectView(R.id.desc)
        TextView tvAddress;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }

    class deleteClientVendor extends AsyncTask<Void, String, String> {


        String response;
        int index;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pd != null) {
                pd.show();
            }
        }

        public deleteClientVendor(int pos) {
            this.index = pos;

        }

        @Override
        protected String doInBackground(Void... params) {

            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);

            params1.add(new BasicNameValuePair("AddressMasterID", ""+data.get(index).getAddresId()));
            params1.add(new BasicNameValuePair("MemberID", ""+data.get(index).getUserId()));
            params1.add(new BasicNameValuePair("CompanyID",""+data.get(index).getCompanyID()));
            params1.add(new BasicNameValuePair("ClientVendorID", ""+data.get(index).getClientVendorId()));
            params1.add(new BasicNameValuePair("Type", "permanent"));
            Log.e("params1", ">>" + params1);
            response = Util.makeServiceCall(Constant.URL
                    + "removeClientVendorLocation", 2, params1, mContext);
            Log.e("response", "<<" + response);

            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            /*String date = (String) spinnerYear.getSelectedItem() + "_" + (spinnerMonth.getSelectedItemPosition() + 1);
            Util.WriteFile(getCacheDir() + "/AttendanceHistory", userId + "_" + date + ".txt", result);
            reload(result);*/
            try {

                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                if (pd != null) {
                    pd.dismiss();
                }

                if (status.equals("Success")) {
                    removeFromArrayList(index);
                } else {
                    Toast.makeText(mContext, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (pd != null) {
                    pd.dismiss();
                }
            }


        }
    }



    private void removeFromArrayList(int index) {
        data.remove(index);
        notifyDataSetChanged();
    }


}
