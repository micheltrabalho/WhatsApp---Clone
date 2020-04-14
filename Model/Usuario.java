package br.com.michel.android.whatsapp.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import br.com.michel.android.whatsapp.congfirebase.ConfigFirebase;
import br.com.michel.android.whatsapp.helper.UsuarioFirebae;


public class Usuario implements Serializable {

    private String nome;
    private String email;
    private String senha;
    private String idUsuario;
    private String foto;


    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public void save(){
        DatabaseReference reference = ConfigFirebase.getFirebase();
        reference.child("usuariosWhatsApp").child(this.idUsuario).setValue(this);
    }

    public void atualizar(){
        String idUsuario = UsuarioFirebae.getIdentificadordeUsuario();
        DatabaseReference databaseReference = ConfigFirebase.getFirebase();

        DatabaseReference usuarioRef = databaseReference.child("usuariosWhatsApp").child(idUsuario);

        Map<String, Object> valorusuario = converterParaMap();

        usuarioRef.updateChildren(valorusuario);
    }

    @Exclude
    public Map<String, Object> converterParaMap (){
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("email", getEmail());
        usuarioMap.put("nome", getNome());
        usuarioMap.put("foto", getFoto());

        return usuarioMap;
    }

    public Usuario() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }
}
