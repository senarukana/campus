package com.campusrecruit.activity;

import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.BBSReply;
import com.campusrecruit.bean.BBSTopic;
import com.campusrecruit.bean.Result;
import com.campusrecruit.common.SmileyParser;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

public class CommentPubActivity extends BaseActivity {

//	private ImageView mBack;
	private EditText mContent;
//	private Button mPublish;
	private LinkView mQuote;
	private ProgressDialog mProgress;

	private int _topicID;
	private String _content;
	private int _replyid;// 被回复的单个评论id

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_pub);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("评论回复");
		
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
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.menu_finish_id:
			save();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// 初始化视图控件
	private void initView() {
		Log.i("td", "initCommentPub");
		_topicID = getIntent().getIntExtra("topicID", 0);
		_replyid = getIntent().getIntExtra("replyID", 0);

//		mBack = (ImageView) findViewById(R.id.comment_list_back);
//		mPublish = (Button) findViewById(R.id.comment_pub_publish);
		mContent = (EditText) findViewById(R.id.comment_pub_content);

		/*mBack.setOnClickListener(UIHelper.finish(this));
		mPublish.setOnClickListener(publishClickListener);*/

		mQuote = (LinkView) findViewById(R.id.comment_pub_quote);
		mQuote.setText(UIHelper.parseQuoteSpan(
				getIntent().getStringExtra("replyUserName"), getIntent()
						.getStringExtra("replyContent")));
		mQuote.parseLinkText();
		SmileyParser.init(this);
		SmileyParser parser = SmileyParser.getInstance();
		mQuote.setText(parser.addSmileySpans(mQuote.getText()));
	}

	private void save(){
		_content = mContent.getText().toString();
		Log.i("td", "commentpub click");
		if (StringUtils.isEmpty(_content)) {
			UIHelper.ToastMessage(CommentPubActivity.this, "请输入评论内容");
			return;
		}

		final AppContext ac = (AppContext) getApplication();
		// if(!ac.isLogin()){
		// //TODO
		// // UIHelper.showLoginDialog(CommentPub.this);
		// return;
		// }

		mProgress = ProgressDialog.show(CommentPubActivity.this, null, "发表中···",
				true, true);

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				Log.i("td", "CommentPub handler");
				if (mProgress != null)
					mProgress.dismiss();
				if (msg.what == 1) {
					BBSReply reply = (BBSReply) msg.obj;
					UIHelper.ToastMessageCommentSucess(CommentPubActivity.this);
					// 返回刚刚发表的评论
					Intent intent = new Intent();
					intent.putExtra("COMMENT_SERIALIZABLE", reply);
					setResult(RESULT_OK, intent);
					// 跳转到文章详情
					finish();
				} else {
					((AppException) msg.obj)
							.makeToast(CommentPubActivity.this);
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					BBSReply reply = null;
					// 发表评论
					if (_replyid == 0) {
						reply = ac.addReply(_topicID, _content);
						Log.i("test", "test");
					}
					// 对评论进行回复
					else if (_replyid != 0) {
						reply = ac.replyComment(_topicID, _content,
								_replyid);
					}
					msg.what = 1;
					msg.obj = reply;
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
			_content = mContent.getText().toString();
			Log.i("td", "commentpub click");
			if (StringUtils.isEmpty(_content)) {
				UIHelper.ToastMessage(v.getContext(), "请输入评论内容");
				return;
			}

			final AppContext ac = (AppContext) getApplication();
			// if(!ac.isLogin()){
			// //TODO
			// // UIHelper.showLoginDialog(CommentPub.this);
			// return;
			// }

			mProgress = ProgressDialog.show(v.getContext(), null, "发表中···",
					true, true);

			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					Log.i("td", "CommentPub handler");
					if (mProgress != null)
						mProgress.dismiss();
					if (msg.what == 1) {
						BBSReply reply = (BBSReply) msg.obj;
						UIHelper.ToastMessageCommentSucess(CommentPubActivity.this);
						// 返回刚刚发表的评论
						Intent intent = new Intent();
						intent.putExtra("COMMENT_SERIALIZABLE", reply);
						setResult(RESULT_OK, intent);
						// 跳转到文章详情
						finish();
					} else {
						((AppException) msg.obj)
								.makeToast(CommentPubActivity.this);
					}
				}
			};
			new Thread() {
				public void run() {
					Message msg = new Message();
					try {
						BBSReply reply = null;
						// 发表评论
						if (_replyid == 0) {
							reply = ac.addReply(_topicID, _content);
							Log.i("test", "test");
						}
						// 对评论进行回复
						else if (_replyid != 0) {
							reply = ac.replyComment(_topicID, _content,
									_replyid);
						}
						msg.what = 1;
						msg.obj = reply;
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
