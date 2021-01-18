package com.ka.testredsoft;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ka.testredsoft.adapters.ProdAdapter;
import com.ka.testredsoft.api.ApiFactory;
import com.ka.testredsoft.api.ApiService;
import com.ka.testredsoft.pojo.Datum;
import com.ka.testredsoft.pojo.Example;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private RecyclerView recyclerViewProducts;
    private ProdAdapter adapter;
    private Disposable disposable;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerViewProducts = findViewById(R.id.recyclerViewProduct);
        adapter = new ProdAdapter();
        adapter.setProducts(new ArrayList<Datum>());

        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProducts.setAdapter(adapter);
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        disposable = apiService.getData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Example>() {
                    @Override
                    public void accept(Example example) throws Exception {
                        adapter.setProducts(example.getData());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "Ошибка получения данных", Toast.LENGTH_SHORT).show();

                    }
                });
        compositeDisposable.add(disposable);
        adapter.setOnProductClickListener(new ProdAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(int position, int id, int num) {



                Intent intent = new Intent(getApplicationContext(),ProductCard.class);

                        intent.putExtra("numProd", num);
                        intent.putExtra("id",id);
                       startActivity(intent);
            }
        });

    }

    @Override
    protected void onDestroy() {
        if (compositeDisposable!=null){
            compositeDisposable.dispose();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}