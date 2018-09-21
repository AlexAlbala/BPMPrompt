package com.a2t.autobpmprompt.app.lib.database;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmFactory {

    static RealmConfiguration cfg;

    public static Realm getRealm() {
        if(cfg != null){
            return Realm.getInstance(cfg);
        } else{
            return Realm.getDefaultInstance();
        }
    }

    public static Realm getDefaultRealm(){
        return Realm.getDefaultInstance();
    }

    /*public static void setContext(Context ctx) {
        cfg = new RealmConfiguration.Builder(ctx).build();
        Realm.setDefaultConfiguration(cfg);
    }*/

    public static void setRealmConfiguration(RealmConfiguration _cfg) {
        cfg = _cfg;
        //Realm.setDefaultConfiguration(cfg);
    }
}
