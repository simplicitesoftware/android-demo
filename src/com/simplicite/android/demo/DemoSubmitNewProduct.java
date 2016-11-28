/*
 * Simplicite(R) for Google Android(R)
 * http://www.simplicite.fr
 */
package com.simplicite.android.demo;

import com.simplicite.android.ui.ExternalObjectCommon;

import android.widget.TextView;

public class DemoSubmitNewProduct extends ExternalObjectCommon {
	@Override
	protected void display() throws Exception {
		TextView tmp = new TextView(this);
		tmp.setText(app.texts.get("NOT_IMPLEMENTED"));
		extobjLayout.addView(tmp);
	}
}