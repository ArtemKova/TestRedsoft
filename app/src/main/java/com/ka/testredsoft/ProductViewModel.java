package com.ka.testredsoft;

import android.app.Application;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ka.testredsoft.api.ApiFactory;
import com.ka.testredsoft.api.ApiService;
import com.ka.testredsoft.data.AppDataBase;
import com.ka.testredsoft.pojo.Datum;
import com.ka.testredsoft.pojo.Example;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ProductViewModel extends AndroidViewModel {
    private static AppDataBase db;
    private LiveData<List<Datum>> datum;
    private MutableLiveData<Throwable> errors;
    private CompositeDisposable compositeDisposable;



    public ProductViewModel(@NonNull Application application) {
        super(application);
        db = AppDataBase.getInstance(application);
        datum = db.productDao().getAllProduct();
        errors = new MutableLiveData<>();
    }

    public LiveData<List<Datum>> getDatum() {
        return datum;
    }

    public LiveData<Throwable> getErrors() {
        return errors;
    }

    @SuppressWarnings("unchecked")
    public void insertProduct (List<Datum> datum){
        new InsertProductTask().execute(datum);

    }
    private static class InsertProductTask extends AsyncTask<List<Datum>,Void, Void>{

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<Datum>... lists) {
            if (lists != null && lists.length> 0){
                db.productDao().insertProduct(lists[0]);
            }
            return null;
        }
    }
    private void deleteAllProduct(){
        new DeleteAllProductTask().execute();
    }
    private static class DeleteAllProductTask extends AsyncTask <Void, Void ,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            db.productDao().deleteAllProduct();
            return null;
        }

    }

    public void loadData(){
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();
        compositeDisposable = new CompositeDisposable();
        Disposable disposable = apiService.getData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Example>() {
                    @Override
                    public void accept(Example example) throws Exception {
                        deleteAllProduct();
                        insertProduct(example.getData());

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        errors.setValue(throwable);
                    }
                });
        compositeDisposable.add(disposable);
    }
    public void clearErrors (){
        errors.setValue(null);
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }
}
