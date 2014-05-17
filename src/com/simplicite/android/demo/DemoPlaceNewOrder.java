/*
 * Simplicite(R) for Google Android(R)
 * http://www.simplicite.fr
 */
package com.simplicite.android.demo;

import java.util.ArrayList;
import java.util.HashMap;

import com.simplicite.android.R;
import com.simplicite.android.core.BusinessObject;
import com.simplicite.android.core.Document;
import com.simplicite.android.core.Field;
import com.simplicite.android.ui.ExternalObjectCommon;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

public class DemoPlaceNewOrder extends ExternalObjectCommon {
	private HashMap<String, Object> product;
	private HashMap<String, Object> client;

	@Override
	protected void display() throws Exception {
		TextView tp = new TextView(this);
		tp.setText("Select a product:");
		extobjLayout.addView(tp);
		
		final Button bok = new Button(this);
		bok.setEnabled(false);

		BusinessObject prd = app.getBusinessObject("DemoProduct");
		Gallery g = new Gallery(this);
		g.setAdapter(new ImageAdapter(prd.search(BusinessObject.CONTEXT_LIST, true, false)));
		g.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> item = (HashMap<String, Object>)v.getTag();
				toastMessage("Selected product: " + item.get("prdReference") + " by " + item.get("prdSupId__supName"));
				product = item;
				bok.setEnabled(client != null);
			}
		});
		extobjLayout.addView(g);
		
		final Button bc = new Button(this);
		bc.setText("Select client");

		BusinessObject cli = app.getBusinessObject("DemoClient");
		final ArrayList<HashMap<String, Object>> clients = cli.search(BusinessObject.CONTEXT_LIST, false, false);
		final String[] items = new String[clients.size()];
		for (int i = 0; i < clients.size(); i++) {
			items[i] = (String)(clients.get(i)).get("cliCode");
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select client");
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		        client = clients.get(item);
		        bc.setText((String)client.get("cliCode"));
		        toastMessage("Selected client: " + client.get("cliFirstname") + " " + client.get("cliLastname"));
				bok.setEnabled(product != null);
				dialog.dismiss();
		    }
		});
		final AlertDialog alert = builder.create();
		
		bc.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				alert.show();
			}
		});
		extobjLayout.addView(bc);

		final EditText etq = new EditText(this);
		etq.setInputType(InputType.TYPE_CLASS_NUMBER);
		etq.setHint("Enter quantity");
		extobjLayout.addView(etq);
		
		bok.setText("OK");
		bok.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				new LoadingAsyncTask() {
					@Override
					protected Object task(Object[] params) throws Exception {
						BusinessObject ord = app.getBusinessObject("DemoOrder");
						ord.item = new HashMap<String, Object>();
						ord.item.put("row_id", Field.DEFAULT_ROW_ID);
						ord.getMetaData(BusinessObject.CONTEXT_CREATE);
						ord.item.put("ordPrdId", product.get("row_id"));
						ord.item.put("ordCliId", client.get("row_id"));
						ord.item.put("ordQuantity", etq.getText());
						ord.item.put("ordComments", "Created from Android(R) order entry");
						ord.populate();
						ord.create();
						return "Thank you !\nOrder created with number " + ord.item.get("ordNumber") + "\nTotal = " + ord.item.get("ordTotal");
					}
					
					protected void postTask(Object result) {
						extobjLayout.removeAllViews();
						TextView res = new TextView(DemoPlaceNewOrder.this);
						res.setText((String)result);
						extobjLayout.addView(res);
					}
				}.execute();
			}
		});
		extobjLayout.addView(bok);
	}
	
	private class ImageAdapter extends BaseAdapter {
		private int bg;
		private ArrayList<ImageView> images = new ArrayList<ImageView>();
		
		public ImageAdapter(ArrayList<HashMap<String, Object>> list) {
			TypedArray a = obtainStyledAttributes(R.styleable.DemoPlaceNewOrderImage);
	        bg = a.getResourceId(R.styleable.DemoPlaceNewOrderImage_android_galleryItemBackground, 0);
	        a.recycle();

	        int n = 0;
			for (HashMap<String, Object> item : list) {
		        ImageView i = image((Document)item.get("prdPicture"), 0, 250);
		        i.setId(n++);
		        i.setTag(item);
		        i.setBackgroundResource(bg);
		        images.add(i);
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
	        return images.get(position);
		}

		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public Object getItem(int position) {
			return images.get(position).getTag();
		}

		@Override
		public long getItemId(int position) {
			return images.get(position).getId();
		}
	}
}