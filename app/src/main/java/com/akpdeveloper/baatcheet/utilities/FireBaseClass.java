package com.akpdeveloper.baatcheet.utilities;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class FireBaseClass {
    private static final String STORAGE_UPLOAD = "uploads/";
    private static final String STORAGE_PROFILE_UPLOAD = "/profile_photo";
    private static final String STORAGE_MEDIA_UPLOAD = "/media/";
    private static final String STORAGE_STATUS_UPLOAD = "/status/";
    private static final String USERS_COLLECTION_NAME = "users";
    private static final String CHATS_COLLECTION_NAME = "chats";
    private static final String MESSAGES_COLLECTION_NAME = "messages";
    private static final String GROUPS_COLLECTION_NAME = "groups";
    private static final String STATUS_COLLECTION_NAME = "status";
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();

    public static FirebaseAuth auth() {return auth;}

    public static String myUserUID(){return auth().getUid();}

    public static StorageReference getProfileStorage(String ID){return FirebaseStorage.getInstance().getReference().child(STORAGE_UPLOAD+ID+STORAGE_PROFILE_UPLOAD);}

    public static StorageReference getStatusStorage(String ID){return FirebaseStorage.getInstance().getReference().child(STORAGE_UPLOAD+ID+STORAGE_STATUS_UPLOAD);}
    public static StorageReference getMediaStorage(String ID){return FirebaseStorage.getInstance().getReference().child(STORAGE_UPLOAD+ID+STORAGE_MEDIA_UPLOAD);}

    public static CollectionReference allUsersCollectionReference(){return FirebaseFirestore.getInstance().collection(USERS_COLLECTION_NAME);}

    public static DatabaseReference allChatCollectionReference(){return FirebaseDatabase.getInstance().getReference(CHATS_COLLECTION_NAME);}

    public static DatabaseReference getChatDocumentReference(String id){return FirebaseDatabase.getInstance().getReference(CHATS_COLLECTION_NAME).child(id);}

    public static DatabaseReference getMessageFromDatabase(String id){return getChatDocumentReference(id).child(MESSAGES_COLLECTION_NAME);}

    public static DatabaseReference getStatusCollectionReference(){return FirebaseDatabase.getInstance().getReference(STATUS_COLLECTION_NAME);}

    public static DocumentReference getOtherUserByUID(List<String> user){
        if(user.get(0).equals(myUserUID())){
            return allUsersCollectionReference().document(user.get(1));
        }else{
            return allUsersCollectionReference().document(user.get(0));
        }
    }



}
