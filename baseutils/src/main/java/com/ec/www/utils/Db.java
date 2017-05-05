package com.ec.www.utils;

/**
 * Created by huang on 2016/11/24.
 */

@Deprecated
public class Db {
//
//    private Realm mRealm;
//
//    private Db(Context context, String name) {
//        Realm.init(context);
//        RealmConfiguration config = new RealmConfiguration
//                .Builder()
//                .name(name)
//                .build();
//
//        try {
//            mRealm = Realm.getInstance(config);
//        } catch (RealmMigrationNeededException e){
//            try {
//                Realm.deleteRealm(config);
//                //Realm file has been deleted.
//                mRealm = Realm.getInstance(config);
//            } catch (Exception ex){
//                throw ex;
//                //No Realm file to remove.
//            }
//        }
//    }
//
//    private Db(Context context) {
//        this(context, "moplus");
//    }
//
//    public static Realm getInstance(Context context) {
//        return new Db(context).mRealm;
//    }
//
//    public static Realm getInstance(Context context, String name) {
//        return new Db(context, name).mRealm;
//    }
}
