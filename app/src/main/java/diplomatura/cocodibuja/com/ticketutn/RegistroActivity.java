package diplomatura.cocodibuja.com.ticketutn;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class RegistroActivity extends AppCompatActivity {

    private Button botonCrearUsuario;
    private EditText emailR,passwordR,nombre,apellido;
    private RadioButton espectador,patova;
    Activity miActivity;

   static final String URL_FIREBASE = "https://ticketutn-718df.firebaseapp.com";
    Firebase firebase;
    String child = "usuarios";


    //para la Autenticacion
    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthStateListener;
    public FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        miActivity = this;

        botonCrearUsuario = (Button) findViewById(R.id.button_registro_Registrar);

        emailR = (EditText) findViewById(R.id.editText_registro_email);
        passwordR = (EditText) findViewById(R.id.editText_registro_password);
        nombre = (EditText) findViewById(R.id.editText_registro_nombre);
        apellido = (EditText) findViewById(R.id.editText_registro_apellido);

        botonCrearUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });

    }


     private void createUser(){
        String correoNuevo,contrasenaNuevo;
        if(!checkFields()){
            Toast.makeText(getApplicationContext(), "Datos incorrectos", Toast.LENGTH_SHORT).show();
        }else{
            correoNuevo = emailR.getText().toString();
            contrasenaNuevo = passwordR.getText().toString();

            mAuth.createUserWithEmailAndPassword(correoNuevo,contrasenaNuevo).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(miActivity, "Se creo el Usuario", Toast.LENGTH_SHORT).show();

                        Firebase.setAndroidContext(miActivity);
                        firebase = new Firebase(URL_FIREBASE).child(child);
                        firebase.setValue(user.getEmail());

                    }else{
                        Toast.makeText(miActivity, "no se pudo crear el Usuario", Toast.LENGTH_SHORT).show();

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
    }

    private boolean checkFields(){
        String correoCheck,contrasenaCheck,nombreCheck,apellidoCheck;

        correoCheck = emailR.getText().toString();
        contrasenaCheck = passwordR.getText().toString();
        nombreCheck = nombre.getText().toString();
        apellidoCheck = apellido.getText().toString();



        if(correoCheck.isEmpty()){
            emailR.setText("Ingresar un correo");
            return false;
        }
        if(contrasenaCheck.isEmpty()){
            passwordR.setText("Escribir contraseña");
            return false;
        }
        if(nombreCheck.isEmpty()){
            nombre.setText("Escribir nombre");
            return false;
        }
        if(apellidoCheck.isEmpty()){
            apellido.setText("Escribir apellido");
            return false;
        }

        return true;
    }

    private void updateState(){
        FirebaseUser user = mAuth.getCurrentUser(); // con esto traemos el estado

        if(user!=null){
      //      viendoElEstado.setText("sesion iniciada: "+user.getEmail());
        }else{
        //    viendoElEstado.setText("sesion no iniciada");

        }
    }

    public void signOut(){
        mAuth.signOut();
        updateState();
    }

    private void updateState(String mensaje){
        //viendoElEstado.setText(mensaje.toString());
    }

/*  protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }
    protected void onStop(){
        super.onStop();
        if(mAuthStateListener != null)
            mAuth.removeAuthStateListener(mAuthStateListener);
    }
*/

}
