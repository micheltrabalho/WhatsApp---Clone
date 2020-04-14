package br.com.michel.android.whatsapp.helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permissao {

    public static boolean validarPermissao(String[] permissoes, Activity activity, int requestcode){

        if (Build.VERSION.SDK_INT >= 23 ){

            List<String> list = new ArrayList<>();

            for (String permissao : permissoes){
                Boolean temPermissao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;
                if (!temPermissao) list.add(permissao);
            }

            if (list.isEmpty()) return true;

            String[] novasPermissoes = new String[list.size()];
            list.toArray(novasPermissoes);

            ActivityCompat.requestPermissions(activity, novasPermissoes, requestcode);
        }

        return true;
    }
}

