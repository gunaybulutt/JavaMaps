package com.gunay.javamaps.view;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.gunay.javamaps.R;
import com.gunay.javamaps.databinding.ActivityMapsBinding;
import com.gunay.javamaps.model.Place;
import com.gunay.javamaps.roomdb.PlaceDao;
import com.gunay.javamaps.roomdb.PlaceDatabase;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    ActivityResultLauncher<String> permissionLauncher;
    LocationManager locationManager;
    LocationListener locationListener;
    boolean one = false;

    // database sınıfının çağrımı
    PlaceDatabase db;

    // dao sınıfının çağrımı
    PlaceDao placeDao;

    Double selectedLatitute;
    Double selectedLongitude;
    Place selectedPlace;

    //CompositeDisposable: kullan at gibi bir anlamı var
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        registerLuncher();

        // farklı yerlerde kullanılabilmesi için onCreate altında database ve dao nun initialize edilmesi
        //Places initialize ettimiz database'in adı
        // MapsActivity.this ile getApplicationContext() benzer şeyler ve burada birbirleri yerine kullanılabilir
        // allowMainThreadQueries() = main thread üzerinde işlem yapılmasına izin verir
        // db = Room.databaseBuilder(getApplicationContext(),PlaceDatabase.class,"Places").allowMainThreadQueries().build();
        db = Room.databaseBuilder(getApplicationContext(),PlaceDatabase.class,"Places").build();
        //Database'e dao'yu bağladığımız için artık dao'yu database üzerinden initialize ediyoruz
        placeDao = db.placeDao();

        selectedLatitute = 0.0;
        selectedLongitude = 0.0;
        binding.saveButton.setEnabled(false);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //uzun tıklama dinleyicisi dinleyiciyi ise MapsActivity sınıfına arayüz olarak bağladık o nedenle this yazdık
        // kısaca oluşturduğumuz oogleMap.OnMapLongClickListener arayüzünü ve override ettiğimiz onMapLongClick methodunu güncel haritada kullanıcağımızı burada belirtiyoruz
        mMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();
        String infox = intent.getStringExtra("info");

        if (infox.equals("new")){

            binding.saveButton.setVisibility(View.VISIBLE);
            // invisibledeki gibi sadece görünümü değil tamamen yok oluyo ve sayfa açılırken layout o yokmuş gibi görünümleri konumlandırıyo
            binding.deleteButton.setVisibility(View.GONE);

            //android işletim sisteminin konum servislerine erşim sağlar
            //konum servisleri kullanılarak kullanı konumu ile ilgili işlemler yapılabilir
            //this kullanmadanda yazılabilir
            //System services sadece konumla ilgili değil camera gibi farklı servislerde olabilir
            // konumla ilgili işlemlerin yapılabilmesi için 2 tane sınıfa ihtiyaç duyulur biri locationManager diğeri locationListener
            //casting -- şu tip değer döneceğine eminim // gelen objeyi parantez içinde yazılan türde kaydet
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            //bu bir arayüz (interface) dir
            //locationManager'dan konumun değiştigine dair uyarıları alabildiğimiz bir arayüz
            //sadece konum değişme uyarısı değil başka verilerde alınıp bunun üzerinden işlem yapılabilir
            //kısacası managerın (bütün işlem burada dönüyo) verdiği mesajları alıp kullanabilmemizi bize listener sağlar
            //LatLng'dan daha fazla bilgi alınıp kullanılabilir
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    //System.out.println("Location : " + location);
                    //uygulama ilk acıldığında aktif konuma 1 kereligine gelinmesini saglayan kod
                    if (one == false){
                        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,10));
                        one = true;
                    }



                }

                //listener içinde farklı metodlar ovverride edilebilir
                //bu method tanımlanmadığında bazı sürümlerde hata oluşabiliyor kullanmasan bile boş tanımla
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    LocationListener.super.onStatusChanged(provider, status, extras);
                }
            };

            // izin kontrolü
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                //request permission
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                    //view içinde değilsen binding.getRoot kullan
                    Snackbar.make(binding.getRoot(),"Permission needed for maps",Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //request permission
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                        }
                    }).show();
                }else {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

                }
            }else{
                // 0,0 olan kısımlar sırasıyla kaç salisede ve kaç metrede bir konum güncellemesi alınacağını belirtir burada yapacağın uygulamaya göre değişir
                //0,0 olması nedeniyle listener üzerine overrride ettiğimiz onLocationChanged metodu sürekli çalıştırılarak izinin verilmesinden itibaren sürekli belirrtimiz gibi konumu bastırır
                // yukarıda tanımladığımız manager ve listener burada kullanılır
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                // son konumu almak için bir location objesi tanımlanır ve gps_provider'in son konum bilgisi objenin içine tanımlanır
                // anlık konum bulunmadan önce uygulama ilk açıldığında son bilinen konum gösterimi yapmak için (opsiyonel bişey)
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLocation != null){
                    LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,10));
                }
                // bulunduğun konumun üzerine mavi nokta koyar
                //bu işlemi özellikle konum izini alındıktan sonra yapmak için else altına yazıldı
                mMap.setMyLocationEnabled(true);
            }



            //Lat --> latitute -enlem
            // lon --> longitute - boylam
            // Add a marker in Sydney and move the camera
            // sydney'in enlem ve boylamının ayarlanması
            //LatLng sydney = new LatLng(-34, 151);
            // marker yani sydney üzerinde kırmızı nokta oluşturulması
            //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            //harita baslatıldıgında sydney i ortalayarak baslaması
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
/*
        //51.5285262,-0.2664023
        LatLng london = new LatLng(51.50575328866147, -0.07540179769289283);
        mMap.addMarker(new MarkerOptions().position(london).title("Marker in london"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(london));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(london,10));
*/


        }else{

            mMap.clear();
            selectedPlace = (Place) intent.getSerializableExtra("place");
            LatLng latLng = new LatLng(selectedPlace.latitute,selectedPlace.longitude);
            mMap.addMarker(new MarkerOptions().position(latLng).title(selectedPlace.name));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));

            binding.placeNameText.setText(selectedPlace.name);
            binding.saveButton.setVisibility(View.GONE);
            binding.deleteButton.setVisibility(View.VISIBLE);

        }


    }

    private void registerLuncher(){
        permissionLauncher= registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    //permission grnted
                    if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // konumun alınması işlemi
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                        // son konumu almak için bir location objesi tanımlanır ve gps_provider'in son konum bilgisi objenin içine tanımlanır
                        // anlık konum bulunmadan önce uygulama ilk açıldığında son bilinen konum gösterimi yapmak için (opsiyonel bişey)
                        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (lastLocation != null) {
                            LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 10));
                        }
                    }
                }else{
                    //permission denied
                    Toast.makeText(MapsActivity.this,"Permission needed !",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    // haritaya uzun tıklandığında yapılacak şey override edilir
    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        //önceden marker oluşturulduysa sadece tek marker kalması için harita temizlenir
        mMap.clear();
        //uzun tıklanıldıgında marker ekler
        mMap.addMarker(new MarkerOptions().position(latLng));

        selectedLatitute = latLng.latitude;
        selectedLongitude = latLng.longitude;
        binding.saveButton.setEnabled(true);
    }

    public void save(View view){
        Place place = new Place(binding.placeNameText.getText().toString(),selectedLatitute,selectedLongitude);

        //threading -> Main (UI), default (CPU Intensive), IO (network, database)

        //kolay yol ama daha az verimli
        //placeDao.insert(place).subscribeOn(Schedulers.io()).subscribe();

        //daha karmaşık ama verimli yol -- subscribeOn diyip işlemin yapıldığı yeri  observeOn diyip ise sonucun kullanılacağı yeri belirtiriz
        //.subscribe() işlemi başltır --- işlem bittikten sonra yapılacak şeyi contex belirttikten sonra :: ile method olarak verebiliriz MapsActivity.this::handleResponse --> ,
        // ama burada fark metodu yazarken parantezlerini koyma  çünkü metodu çalıştır değil referans veriyoruz çalıştırma işini .subscribe() yapıcak
        // obsserveOn kısmını koymasakta çalışacaktır opsionel
        compositeDisposable.add(placeDao.insert(place).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(MapsActivity.this::handleResponse));
    }

    private void handleResponse(){
        Intent intent = new Intent(MapsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void delete(View view){
        if (selectedPlace != null){
            compositeDisposable.add(placeDao.delete(selectedPlace).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(MapsActivity.this::handleResponse));
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // yaptımız işlemle flowble single observe gibi bir çöp torbasına yani compositeDisposable  --> devamı alt satır
        // içine konup on destroy metodu sırasında .clear ile yokedilebilir ve hafızada yer tutmaz
        compositeDisposable.clear();
    }
}