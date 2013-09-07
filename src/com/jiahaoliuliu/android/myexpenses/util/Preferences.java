package com.jiahaoliuliu.android.myexpenses.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class utilized to save the user elemental data persistently.
 */
public class Preferences {

	// The id of the booleans
	// Update the method clearUserData when modified
	public enum BooleanId {
		ALREADY_STARTED, SHOWN_ADD_NEW_EXPENSE_AT_BEGINNING,
		
		// The default value of the boolean id
		DEFAULT_BOOLEAN_ID;
		
		public static BooleanId toBooleanId(String booleanId) {
			try {
				return valueOf(booleanId);
			} catch (Exception ex) {
				return DEFAULT_BOOLEAN_ID;
			}
		}
	}
	
	// The id of the strings
	// Update the method clearUserData when modified
	public enum StringId {
		// The default value of the string id
		DEFAULT_STRING_ID;
		
		public static StringId toStringId(String stringId) {
			try {
				return valueOf(stringId);
			} catch (Exception ex) {
				return DEFAULT_STRING_ID;
			}
		}
	}

	public enum IntId {
		// The default id
		DEFAULT_INT_ID;
		public static IntId toIntId(String intId) {
			try {
				return valueOf(intId);
			} catch (Exception ex) {
				return DEFAULT_INT_ID;
			}
		}
	}

	public enum DoubleId {
		// The default id
		DEFAULT_DOUBLE_ID;
		
		public static DoubleId toDoubleId(String doubleId) {
			try {
				return valueOf(doubleId);
			} catch (Exception ex) {
				return DEFAULT_DOUBLE_ID;
			}
		}
	}
	
	// The id of the long
	// It is used mainly to store the date
	public enum LongId {
		// The default value of the boolean id
		DEFAULT_LONG_ID;
		
		public static LongId toLongId(String longId) {
			try {
				return valueOf(longId);
			} catch (Exception ex) {
				return DEFAULT_LONG_ID;
			}
		}
		
	}
	
	// The id of the list Strings
	// Update the method clearUserData when modified
	public enum ListStringId {
		// The default value of the list string id
		DEFAULT_LIST_STRING_ID;
		public static ListStringId toListStringId(String listStringId) {
			try {
				return valueOf(listStringId);
			} catch (Exception ex) {
				return DEFAULT_LIST_STRING_ID;
			}
		}
	}

	public enum HashMapListStringId {
		DEFAULT_HASH_MAP_LIST_STRING_ID;
		
		public static HashMapListStringId toHashMapListStringId(String hashMapListStringId) {
			try {
				return valueOf(hashMapListStringId);
			} catch (Exception ex) {
				return DEFAULT_HASH_MAP_LIST_STRING_ID;
			}
		}
	}

    /**
     * The tag utilized for the log.
     */
    private static final String LOG_TAG = Preferences.class.getSimpleName();

    /**
     * The name of the file utilized to store the data.
     */
    private static final String FILE_NAME = "MyExpenses.preferences";

    //The default values
    private static final boolean DEFAULT_BOOLEAN = false;
    private static final String DEFAULT_STRING = null;
    
    // It doesn't matter what value has been set, it will never be used
    private static final Integer DEFAULT_INT = -1;

    // It doesn't matter what value has been set, it will never be used
    // Because the double is saved as long.
    private static final Double DEFAULT_DOULBE = -1.0;

    // It doesn't matter what value has been set, it will never be used.
    private static final Long DEFAULT_LONG = Long.valueOf(-1);

    // The default data for static set
    private static final HashSet<String> DEFAULT_HASH_SET = new HashSet<String>();
    
    private static final List<String> DEFAULT_LIST_STRING = new ArrayList<String>();

    /**
     * The context passed by any Android's component.
     */
    private final Context context;

    /**
     * The shared preferences to save/restore the data.
     */
    private final SharedPreferences sharedPreferences;

    /**
     * The editor to save the data.
     */
    private final SharedPreferences.Editor editor;

