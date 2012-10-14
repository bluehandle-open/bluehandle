package com.whyun.message.key;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

public final class KeyMap {
	public static final byte KeyLButton = 1;//鼠标左键
	public static final byte KeyRButton = 2;//鼠标右键
	public static final byte KeyCancel = 3;//CANCEL
	public static final byte KeyMButton = 4;//鼠标中键
	public static final byte KeyBack = 8;//BACKSPACE
	public static final byte KeyTab = 9;//TAB
	public static final byte KeyClear = 12;//CLEAR
	public static final byte KeyReturn = 13;//ENTER
	public static final byte KeyShift = 16;//SHIFT
	public static final byte KeyControl = 17;//CTRL
	public static final byte KeyAlt = 18;//菜单键
	public static final byte KeyPause = 19;//PAUSE
	public static final byte KeyCapital = 20;// CAPS LOCK
	public static final byte KeyEscape = 27;//ESC
	public static final byte KeySpace = 32;//SPACEBAR
	public static final byte KeyPageUp = 33;//PAGEUP
	public static final byte KeyPageDown = 34;//PAGEDOWN
	public static final byte KeyEnd = 35;//END
	public static final byte KeyHome = 36;//HOME
	public static final byte KeyLeft = 37; // LEFT ARROW
	public static final byte KeyUp = 38; //UP ARROW
	public static final byte KeyRight = 39 ;//RIGHT ARROW
	public static final byte KeyDown = 40; //DOWN ARROW
	public static final byte KeySelect = 41;//SELECT
	public static final byte KeyPrint = 42;// PRINT SCREEN
	public static final byte KeyExecute = 43;//EXECUTE
	public static final byte KeySnapshot= 44 ;// SNAP SHOT
	public static final byte KeyInser = (byte) 0x9b;//INS (155)
	public static final byte KeyDelete = 127;//DEL
	public static final byte KeyHelp = 47;//HELP
	public static final byte KeyNumlock = (byte)144; // NUMLOCK
	public static final byte KeyMaoHao = 59;//冒号------
	public static final byte KeySmaller = 60;//<----
	public static final byte KeyEqual = 61;//=------
	public static final byte KeyBigger = 62;//>
	public static final byte KeyQuestion = 63;//?-----
	public static final byte KeyAt = 64;//@
	public static final byte KeyA = 65;//A
	public static final byte KeyB = 66;//B
	public static final byte KeyC = 67;//C
	public static final byte KeyD = 68;//D
	public static final byte KeyE = 69;//E
	public static final byte KeyF = 70;//F
	public static final byte KeyG = 71;//G
	public static final byte KeyH = 72;//H
	public static final byte KeyI = 73;//I
	public static final byte KeyJ = 74;//J
	public static final byte KeyK = 75;//K
	public static final byte KeyL = 76;//L
	public static final byte KeyM = 77;//M
	public static final byte KeyN = 78;//N
	public static final byte KeyO = 79;//O
	public static final byte KeyP = 80;//P
	public static final byte KeyQ = 81;//Q
	public static final byte KeyR = 82;//R
	public static final byte KeyS = 83;//S
	public static final byte KeyT = 84;//T
	public static final byte KeyU = 85;//U
	public static final byte KeyV = 86;//V
	public static final byte KeyW = 87;//W
	public static final byte KeyX = 88;//X
	public static final byte KeyY = 89;//Y
	public static final byte KeyZ = 90;//Z
	public static final byte Key0 = 48;//0
	public static final byte Key1 = 49;//1
	public static final byte Key2 = 50;//2
	public static final byte Key3 = 51;//3
	public static final byte Key4 = 52;//4
	public static final byte Key5 = 53;//5
	public static final byte Key6 = 54;//6
	public static final byte Key7 = 55;//7
	public static final byte Key8 = 56;//8
	public static final byte Key9 = 57;//9
	public static final byte KeyNumpad0 = 96;//0
	public static final byte KeyNumpad1 = 97;//1
	public static final byte KeyNumpad2 = 98;//2
	public static final byte KeyNumpad3 = 99;//3
	public static final byte KeyNumpad4 = 100;//4
	public static final byte KeyNumpad5 = 101;//5
	public static final byte KeyNumpad6 = 102;//6
	public static final byte KeyNumpad7 = 103;//7
	public static final byte KeyNumpad8 = 104;//8
	public static final byte KeyNumpad9 = 105;//9
	public static final byte KeyMultiply = 106;//乘号(*)
	public static final byte KeyAdd = 107;//加号(+)
	public static final byte KeyVertical = 108;//|
	public static final byte KeySubtract = 109;//减号(-)
	public static final byte KeyDecimal = 110;//小数点(.)
	public static final byte KeyDivide = 111;//除号(/)
	public static final byte KeyF1 = 112;//F1
	public static final byte KeyF2 = 113;//F2
	public static final byte KeyF3 = 114;//F3
	public static final byte KeyF4 = 115;//F4
	public static final byte KeyF5 = 116;//F5
	public static final byte KeyF6 = 117;//F6
	public static final byte KeyF7 = 118;//F7
	public static final byte KeyF8 = 119;//F8
	public static final byte KeyF9 = 120;//F9
	public static final byte KeyF10 = 121;//F10
	public static final byte KeyF11 = 122;//F11
	public static final byte KeyF12 = 123;//F12
	public static final byte KeyF13 = 124;//F13
	public static final byte KeyF14 = 125;//F14
	public static final byte KeyF15 = 126;//F15
	public static final byte KeyF16 = 127;//F16
	public static final byte KeyWave = (byte)192;//~
	public static final byte KeyReduce = 45;//-
	
