package com.example.joseantonio.indicadoresmovil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
/**
 * Created by luis  manuel cobian on 18/01/2017.
 */
public class MainActivity extends AppCompatActivity {
    Button boton;

    private EditText name,email;
    private RequestQueue requestQueue;
    private static final String URL = "http://plancolima.col.gob.mx/apis/get_login";
    private StringRequest request;
    private Session session;
    private CheckBox checkBox;
    private ProgressDialog pDialog;
    String seccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boton=(Button)findViewById(R.id.button);
        name=(EditText)findViewById(R.id.editText);
        email=(EditText)findViewById(R.id.editText2);
        checkBox=(CheckBox)findViewById(R.id.checkBox);
        session = new Session(this);
        requestQueue= Volley.newRequestQueue(this);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        if(session.loggedin()){
            cambio();
            finish();
        }



        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nombre=name.getText().toString();
                final String correo=email.getText().toString();

                //MANDO EL CORREO Y EL PASSS PARA ENVIARSELO A LA CLASE HTTPHEADLER
                //para enviar
                SharedPreferences preferencias=getSharedPreferences("usuario", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=preferencias.edit();
                editor.putString("mail", name.getText().toString());
                editor.putString("pass", email.getText().toString());
                editor.commit();

                String bandera="false";
                showpDialog();
                Conexion(nombre,correo);




            }
        });
    }

    public void cambio(){
        Intent favoritos=new Intent(MainActivity.this,Favoritos.class);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(favoritos);
        finish();
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }
    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private  void  mensaje(String mensajes,String title){
        Context context=this;
        AlertDialog.Builder aler=new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(mensajes).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        AlertDialog alertDialog=aler.create();
        alertDialog.show();
    }
    private void Conexion(String names,String correo) {

        ConnectivityManager cm;
        NetworkInfo ni;
        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();
        boolean tipoConexion1 = false;
        boolean tipoConexion2 = false;

        if (ni != null) {
            ConnectivityManager connManager1 = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager1.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            ConnectivityManager connManager2 = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobile = connManager2.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (mWifi.isConnected()) {
                tipoConexion1 = true;
            }
            if (mMobile.isConnected()) {
                tipoConexion2 = true;
            }

            if (tipoConexion1 == true || tipoConexion2 == true) {
               /* Estas conectado a internet usando wifi o redes moviles, puedes enviar tus datos */

                if(names.length()<=0&&correo.length()<=0){

                    mensaje("Para ingresar se requiere que usted escriba su correo electrónico y su contraseña","Confirmacion");
                    hidepDialog();
                }

                else{

                   String token= FirebaseInstanceId.getInstance().getToken();
                    Login(names,correo);
                    agregar_token(token);
                }



            }
        } else {
       /* No estas conectado a internet */
            mensaje("Revise Su Conexión a Internet","Sin Conexión");
          hidepDialog();
        }
    }

    private void Login(final String name, final String pass){

        showpDialog();

        request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("clientes")){
                        if(checkBox.isChecked()){
                            session.setLoggedin(true);
                            String otra="favoritos";
                            SharedPreferences preferencias=getSharedPreferences("menus", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=preferencias.edit();
                            editor.putString("back", "favoritos");
                            editor.commit();

                        }

                        hidepDialog();
                        cambio();

                        finish();
                    }

                    else {
                        mensaje(jsonObject.getString("error"),"Confirmacion");

                        hidepDialog();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<String, String>();
                hashMap.put("email",name);
                hashMap.put("password",pass);
                return hashMap;
            }
        };

        requestQueue.add(request);

    }

    private  void  agregar_token( final String token){
        //aqui se agrega a favoritos
        //final String id_indica = String.valueOf(id);//recibo el di lo convierto en int

        String json="http://plancolima.col.gob.mx/apis/token_registro";
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(getApplicationContext());


        StringRequest request=new StringRequest(Request.Method.POST, json, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>parameters=new HashMap<String, String>();

                parameters.put("Token",token);
                parameters.put("usr",name.getText().toString());


                return parameters;

            }
        };
        requestQueue.add(request);



    }



}
