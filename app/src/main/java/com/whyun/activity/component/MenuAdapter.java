package com.whyun.activity.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.whyun.bluetooth.R;

import android.content.Context;
import android.widget.SimpleAdapter;

public class MenuAdapter extends SimpleAdapter {
	private List<HashMap<String, Object>> data;
	public static final String MAP_ITEM_IMAGE = "itemImage";
	public static final String MAP_ITEM_TEXT = "itemText";
	public static final String MAP_ITEM_ID = "itemId";
	private static final String[] ADAPTER_FROM_MAP = new String[] { MAP_ITEM_IMAGE, MAP_ITEM_TEXT }; 
	private static final int[] ADAPTER_TO_ID = new int[] { R.id.item_image, R.id.item_text };
	public MenuAdapter(Context context, ArrayList<HashMap<String, Object>> data) {
		super(context, data, R.layout.item_menu, ADAPTER_FROM_MAP, ADAPTER_TO_ID);
		this.data = data;
	}
	@Override
	public long getItemId(int position) {
		int id = -1;
		if (position < data.size()) {
			Integer idInt = (Integer)data.get(position).get(MAP_ITEM_ID);
			if (idInt != null) {
				id = idInt;
			}
		}
		return id;
	}

	

}
