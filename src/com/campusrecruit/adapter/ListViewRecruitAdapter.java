package com.campusrecruit.adapter;

import java.util.ArrayList;
import java.util.List;

import com.campusrecruit.activity.MainActivity;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.CareerTalk;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.bean.Result;
import com.campusrecruit.common.StringUtils;
import com.campusrecruit.common.UIHelper;
import com.campusrecruit.fragment.RecruitFragment;
import com.pcncad.campusRecruit.R;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ListViewRecruitAdapter extends BaseAdapter {

	private Context context;
	private AppContext appContext;
	private List<Recruit> listItem;
	
	private LayoutInflater listContainer;
	private int itemViewResource;

	static class ListItemView {
		LinearLayout clickLayout;
		LinearLayout dateLayout;
		ImageView newFlag;
		ImageView famousFlag;
		TextView companyName;
		TextView createdTime;
		TextView position;
		TextView place;
		TextView companyIndustry;
		TextView companyType;
		TextView sourceFrom;

		LinearLayout detailLayout;
		LinearLayout replysLayout;
		TextView replies;
		TextView clicks;
		LinearLayout joinsLayout;
		TextView joins;
		ToggleButton joinsImageButton;

	}

	public void setData(List<Recruit> data) {
		this.listItem = data;
	}

	public ListViewRecruitAdapter(Context context, AppContext appContext,
			int resource) {
		this.context = context;
		this.appContext = appContext;
		this.listContainer = LayoutInflater.from(context);
		this.listItem = new ArrayList<Recruit>();
		this.itemViewResource = resource;
	}

	@Override
	public int getCount() {
		return this.listItem.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemView holder = null;
		if (convertView == null) {
			holder = new ListItemView();

			convertView = this.listContainer.inflate(this.itemViewResource,
					null);
			// item
			holder.dateLayout = (LinearLayout) convertView
					.findViewById(R.id.recruit_date_layout);
			holder.clickLayout = (LinearLayout) convertView
					.findViewById(R.id.recruit_click_layout);
			holder.companyName = (TextView) convertView
					.findViewById(R.id.recruit_company_name);
			holder.createdTime = (TextView) convertView
					.findViewById(R.id.recruit_created_time);
			holder.position = (TextView) convertView
					.findViewById(R.id.recruit_position);
			holder.place = (TextView) convertView
					.findViewById(R.id.recruit_place);
			holder.companyType = (TextView) convertView
					.findViewById(R.id.recruit_company_type);
			holder.companyIndustry = (TextView) convertView
					.findViewById(R.id.recruit_company_industry);
			holder.newFlag = (ImageView) convertView
					.findViewById(R.id.recruit_new_flag);
			holder.famousFlag = (ImageView) convertView
					.findViewById(R.id.recruit_famous_flag);
			holder.sourceFrom = (TextView) convertView
					.findViewById(R.id.recruit_source_from);

			// footer
			holder.detailLayout = (LinearLayout) convertView
					.findViewById(R.id.item_footer_clicks_layout);
			holder.replies = (TextView) convertView
					.findViewById(R.id.item_footer_comments);
			holder.replysLayout = (LinearLayout) convertView
					.findViewById(R.id.item_footer_comments_layout);
			holder.joins = (TextView) convertView
					.findViewById(R.id.item_footer_joins);
			holder.clicks = (TextView) convertView
					.findViewById(R.id.item_footer_clicks);
			holder.joinsImageButton = (ToggleButton) convertView
					.findViewById(R.id.item_footer_joins_image);
			holder.joins = (TextView) convertView
					.findViewById(R.id.item_footer_joins);
			holder.joinsLayout = (LinearLayout) convertView
					.findViewById(R.id.item_footer_joins_layout);

			convertView.setTag(holder);
		} else {
			holder = (ListItemView) convertView.getTag();
		}
		Recruit recruit = this.listItem.get(position);
		
		// date header
		String friendlyTime = null;
		try {
			friendlyTime = StringUtils.friendly_created_time(recruit
					.getCreatedTime());
		} catch (Exception e) {
			friendlyTime = recruit.getCreatedTime();
		}
		String trimmedDate = StringUtils.trimed_time(recruit.getCreatedTime());
		String trimmedPreviousDate = null;
		if (position != 0) {
			trimmedPreviousDate = StringUtils.trimed_time(listItem.get(position - 1).getCreatedTime());
		}
		// 0 for init, 1 for header, 2 for othersreplysLayout
		if (position == 0 ||
					(!trimmedDate.equals(trimmedPreviousDate))) {
			holder.dateLayout.setVisibility(View.VISIBLE);
			holder.createdTime.setText(friendlyTime);
		} else {
			holder.dateLayout.setVisibility(View.GONE);
		}
		/*
		 * if (recruit.getStatus() == 1)
		 * holder.newFlag.setVisibility(View.VISIBLE); else
		 * holder.newFlag.setVisibility(View.INVISIBLE);
		 */
		if (StringUtils.isToday(recruit.getCreatedTime()))
			holder.newFlag.setVisibility(View.VISIBLE);
		else
			holder.newFlag.setVisibility(View.INVISIBLE);

		if (recruit.getFamous() == 1)
			holder.famousFlag.setVisibility(View.VISIBLE);
		else
			holder.famousFlag.setVisibility(View.INVISIBLE);
		holder.clicks.setText(recruit.getClicks() + "");
		holder.replies.setText(recruit.getReplies() + "");
		holder.joins.setText(recruit.getJoins() + "");
		holder.companyName.setText(recruit.getCompany().getCompanyName());
		holder.companyName.setTag(recruit.getRecruitID());
		holder.position.setText(recruit.getPosition());
		holder.place.setText(recruit.getPlace());
		holder.companyIndustry.setText(recruit.getCompany().getIndustry());
		holder.companyType.setText(recruit.getCompany().getType());
		holder.sourceFrom.setText(recruit.getSourceFrom());
		/*
		 * holder.joins.setText(recruit.getJoins()+"");
		 */
		// holder.replies.setText(recruit.getReplies()+"");
		if (recruit.getIsJoined() == 1)
			holder.joinsImageButton.setChecked(true);
		else
			holder.joinsImageButton.setChecked(false);
		holder.detailLayout.setOnClickListener(new DetailListener(recruit));
		holder.clickLayout.setOnClickListener(new DetailListener(recruit));
		holder.joinsLayout.setOnClickListener(new JoinsListener(recruit,
				holder.joinsImageButton, holder.joins));
		holder.joinsImageButton.setOnClickListener(new JoinsListener(recruit,
				holder.joinsImageButton, holder.joins));
		holder.replysLayout.setOnClickListener(new RepliesListener(recruit));
		return convertView;
	}

	private class DetailListener implements OnClickListener {
		public DetailListener(Recruit recruit) {
			this.recruit = recruit;
		}

		private Recruit recruit;

		@Override
		public void onClick(View v) {
			Log.i("recruit", recruit.getSourceFrom() + "");
			// 跳转到招聘详情
			if (recruit.getSourceFrom().equals("大街网")) {
				UIHelper.showRecruitDetail(context, recruit, true);
			} else {
				UIHelper.showRecruitSimpleDetail(context, recruit, true);
			}
		}

	}

	private class JoinsListener implements OnClickListener {
		public JoinsListener(Recruit recruit, ToggleButton vJoinsImage,
				TextView vJoinsText) {
			this.recruit = recruit;
			this.vJoinsImage = vJoinsImage;
			this.vJoinsText = vJoinsText;
		}

		private Recruit recruit;
		private ToggleButton vJoinsImage;
		private TextView vJoinsText;

		@Override
		public void onClick(View v) {
			Log.i("test","onclick");
			if (!appContext.isInit()) {
				UIHelper.showLoginDialog((Activity) context);
				vJoinsImage.setChecked(false);
				return;
			}
			if (!appContext.getTutorialRecruitFavorate()) {
				UIHelper.showRecruitFavorateTutorial((Activity)context);
				appContext.setTutorialRecruitFavorate();
			}
			Log.i("onclick","1");
			int joins = 0;
			try {
				joins = Integer.parseInt(vJoinsText.getText().toString());
			} catch (NumberFormatException e) {
				Log.i("bug", "tansfrom error");
				vJoinsText.setText("0");
			}
			Log.i("onclick","2");
			if (recruit.getIsJoined() == 1) {
				UIHelper.ToastMessage(context, "取消收藏");
				vJoinsImage.setChecked(false);
				joins--;
				recruit.setIsJoined(0);
				Log.i("onclick","3");
				appContext.recruitJoin(recruit, false);
			} else {
				UIHelper.ToastMessage(context, "收藏成功");
				vJoinsImage.setChecked(true);
				recruit.setIsJoined(1);
				joins++;
				appContext.recruitJoin(recruit, true);
			}
			Log.i("onclick","4");
			vJoinsText.setText(joins + "");
			Log.i("onclick",joins+"!!!!!");
			recruit.setJoins(joins);
			notifyDataSetChanged();
			new Thread() {
				public void run() {
					try {
						if (recruit.getIsJoined() == 1) {
							appContext
									.joinRecruit(recruit.getRecruitID(), true);
						} else {
							appContext.joinRecruit(recruit.getRecruitID(),
									false);
						}
					} catch (AppException e) {
						
					}
				}
			}.start();
		}

	}

	private class RepliesListener implements OnClickListener {
		public RepliesListener(Recruit recruit) {
			this.recruit = recruit;
		}

		private Recruit recruit;

		@Override
		public void onClick(View v) {
			// 跳转到招聘详情
			if (recruit.getSourceFrom().equals("大街网")) {
				UIHelper.showRecruitDetail(context, recruit, false);
			} else {
				UIHelper.showRecruitSimpleDetail(context, recruit, false);
			}
		}

	}

}
