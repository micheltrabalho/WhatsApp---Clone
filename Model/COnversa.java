package br.com.michel.android.whatsapp.model;

import com.google.firebase.database.DatabaseReference;

import br.com.michel.android.whatsapp.congfirebase.ConfigFirebase;

public class COnversa {

    private String idRemetente;
    private String idDestinatario;
    private String ultimamsg;
    private Usuario usuario;
    private Boolean isgrupo;
    private Grupo grupo;

    public COnversa() {
        //this.setIsgrupo(false);
    }

    public String getIdRemetente() {
        return idRemetente;
    }

    public void salvar(){
        DatabaseReference databaseReference = ConfigFirebase.getFirebase();
        DatabaseReference conversaRef = databaseReference.child("usuariosWhatsApp");

        conversaRef.child("conversas").child(this.getIdRemetente()).child(getIdDestinatario()).setValue(this);
    }

    public Boolean getIsgrupo() {
        return isgrupo;
    }

    public void setIsgrupo(Boolean isgrupo) {
        this.isgrupo = isgrupo;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public void setIdRemetente(String idRemetente) {
        this.idRemetente = idRemetente;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getUltimamsg() {
        return ultimamsg;
    }

    public void setUltimamsg(String ultimamsg) {
        this.ultimamsg = ultimamsg;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
