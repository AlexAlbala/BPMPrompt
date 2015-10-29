package com.a2t.a2tlib.bbdd;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class RealmDriver<T extends RealmObject> {
    private Class<T> mClass;

    public RealmDriver(Class<T> clazz) {
        this.mClass = clazz;
    }

    private Realm getRealm(Context ctx) {
        return Realm.getInstance(ctx);
    }

    public T getOne(Context ctx, String key, String value) {
        Realm r = getRealm(ctx);
        RealmQuery<T> query = r.where(mClass);
        query.equalTo(key, value);
        return query.findFirst();
    }

    public T getOne(Context ctx, String key, short value) {
        Realm r = getRealm(ctx);
        RealmQuery<T> query = r.where(mClass);
        query.equalTo(key, value);
        return query.findFirst();
    }

    public T getOne(Context ctx, String key, int value) {
        Realm r = getRealm(ctx);
        RealmQuery<T> query = r.where(mClass);
        query.equalTo(key, value);
        return query.findFirst();
    }

    public T getOne(Context ctx, String key, long value) {
        Realm r = getRealm(ctx);
        RealmQuery<T> query = r.where(mClass);
        query.equalTo(key, value);
        return query.findFirst();
    }

    public RealmResults<T> getByQuery(Context ctx, RealmQuery<T> query) {
        return query.findAll();
    }

    public T getOneByQuery(Context ctx, RealmQuery<T> query) {
        return query.findFirst();
    }

    public void deleteOne(Context ctx, String key, String value) {
        Realm r = getRealm(ctx);
        T obj = r.where(mClass).equalTo(key, value).findFirst();
        r.beginTransaction();
        obj.removeFromRealm();
        r.commitTransaction();
    }

    public void deleteOne(Context ctx, String key, int value) {
        Realm r = getRealm(ctx);
        T obj = r.where(mClass).equalTo(key, value).findFirst();
        r.beginTransaction();
        obj.removeFromRealm();
        r.commitTransaction();
    }

    public void deleteOne(Context ctx, String key, long value) {
        Realm r = getRealm(ctx);
        T obj = r.where(mClass).equalTo(key, value).findFirst();
        r.beginTransaction();
        obj.removeFromRealm();
        r.commitTransaction();
    }

    public void deleteOne(Context ctx, String key, short value) {
        Realm r = getRealm(ctx);
        T obj = r.where(mClass).equalTo(key, value).findFirst();
        r.beginTransaction();
        obj.removeFromRealm();
        r.commitTransaction();
    }

    public void deleteByQuery(Context ctx, RealmQuery<T> query) {
        Realm r = getRealm(ctx);
        RealmResults<T> list = query.findAll();
        r.beginTransaction();
        for (T result : list) {
            result.removeFromRealm();
        }
        r.commitTransaction();
    }

    public void insertOne(Context ctx, T object) {
        Realm r = getRealm(ctx);
        r.beginTransaction();
        r.copyToRealm(object);
        r.commitTransaction();
    }

    public RealmResults<T> getAll(Context ctx) {
        return getRealm(ctx).allObjects(mClass);
    }

    public void updateOne(Context ctx, T update) {
        Realm r = getRealm(ctx);
        r.beginTransaction();
        r.copyToRealmOrUpdate(update);
        r.commitTransaction();
    }
}
