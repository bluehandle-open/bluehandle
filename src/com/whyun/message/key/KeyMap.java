package com.whyun.message.key;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

public final class KeyMap {
	public static final int KeyLButton = 1;//鼠标左键
	public static final int KeyRButton = 2;//鼠标右键
	public static final int KeyCancel = 3;//CANCEL
	public static final int KeyMButton = 4;//鼠标中键
	public static final int KeyBack = 8;//BACKSPACE
	public static final int KeyTab = 9;//TAB
	public static final int KeyClear = 12;//CLEAR
	public static final int KeyReturn = 13;//ENTER
	public static final int KeyShift = 16;//SHIFT
	public static final int KeyControl = 17;//CTRL
	public static final int KeyAlt = 18;//菜单键
	public static final int KeyPause = 19;//PAUSE
	public static final int KeyCapital = 20;// CAPS LOCK
	public static final int KeyEscape = 27;//ESC
	public static final int KeySpace = 32;//SPACEBAR
	public static final int KeyPageUp = 33;//PAGEUP
	public static final int KeyPageDown = 34;//PAGEDOWN
	public static final int KeyEnd = 35;//END
	public static final int KeyHome = 36;//HOME
	public static final int KeyLeft = 37; // LEFT ARROW
	public static final int KeyUp = 38; //UP ARROW
	public static final int KeyRight = 39 ;//RIGHT ARROW
	public static final int KeyDown = 40; //DOWN ARROW
	public static final int KeySelect = 41;//SELECT
	public static final int KeyPrint = 42;// PRINT SCREEN
	public static final int KeyExecute = 43;//EXECUTE
	public static final int KeySnapshot= 44 ;// SNAP SHOT
	public static final int KeyInser = 155;//INS
	public static final int KeyDelete = 127;//DEL
	public static final int KeyHelp = 47;//HELP
	public static final int KeyNumlock = 144; // NUMLOCK
	public static final int KeyMaoHao = 59;//冒号------
	public static final int KeySmaller = 60;//<----
	public static final int KeyEqual = 61;//=------
	public static final int KeyBigger = 62;//>
	public static final int KeyQuestion = 63;//?-----
	public static final int KeyAt = 64;//@
	public static final int KeyA = 65;//A
	public static final int KeyB = 66;//B
	public static final int KeyC = 67;//C
	public static final int KeyD = 68;//D
	public static final int KeyE = 69;//E
	public static final int KeyF = 70;//F
	public static final int KeyG = 71;//G
	public static final int KeyH = 72;//H
	public static final int KeyI = 73;//I
	public static final int KeyJ = 74;//J
	public static final int KeyK = 75;//K
	public static final int KeyL = 76;//L
	public static final int KeyM = 77;//M
	public static final int KeyN = 78;//N
	public static final int KeyO = 79;//O
	public static final int KeyP = 80;//P
	public static final int KeyQ = 81;//Q
	public static final int KeyR = 82;//R
	public static final int KeyS = 83;//S
	public static final int KeyT = 84;//T
	public static final int KeyU = 85;//U
	public static final int KeyV = 86;//V
	public static final int KeyW = 87;//W
	public static final int KeyX = 88;//X
	public static final int KeyY = 89;//Y
	public static final int KeyZ = 90;//Z
	public static final int Key0 = 48;//0
	public static final int Key1 = 49;//1
	public static final int Key2 = 50;//2
	public static final int Key3 = 51;//3
	public static final int Key4 = 52;//4
	public static final int Key5 = 53;//5
	public static final int Key6 = 54;//6
	public static final int Key7 = 55;//7
	public static final int Key8 = 56;//8
	public static final int Key9 = 57;//9
	public static final int KeyNumpad0 = 96;//0
	public static final int KeyNumpad1 = 97;//1
	public static final int KeyNumpad2 = 98;//2
	public static final int KeyNumpad3 = 99;//3
	public static final int KeyNumpad4 = 100;//4
	public static final int KeyNumpad5 = 101;//5
	public static final int KeyNumpad6 = 102;//6
	public static final int KeyNumpad7 = 103;//7
	public static final int KeyNumpad8 = 104;//8
	public static final int KeyNumpad9 = 105;//9
	public static final int KeyMultiply = 106;//乘号(*)
	public static final int KeyAdd = 107;//加号(+)
	public static final int KeyVertical = 108;//|
	public static final int KeySubtract = 109;//减号(-)
	public static final int KeyDecimal = 110;//小数点(.)
	public static final int KeyDivide = 111;//除号(/)
	public static final int KeyF1 = 112;//F1
	public static final int KeyF2 = 113;//F2
	public static final int KeyF3 = 114;//F3
	public static final int KeyF4 = 115;//F4
	public static final int KeyF5 = 116;//F5
	public static final int KeyF6 = 117;//F6
	public static final int KeyF7 = 118;//F7
	public static final int KeyF8 = 119;//F8
	public static final int KeyF9 = 120;//F9
	public static final int KeyF10 = 121;//F10
	public static final int KeyF11 = 122;//F11
	public static final int KeyF12 = 123;//F12
	public static final int KeyF13 = 124;//F13
	public static final int KeyF14 = 125;//F14
	public static final int KeyF15 = 126;//F15
	public static final int KeyF16 = 127;//F16
	public static final int KeyWave = 192;//~
	public static final int KeyReduce = 45;//-
	
	public static final HashMap<String,Integer> KEY_MAP 
		= new HashMap<String,Integer>(){
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
	public static final HashMap<Integer,String> CODE_2_KEY_MAP = new HashMap<Integer,String>();
	
	static {
		 for (Map.Entry<String, Integer> element : KEY_MAP.entrySet()) {
			 CODE_2_KEY_MAP.put(element.getValue(), element.getKey());
		 }
	}
}
