package diplomatura.cocodibuja.com.ticketutn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button botonIniciarSesion,botonCerrarSesion;
    private EditText email,password;
    private TextView viendoElEstado,registrarse;

    /*static final String URL_FIREBASE = "https://ticketutn-718df.firebaseapp.com";
    Firebase firebase;
    String child = "usuarios";

    */
    Activity miActivity;

    //para la Autenticacion
    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthStateListener;
    public FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        miActivity = this;
        mAuth = FirebaseAuth.getInstance(); //generalmente de esta manera obtenemos la instancia



        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //verficar el estado en que se encuentra el usuario, por eso usamos el Listener
                user = firebaseAuth.getCurrentUser(); // con esto traemos el estado

                if(user!=null){
                    Log.d("TAG","Esta logueado: con el id: " + user.getUid());

                    Toast.makeText(MainActivity.this, "esta logueado", Toast.LENGTH_SHORT).show();

                    finish();

                    //and open profile activity
                    startActivity(new Intent(getApplicationContext(), Ingreso.class));


                }else{
                    Log.d("TAG","NO Esta logueado");

                }

            }
        };

        //cargo las vistas del main en caso que no este logueado

        botonIniciarSesion = (Button) findViewById(R.id.button_login_singUp);
        botonCerrarSesion = (Button) findViewById(R.id.button_login_singOut);
        registrarse = (TextView) findViewById(R.id.textView_login_registrar);
        email = (EditText) findViewById(R.id.editText_login_email);
        password = (EditText) findViewById(R.id.editText_login_password);
        viendoElEstado = (TextView) findViewById(R.id.textView_estado);


        //Onclick
        botonIniciarSesion.setOnClickListener(this);
        //botonCerrarSesion.setOnClickListener(this);
        registrarse.setOnClickListener(this);

        updateState();
    }

    @Override
    public void onClick(View view) {
        if(view == botonIniciarSesion){
            signIn();
        }
       // if (view == botonCerrarSesion){}
        if (view == registrarse){

            finish();
            startActivity(new Intent(getApplicationContext(), Main2Activity.class));


        }

    }

 /*   private void createUser(){
        String correoNuevo,contrasenaNuevo;
        if(!checkFields()){
            Toast.makeText(getApplicationContext(), "Datos incorrectos", Toast.LENGTH_SHORT).show();
        }else{
            correoNuevo = email.getText().toString();
            contrasenaNuevo = password.getText().toString();

            mAuth.createUserWithEmailAndPassword(correoNuevo,contrasenaNuevo).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(MainActivity.this, "Se creo el Usuario", Toast.LENGTH_SHORT).show();

                        Firebase.setAndroidContext(miActivity);
                        firebase = new Firebase(URL_FIREBASE).child(child);
                        firebase.setValue(user.getEmail());

                    }else{
                        Toast.makeText(MainActivity.this, "no se pudo crear el Usuario", Toast.LENGTH_SHORT).show();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof FirebaseAuthUserCollisionException){
                        updateState("Ese correo ya esta en uso");

                    }else{

                       updateState(e.getLocalizedMessage());
                    }
                }
            });

        }
    }*/

    private void signIn(){
        String correo,contrasena;
        if(!checkFields()){
            Toast.makeText(getApplicationContext(), "Datos incorrectos", Toast.LENGTH_SHORT).show();
        }else {
            correo = email.getText().toString();
            contrasena = password.getText().toString();
            mAuth.signInWithEmailAndPassword(correo,contrasena).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(MainActivity.this, "Sign in", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();

                    }
                    updateState();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof FirebaseAuthInvalidCredentialsException){
                        updateState("Contraseña equivocada");

                    }else if(e instanceof FirebaseAuthInvalidUserException){
                        updateState("No existe el usuario");

                    }
                    else{
                        updateState(e.getLocalizedMessage());

                    }
                }
            })


            ;

        }

    }

    private void updateState(){
        FirebaseUser user = mAuth.getCurrentUser(); // con esto traemos el estado

        if(user!=null){
            viendoElEstado.setText("sesion iniciada: "+user.getEmail());
        }else{
            viendoElEstado.setText("sesion no iniciada");

        }
    }

 /*   public void signOut(){
        mAuth.signOut();
        updateState();
    }
*/
    private void updateState(String mensaje){
        viendoElEstado.setText(mensaje.toString());
    }

    private boolean checkFields(){
        String correoCheck,contrasenaCheck;

        correoCheck = email.getText().toString();
        contrasenaCheck = password.getText().toString();

        if(correoCheck.isEmpty()){
            email.setText("Ingresar un correo");
            return false;
        }
        if(contrasenaCheck.isEmpty()){
            password.setText("Escribir contraseña");
            return false;
        }

        return true;
    }

    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }
    protected void onStop(){
        super.onStop();
        if(mAuthStateListener != null)
            mAuth.removeAuthStateListener(mAuthStateListener);
    }
}
