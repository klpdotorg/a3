/**
 * Copyright 2013 Maarten Pennings
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * If you use this software in a product, an acknowledgment in the product
 * documentation would be appreciated but is not required.
 */

package com.gka.akshara.assesseasy;

import android.inputmethodservice.KeyboardView;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * When an activity hosts a keyboardView, this class allows several EditText's to register for it.
 */
class AssessEasyKeyboard {

    /** A link to the KeyboardView that is used to render this CustomKeyboard. */
    private KeyboardView mKeyboardView;
    /** A link to the activity that hosts the {@link #mKeyboardView}. */
    private Activity     mHostActivity;

    /* The codes assigned for numerical keys 0-9 for different languages:
        1000 - 1009 for Kannada
        2000 - 2009 for Hindi
        3000 - 3009 for English
        4000 - 4009 for Urdu
        5000 - 5009 for Odia
        6000 - 6009 for Telugu
        7000 - 7009 for Gujarati
        8000 - 8009 for Malayalam*/

    /* ODIYA Numerals. Ref:  https://en.wikipedia.org/wiki/Oriya_(Unicode_block)
      {"୦","୧","୨","୩","୪","୫","୬","୭","୮","୯"};
    */
    /* Do not set 'inputType' in the layout xml file to 'number' type as the local numerals would be characters instead of numbers. set only android:inputType="textNoSuggestions" and not android:inputType="textNoSuggestions|number"*/
    public final static String[] keyboardlanguageslist = {"kannada","hindi","english","urdu","odia","telugu","gujarati","malayalam"};
    public final int[] languagestartcodes = {1000,2000,3000,4000,5000,6000,7000,8000};

    public final static String[][] numberstrings = {
        {"0","1","2","3","4","5","6","7","8","9"},
        {"0","1","2","3","4","5","6","7","8","9"},
        {"0","1","2","3","4","5","6","7","8","9"},
        {"0","1","2","3","4","5","6","7","8","9"},
        {"୦","୧","୨","୩","୪","୫","୬","୭","୮","୯"},
        {"0","1","2","3","4","5","6","7","8","9"},
        {"૦","૧","૨","૩","૪","૫","૬","૭","૮","૯"},
        {"൦","൧","൨","൩","൪","൫","൬","൭","൮","൯"}
    };

    public static int getLanguageArrayIndex(String kblanguage) {

        for(int i = 0; i < keyboardlanguageslist.length-1; i++) {
            if(keyboardlanguageslist[i].equals(kblanguage.toLowerCase())) {
                return i;
            }
        }
        return(2); // If no match, return the index of 'english'
    }

    public static String replaceLocalnumeralsToEnglish(String orgstr) {

        String[] splitstr = orgstr.trim().split("");
        String newstr = "";

        int lanindex = getLanguageArrayIndex(globalvault.keyboardlanguage);

        int startcount = 0;

        if(splitstr[0].isEmpty())  // if splitstr[0] is an empty string
            startcount = 1;
        else
            startcount = 0;

        if (MainActivity.debugalerts)
            Log.d("EASYASSESS", "AssessEasyKeyboard. replaceLocalnumeralsToEnglish. startcount:"+startcount+" splitstrlength:"+splitstr.length);

        for(int i=startcount; i < splitstr.length; i++) {

            if(splitstr[i].equals("."))
                newstr += ".";
            else {
                for (int j = 0; j < numberstrings[lanindex].length; j++) {
                    if (splitstr[i].equals(numberstrings[lanindex][j])) {
                        newstr += numberstrings[2][j];
                        break;
                    }
                }
            }
        }
        return newstr;
    }

