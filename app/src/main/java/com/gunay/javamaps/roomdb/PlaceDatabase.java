package com.gunay.javamaps.roomdb;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.gunay.javamaps.model.Place;

// database tanımlanır ve kullanılacak entity veya entityler girilir entity'lerde değişiklik yapıldıkça version artırımı gerekebilir
// modelin database'e bağlanması
@Database(entities = {Place.class},version = 1)
// RoomDatabase sınıfından extend alınır
public abstract class PlaceDatabase extends RoomDatabase {
    //Dao'nun database'e bağlanması
    public abstract PlaceDao placeDao();


}
