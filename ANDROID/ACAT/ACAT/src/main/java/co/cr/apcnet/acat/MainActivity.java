package co.cr.apcnet.acat;

import java.io.IOException;
import java.util.Locale;

import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.app.Dialog;
import android.view.Window;

public class MainActivity extends Activity implements OnClickListener, OnInitListener {

	private EditText editText;
	private int ind_number = 0;
	private int tblindex=0;
	private int predwindex=0;
	private int wordcusedindex=0;
	private int rowstb0index=0;
	private int rowbtsindex=0;
	private int lblcycle=0;
	private int indexrowb=0;
	private int cltbl=Color.YELLOW;
	private String colorm="Y";
	private Button[] buttons;
	private Button[] rowbts;
	private EditText[] wordcused;
	private TableLayout[] tbls;
	private TableRow[] rowstb0;
	private EditText[] predw;
	private Boolean Ind_Caps=false;
	private TextToSpeech textToSpeech;
	public static final String TAG = "Main";
	private Handler handler = new Handler();
	private DatabaseHelper mDBHelper;
	private SQLiteDatabase mDb;
	private String DBName="dictionary.db";
	private String lastword=" ";
	private String[] items = {"Si", "No","X"};
	private String SelectD="";
	private Boolean Sel_letter=false;
	private Button[] bts_letter;
	private int btletter_index=0;
	private Boolean Sel_setting=false;
	private Button[] bts_setting;
	private int btsetting_index=0;
	private Dialog customDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mDBHelper = new DatabaseHelper(this,DBName);
		
		try {
		    mDBHelper.updateDataBase();
		} catch (IOException mIOException) {
		    throw new Error("UnableToUpdateDatabase");
		}
		 
		try {
		    mDb = mDBHelper.getWritableDatabase();
		} catch (SQLException mSQLException) {
		    throw mSQLException;
		}
		
		DisplayMetrics dm = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(dm);
	    double x = Math.pow(dm.widthPixels/dm.xdpi,2);
	    double y = Math.pow(dm.heightPixels/dm.ydpi,2);
	    double screenInches = Math.sqrt(x+y);
	    long Inches=Math.round(screenInches);
	    int la=70;
	    int sm=50;
		Inches=5;
	    if (Inches ==7){
	    	la=50;
	    	sm=30;
	    } else if (Inches <7) {
	    	la=30;
	    	sm=15;
	    }
	    
