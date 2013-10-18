package com.campusrecruit.activity;


import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.BBSReply;
import com.campusrecruit.bean.BBSTopic;
import com.campusrecruit.bean.Result;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.widget.LinkView;
import com.pcncad.campusRecruit.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;


public class TopicPubActivity extends BaseActivity{
	
//	private ImageView vBack;
	private EditText eTitle;
	private EditText eContent;
//	private Button bPublish;
    private ProgressDialog mProgress;
	
    private int sectionID;
    private int companyID;
	private String title;
	private String content;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("ac","topicPubAc");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topic_pub);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("新建主题");
		sectionID = getIntent().getIntExtra("sectionID",0);
		companyID = getIntent().getIntExtra("companyID", 0);
		this.initView();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.menu_finish, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.menu_finish_id:
			save();
			return true;
		default:
			return false;
		}
	}
	
    //初始化视图控件
    private void initView()
    {
    	Log.i("activity","initTopicPub");
    	
//    	vBack = (ImageView)findViewById(R.id.topic_pub_back);
//    	bPublish = (Button)findViewById(R.id.topic_pub_publish);
    	eTitle = (EditText)findViewById(R.id.topic_pub_title);
    	eContent = (EditText)findViewById(R.id.topic_pub_content);
    	
//    	vBack.setOnClickListener(UIHelper.finish(this));
//    	bPublish.setOnClickListener(publishClickListener);    	
    	
    }
	
    private void save() {
    	title = eTitle.getText().toString();
		content = eContent.getText().toString();
		Log.i("activity","commentpub click");
		if(StringUtils.isEmpty(title)){
			UIHelper.ToastMessage(this, "请输入主题");
			return;
		}
		if (title.length() < 5) {
			UIHelper.ToastMessage(this, "请输入5字以上主题内容");
			return;
		}
		if(StringUtils.isEmpty(content)){
			UIHelper.ToastMessage(this, "请输入主题内容");
			return;
		}
		
		final AppContext ac = (AppContext)getApplication();
		
    	mProgress = ProgressDialog.show(this, null, "发表中···",true,true); 			
		
		final Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				Log.i("td","CommentPub handler");
				if(mProgress!=null)mProgress.dismiss();
				if(msg.what == 1){
					BBSTopic topic = (BBSTopic)msg.obj;
					UIHelper.ToastMessageCommentSucess(TopicPubActivity.this);
					UIHelper.completeTopicPub(TopicPubActivity.this, topic);
				}
				else {
					((AppException)msg.obj).makeToast(TopicPubActivity.this);
				}
			}
		};
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {
					msg.what = 1;
					if (sectionID != 0) {
						msg.obj = ac.addTopic(sectionID, title, content);
					} else {
						msg.obj = ac.addTopicByCompanyID(companyID, title, content);
					}
	            } catch (AppException e) {
	            	
					msg.what = -1;
					msg.obj = e;
	            }
				handler.sendMessage(msg);
			}
		}.start();
	}
    
    
	/*private View.OnClickListener publishClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			title = eTitle.getText().toString();
			content = eContent.getText().toString();
			Log.i("activity","commentpub click");
			if(StringUtils.isEmpty(content)){
				UIHelper.ToastMessage(v.getContext(), "请输入主题");
				return;
			}
			if(StringUtils.isEmpty(content)){
				UIHelper.ToastMessage(v.getContext(), "请输入主题内容");
				return;
			}
			
			final AppContext ac = (AppContext)getApplication();
			
	    	mProgress = ProgressDialog.show(v.getContext(), null, "发表中···",true,true); 			
			
			final Handler handler = new Handler(){
				public void handleMessage(Message msg) {
					Log.i("td","CommentPub handler");
					if(mProgress!=null)mProgress.dismiss();
					if(msg.what == 1){
						BBSTopic topic = (BBSTopic)msg.obj;
						UIHelper.ToastMessageCommentSucess(TopicPubActivity.this);
						UIHelper.completeTopicPub(TopicPubActivity.this, topic);
					}
					else {
						((AppException)msg.obj).makeToast(TopicPubActivity.this);
					}
				}
			};
			new Thread(){
				public void run() {
					Message msg = new Message();
					try {
						msg.what = 1;
						if (sectionID != 0) {
							msg.obj = ac.addTopic(sectionID, title, content);
						} else {
							msg.obj = ac.addTopicByCompanyID(companyID, title, content);
						}
		            } catch (AppException e) {
		            	
						msg.what = -1;
						msg.obj = e;
		            }
					handler.sendMessage(msg);
				}
			}.start();
		}
	};*/
}
