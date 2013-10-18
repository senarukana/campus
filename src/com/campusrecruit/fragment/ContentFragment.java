package com.campusrecruit.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pcncad.campusRecruit.R;
/**
 * 
 * @author <a href="mailto:kris@krislq.com">Kris.lee</a>
 * @since Mar 12, 2013
 * @version 1.0.0
 */
@SuppressLint("ValidFragment")
public class ContentFragment extends BaseFragment {
    String text = null;

    public ContentFragment() {
    }

    public ContentFragment(String text) {
        Log.e("Krislq", text);
        this.text = text;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.e("Krislq", "onCreate:"+text);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("Krislq", "onCreateView:"+ text);
        //inflater the layout 
        View view = inflater.inflate(R.layout.fragment_text, null);
       
        TextView textView =(TextView)view.findViewById(R.id.textView);
        if(!TextUtils.isEmpty(text)) {
            textView.setText(text);
        }
        
        return view;
    }
    
  
	public String getText(){
        return text;
    }

    @Override
    public void onDestroy() {
        Log.e("Krislq", "onDestroy:"+ text);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.e("Krislq", "onDetach:"+ text);
        super.onDetach();
    }

    @Override
    public void onPause() {
        Log.e("Krislq", "onPause:"+ text);
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.e("Krislq", "onResume:"+ text);
        super.onResume();
    }

    @Override
    public void onStart() {
        Log.e("Krislq", "onStart:"+ text);
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.e("Krislq", "onStop:"+ text);
        super.onStop();
    }
    
}
