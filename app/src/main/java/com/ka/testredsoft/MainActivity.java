package com.ka.testredsoft;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ka.testredsoft.adapters.ProdAdapter;
import com.ka.testredsoft.api.ApiFactory;
import com.ka.testredsoft.api.ApiService;
import com.ka.testredsoft.pojo.Datum;
import com.ka.testredsoft.pojo.Example;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewProducts;
    private ProdAdapter adapter;
    private Disposable disposable;
    private CompositeDisposable compositeDisposable;
    private SearchView searchView;
    private EditText editText;
    public List<Datum> prods = new ArrayList<>();
    private int wTime = 250;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerViewProducts = findViewById(R.id.recyclerViewProduct);
        adapter = new ProdAdapter();
        adapter.setProducts(new ArrayList<Datum>());
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProducts.setAdapter(adapter);
        recyclerViewProducts.setScrollX(4);
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        disposable = apiService.getData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Example>() {
                    @Override
                    public void accept(Example example) throws Exception {
                        prods.addAll(example.getData());
                        adapter.setProducts(prods);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "Ошибка получения данных", Toast.LENGTH_SHORT).show();
                    }
                });
        compositeDisposable.add(disposable);
        adapter.setProducts(prods);
        adapter.setOnProductClickListener(new ProdAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(int position, int id, int num) {
                Intent intent = new Intent(getApplicationContext(), ProductCard.class);
                intent.putExtra("numProd", num);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        CharSequence query = "Я ищу";
        searchView = (SearchView) searchItem.getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setQueryHint(query);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {return false;}

            @Override
            public boolean onQueryTextChange(final String newText) {  //Задержка по времени
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                countDownTimer = new CountDownTimer(wTime, 500) {
                    public void onTick(long millisUntilFinished) {
                        Log.d("TIME", "seconds remaining: " + millisUntilFinished / 1000);
                    }
                    public void onFinish() {
                        adapter.getFilter().filter(newText);
                        adapter.getProducts();
                        Log.d("text", newText);
                    }
                };
                countDownTimer.start();

                return false;
            }

        });
        return super.onCreateOptionsMenu(menu);
    }
}
