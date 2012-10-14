package com.whyun.message.bean;

import java.io.Serializable;
import java.util.ArrayList;

import com.whyun.message.data.KeyTableOperator;
import com.whyun.message.key.KeyMap;

public class KeyInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private int keyId;
	private String keyname;
	private String up1 = "";
	private String up2 = "";
	private String up3 = "";
	private String down1 = "";
	private String down2 = "";
	private String down3 = "";
	private String left1 = "";
	private String left2 = "";
	private String left3 = "";
	private String right1 = "";
	private String right2 = "";
	private String right3 = "";
	private String a1 = "";
	private String a2 = "";
	private String a3 = "";
	private String b1 = "";
	private String b2 = "";
	private String b3 = "";
	private String x1 = "";
	private String x2 = "";
	private String x3 = "";
	private String y1 = "";
	private String y2 = "";
	private String y3 = "";
	private String start1 = "";
	private String start2 = "";
	private String start3 = "";
	private String select1 = "";
	private String select2 = "";
	private String select3 = "";

	public int getKeyId() {
		return keyId;
	}

	public void setKeyId(int keyId) {
		this.keyId = keyId;
	}

	public String getKeyname() {
		return keyname;
	}

	public void setKeyname(String keyname) {
		this.keyname = keyname;
	}

	public String getUp1() {
		return up1;
	}

	public void setUp1(String up1) {
		if (up1 != null) {
			this.up1 = up1;
		}
	}

	public String getUp2() {
		return up2;
	}

	public void setUp2(String up2) {
		if (up2 != null) {
			this.up2 = up2;
		}
	}

	public String getUp3() {
		return up3;
	}

	public void setUp3(String up3) {
		if (up3 != null) {
			this.up3 = up3;
		}
	}

	public String getDown1() {
		return down1;
	}

	public void setDown1(String down1) {
		if (down1 != null) {
			this.down1 = down1;
		}
	}

	public String getDown2() {
		return down2;
	}

	public void setDown2(String down2) {
		if (down2 != null) {
			this.down2 = down2;
		}
	}

	public String getDown3() {

		return down3;
	}

	public void setDown3(String down3) {
		if (down3 != null) {
			this.down3 = down3;
		}
	}

	public String getLeft1() {
		return left1;
	}

	public void setLeft1(String left1) {
		if (left1 != null) {
			this.left1 = left1;
		}
	}

	public String getLeft2() {
		return left2;
	}

	public void setLeft2(String left2) {
		if (left2 != null) {
			this.left2 = left2;
		}
	}

	public String getLeft3() {
		return left3;
	}

	public void setLeft3(String left3) {
		if (left3 != null) {
			this.left3 = left3;
		}
	}

	public String getRight1() {
		return right1;
	}

	public void setRight1(String right1) {
		if (right1 != null) {
			this.right1 = right1;
		}
	}

	public String getRight2() {
		return right2;
	}

	public void setRight2(String right2) {
		if (right2 != null) {
			this.right2 = right2;
		}
	}

	public String getRight3() {
		return right3;
	}

	public void setRight3(String right3) {
		if (right3 != null) {
			this.right3 = right3;
		}
	}

	public String getA1() {
		return a1;
	}

	public void setA1(String a1) {
		if (a1 != null) {
			this.a1 = a1;
		}
	}

	public String getA2() {
		return a2;
	}

	public void setA2(String a2) {
		if (a2 != null) {
			this.a2 = a2;
		}
	}

	public String getA3() {
		return a3;
	}

	public void setA3(String a3) {
		if (a3 != null) {
			this.a3 = a3;
		}
	}

	public String getB1() {
		return b1;
	}

	public void setB1(String b1) {
		if (b1 != null) {
			this.b1 = b1;
		}
	}

	public String getB2() {
		return b2;
	}

	public void setB2(String b2) {
		if (b2 != null) {
			this.b2 = b2;
		}
	}

	public String getB3() {

		return b3;
	}

	public void setB3(String b3) {
		if (b3 != null) {
			this.b3 = b3;
		}
	}

	public String getX1() {

		return x1;
	}

	public void setX1(String x1) {
		if (x1 != null) {
			this.x1 = x1;
		}
	}

	public String getX2() {
		return x2;
	}

	public void setX2(String x2) {
		if (x2 != null) {
			this.x2 = x2;
		}
	}

	public String getX3() {
		return x3;
	}

	public void setX3(String x3) {
		if (x3 != null) {
			this.x3 = x3;
		}
	}

	public String getY1() {
		return y1;
	}

	public void setY1(String y1) {
		if (y1 != null) {
			this.y1 = y1;
		}
	}

	public String getY2() {
		return y2;
	}

	public void setY2(String y2) {
		if (y2 != null) {
			this.y2 = y2;
		}
	}

	public String getY3() {
		return y3;
	}

	public void setY3(String y3) {
		if (y3 != null) {
			this.y3 = y3;
		}
	}

	public String getStart1() {
		return start1;
	}

	public void setStart1(String start1) {
		if (start1 != null) {
			this.start1 = start1;
		}
	}

	public String getStart2() {
		return start2;
	}

	public void setStart2(String start2) {
		if (start2 != null) {
			this.start2 = start2;
		}
	}

	public String getStart3() {
		return start3;
	}

	public void setStart3(String start3) {
		if (start3 != null) {
			this.start3 = start3;
		}
	}

	public String getSelect1() {
		return select1;
	}

	public void setSelect1(String select1) {
		if (select1 != null) {
			this.select1 = select1;
		}
	}

	public String getSelect2() {
		return select2;
	}

	public void setSelect2(String select2) {
		if (select2 != null) {
			this.select2 = select2;
		}
	}

	public String getSelect3() {
		return select3;
	}

	public void setSelect3(String select3) {
		if (select3 != null) {
			this.select3 = select3;
		}
	}
	
	public String getKey(String keyField, int index) {
		String key = "";
		if (KeyTableOperator.KEY_TABLE_UP_FIELD.equals(keyField)) {
			switch(index) {
			case 1:
				key = up1;
				break;
			case 2:
				key = up2;
				break;
			case 3:
				key = up3;
				break;
			}
		} else if (KeyTableOperator.KEY_TABLE_DOWN_FIELD.equals(keyField)) {
			switch(index) {
			case 1:
				key=down1;
				break;
			case 2:
				key = down2;
				break;
			case 3:
				key = down3;
				break;
			}
		} else if (KeyTableOperator.KEY_TABLE_LEFT_FIELD.equals(keyField)) {
			switch(index) {
			case 1:
				key = left1;
				break;
			case 2:
				key = left2;
				break;
			case 3:
				key = left3;
				break;
			}
		} else if (KeyTableOperator.KEY_TABLE_RIGHT_FIELD.equals(keyField)) {
			switch(index) {
			case 1:
				key = right1;
				break;
			case 2:
				key = right2;
				break;
			case 3:
				key = right3;
				break;
			}
		} else if (KeyTableOperator.KEY_TABLE_X_FIELD.equals(keyField)) {
			switch(index) {
			case 1:
				key = x1;
				break;
			case 2:
				key = x2;
				break;
			case 3:
				key = x3;
				break;
			}
		} else if (KeyTableOperator.KEY_TABLE_Y_FIELD.equals(keyField)) {
			switch(index) {
			case 1:
				key = y1;
				break;
			case 2:
				key = y2;
				break;
			case 3:
				key = y3;
				break;
			}
		} else if (KeyTableOperator.KEY_TABLE_A_FIELD.equals(keyField)) {
			switch(index) {
			case 1:
				key = a1;
				break;
			case 2:
				key = a2;
				break;
			case 3:
				key = a3;
				break;
			}
		} else if (KeyTableOperator.KEY_TABLE_B_FIELD.equals(keyField)) {
			switch(index) {
			case 1:
				key = b1;
				break;
			case 2:
				key = b2;
				break;
			case 3:
				key = b3;
				break;
			}
		} else if (KeyTableOperator.KEY_TABLE_START_FIELD.equals(keyField)) {
			switch(index) {
			case 1:
				key = start1;
				break;
			case 2:
				key = start2;
				break;
			case 3:
				key = start3;
				break;
			}
		} else if (KeyTableOperator.KEY_TABLE_SELECT_FIELD.equals(keyField)) {
			switch(index) {
			case 1:
				key = select1;
				break;
			case 2:
				key = select2;
				break;
			case 3:
				key = select3;
				break;
			}
		}
		return key;
	}
	
	public byte[] getBytes(String keyField) {
		byte[] bytes = null;
		ArrayList<Byte> list = new ArrayList<Byte>(3);
		String first = getKey(keyField,1);
		if (!"".equals(first)) {
			list.add(KeyMap.KEY_MAP.get(first));
		}
		String second = getKey(keyField,2);
		if (!"".equals(second)) {
			list.add(KeyMap.KEY_MAP.get(second));
		}
		String third = getKey(keyField,3);
		if (!"".equals(KeyMap.KEY_MAP.get(third))) {
			list.add(KeyMap.KEY_MAP.get(third));
		}
		if (list.size() > 0) {
			bytes = new byte[3];
			int i = 0;
			for (byte element:list) {
				bytes[i++] = element;
			}
		} else {
			bytes = null;
		}
		
		return bytes;
	}
}