    public static String replaceEnglishnumeralsToLocal(String orgstr) {

        String[] splitstr = orgstr.trim().split("");
        String newstr = "";

        int lanindex = getLanguageArrayIndex(globalvault.keyboardlanguage);

        int startcount = 0;

        if(splitstr[0].isEmpty())  // if splitstr[0] is an empty string
            startcount = 1;
        else
            startcount = 0;

        if (MainActivity.debugalerts)
            Log.d("EASYASSESS", "AssessEasyKeyboard. replaceEnglishnumeralsToLocal. startcount:"+startcount+" splitstrlength:"+splitstr.length+" splitstr[1]:"+splitstr[1]);

        for(int i=startcount; i < splitstr.length; i++) {

            if(splitstr[i].equals("."))
                newstr += ".";
            else {
                for (int j = 0; j < numberstrings[lanindex].length; j++) {
                    if (splitstr[i].equals(numberstrings[2][j])) {
                        newstr += numberstrings[lanindex][j];
                        break;
                    }
                }
            }
        }
        return newstr;
    }

    /** The key (code) handler. */
    private OnKeyboardActionListener mOnKeyboardActionListener = new OnKeyboardActionListener() {

        public final static int CodeDelete   = -5; // Keyboard.KEYCODE_DELETE
        public final static int CodeCancel   = -3; // Keyboard.KEYCODE_CANCEL
        public final static int CodePrev     = 55000;
        public final static int CodeAllLeft  = 55001;
        public final static int CodeLeft     = 55002;
        public final static int CodeRight    = 55003;
        public final static int CodeAllRight = 55004;
        public final static int CodeNext     = 55005;
        public final static int CodeClear    = 55006;

        public final static int CodeDot   = 158; // Keyboard.KEYCODE_NUMPAD_DOT; added for handling decimal point



        @Override public void onKey(int primaryCode, int[] keyCodes) {
            // NOTE We can say '<Key android:codes="49,50" ... >' in the xml file; all codes come in keyCodes, the first in this list in primaryCode
            // Get the EditText and its Editable

            View focusCurrent = mHostActivity.getWindow().getCurrentFocus();
            if(focusCurrent == null) return;

            // Log.d("EASYASSESS", "AssessEasyKeyboard. Primarycode:"+primaryCode);
            /*
            if( (focusCurrent == null) || (focusCurrent.getClass() != EditText.class) ) { // Class is android.support.v7.widget.AppCompatEditText - suresh
                Log.d("KEY","Inside OnKey. class:"+focusCurrent.getClass());
                return;
            } */

            EditText edittext = (EditText) focusCurrent;
            Editable editable = edittext.getText();
            int start = edittext.getSelectionStart();

            // Apply the key to the edittext
            if( primaryCode==CodeCancel ) {
                hideCustomKeyboard();
            } else if( primaryCode==CodeDelete ) {
                if( editable!=null && start>0 ) editable.delete(start - 1, start);
            } else if( primaryCode==CodeClear ) {
                if( editable!=null ) editable.clear();
            } else if( primaryCode==CodeLeft ) {
                if( start>0 ) edittext.setSelection(start - 1);
            } else if( primaryCode==CodeRight ) {
                if (start < edittext.length()) edittext.setSelection(start + 1);
            } else if( primaryCode==CodeAllLeft ) {
                edittext.setSelection(0);
            } else if( primaryCode==CodeAllRight ) {
                edittext.setSelection(edittext.length());
            } else if( primaryCode==CodePrev ) {
                View focusNew= edittext.focusSearch(View.FOCUS_BACKWARD);
                if( focusNew!=null ) focusNew.requestFocus();
            } else if( primaryCode==CodeNext ) {
                View focusNew= edittext.focusSearch(View.FOCUS_FORWARD);
                if( focusNew!=null ) focusNew.requestFocus();
            } else if( primaryCode==CodeDot ) { // Added for dot key for decimal values
                editable.insert(start, ".");
            } else { // insert character (the number pressed)
                // Log.d("KEY","Inside OnKey case: insert Character. Primary code:"+Integer.toString(primaryCode));
                //editable.insert(start, Character.toString((char) primaryCode));

                // editable.insert(start, Integer.toString(primaryCode));
                int languageindex = (primaryCode / 1000) - 1; // 0 for Kannda, 1 for Hindi, 2 for English etc
                //Log.d("EASYASSESS", "AssessEasyKeyboard. languageindex:"+languageindex);
                int stringindex = primaryCode - languagestartcodes[languageindex]; // 0 - 9
                //Log.d("EASYASSESS", "AssessEasyKeyboard. stringindex:"+stringindex+"char :"+numberstrings[languageindex][stringindex]);
                editable.insert(start,numberstrings[languageindex][stringindex]);
            }
        }

        @Override public void onPress(int arg0) {
        }

        @Override public void onRelease(int primaryCode) {
        }

        @Override public void onText(CharSequence text) {
        }

        @Override public void swipeDown() {
        }

        @Override public void swipeLeft() {
        }

        @Override public void swipeRight() {
        }

        @Override public void swipeUp() {
        }
    };

