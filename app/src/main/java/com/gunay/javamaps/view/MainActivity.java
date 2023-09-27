package com.gunay.javamaps.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.gunay.javamaps.R;
import com.gunay.javamaps.adapter.PlaceAdapter;
import com.gunay.javamaps.databinding.ActivityMainBinding;
import com.gunay.javamaps.model.Place;
import com.gunay.javamaps.roomdb.PlaceDao;
import com.gunay.javamaps.roomdb.PlaceDatabase;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    // database sınıfının çağrımı
    PlaceDatabase db;

    // dao sınıfının çağrımı
    PlaceDao placeDao;

    //CompositeDisposable: kullan at gibi bir anlamı var
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // farklı yerlerde kullanılabilmesi için onCreate altında database ve dao nun initialize edilmesi
        //Places initialize ettimiz database'in adı
        // MapsActivity.this ile getApplicationContext() benzer şeyler ve burada birbirleri yerine kullanılabilir
        // allowMainThreadQueries() = main thread üzerinde işlem yapılmasına izin verir
        // db = Room.databaseBuilder(getApplicationContext(),PlaceDatabase.class,"Places").allowMainThreadQueries().build();
        db = Room.databaseBuilder(getApplicationContext(),PlaceDatabase.class,"Places").build();
        //Database'e dao'yu bağladığımız için artık dao'yu database üzerinden initialize ediyoruz
        placeDao = db.placeDao();

        compositeDisposable.add(placeDao.getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(MainActivity.this::handleResponsible));

    }

    // getAll metodunda Flowable kullanıldığı için bu metodun List<Place> placeList alması gerekir
    private void handleResponsible(List<Place> placeList){
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        PlaceAdapter placeAdapter = new PlaceAdapter(placeList);
        binding.recyclerView.setAdapter(placeAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //xml ile kod u bağlar
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.recycler_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_place){
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // yaptımız işlemle flowble single observe gibi bir çöp torbasına yani compositeDisposable  --> devamı alt satır
        // içine konup on destroy metodu sırasında .clear ile yokedilebilir ve hafızada yer tutmaz
        compositeDisposable.clear();
    }
}