package com.ka.testredsoft.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ka.testredsoft.R;
import com.ka.testredsoft.pojo.Datum;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ProdAdapter extends RecyclerView.Adapter<ProdAdapter.ProdViewHolder> {
    private List<Datum> products;
    public OnProductClickListener onProductClickListener;
    public Context context;
    final int[] a = {0};

    public List<Datum> getProducts() {
        return products;
    }

    public void setProducts(List<Datum> products) {
        this.products = products;
        notifyDataSetChanged();
    }


    public interface OnProductClickListener {
        void onProductClick(int position, int id, int num);
    }

    public void setOnProductClickListener(OnProductClickListener onProductClickListener) {
        this.onProductClickListener = onProductClickListener;
    }

    @NonNull
    @Override
    public ProdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProdViewHolder holder, final int position) {
        final Datum product = products.get(position);
        switch (product.getCategories().size()) {
            case 0:
                holder.textViewCategiryFirst.setText("Нет категорий");
                break;
            case 1:
                holder.textViewCategiryFirst.setText(product.getCategories().get(0).getTitle());
                break;
            case 2:
                holder.textViewCategiryFirst.setText(product.getCategories().get(0).getTitle());
                holder.textViewCategirySecond.setText(product.getCategories().get(1).getTitle());
                break;
            case 3:
                holder.textViewCategiryFirst.setText(product.getCategories().get(0).getTitle());
                holder.textViewCategirySecond.setText(product.getCategories().get(1).getTitle());
                holder.textViewCategiryThird.setText(product.getCategories().get(2).getTitle());
                break;
        }
        holder.textViewNameProduct.setText(product.getTitle());
        holder.textViewNameProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = product.getId();
            }
        });
        holder.textViewManufacturer.setText(product.getProducer());
        holder.textViewLatin.setText(product.getShortDescription());
        holder.textViewCost.setText(String.format("%s Р", product.getPrice()));
        holder.buttonBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a[0] = a[0] + 1;
                holder.textViewNum.setText("" + a[0]);
                holder.buttonBasket.setVisibility(View.INVISIBLE);
                holder.buttonPlus.setVisibility(View.VISIBLE);
                holder.buttonMin.setVisibility(View.VISIBLE);
                holder.textViewNum.setVisibility(View.VISIBLE);
            }
        });
        holder.buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a[0] = a[0] + 1;
                holder.textViewNum.setText("" + a[0]);
            }
        });
        holder.buttonMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a[0] = a[0] - 1;
                holder.textViewNum.setText("" + a[0]);
                if (a[0] == 0) {
                    holder.buttonBasket.setVisibility(View.VISIBLE);
                    holder.buttonPlus.setVisibility(View.INVISIBLE);
                    holder.buttonMin.setVisibility(View.INVISIBLE);
                    holder.textViewNum.setVisibility(View.INVISIBLE);
                }
            }
        });
        DownloadImage task = new DownloadImage();
        String url = product.getImageUrl();
        Bitmap bitmap = null;
        try {
            bitmap = task.execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        holder.imageViewProduct.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProdViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewNameProduct;
        private TextView textViewManufacturer;
        private TextView textViewLatin;
        private TextView textViewCategiryFirst;
        private TextView textViewCategirySecond;
        private TextView textViewCategiryThird;
        private TextView textViewCost;
        private ImageView imageViewProduct;
        private FloatingActionButton buttonBasket;
        private TextView buttonPlus;
        private TextView buttonMin;
        private TextView textViewNum;

        public ProdViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNameProduct = itemView.findViewById(R.id.textViewNameProduct);
            textViewManufacturer = itemView.findViewById(R.id.textViewManufacturer);
            textViewLatin = itemView.findViewById(R.id.textViewLatin);
            textViewCategiryFirst = itemView.findViewById(R.id.textViewCategoryFirst);
            textViewCategirySecond = itemView.findViewById(R.id.textViewCategorySecond);
            textViewCategiryThird = itemView.findViewById(R.id.textViewCategoryThird);
            textViewCost = itemView.findViewById(R.id.textViewCost);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            buttonBasket = itemView.findViewById(R.id.floatingActionButtonBasket);
            buttonPlus = itemView.findViewById(R.id.buttonPlus);
            buttonMin = itemView.findViewById(R.id.buttonMin);
            textViewNum = itemView.findViewById(R.id.textViewNum);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onProductClickListener != null) {
                        onProductClickListener.onProductClick(getAdapterPosition(), products.get(getAdapterPosition()).getId(), a[0]);
                    }
                }
            });
        }
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
