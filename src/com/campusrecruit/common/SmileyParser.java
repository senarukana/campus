package com.campusrecruit.common;

/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

//import com.android.mms.R;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pcncad.campusRecruit.R;

/**
 * A class for annotating a CharSequence with spans to convert textual emoticons
 * to graphical ones.
 */
public class SmileyParser {
	// Singleton stuff

	private static SmileyParser sInstance;

	public static SmileyParser getInstance() {
		return sInstance;
	}

	public static void init(Context context) {
		sInstance = new SmileyParser(context);
	}

	private final Context mContext;
	
	private final Pattern mPattern;
	private final HashMap<String, Integer> mSmileyToRes;

	private String[] nSmileyTexts;
	private int[] nResourceId;

	private SmileyParser(Context context) {
		mContext = context;
		// mSmileyTexts = initSmileyTexts();

		nSmileyTexts = new String[105];
		initSmileyTexts(nSmileyTexts);
		nResourceId = new int[105];
		initResource(nResourceId);

		mSmileyToRes = buildSmileyToRes();
		mPattern = buildPattern();
	}

	private void initSmileyTexts(String[] text) {
		// String[] text = new String[105];
		for (int i = 0; i < 105; i++) {
			text[i] = "[" + i  + "]";
		}
		// return text;
	}

	private int getIconResourceId(String resName) {
		R.drawable drawalbes = new R.drawable();
		int resId = 0x7f0200c3;
		try {
			Field field = R.drawable.class.getField(resName);
			resId = (Integer) field.get(drawalbes);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			
		}
		return resId;
	}

	private void initResource(int[] resourceArray) {
		for (int i = 0; i < 105; i++) {
			resourceArray[i] = getIconResourceId(getResourceNameFromInt(i + 1));
		}
	}

	private String getResourceNameFromInt(int i) {
		String str = "";
		if (i < 10) {
			str = "f00" + i;
		} else if (i < 100) {
			str = "f0" + i;
		} else {
			str = "f" + i;
		}
		return str;
	}

	

	// NOTE: if you change anything about this array, you must make the
	// corresponding change

	// to the string arrays: default_smiley_texts and default_smiley_names in
	// res/values/arrays.xml

	

	/**
	 * Builds the hashtable we use for mapping the string version of a smiley
	 * (e.g. ":-)") to a resource ID for the icon version.
	 */
	private HashMap<String, Integer> buildSmileyToRes() {
		if (nResourceId.length != nSmileyTexts.length) {
			throw new IllegalStateException("Smiley resource ID/text mismatch");
		}

		HashMap<String, Integer> smileyToRes = new HashMap<String, Integer>(
				nSmileyTexts.length);
		for (int i = 0; i < nSmileyTexts.length; i++) {
			smileyToRes.put(nSmileyTexts[i], nResourceId[i]);
		}
		return smileyToRes;
	}

	/**
	 * Builds the regular expression we use to find smileys in
	 * {@link #addSmileySpans}.
	 */
	// 构建正则表达式
	private Pattern buildPattern() {
		StringBuilder patternString = new StringBuilder(nSmileyTexts.length * 3);

		// Build a regex that looks like (:-)|:-(|...), but escaping the smilies

		// properly so they will be interpreted literally by the regex matcher.

		patternString.append('(');

		for (String s : nSmileyTexts) {
			patternString.append(Pattern.quote(s));
			patternString.append('|');
		}
		// Replace the extra '|' with a ')'

		patternString.replace(patternString.length() - 1,
				patternString.length(), ")");

		return Pattern.compile(patternString.toString());
	}

	/**
	 * Adds ImageSpans to a CharSequence that replace textual emoticons such as
	 * :-) with a graphical version.
	 * 
	 * @param text
	 *            A CharSequence possibly containing emoticons
	 * @return A CharSequence annotated with ImageSpans covering any recognized
	 *         emoticons.
	 */
	// 根据文本替换成图片
	public CharSequence addSmileySpans(CharSequence text) {
		SpannableStringBuilder builder = new SpannableStringBuilder(text);
		Matcher matcher = mPattern.matcher(text);
		while (matcher.find()) {
			int resId = mSmileyToRes.get(matcher.group());
			builder.setSpan(new ImageSpan(mContext, resId), matcher.start(),
					matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return builder;
	}
}
