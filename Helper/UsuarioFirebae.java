package br.com.michel.android.whatsapp.helper;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import androidx.annotation.NonNull;
import br.com.michel.android.whatsapp.congfirebase.ConfigFirebase;
import br.com.michel.android.whatsapp.model.Usuario;

public class UsuarioFirebae {

    public static String getIdentificadordeUsuario(){
        FirebaseAuth user = ConfigFirebase.getFirebaseAutentication();
        String email = user.getCurrentUser().getEmail();
        String iduser = Base64Custom.configBase64(email);

        return iduser;
    }

    public static FirebaseUser getUserAtual(){
        FirebaseAuth user = ConfigFirebase.getFirebaseAutentication();
        return user.getCurrentUser();
    }

    public static boolean atualizarNomeUsuario(String nome){

        try {
            FirebaseUser user = getUserAtual();
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(nome).build();
            user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if ( !task.isSuccessful()){
                        Log.d("perfil", "Erro ao atualizar nome de perfil.");
                    }
                }
            });
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean atualizarFotoUsuario(Uri url){

        try {
            FirebaseUser user = getUserAtual();
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(url).build();
            user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if ( !task.isSuccessful()){
                        Log.d("perfil", "Erro ao atualizar foto de perfil.");
                    }
                }
            });
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static Usuario getDadosUser() {
        FirebaseUser firebaseUseruser = getUserAtual();
        Usuario usuario = new Usuario();
        usuario.setEmail(firebaseUseruser.getEmail());
        usuario.setNome(firebaseUseruser.getDisplayName());
        if (firebaseUseruser.getPhotoUrl() == null){
            usuario.setFoto("");
        }
        else {
            usuario.setFoto(firebaseUseruser.getPhotoUrl().toString());
        }

        return usuario;
    }
}
