package com.a2t.autobpmprompt.app.lib.database;

import android.content.Context;
import android.util.Log;


import com.a2t.autobpmprompt.app.lib.StringUtils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmDriver<T extends RealmObject> {
    private static final String TAG = "REALMDRIVER";
    private Class<T> mClass;
    private int mElementsLimit;
    private RealmConfiguration config;

    public RealmDriver(Class<T> clazz) {
        this.mClass = clazz;
        mElementsLimit = 0;
    }

    /*public RealmDriver(Class<T> clazz, RealmConfiguration config) {
        this.mClass = clazz;
        mElementsLimit = 0;
        this.config = config;
        RealmFactory.setRealmConfiguration(config);
    }*/

    public Realm getRealm() {
        return RealmFactory.getRealm();
        /*if (config != null) {
            return Realm.getInstance(config);
        } else {
            return Realm.getInstance(ctx);
        }*/
        //return Realm.getInstance(ctx);
    }

    public void setConfiguration(RealmConfiguration rc) {
        this.config = rc;
    }

    public void setElementsLimit(int elementsLimit) {
        mElementsLimit = elementsLimit;
    }

    public T getOne(Context ctx, String key, String value) {
        Realm r = getRealm();
        RealmQuery<T> query = r.where(mClass);
        query.equalTo(key, value);
        return query.findFirst();
    }

    public T getOne(Context ctx, String key, short value) {
        Realm r = getRealm();
        RealmQuery<T> query = r.where(mClass);
        query.equalTo(key, value);
        return query.findFirst();
    }

    public T getOne(Context ctx, String key, int value) {
        Realm r = getRealm();
        RealmQuery<T> query = r.where(mClass);
        query.equalTo(key, value);
        return query.findFirst();
    }

    public T getOne(Context ctx, String key, long value) {
        Realm r = getRealm();
        RealmQuery<T> query = r.where(mClass);
        query.equalTo(key, value);
        return query.findFirst();
    }

    public long size(Context ctx) {
        Realm r = getRealm();
        RealmQuery<T> query = r.where(mClass);
        return query.count();
    }

    public RealmResults<T> getByQuery(Context ctx, RealmQuery<T> query) {
        return getByQuery(ctx, query, null, false);
    }

    public RealmResults<T> getByQuery(Context ctx, RealmQuery<T> query, String sortBy, boolean descendent) {
        if (StringUtils.isEmpty(sortBy)) {
            return query.findAll();
        } else {
            RealmResults<T> results = query.findAll();
            results.sort(sortBy, descendent ? Sort.DESCENDING : Sort.ASCENDING);
            return results;
        }
    }

    public T getOneByQuery(Context ctx, RealmQuery<T> query) {
        return query.findFirst();
    }

    public void deleteOne(Context ctx, String key, String value) {
        Realm r = getRealm();
        T obj = r.where(mClass).equalTo(key, value).findFirst();

        if (obj == null) {
            Log.w(TAG, "Trying to remove a non existing Realm");
            return;
        }

        r.beginTransaction();
        obj.deleteFromRealm();
        r.commitTransaction();
    }

    public void deleteOne(Context ctx, String key, int value) {
        Realm r = getRealm();
        T obj = r.where(mClass).equalTo(key, value).findFirst();

        if (obj == null) {
            Log.w(TAG, "Trying to remove a non existing Realm");
            return;
        }

        r.beginTransaction();
        obj.deleteFromRealm();
        r.commitTransaction();
    }

    public void deleteOne(Context ctx, String key, long value) {
        Realm r = getRealm();
        T obj = r.where(mClass).equalTo(key, value).findFirst();

        if (obj == null) {
            Log.w(TAG, "Trying to remove a non existing Realm");
            return;
        }

        r.beginTransaction();
        obj.deleteFromRealm();
        r.commitTransaction();
    }

    public void deleteOne(Context ctx, String key, short value) {
        Realm r = getRealm();
        T obj = r.where(mClass).equalTo(key, value).findFirst();

        if (obj == null) {
            Log.w(TAG, "Trying to remove a non existing Realm");
            return;
        }

        r.beginTransaction();
        obj.deleteFromRealm();
        r.commitTransaction();
    }

    public void deleteByQuery(Context ctx, RealmQuery<T> query) {
        Realm r = getRealm();
        RealmResults<T> list = query.findAll();
        r.beginTransaction();
        for (T result : list) {
            result.deleteFromRealm();
        }
        r.commitTransaction();
    }

    public T insertOne(Context ctx, T object) {
        return insertOne(ctx, object, false);
    }

    public T insertOne(Context ctx, T object, boolean trueInsert) {
        Realm r = getRealm();
        r.beginTransaction();
        T obj;
        if (trueInsert) {
            obj = r.copyToRealm(object);
        } else {
            obj = r.copyToRealmOrUpdate(object);
        }
        r.commitTransaction();

        if (mElementsLimit > 0) {
            RealmResults<T> all = getAll(ctx);
            if (all.size() > mElementsLimit) {
                for (int i = 0; i < mElementsLimit; i++) {
                    r.beginTransaction();
                    all.get(i).deleteFromRealm();
                    r.commitTransaction();
                }
            }
        }

        return obj;
    }

    public void insertAll(Context ctx, List<T> elements) {
        insertAll(ctx, elements, false);
    }

    public void insertAll(Context ctx, List<T> elements, boolean trueInsert) {
        Realm r = getRealm();
        r.beginTransaction();

        if (trueInsert) {
            r.copyToRealm(elements);
        } else {
            r.copyToRealmOrUpdate(elements);
        }
        r.commitTransaction();

        if (mElementsLimit > 0) {
            RealmResults<T> all = getAll(ctx);
            if (all.size() > mElementsLimit) {
                for (int i = 0; i < mElementsLimit; i++) {
                    r.beginTransaction();
                    all.get(i).deleteFromRealm();
                    r.commitTransaction();
                }
            }
        }
    }

    public RealmResults<T> getAll(Context ctx) {
        return getRealm().where(mClass).findAll();
    }

    public RealmResults<T> getAll(Context ctx, String sortBy, boolean descendent) {
        if (StringUtils.isEmpty(sortBy)) {
            return getRealm().where(mClass).findAll();
        } else {
            RealmResults<T> results=  getRealm().where(mClass).findAll();
            results.sort(sortBy, descendent ? Sort.DESCENDING : Sort.ASCENDING);
            return results;
        }
    }

    public void updateOne(Context ctx, T update) {
        Realm r = getRealm();
        r.beginTransaction();
        r.copyToRealmOrUpdate(update);
        r.commitTransaction();

        if (mElementsLimit > 0) {
            RealmResults<T> all = getAll(ctx);
            if (all.size() > mElementsLimit) {
                for (int i = 0; i < mElementsLimit; i++) {
                    r.beginTransaction();
                    all.get(i).deleteFromRealm();
                    r.commitTransaction();
                }
            }
        }
    }

    public void deleteAll(Context ctx) {
        Realm r = getRealm();
        r.beginTransaction();
        r.delete(mClass);
        r.commitTransaction();
    }


}