    /**
     * Create a custom keyboard, that uses the KeyboardView (with resource id <var>viewid</var>) of the <var>host</var> activity,
     * and load the keyboard layout from xml file <var>layoutid</var> (see {@link Keyboard} for description).
     * Note that the <var>host</var> activity must have a <var>KeyboardView</var> in its layout (typically aligned with the bottom of the activity).
     * Note that the keyboard layout xml file may include key codes for navigation; see the constants in this class for their values.
     * Note that to enable EditText's to use this custom keyboard, call the {@link #registerEditText(int)}.
     *
     * @param host The hosting activity.
     * @param viewid The id of the KeyboardView.
     * @param layoutid The id of the xml file containing the keyboard layout.
     */
    public AssessEasyKeyboard(Activity host, int viewid, int layoutid) {
        mHostActivity= host;
        mKeyboardView= (KeyboardView)mHostActivity.findViewById(viewid);
        mKeyboardView.setKeyboard(new Keyboard(mHostActivity, layoutid));
        mKeyboardView.setPreviewEnabled(false); // NOTE Do not show the preview balloons
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        // Hide the standard keyboard initially
        mHostActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /** Returns whether the CustomKeyboard is visible. */
    public boolean isCustomKeyboardVisible() {
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }

    /** Make the CustomKeyboard visible, and hide the system keyboard for view v. */
    public void showCustomKeyboard( View v ) {
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if( v!=null ) ((InputMethodManager)mHostActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /** Make the CustomKeyboard invisible. */
    public void hideCustomKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
    }

    /**
     * Register <var>EditText<var> with resource id <var>resid</var> (on the hosting activity) for using this custom keyboard.
     *
     * @param resid The resource id of the EditText that registers to the custom keyboard.
     */
    public void registerEditText(int resid) {
        // Find the EditText 'resid'
        EditText edittext= (EditText)mHostActivity.findViewById(resid);
        // Make the custom keyboard appear
        edittext.setOnFocusChangeListener(new OnFocusChangeListener() {
            // NOTE By setting the on focus listener, we can show the custom keyboard when the edit box gets focus, but also hide it when the edit box loses focus
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if( hasFocus ) showCustomKeyboard(v); else hideCustomKeyboard();
            }
        });
        edittext.setOnClickListener(new OnClickListener() {
            // NOTE By setting the on click listener, we can show the custom keyboard again, by tapping on an edit box that already had focus (but that had the keyboard hidden).
            @Override public void onClick(View v) {
                showCustomKeyboard(v);
            }
        });
        // Disable standard keyboard hard way
        // NOTE There is also an easy way: 'edittext.setInputType(InputType.TYPE_NULL)' (but you will not have a cursor, and no 'edittext.setCursorVisible(true)' doesn't work )
        edittext.setOnTouchListener(new OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;
                int inType = edittext.getInputType();       // Backup the input type
                edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
                edittext.onTouchEvent(event);               // Call native handler
                edittext.setInputType(inType);              // Restore input type
                return true; // Consume touch event
            }
        });
        // Disable spell check (hex strings look like words to Android)
        edittext.setInputType(edittext.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

}

