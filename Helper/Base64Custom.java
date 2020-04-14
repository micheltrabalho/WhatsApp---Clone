package br.com.michel.android.whatsapp.congfirebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfigFirebase {

    private static FirebaseAuth auth;
    private static StorageReference storege;
    private static DatabaseReference databaseReference;


    public static DatabaseReference getFirebase() {
        if (databaseReference == null){
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }
        return databaseReference;
    }

    public static FirebaseAuth getFirebaseAutentication() {
        if (auth == null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }
    
    // metodo para guaradar a imagem no firebase
    public static StorageReference getFirebaseStorege(){
        if (storege == null){
            storege = FirebaseStorage.getInstance().getReference();
        }
        return storege;
    }
}
