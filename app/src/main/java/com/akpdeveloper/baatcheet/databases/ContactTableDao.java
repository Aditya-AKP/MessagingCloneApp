package com.akpdeveloper.baatcheet.databases;


import android.database.sqlite.SQLiteAccessPermException;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactTableDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveContact(ContactTable contactTable);

    @Update
    void updateContact(ContactTable contactTable);

//    default void saveContacts(List<ContactTable> contactTables){
//        for(ContactTable ct:contactTables){
//            try{
//                insertContact(ct);
//            }catch (SQLiteAccessPermException e){
//                updateContact(ct);
//            }
//        }
//    }

    @Query("SELECT * FROM contactTable ORDER BY name")
    List<ContactTable> getAllContact();

    @Query("SELECT * FROM contactTable WHERE UID=:uid")
    ContactTable getContact(String uid);


    @Query("SELECT customName FROM contactTable WHERE UID=:uid")
    String getCustomName(String uid);

    @Query("SELECT name FROM contactTable WHERE UID=:uid")
    String getName(String uid);

}