    /**
     * The main constructor.
     * @param context The context passed by any Android's component.
     */
    public Preferences(Context context) {
        this.context = context;
        
        // The user shared preferences
        sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    //=========================================== public methods ==============================
    // Boolean
    /**
     * Get the data from shared preference
     * @param booleanId The id of the data to get
     * @return          The data if it has been saved
     *                  false otherwise
     */
    public boolean getBoolean(BooleanId booleanId) {
    	boolean bool = sharedPreferences.getBoolean(booleanId.toString(), DEFAULT_BOOLEAN);
    	return bool;
    	
    }
    
    public void setBoolean(BooleanId booleanId, boolean bool) {
    	// The data will be set if it is not the default one
    	if (booleanId != BooleanId.DEFAULT_BOOLEAN_ID) {
    		editor.putBoolean(booleanId.toString(), bool);
    		editor.commit();
    	}
    }
    
    // String
    public String getString(StringId stringId) {
    	String string = sharedPreferences.getString(stringId.toString(), DEFAULT_STRING);
    	return string;
    	
    }
    
    public void setString(StringId stringId, String string) {
    	// The data will be set if the id is not the default one
    	if (stringId != StringId.DEFAULT_STRING_ID) {
    		editor.putString(stringId.toString(), string);
    		editor.commit();
    	}
    }

    // Integer
    /**
     * Get the data in the shared preferences. If it is not set, return null
     * @param intId The id of the data saved
     * @return      The data if it has been saved
     *              null otherwise
     */
    public Integer getInt(IntId intId) {
    	if (sharedPreferences.contains(intId.toString())) {
    		return sharedPreferences.getInt(intId.toString(), DEFAULT_INT); 
    	} else {
    		return null;
    	}
    }

    public void setInt(IntId intId, Integer integer) {
    	// The data will be set if the id is not the default one
    	if (intId != IntId.DEFAULT_INT_ID) {
    		editor.putInt(intId.toString(), integer);
    		editor.commit();
    	}
    }

    // Double
    /**
     * Get the data in the shared preferences. If it is not set, return null
     * @param doubleId The id of the data saved
     * @return      The data if it has been saved
     *              null otherwise
     */
    public Double getDouble(DoubleId doubleId) {
    	if (sharedPreferences.contains(doubleId.toString())) {
    		return
    			Double.longBitsToDouble(
    					sharedPreferences.getLong(doubleId.toString(),
    					DEFAULT_LONG));
    	} else {
    		return null;
    	}
    }

    public void setDouble(DoubleId doubleId, double doubleData) {
    	// The data will be set if the id is not the default one
    	if (doubleId != DoubleId.DEFAULT_DOUBLE_ID) {
    		editor.putLong(doubleId.toString(), Double.doubleToRawLongBits(doubleData));
    		editor.commit();
    	}
    }

    // Long
    /**
     * Get the data saved in the shared preferences.
     * @param longId The id of the data saved
     * @return       The data if it has been set
     *               null otherwise
     */
    public Long getLong(LongId longId) {
    	if (sharedPreferences.contains(longId.toString())) {
    		return sharedPreferences.getLong(longId.toString(), DEFAULT_LONG);
    	} else {
    		return null;
    	}
    }
    
    public void setLong(LongId longId, Long longData) {
    	// The data will be set if it is not the default one
    	if (longId != LongId.DEFAULT_LONG_ID) {
    		editor.putLong(longId.toString(), longData);
    		editor.commit();
    	}
    }

    //List String
    /**
     * Get the data saved in the shared preferences.
     * @param listStringId The id of the data saved
     * @return             The data if it has been set
     *                     Empty list otherwise
     */
    public List<String> getListString(ListStringId listStringId) {
    	HashSet<String> hashSet =
        			(HashSet<String>) sharedPreferences.getStringSet(
        					listStringId.toString(), DEFAULT_HASH_SET);
        return new ArrayList<String>(hashSet);
    }
    
    public void setListString(ListStringId listStringId, List<String> listString) {
    	// The data will be set if the id is not the defaul tone
    	if (listStringId != ListStringId.DEFAULT_LIST_STRING_ID) {
	    	HashSet<String> data = new HashSet<String>(listString);
	    	editor.putStringSet(listStringId.toString(), data);
	    	editor.commit();
    	}
    }

	// HashMap of List of strings
	public HashMap<String, ArrayList<String>> getHashMapListString(HashMapListStringId hashMapListStringId) {
		HashMap<String, ArrayList<String>> hashMapListString = new HashMap<String, ArrayList<String>>();
		Set<String> keys = sharedPreferences.getStringSet(hashMapListStringId.toString(), DEFAULT_HASH_SET);
		for (String key : keys) {
			ArrayList<String> content =
					new ArrayList<String>(
							(HashSet<String>)sharedPreferences.getStringSet(hashMapListStringId.toString() + "_" + key, DEFAULT_HASH_SET));
			hashMapListString.put(key, content);
		}

		return hashMapListString;
	}

	public void setHashMapListString(
			HashMapListStringId hashMapListStringId,
			HashMap<String, ArrayList<String>> hashMap) {
		if (hashMapListStringId != HashMapListStringId.DEFAULT_HASH_MAP_LIST_STRING_ID) {

			// Clear all the previous data
			removeHashMapListString(hashMapListStringId);
			
			// If the content is null, do not set the data
			if (hashMap.isEmpty()) {
				return;
			}

			Set<String> keys = hashMap.keySet();
			editor.putStringSet(hashMapListStringId.toString(), keys);
			for (String key: keys) {
				editor.putStringSet(
						hashMapListStringId.toString() + "_" + key,
						new HashSet<String>(hashMap.get(key)));
			}
			editor.commit();
		}
	}

	public void removeHashMapListString(HashMapListStringId hashMapListStringId) {
		// Clear the shared preferences for all the keys which starts with the hasMapListStringId
		editor.remove(hashMapListStringId.toString());
		Map<String, ?> preferencesKeys = sharedPreferences.getAll();
		for (String key: preferencesKeys.keySet()) {
			if (key.startsWith(hashMapListStringId.toString())) {
				editor.remove(key);
			}
		}
		editor.commit();
	}

	public boolean isHashMapListStringSaved(HashMapListStringId hashMapListStringId) {
		return sharedPreferences.contains(hashMapListStringId.toString());
	}

    /**
     * Remove all the content of the shared preferences
     */
    public void clearAll() {
    	editor.clear();
    	editor.commit();
    }
}
