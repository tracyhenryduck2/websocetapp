package com.siterwell.demo.common;

public enum ECPreferenceSettings {

    /**
     * Whether is the first use of the application
     *
     */
	
	
    SETTINGS_USERNAME("com.siterwell.demo.username" , ""),
    SETTINGS_PASSWORD("com.siterwell.demo.password" , ""),
    SETTINGS_REMEMBER_PASSWORD("com.siterwell.demo.rememberpassword", Boolean.FALSE),
    SETTINGS_CONFIG_REMEMBER_PASSWORD("com.siterwell.demo.configrememberpassword", Boolean.TRUE),
    SETTINGS_HUAWEI_TOKEN("com.siterwell.demo.huaweitoken","");
    private final String mId;
    private final Object mDefaultValue;

    /**
     * Constructor of <code>CCPPreferenceSettings</code>.
     * @param id
     *            The unique identifier of the setting
     * @param defaultValue
     *            The default value of the setting
     */
    private ECPreferenceSettings(String id, Object defaultValue) {
        this.mId = id;
        this.mDefaultValue = defaultValue;
    }

    /**
     * Method that returns the unique identifier of the setting.
     * @return the mId
     */
    public String getId() {
        return this.mId;
    }

    /**
     * Method that returns the default value of the setting.
     *
     * @return Object The default value of the setting
     */
    public Object getDefaultValue() {
        return this.mDefaultValue;
    }

    /**
     *
     * @param id
     *            The unique identifier
     * @return CCPPreferenceSettings The navigation sort mode
     */
    public static ECPreferenceSettings fromId(String id) {
        ECPreferenceSettings[] values = values();
        int cc = values.length;
        for (int i = 0; i < cc; i++) {
            if (values[i].mId == id) {
                return values[i];
            }
        }
        return null;
    }
}
