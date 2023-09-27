package com.gunay.javamaps.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;


//@Entity : room database'e bu sınıfı kullanacağımızı ve model olarak (entity sınıfı olarak) kullanacağımzı belirtir
// yanina tablo ismi kaydetmessek sınıf ismimiz tablo ismi olarak kaydedilir @Entity(tablename = "Place") gibi
@Entity
public class Place implements Serializable {

    // sanki sql kodu yazıyormuşuz gibi primary key'i belirtiyoruz . room bizim için geri kalanını halledicek sadece @ ile belirtmemiz yeterli
    //autoGenerate = true ise bizim için otomatik oluştur demek
    @PrimaryKey(autoGenerate = true)
    public int id;

    // column ismini belirtiyoruz (dikey sütun ismi)
    // veri değişkene tanımlanacak ve @ColumnInfo parantezinde ayarladığımız "name" kısmına yani "name" kolon ismiyle kaydedilecek
    // bizim işimiz bundan sonra kolon adıyla değil değişkenle
    @ColumnInfo(name = "name")
    public String name;

    // column ismini belirtiyoruz (dikey sütun ismi)
    // veri değişkene tanımlanacak ve @ColumnInfo parantezinde ayarladığımız "latitute" kısmına yani "latitute" kolon ismiyle kaydedilecek
    // bizim işimiz bundan sonra kolon adıyla değil değişkenle
    @ColumnInfo(name = "latitute")
    public Double latitute;

    // column ismini belirtiyoruz (dikey sütun ismi)
    // veri değişkene tanımlanacak ve @ColumnInfo parantezinde ayarladığımız "longitude" kısmına yani "longitude" kolon ismiyle kaydedilecek
    // bizim işimiz bundan sonra kolon adıyla değil değişkenle
    @ColumnInfo(name = "longitude")
    public Double longitude;

    // id bilgisi primary key olarak otomatik oluşturulacağı için consturctor ile istemeye gerek yok
    public Place(String name, Double latitute, Double longitude) {
        this.name = name;
        this.latitute = latitute;
        this.longitude = longitude;
    }
}
