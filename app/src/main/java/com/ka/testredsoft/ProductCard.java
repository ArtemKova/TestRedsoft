package com.ka.testredsoft;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ka.testredsoft.api.ApiFactory;
import com.ka.testredsoft.api.ApiProdCard;
import com.ka.testredsoft.pojo.ExampleCard;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ProductCard extends AppCompatActivity  {
    private int a;
    private int id;
    private TextView textViewNameProductCard;
    private TextView textViewManufacturerCard;
    private TextView textViewLatinCard;
    private TextView textViewCategiryFirstCard;
    private TextView textViewCategirySecondCard;
    private TextView textViewCategiryThirdCard;
    private TextView textViewCostCard;
    private ImageView imageViewProductCard;
    private FloatingActionButton buttonBasketCard;
    private TextView buttonPlusCard;
    private TextView buttonMinCard;
    private TextView textViewNumCard;
    private CompositeDisposable compositeDisposable;
    private Disposable disposable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_card);
        a = getIntent().getIntExtra("numProd", 0);
        id = getIntent().getIntExtra("id", 122);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);



        textViewNameProductCard = findViewById(R.id.textViewNameProductCard);
        textViewManufacturerCard = findViewById(R.id.textViewManufacturerCard);
        textViewLatinCard = findViewById(R.id.textViewLatinCard);
        textViewCategiryFirstCard = findViewById(R.id.textViewCategoryFirstCard);
        textViewCategirySecondCard = findViewById(R.id.textViewCategorySecondCard);
        textViewCategiryThirdCard = findViewById(R.id.textViewCategoryThirdCard);
        textViewCostCard = findViewById(R.id.textViewCostCard);
        imageViewProductCard = findViewById(R.id.imageViewProductCard);
        buttonBasketCard = findViewById(R.id.floatingActionButtonBasketCard);
        buttonPlusCard = findViewById(R.id.buttonPlusCard);
        buttonMinCard = findViewById(R.id.buttonMinCard);
        textViewNumCard = findViewById(R.id.textViewNumCard);
        textViewNumCard.setText("" + a);
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiProdCard apiProdCard = (ApiProdCard) apiFactory.getApiProdCard();
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        disposable = apiProdCard.getData(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ExampleCard>() {
                    @Override
                    public void accept(ExampleCard exampleCard) throws Exception {

                        String title = exampleCard.getData().getTitle();
                        textViewNameProductCard.setText(title);
                        setTitle(title);
                        textViewManufacturerCard.setText(exampleCard.getData().getProducer());
                        textViewLatinCard.setText(exampleCard.getData().getShortDescription());
                        textViewCostCard.setText(String.format("%s Р", exampleCard.getData().getPrice()));
                        switch (exampleCard.getData().getCategories().size()) {
                            case 0:
                                textViewCategiryFirstCard.setText("Нет категорий");
                                break;
                            case 1:
                                textViewCategiryFirstCard.setText(exampleCard.getData().getCategories().get(0).getTitle());
                                break;
                            case 2:
                                textViewCategiryFirstCard.setText(exampleCard.getData().getCategories().get(0).getTitle());
                                textViewCategirySecondCard.setText(exampleCard.getData().getCategories().get(1).getTitle());
                                break;
                            case 3:
                                textViewCategiryFirstCard.setText(exampleCard.getData().getCategories().get(0).getTitle());
                                textViewCategirySecondCard.setText(exampleCard.getData().getCategories().get(1).getTitle());
                                textViewCategiryThirdCard.setText(exampleCard.getData().getCategories().get(2).getTitle());

                                break;

                        }
                        DownloadImage task = new DownloadImage();
                        String url = exampleCard.getData().getImageUrl();

                        Bitmap bitmap = null;
                        try {
                            bitmap = task.execute(url).get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
//        imageView.setImageBitmap(bitmap);
//        if (bitmap == null){holder.imageViewProduct.setImageResource(R.drawable.net_foto);
//        }else{
                        imageViewProductCard.setImageBitmap(bitmap);


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(disposable);
        if (a > 0) {
            buttonBasketCard.setVisibility(View.INVISIBLE);
            buttonPlusCard.setVisibility(View.VISIBLE);
            buttonMinCard.setVisibility(View.VISIBLE);
            textViewNumCard.setVisibility(View.VISIBLE);
        }

    }

    public void onClickAddProduct(View view) {
        a = a + 1;
        textViewNumCard.setText("" + a);
        buttonBasketCard.setVisibility(View.INVISIBLE);
        buttonPlusCard.setVisibility(View.VISIBLE);
        buttonMinCard.setVisibility(View.VISIBLE);
        textViewNumCard.setVisibility(View.VISIBLE);

    }

    public void onClickPlusButton(View view) {
        a = a + 1;
        textViewNumCard.setText("" + a);
    }

    public void onClickMinButton(View view) {
        a = a - 1;
        textViewNumCard.setText("" + a);
        if (a == 0) {
            buttonBasketCard.setVisibility(View.VISIBLE);
            buttonPlusCard.setVisibility(View.INVISIBLE);
            buttonMinCard.setVisibility(View.INVISIBLE);
            textViewNumCard.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        super.onDestroy();
    }

    private static class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (MalformedURLException e) {

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
    }


}