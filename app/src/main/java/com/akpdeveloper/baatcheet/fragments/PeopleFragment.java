package com.akpdeveloper.baatcheet.fragments;

import static com.akpdeveloper.baatcheet.StartActivity.logcat;
import static com.akpdeveloper.baatcheet.utilities.AndroidUtils.DB;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akpdeveloper.baatcheet.adapter.PeopleFragmentAdapter;
import com.akpdeveloper.baatcheet.MainActivity;
import com.akpdeveloper.baatcheet.databases.ContactTable;
import com.akpdeveloper.baatcheet.models.UserModel;
import com.akpdeveloper.baatcheet.R;
import com.akpdeveloper.baatcheet.utilities.DateUtils;
import com.akpdeveloper.baatcheet.utilities.FireBaseClass;
import com.google.android.datatransport.backend.cct.BuildConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PeopleFragment extends Fragment {

    private PeopleFragmentAdapter adapter;
    private List<UserModel> um;

//    private AppDatabase DB;
//
//    private synchronized void getDB(){
//        DB = Room.databaseBuilder(requireContext(), AppDatabase.class,"BhaatCheetDB")
//                .allowMainThreadQueries()
//                .fallbackToDestructiveMigration()
//                .build();
//    }

    public PeopleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getDB();
        um = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_people, container, false);

        setUpTheRecyclerView(view);
        return view;
    }

    private List<String> getContactList(){
        ArrayList<String> phoneList = new ArrayList<>();
        ContentResolver cr = requireContext().getContentResolver();
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        if(cur!=null) {
            while (cur.moveToNext()) {
                int index = cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String phoneNo = cur.getString(index);
                if(phoneNo.charAt(0) != '+'){
                    phoneNo = "+91"+phoneNo;
                }
                phoneList.add(phoneNo.replace(" ",""));
            }
            cur.close();
        }
        return phoneList;
    }

    private void getUserFromFirebase(){
        List<String> phoneNoList = getContactList();

        logcat(String.valueOf(phoneNoList.size()));
        logcat(String.valueOf(phoneNoList.contains("+918102265815")));
        logcat(String.valueOf(phoneNoList.contains("+919430644447")));
        //TODO: REMOVE BELOW LINE IN PRODUCTION CODE
            phoneNoList.add("+919999999911");
        logcat(String.valueOf(phoneNoList.size()));

        List<UserModel> userModelList = new ArrayList<>();
        fetchingTheContactsFromFirebase(userModelList,phoneNoList,0);
    }

    private void fetchingTheContactsFromFirebase(List<UserModel> userModelList,List<String> phoneNoList,int i){
        if(phoneNoList.isEmpty())return;
        if(i+30>=phoneNoList.size()){
            FireBaseClass.allUsersCollectionReference().whereIn("number",phoneNoList.subList(i, phoneNoList.size())).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    userModelList.addAll(task.getResult().toObjects(UserModel.class));
                    logcat(userModelList.size()+"");
                    updateTheRecyclerViewFromFirebase(userModelList);
                }
            });
        }else{
            FireBaseClass.allUsersCollectionReference().whereIn("number",phoneNoList.subList(i,i+29)).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    userModelList.addAll(task.getResult().toObjects(UserModel.class));
                    logcat(userModelList.size()+"");
                    fetchingTheContactsFromFirebase(userModelList,phoneNoList,i+30);
                }else{
                    logcat(task.getException()+"");
                    logcat(String.valueOf(task.isComplete()));
                }
            });
        }

    }

    private void updateTheRecyclerViewFromFirebase(List<UserModel> userModelList){
        um.clear();
        logcat("size of user model list: "+userModelList.size()+" size of um: "+um.size());
        for(UserModel userModel:userModelList){
            if(!um.contains(userModel)){
                ContactTable ct = new ContactTable(userModel.getName(),userModel.getNumber(),userModel.getAbout(), userModel.getuID(), userModel.getImageUrl(), DateUtils.getLongFromTimestamp(userModel.getAccountCreationTime()));
                DB.ContactTableDao().saveContact(ct);
                um.add(userModel);
            }
        }
        um.sort(Comparator.comparing(UserModel::getName));
        adapter.notifyItemRangeChanged(0,um.size());
    }

    private void getUserFromRoomDB(){
        List<ContactTable> contacts = DB.ContactTableDao().getAllContact();
        if(!contacts.isEmpty()){
            for(ContactTable ct:contacts){
                um.add(new UserModel(ct.getName(),ct.getAbout(),ct.getUID(),ct.getNumber(),ct.getImageUrl(),DateUtils.getTimestampFromLong(ct.getAccountCreationTime())));
            }
        }
    }

    private void setUpTheRecyclerView(View v){
        getUserFromRoomDB();
        um.sort(Comparator.comparing(UserModel::getName));

        adapter = new PeopleFragmentAdapter(requireContext(),um);
        v.findViewById(R.id.PeopleFragTextView).setVisibility(View.GONE);
        RecyclerView recyclerView = v.findViewById(R.id.PeopleFragRecyclerView);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        if(MainActivity.isContactPermissionGranted) {
            getUserFromFirebase();
        }
    }
}