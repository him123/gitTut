package com.bigbang.superteam.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.bigbang.superteam.util.Util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * Created by dgohil on 6/11/15.
 */
public class BaseActivity extends Activity {

    @Override
    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);
        ButterKnife.inject(this);
    }

    protected void toast(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    protected void toast(int resId) {
        toast(this.getResources().getText(resId));
    }

    protected void startActivity(Class klass) {
        startActivity(new Intent(this, klass));
    }

    protected void startActivityWithData(Class klass, HashMap hashMap) {
        Intent intent = new Intent(this, klass);
        Iterator it = hashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            intent.putExtra((String) pair.getKey(), (String) pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
        startActivity(intent);
    }

    protected String getText(EditText eTxt) {
        return eTxt == null ? "" : eTxt.getText().toString().trim();
    }

    protected boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    protected void write(String key, String val) {
        Util.WriteSharePrefrence(this, key, val);
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(this, key);
    }

    protected void hideKeyboard() {
        // Check if no view has focus:
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
