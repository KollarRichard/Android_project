package com.example.appka.mtaaaplikacia;

public class MyProperties {
    private static MyProperties mInstance= null;

    public String lastLogin = "";

    protected MyProperties(){}

    public static synchronized MyProperties getInstance(){
        if(null == mInstance){
            mInstance = new MyProperties();
        }
        return mInstance;
    }
}