package com.ka.testredsoft.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ka.testredsoft.pojo.Datum;

import java.util.List;

@Dao
public interface ProductDao {
    @Query("SELECT * FROM datum")
    LiveData<List<Datum>> getAllProduct();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProduct(List<Datum>data);

    @Query("DELETE FROM datum")
    void deleteAllProduct();
}
