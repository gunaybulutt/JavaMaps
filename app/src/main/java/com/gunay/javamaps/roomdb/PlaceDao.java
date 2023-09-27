package com.gunay.javamaps.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.gunay.javamaps.model.Place;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

// modeli oluşturduğumuza benzer bir yapıyla Dao oluşturulur
// @Dao dao oluşturur
// Dao interface bir yapıdır (arayüz)
// Dao sql kodlarını daha az kullanarak sql üzerinde işlem yapmamızı kolaylaştıran bir arayüzdür
// ayrıca room kullanmak için oluşturmamız gereken 3 ögeden biridir (database-dao-model)
@Dao
public interface PlaceDao {

    //Sorgu(query) işlemi yapacağımızı belirtir ve dao arayüz olduğu için metodu sadece tanımlarız
    // bu sorgu bizim getAll metodunu kullanaral geriye Place sınıfından oluşan bir List almamızı sağlar
    // List'in içindeki Place'ler ise bizim sorgumuzun ne olduğuna göre şekillenir burada bütün hepsi çağrılmış
    // her geri dönüş List<Place> şeklinde olmak zorunda değil
    //Flowable arka planda işlem yapılacak ve tamamlanınca geriye bişey dönecek (RXjava)
    @Query("SELECT * FROM Place")
    Flowable<List<Place>> getAll();

    //insert işlemi yapacağımızı belirtir ve dao arayüz olduğu için metodu sadece tanımlarız
    //Completable arka planda işlem yapılacak ama işlem tamamlanınca geriye bişey döndürmeyecek (RXjava)
    @Insert
    Completable insert(Place place);

    //delete işlemi yapacağımızı belirtir ve dao arayüz olduğu için metodu sadece tanımlarız
    //Completable arka planda işlem yapılacak ama işlem tamamlanınca geriye bişey döndürmeyecek (RXjava)
    @Delete
    Completable delete(Place place);



    //alternatif bir sorgu kullanımı örneği
    /*
    Bu SQL kodu, "Place" adlı bir tablodan veri çekmeyi amaçlar. "name" sütunu, ":nameinput" değerine eşit olan satırları döndürür.
    Burada ":nameinput" bir parametredir ve bu değer çalıştırma zamanında değiştirilmelidir.
    Örneğin, eğer ":nameinput" parametresine "New York" verildiyse, bu sorgu "Place" tablosundaki "name" sütunu "New York" olan tüm satırları getirecektir.

    @Query("SELECT * FROM Place WHERE name = :nameinput")
    List<Place> getAll(String nameinput);
     */

}