	public static final HashMap<String,Byte> KEY_MAP 
		= new HashMap<String,Byte>(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			{
				put ("lButton",KeyLButton);
				put ("rButton",KeyRButton);
				put ("cancel",KeyCancel);
				put ("mButton",KeyMButton);
				put ("Backspace",KeyBack);
				put ("Tab",KeyTab);
				put ("clear",KeyClear);
				put ("Enter",KeyReturn);
				put ("shift",KeyShift);
				put (" Ctrl ",KeyControl);
				put (" Alt ",KeyAlt);
				put ("pause",KeyPause);
				put ("CapsLock",KeyCapital);
				put ("escape",KeyEscape);
				put ("空格",KeySpace);
				put ("PgUp",KeyPageUp);
				put ("PgDn",KeyPageDown);
				put (" End ",KeyEnd);
				put ("Home",KeyHome);
				put ("向左",KeyLeft);
				put ("向上",KeyUp);
				put ("向右",KeyRight);
				put ("向下",KeyDown);
				put ("select",KeySelect);
				put ("print",KeyPrint);
				put ("execute",KeyExecute);
				put ("Insert",KeyInser);
				put ("Delete",KeyDelete);
				put ("help",KeyHelp);
				put ("numlock",KeyNumlock);
				put(";",KeyMaoHao);
				put("<",KeySmaller);
				put("=",KeyEqual);
				put(">",KeyBigger);
				put ("A",KeyA);
				put ("B",KeyB);
				put ("C",KeyC);
				put ("D",KeyD);
				put ("E",KeyE);
				put ("F",KeyF);
				put ("G",KeyG);
				put ("H",KeyH);
				put ("I",KeyI);
				put ("J",KeyJ);
				put ("K",KeyK);
				put ("L",KeyL);
				put ("M",KeyM);
				put ("N",KeyN);
				put ("O",KeyO);
				put ("P",KeyP);
				put ("Q",KeyQ);
				put ("R",KeyR);
				put ("S",KeyS);
				put ("T",KeyT);
				put ("U",KeyU);
				put ("V",KeyV);
				put ("W",KeyW);
				put ("X",KeyX);
				put ("Y",KeyY);
				put ("Z",KeyZ);
				put ("0",Key0);
				put ("1",Key1);
				put ("2",Key2);
				put ("3",Key3);
				put ("4",Key4);
				put ("5",Key5);
				put ("6",Key6);
				put ("7",Key7);
				put ("8",Key8);
				put ("9",Key9);
				put ("numpad0",KeyNumpad0);
				put ("numpad1",KeyNumpad1);
				put ("numpad2",KeyNumpad2);
				put ("numpad3",KeyNumpad3);
				put ("numpad4",KeyNumpad4);
				put ("numpad5",KeyNumpad5);
				put ("numpad6",KeyNumpad6);
				put ("numpad7",KeyNumpad7);
				put ("numpad8",KeyNumpad8);
				put ("numpad9",KeyNumpad9);
				put ("multiply",KeyMultiply);
				put ("add",KeyAdd);
				put ("|",KeyVertical);
				put ("-",KeySubtract);
				put ("decimal",KeyDecimal);
				put ("divide",KeyDivide);
				put ("F1",KeyF1);
				put ("F2",KeyF2);
				put ("F3",KeyF3);
				put ("F4",KeyF4);
				put ("F5",KeyF5);
				put ("F6",KeyF6);
				put ("F7",KeyF7);
				put ("F8",KeyF8);
				put ("F9",KeyF9);
				put ("F10",KeyF10);
				put ("F11",KeyF11);
				put ("F12",KeyF12);
				put ("F13",KeyF13);
				put ("F14",KeyF14);
				put ("F15",KeyF15);
				put ("F16",KeyF16);
				put("~",KeyWave);
				put("-",KeyReduce);
				put("？",KeyQuestion);
			}
		
	};	
	/**
	 * 键盘码到键盘名的映射
	 * */
	@SuppressLint("UseSparseArrays")
	public static final HashMap<Byte,String> CODE_2_KEY_MAP = new HashMap<Byte,String>();
	
	static {
		 for (Map.Entry<String, Byte> element : KEY_MAP.entrySet()) {
			 CODE_2_KEY_MAP.put(element.getValue(), element.getKey());
		 }
	}
}