		buttons = new Button[37];
		bts_letter=new Button[3];
		bts_setting=new Button[2];
		wordcused = new EditText[4];
		rowstb0 = new TableRow[4];
		tbls = new TableLayout[3];
		predw=new EditText[8];
		editText = ( EditText ) findViewById( R.id.editText );
		editText.onEditorAction(EditorInfo.IME_ACTION_DONE);
		editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, sm);
		textToSpeech = new TextToSpeech( this, this );


    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // API 21
            editText.setShowSoftInputOnFocus(false);
        } else { // API 11-20
            editText.setTextIsSelectable(true);
        }
    	
    	for(int i=0; i<wordcused.length;i++){
    		String etextID = "WordUse" + (i+1);
    		int resID = getResources().getIdentifier(etextID, "id", getPackageName());
    		wordcused[i] = ((EditText) findViewById(resID));
    		wordcused[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, sm);
    		wordcused[i].setOnClickListener(onClickWU);
    	}
    	for(int i=0; i<predw.length;i++){
    		String etextID = "WordS" + (i+1);
    		int resID = getResources().getIdentifier(etextID, "id", getPackageName());
    		predw[i] = ((EditText) findViewById(resID));
    		predw[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, sm);
    		predw[i].setOnClickListener(onClickWS);
    	}
    	
    	pressageWord("");
    	
    	for(int i=0; i<buttons.length; i++) {
    		{
    		String buttonID = "btnk" + (i);

    		int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
    		buttons[i] = ((Button) findViewById(resID));
    		buttons[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, la);
    		buttons[i].setOnClickListener(this);
    		}
    	}
    	for(int i=0;i<tbls.length;i++){
    		String tblId = "tbl" + (i);
    		int inrw =0;
    		int resID = getResources().getIdentifier(tblId, "id", getPackageName());
    		tbls[i] =((TableLayout) findViewById(resID));
    		if (i==2){
    			for(int j = 0; j < tbls[i].getChildCount(); j++) {
    				View view = tbls[i].getChildAt(j);
	    			if (view instanceof TableRow) {
						TableRow row = (TableRow) view;
						rowstb0[inrw] = row;
						if (inrw==3){
							rowbts = new Button[9];
							for (int k=0;k<rowstb0[inrw].getChildCount();k++){
								View vr = rowstb0[inrw].getChildAt(k);
								rowbts[k]= (Button) vr; 
							}
						}
						inrw =inrw+1;
					}
    			}
    		}
    	}
    	Resources resl = getResources();
		String[] numtextl = resl.getStringArray(R.array.key_letters);
		for (int in=0;in<numtextl.length;in++){
			String txtf=numtextl[in];
			if (Ind_Caps) {
				txtf=txtf.toUpperCase();
			}
			buttons[in].setText(txtf);
		}

    	lblcycle=0;
    	tblindex=0;
    	cltbl =Color.YELLOW;

    	handler.postDelayed(runnable, 1000);
	}

	private Runnable runnable = new Runnable() {
		   @Override
		   public void run() {
			   int postd=1300;
		      /* do what you need to do */
			   try {
			   		if (Sel_letter){
						if (btletter_index > bts_letter.length-1){
							btletter_index=0;
							postd=0;
						} else {
							if (cltbl == Color.YELLOW) {
								bts_letter[btletter_index].setBackgroundColor(cltbl);
								bts_letter[btletter_index].setTextColor(Color.BLACK);
								cltbl = Color.WHITE;
								colorm = "Y";
							} else {
								colorm = "W";
								bts_letter[btletter_index].setBackgroundColor(cltbl);
								bts_letter[btletter_index].setTextColor(Color.WHITE);
								bts_letter[btletter_index].setBackgroundResource(R.drawable.blackbutton);
								btletter_index = btletter_index + 1;
								cltbl = Color.YELLOW;
								postd = 200;
							}
						}
					} else if(Sel_setting){
						if (btsetting_index > bts_setting.length-1){
							btsetting_index=0;
							postd=0;
						} else {
							if (cltbl == Color.YELLOW) {
								bts_setting[btsetting_index].setBackgroundColor(cltbl);
								bts_setting[btsetting_index].setTextColor(Color.BLACK);
								cltbl = Color.WHITE;
								colorm = "Y";
							} else {
								colorm = "W";
								bts_setting[btsetting_index].setBackgroundColor(cltbl);
								bts_setting[btsetting_index].setTextColor(Color.WHITE);
								bts_setting[btsetting_index].setBackgroundResource(R.drawable.blackbutton);
								btsetting_index = btsetting_index + 1;
								cltbl = Color.YELLOW;
								postd = 200;
							}
						}
					} else {
						switch(lblcycle){
							case 0: //General
								if (tblindex > tbls.length-1){
									tblindex=0;
									postd=0;
								} else {
									if (cltbl ==Color.YELLOW){
										tbls[tblindex].setBackgroundColor(cltbl);
										cltbl=Color.WHITE;
										colorm="Y";
									} else {
										colorm="W";
										tbls[tblindex].setBackground(null);
										tblindex=tblindex+1;
										cltbl =Color.YELLOW;
										postd=200;
									}
								}
								break;
							case 1: //rows predictes words
								if (predwindex > predw.length-1){
									predwindex=0;
									lblcycle=0;
								} else {
									if (cltbl ==Color.YELLOW){
										colorm="Y";
										predw[predwindex].setBackgroundColor(cltbl);
										cltbl=Color.WHITE;
									} else {
										colorm="W";
										predw[predwindex].setBackground(null);
										predwindex=predwindex+1;
										cltbl =Color.YELLOW;
									}
								}
								break;
							case 2: //rows used words
								if (wordcusedindex > wordcused.length-1){
									wordcusedindex=0;
									lblcycle=0;
								} else {
									if (cltbl ==Color.YELLOW){
										wordcused[wordcusedindex].setBackgroundColor(cltbl);
										cltbl=Color.WHITE;
										colorm="Y";
									} else {
										colorm="W";
										wordcused[wordcusedindex].setBackground(null);
										wordcusedindex=wordcusedindex+1;
										cltbl =Color.YELLOW;
									}
								}
								break;
							case 3: //rows keyboard
								if (rowstb0index > rowstb0.length-1){
									rowstb0index=0;
									lblcycle=0;
								} else {
									if (cltbl ==Color.YELLOW){
										rowstb0[rowstb0index].setBackgroundColor(cltbl);
										cltbl=Color.WHITE;
										colorm="Y";
									} else {
										colorm="W";
										rowstb0[rowstb0index].setBackground(null);
										rowstb0index=rowstb0index+1;
										cltbl =Color.YELLOW;
									}
								}
								break;
							case 4: //button keyboard row
								if (rowbtsindex > rowbts.length-1){
									rowbtsindex=0;
									lblcycle=0;
								} else {
									if (cltbl ==Color.YELLOW){
										if (indexrowb<2){
											rowbts[rowbtsindex].setBackgroundColor(cltbl);
											rowbts[rowbtsindex].setTextColor(Color.BLACK);
										} else if (indexrowb==2){
											if (rowbtsindex>0 && rowbtsindex<8){
												rowbts[rowbtsindex].setBackgroundColor(cltbl);
												rowbts[rowbtsindex].setTextColor(Color.BLACK);
											} else {
												changebackg_bi(rowbts[rowbtsindex].getId());
											}
										} else {
											//Yellow
											if (rowbtsindex==0 || rowbtsindex==6){
												rowbts[rowbtsindex].setBackgroundColor(cltbl);
												rowbts[rowbtsindex].setTextColor(Color.BLACK);
											} else {
												changebackg_bi(rowbts[rowbtsindex].getId());
											}
										}
										cltbl=Color.WHITE;
										colorm="Y";
									} else {
										if (indexrowb<2){
											rowbts[rowbtsindex].setBackgroundResource(R.drawable.blackbutton);
											rowbts[rowbtsindex].setTextColor(Color.WHITE);
										} else if (indexrowb==2){
											if (rowbtsindex>0 && rowbtsindex<8){
												rowbts[rowbtsindex].setBackgroundResource(R.drawable.blackbutton);
												rowbts[rowbtsindex].setTextColor(Color.WHITE);
											} else {
												changebackg_bi(rowbts[rowbtsindex].getId());
											}
										} else {
											//Black
											if (rowbtsindex==0 || rowbtsindex==6){
												rowbts[rowbtsindex].setBackgroundResource(R.drawable.blackbutton);
												rowbts[rowbtsindex].setTextColor(Color.WHITE);
											} else {
												changebackg_bi(rowbts[rowbtsindex].getId());
											}

										}
										colorm="W";
										rowbtsindex=rowbtsindex+1;
										cltbl =Color.YELLOW;
									}
								}
								break;
						}
					}

				} catch(Exception e){
					 System.out.println("Exception occurred"+ e.getMessage()); 
				}
		      /* and here comes the "trick" */
		      handler.postDelayed(this, postd);
		   }
		};

	private OnClickListener onClickWU = new  OnClickListener(){
		public void onClick(View v){
        for(int i=0; i<wordcused.length;i++){
            if (wordcused[i].getId() == v.getId()) {
                String edittx = wordcused[i].getText().toString();
                speak(edittx);
            }
        }
		}
	};

	private OnClickListener onClickWS = new  OnClickListener(){
		public void onClick(View v){
        for(int i=0; i<predw.length;i++){
            if (predw[i].getId() == v.getId()) {
                String edittx = predw[i].getText().toString();
                speak(edittx);
            }
        }
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		for (int i = 0; i < buttons.length; i++)
		{
			if (buttons[i].getId() == v.getId())
			{
				Buttonprocess(i);
			}
		}
	}
	
	private void Buttonprocess(int i){
		try {
			String buttonText = buttons[i].getText().toString();
			if (i<26){
				if (ind_number==0){
					switch (i){
						case 2:
							items = new String[]{"e", "é", "X"};
							ShowDialogOL();
							break;
						case 6:
							items = new String[]{"u", "ú", "X"};
							ShowDialogOL();
							break;
						case 7:
							items = new String[]{"i", "í", "X"};
							ShowDialogOL();
							break;
						case 8:
							items = new String[]{"o", "ó", "X"};
							ShowDialogOL();
							break;
						case 10:
							items = new String[]{"a", "á", "X"};
							ShowDialogOL();
							break;
						case 24:
							items = new String[]{"n", "ñ", "X"};
							ShowDialogOL();
							break;
						default:
							AddChar(buttonText);
							break;
					}
				} else {
					AddChar(buttonText);
				}
			} else {
				switch (i) {
				case 26: //Setting
					ShowDialogSetting();
					break;
				case 27: //Point
					AddChar(buttonText);
					AddChar(" ");
					ChangeUpperLower();
					break;
				case 28://Text to speech
					String multiLines = editText.getText().toString();
					String[] txtspk;
					String delimiter = "\n";

					txtspk = multiLines.split(delimiter);
					if (txtspk.length>0){
						textToSpeech.setLanguage( new Locale( "spa", "US" ) );
						speak( txtspk[txtspk.length-1] );
						if (txtspk[txtspk.length-1].length()>0){
							editText.append("\n");
						}
					}
					break;
				case 29://Number
					if (ind_number == 0) {
						//Pass of letters to  numbers
						Resources res = getResources();
						String[] numtext = res.getStringArray(R.array.key_number1);
						for (int in=0;in<numtext.length;in++){
							buttons[in].setText(numtext[in]);
						}
						buttons[29].setText("ABC");
						buttons[35].setBackgroundResource(R.drawable.blackbutton);
						buttons[35].setText("1/2");
						ind_number = 1;
					} else if (ind_number > 0) {
						//Pass of number to letters
						Resources resl = getResources();
						String[] numtextl = resl.getStringArray(R.array.key_letters);
						for (int in=0;in<numtextl.length;in++){
							String txtf=numtextl[in];
							if (Ind_Caps) {
								txtf=txtf.toUpperCase();
							}
							buttons[in].setText(txtf);
						}
						buttons[29].setText("123");
						buttons[35].setText("");
						buttons[35].setBackgroundResource(R.drawable.ic_caplock);
						ind_number = 0;
					}
					
					break;
				case 30://Enter
					editText.append("\n");
					break;
				case 31://Delete word
					String oriContentw = editText.getText().toString();
					int startw = editText.getSelectionStart();
					int endw = editText.getSelectionEnd();
					if (startw >= 0 && endw > 0 && startw != endw) {
						
					} else {
						int x=-1;
						if (startw < oriContentw.length()){
							char[] ascii2 = oriContentw.substring(startw, startw +1).toCharArray();

							if (oriContentw.substring(startw,startw+1).charAt(0) != 32) {
								x=oriContentw.substring(startw).indexOf(" ");
								if (x != -1) {
									startw=startw+x;
								}
							}
						}
						
						int vc=0;
						int starts =startw;
						do {
							x=oriContentw.substring(0,starts).lastIndexOf(" ");
							if (x != -1) {
								if (oriContentw.substring(x).charAt(0) == 32 && oriContentw.substring(x).length() == 1) {
									starts = starts-1;
									vc = vc +1;
									//Continue
								} else {
									vc = 4;
									//break
								}
							} else {
								vc = 4;
							}
						} while (vc <4);
						
						if (x != -1) {
							String finalString= oriContentw.substring(0,x) + oriContentw.substring(startw,oriContentw.length());
							editText.setText(finalString);
							editText.setSelection(x);
						} else if (oriContentw.length()>0){
							String finalString=  oriContentw.substring(startw,oriContentw.length());
							editText.setText(finalString);
							editText.setSelection(0);
						}
					}
					break;
				case 32://space
					AddChar(" ");
					break;
				case 33://left
				    int start = editText.getSelectionStart();
				    int end = editText.getSelectionEnd();
				    if (start >= 0 && end > 0 && start != end) {
				        //editText.setText(editText.getText().replace(start, end, textx));
				    } else {
				        int index = editText.getSelectionStart() >= 0 ? editText.getSelectionStart() : 0;
				        if (index>0){
				        	editText.setSelection(index-1 );
				        }
				    }
					break;
				case 34://right
				    int startr = editText.getSelectionStart();
				    int endr = editText.getSelectionEnd();
				    if (startr >= 0 && endr > 0 && startr != endr) {
				        //editText.setText(editText.getText().replace(start, end, textx));
				    } else {
				        int index = editText.getSelectionStart() >= 0 ? editText.getSelectionStart() : 0;
				        if (index < editText.length()){
				        	editText.setSelection(index+1 );
				        }
				    }
					break;
				case 35://upper
					switch(ind_number) {
					case 0:
						ChangeUpperLower();
						break;
					case 1: //Pass to symbols
						Resources res = getResources();
						String[] numtext = res.getStringArray(R.array.key_number2);
						for (int in=0;in<numtext.length;in++){
							buttons[in].setText(numtext[in]);
						}
						buttons[35].setText("2/2");
						ind_number = 2;
						break;
					case 2: //Pass to numbers
						Resources resn = getResources();
						String[] numtextn = resn.getStringArray(R.array.key_number1);
						for (int in=0;in<numtextn.length;in++){
							buttons[in].setText(numtextn[in]);
						}
						buttons[35].setText("1/2");
						ind_number = 1;
						break;
					}
					
					
					break;
				case 36://back
					if (editText.getText().length()>0){
						String oriContent = editText.getText().toString();
					    int endb = editText.getSelectionEnd();
					    int index = editText.getSelectionStart() >= 0 ? editText.getSelectionStart() : 0;
				        StringBuilder builder = new StringBuilder(oriContent);
				        if (index>0){
				        	builder.delete(index-1, endb);
				        	editText.setText(builder.toString());
				        	editText.setSelection(index-1 );
				        }
					}
					break;
				}

			}
		} catch(Exception e){
			 System.out.println("Exception occurred"+ e.getMessage()); 
		}
	}

	private void ChangeUpperLower(){
		if (Ind_Caps){
			for (int ib = 0; ib < 26; ib++){
				String buttonTextb = buttons[ib].getText().toString();
				buttons[ib].setText(buttonTextb.toLowerCase());
			}
			Ind_Caps=false;
		} else {
			for (int ib = 0; ib < 26; ib++){
				String buttonTextb = buttons[ib].getText().toString();
				buttons[ib].setText(buttonTextb.toUpperCase());
			}
			Ind_Caps=true;
		}
	}
	private void AddChar(String CharAdd){
		String oriContent = editText.getText().toString();
	    int start = editText.getSelectionStart();
	    int end = editText.getSelectionEnd();
	    if (start >= 0 && end > 0 && start != end) {
	        editText.setText(editText.getText().replace(start, end, CharAdd));
	    } else {
	        int index = editText.getSelectionStart() >= 0 ? editText.getSelectionStart() : 0;
	        StringBuilder builder = new StringBuilder(oriContent);
	        builder.insert(index, CharAdd);
	        editText.setText(builder.toString());
	        editText.setSelection(index + CharAdd.length());
	    }
	    String lw=LastWord();
	    pressageWord(lw);
	}
	
	private void speak( String str )
	{
		textToSpeech.speak( str, TextToSpeech.QUEUE_FLUSH, null );
		textToSpeech.setSpeechRate( 0.0f );
		textToSpeech.setPitch( 0.0f );
	}
	
	private String LastWord(){
		String oriContentw = editText.getText().toString();
		String lwrd="";
		String[] multi_line=oriContentw.split("\n");
		if (multi_line.length>0){
			String[] lw=multi_line[multi_line.length-1].split(" ");
			if (lw.length>0){
				lwrd=lw[lw.length-1];
			}
		}
		
		return lwrd;
	}
	
	private void pressageWord(String word){
		if (lastword!=word){
			lastword=word;
			String whereCond="";
			if (word.trim().length()!=0){
				whereCond=" where lower(word) like '" + word.trim() +"%' ";
			}
			Cursor cur= mDb.rawQuery("SELECT DISTINCT ValNG1,lower(word) wordpres FROM ngram1 " + whereCond +" ORDER BY ngram1.ValNG1 ASC limit 15",null);
	    	int iw=0;
			while (cur.moveToNext()) {
				String result = "";
				String result_1 = cur.getString(1);
				if (iw<predw.length){
					boolean exw=false;
					for (int xi=0;xi<=iw;xi++){
						String mv=predw[xi].getText().toString();
						if (mv.compareTo(result_1.trim())==0){
							exw=true;
						}
					}
					if (exw==false) {
						predw[iw].setText(result_1);
						iw+=1;
					}
				}
			}
			cur.close();
		}
	}

	@Override
	protected void onDestroy()
	{
		if ( textToSpeech != null )
		{
			textToSpeech.stop();
			textToSpeech.shutdown();
		}
		super.onDestroy();
	}

	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    
	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		hideSoftKeyboard();
	}
	public void hideSoftKeyboard() {
	    if (getCurrentFocus()!=null) {
	        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
	        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	    }
	}

	private void changebackg_bi(int idb){
		try {
			for (int i = 0; i < buttons.length; i++)
			{
				if (buttons[i].getId() == idb)
				{
					switch (i) {
					case 26://Text to speech
						if (cltbl ==Color.YELLOW){
							rowbts[rowbtsindex].setBackgroundResource(R.drawable.ic_settingsy);
						} else {
							rowbts[rowbtsindex].setBackgroundResource(R.drawable.ic_settings);
						}
						break;
					case 28://Text to speech
						if (cltbl ==Color.YELLOW){
							rowbts[rowbtsindex].setBackgroundResource(R.drawable.ic_microphoney);
						} else {
							rowbts[rowbtsindex].setBackgroundResource(R.drawable.ic_microphone);
						}
						break;
					case 30://Enter
						if (cltbl ==Color.YELLOW){
							rowbts[rowbtsindex].setBackgroundResource(R.drawable.ic_acepty);
						} else {
							rowbts[rowbtsindex].setBackgroundResource(R.drawable.ic_acept);
						}
						break;
					case 31://Delete word
						if (cltbl ==Color.YELLOW){
							rowbts[rowbtsindex].setBackgroundResource(R.drawable.ic_backwordy);
						} else {
							rowbts[rowbtsindex].setBackgroundResource(R.drawable.ic_backword);
						}
						break;
					case 32://space
						if (cltbl ==Color.YELLOW){
							rowbts[rowbtsindex].setBackgroundResource(R.drawable.ic_spacebary);
						} else {
							rowbts[rowbtsindex].setBackgroundResource(R.drawable.ic_spacebar);
						}
						break;
					case 33://left
						if (cltbl ==Color.YELLOW){
							rowbts[rowbtsindex].setBackgroundResource(R.drawable.ic_arrowlefty);
						} else {
							rowbts[rowbtsindex].setBackgroundResource(R.drawable.ic_arrowleft);
						}
						break;
					case 34://right
						if (cltbl ==Color.YELLOW){
							rowbts[rowbtsindex].setBackgroundResource(R.drawable.ic_arrowrighty);
						} else {
							rowbts[rowbtsindex].setBackgroundResource(R.drawable.ic_arrowright);
						}
						break;
					case 35://upper		
						if (cltbl ==Color.YELLOW){
							rowbts[rowbtsindex].setBackgroundResource(R.drawable.ic_caplocky);
						} else {
							rowbts[rowbtsindex].setBackgroundResource(R.drawable.ic_caplock);
						}
						break;
					case 36://back
						if (cltbl ==Color.YELLOW){
							rowbts[rowbtsindex].setBackgroundResource(R.drawable.ic_backy);
						} else {
							rowbts[rowbtsindex].setBackgroundResource(R.drawable.ic_back);
						}
						break;
					}
				}
			}
		} catch(Exception e){
			System.out.println("Exception occurred"+ e.getMessage()); 
		}
		
	}
	
	 @Override
	 public boolean onKeyUp(int keyCode, KeyEvent event) {
		 try {
			 if (keyCode==142) {
				 String edittx;
				 int valcndclr=0;
				 if (colorm=="W") {
					 valcndclr=1;
				 }
				 if (Sel_letter){
					 bts_letter[btletter_index-valcndclr].setTextColor(Color.WHITE);
					 for (int i = 0; i < bts_letter.length; i++)
					 {
						 if (bts_letter[i].getId() == bts_letter[btletter_index-valcndclr].getId())
						 {
							 //Buttonprocess(i);
							 edittx = bts_letter[i].getText().toString();
							 AddChar(edittx);
							 bts_letter[btletter_index].setBackgroundResource(R.drawable.blackbutton);
							 bts_letter[btletter_index].setTextColor(Color.WHITE);
							 Sel_letter=false;
							 cltbl =Color.YELLOW;
							 customDialog.dismiss();
						 }
					 }
					 rowbtsindex=0;
					 lblcycle=0;
				 } else if(Sel_setting){
					 bts_setting[btsetting_index-valcndclr].setTextColor(Color.WHITE);
					 for (int i = 0; i < bts_setting.length; i++)
					 {
						 if (bts_setting[i].getId() == bts_setting[btsetting_index-valcndclr].getId())
						 {
							 //Buttonprocess(i);
							 edittx = bts_letter[i].getText().toString();
							 AddChar(edittx);
							 bts_setting[btsetting_index].setBackgroundResource(R.drawable.blackbutton);
							 bts_setting[btsetting_index].setTextColor(Color.WHITE);
							 Sel_setting=false;
							 cltbl =Color.YELLOW;
							 customDialog.dismiss();
						 }
					 }
					 rowbtsindex=0;
					 lblcycle=0;
				 } else {
					 switch (lblcycle){
						 case 0: //General
							 if (cltbl==Color.WHITE){
								 lblcycle=tblindex+1;
								 tbls[tblindex].setBackground(null);
							 } else {
								 lblcycle=tblindex;
							 }

							 break;
						 case 1: //Predictive
							 //Add to edittext
							 predw[predwindex-valcndclr].setBackground(null);

							 String oriContent = editText.getText().toString();
							 int lsp=oriContent.lastIndexOf(' ');
							 String wrp = predw[predwindex-valcndclr].getText().toString() + " ";
							 if (lsp == -1) {
								 if(oriContent.length()==0) {
									 edittx=wrp;
								 } else {
									 int wi=wrp.indexOf(oriContent);
									 edittx=wrp.substring(wi+1, wrp.length());
								 }
							 } else {
								 oriContent=oriContent.substring(lsp+1);
								 int wi=wrp.indexOf(oriContent);
								 edittx=wrp.substring(wi+1, wrp.length());
							 }

							 AddChar(edittx);
							 predwindex=0;
							 lblcycle=0;
							 break;
						 case 2: //Used
							 //Speak word
							 wordcused[wordcusedindex-valcndclr].setBackground(null);
							 edittx = wordcused[wordcusedindex-valcndclr].getText().toString();
							 speak(edittx);
							 wordcusedindex=0;
							 lblcycle=0;
							 break;
						 case 3: //Row Key
							 rowstb0[rowstb0index-valcndclr].setBackground(null);
							 indexrowb=rowstb0index-valcndclr;
							 rowbts = new Button[rowstb0[rowstb0index-valcndclr].getChildCount()];
							 for (int k=0;k<rowstb0[rowstb0index-valcndclr].getChildCount();k++){
								 View vr = rowstb0[rowstb0index-valcndclr].getChildAt(k);
								 rowbts[k]= (Button) vr;
							 }
							 rowstb0index=0;
							 lblcycle=4;
							 break;
						 case 4: //Button row
							 //Add char to edittext
							 rowbts[rowbtsindex-valcndclr].setTextColor(Color.WHITE);
							 for (int i = 0; i < buttons.length; i++)
							 {
								 if (buttons[i].getId() == rowbts[rowbtsindex-valcndclr].getId())
								 {
									 Buttonprocess(i);
									 if (indexrowb<2){
										 rowbts[rowbtsindex].setBackgroundResource(R.drawable.blackbutton);
										 rowbts[rowbtsindex].setTextColor(Color.WHITE);
									 } else if (indexrowb==2){
										 if (rowbtsindex>0 && rowbtsindex<8){
											 rowbts[rowbtsindex].setBackgroundResource(R.drawable.blackbutton);
											 rowbts[rowbtsindex].setTextColor(Color.WHITE);
										 } else {
											 changebackg_bi(i);
										 }
									 } else {
										 //Black
										 if (rowbtsindex==0 || rowbtsindex==6){
											 rowbts[rowbtsindex].setBackgroundResource(R.drawable.blackbutton);
											 rowbts[rowbtsindex].setTextColor(Color.WHITE);
										 } else {
											 changebackg_bi(i);
										 }

									 }
								 }
							 }
							 rowbtsindex=0;
							 lblcycle=0;
							 break;
					 }
				 }

				tblindex=0;
				cltbl =Color.YELLOW;
			 }
			 
		 } catch(Exception e){

			 System.out.println("Exception occurred"); 
		 }
		 return super.onKeyDown(keyCode, event);
	 }

	 public void ShowDialogOL(){
		 Sel_letter=true;
		 btletter_index=0;
		 cltbl =Color.YELLOW;
		 String lt_type="";
		 customDialog = new Dialog(this);
		 customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 customDialog.setCancelable(false);
		 customDialog.setContentView(R.layout.option);
		 for(int i=0; i<bts_letter.length; i++) {
			 String buttonID = "btl_" + (i);
			 lt_type=items[i];
			 if (i<bts_letter.length-1 && Ind_Caps){
				 lt_type=items[i].toUpperCase();
			 }
			 int resID = customDialog.getContext().getResources().getIdentifier( buttonID, "id",customDialog.getContext().getPackageName()); //getResources().getIdentifier(buttonID, "id", getPackageName());
			 bts_letter[i] = ((Button) customDialog.findViewById(resID));
			 bts_letter[i].setText(lt_type);
			 bts_letter[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 70);
			 bts_letter[i].setOnClickListener(onClickBL);
		 }

		 customDialog.show();
	 }

	private OnClickListener onClickBL = new  OnClickListener(){
		public void onClick(View v){
			for(int i=0; i<bts_letter.length;i++){
				if(i<2) {
					if (bts_letter[i].getId() == v.getId()) {
						String edittx = bts_letter[i].getText().toString();
						AddChar(edittx);
					}
				}
				Sel_letter=false;
				cltbl =Color.YELLOW;
				customDialog.dismiss();
			}
		}
	};

	public void ShowDialogSetting(){
		Sel_setting=true;
		btsetting_index=0;
		cltbl =Color.YELLOW;
		customDialog = new Dialog(this);
		customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		customDialog.setCancelable(false);
		customDialog.setContentView(R.layout.settings);
		for(int i=0; i<bts_setting.length; i++) {
			String buttonID = "btso_" + (i);
			int resID = customDialog.getContext().getResources().getIdentifier( buttonID, "id",customDialog.getContext().getPackageName()); //getResources().getIdentifier(buttonID, "id", getPackageName());
			bts_setting[i] = ((Button) customDialog.findViewById(resID));
			bts_setting[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
			bts_setting[i].setOnClickListener(onClickBO);
		}

		customDialog.show();
	}

	private OnClickListener onClickBO = new  OnClickListener(){
		public void onClick(View v){
        for(int i=0; i<bts_setting.length;i++){
            if (bts_setting[i].getId() == v.getId()) {
                if (i==1){
                    finish();
                    System.exit(0);
                }
            }
            Sel_setting=false;
            cltbl =Color.YELLOW;
            customDialog.dismiss();
        }
		}
	};
}
