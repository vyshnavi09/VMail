package com.example.vmail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SigninActivity extends AppCompatActivity {
    private boolean IsInitialVoiceFinshed;
    private TextToSpeech tts;
    private int numberofclicks;
    private TextView mailid, password, cnfrimpwd, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        IsInitialVoiceFinshed = false;

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                    speak("Sign Up Page!! Please tell your Mail address");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            IsInitialVoiceFinshed = true;
                        }
                    }, 1);
                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });
        numberofclicks = 0;
        mailid = findViewById(R.id.mailid);
        password = findViewById(R.id.password);
        cnfrimpwd = findViewById(R.id.cnfrimpassword);
//        name = findViewById(R.id.name);
    }

    private void speak(String text){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    public void layoutClicked(View view)
    {
        if(IsInitialVoiceFinshed) {
            numberofclicks++;
            listen();
        }
    }

    private void listen(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN");
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

        try {
            startActivityForResult(i, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(SigninActivity.this, "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
        }
    }

    private void exitFromApp()
    {
        this.finishAffinity();
    }

    public boolean isValidPassword(String password)
    {

        // Regex to check valid password.
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=\\S+$).{8,20}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        if (password == null) {
            return false;
        }
        Matcher m = p.matcher(password);

        return m.matches();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && IsInitialVoiceFinshed){
            IsInitialVoiceFinshed = false;
            if(resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if(result.get(0).equals("Exit")) {
                    speak("Exiting!");
                    exitFromApp();
                }
                else {
                    switch (numberofclicks) {
                        case 1:
                            String mail;
                            mail = result.get(0).replaceAll("underscore","_");
                            mail = mail.replaceAll("\\s+","");
                            mail = mail.replaceAll("underscore","a");
                            mail = mail.replaceAll("dot",".");
                            String id = mail + "@gmail.com";
                            mailid.setText(id);
                            speak("What should be the Password? Your password must between of length of eight and twenty, it must have have capital letter and number and no white spaces");
                            break;
                        case 2:
                            String pwd = result.get(0).replaceAll("\\s+","");
                            pwd = pwd.replaceAll("\\s+","");
                            pwd = pwd.replaceAll("at","@");
                            pwd = pwd.replaceAll("space"," ");
                            pwd = pwd.toLowerCase();
                            if(pwd.indexOf("capital")!=-1) {
                                System.out.println("Hello");
                                boolean flag = false;
                                ArrayList<Integer> a = new ArrayList<Integer>();
                                for (int i = 0; i < pwd.length(); i++) {
                                    if (i+7<pwd.length() && pwd.substring(i, i + 7).equals("capital")) {
                                        a.add(i + 8);
                                    }
                                }
                                for(int i = 0; i < a.size() ; i++) {
                                    pwd = pwd.substring(0,a.get(i)-1) + Character.toUpperCase(pwd.charAt(a.get(i)-1)) + pwd.substring(a.get(i));
                                }
                                pwd = pwd.replaceAll("capital","");
                            }
                            if(isValidPassword(pwd)){
                                password.setText(pwd);
                                speak("Please Confrim your password");
                            }
                            else {
                                password.setText(pwd);
                                speak("Your Password doesn't suit Please tell new password");
                                numberofclicks --;
                            }
                            break;
                        case 3:
                            String p = result.get(0).replaceAll("\\s+","");
                            p = p.replaceAll("\\s+","");
                            p = p.replaceAll("at","@");
                            p = p.replaceAll("space"," ");
                            p = p.toLowerCase();
                            if(p.indexOf("capital")!=-1) {
                                System.out.println("Hello");
                                boolean flag = false;
                                ArrayList<Integer> a = new ArrayList<Integer>();
                                for (int i = 0; i < p.length(); i++) {
                                    if (i+7<p.length() && p.substring(i, i + 7).equals("capital")) {
                                        a.add(i + 8);
                                    }
                                }
                                for(int i = 0; i < a.size() ; i++) {
                                    p = p.substring(0,a.get(i)-1) + Character.toUpperCase(p.charAt(a.get(i)-1)) + p.substring(a.get(i));
                                }
                                p = p.replaceAll("capital","");
                            }
                            cnfrimpwd.setText(p);
//                            if(password.getText().toString().equals(p)) {
//                                cnfrimpwd.setText(p);
//                                speak("Please Confrom, your mail address is " +mailid.getText().toString()+"and your password is "+password.getText().toString()+". Say yes or no to Confrom");
//                            }else {
//                                speak("password doesn't match");
//                                numberofclicks --;
//                            }

                        default:
                            if(result.get(0).equals("yes")||result.get(0).equals("s"))
                            {
                                speak("Login");
                            }else {
                                numberofclicks = 0;
                                mailid.setText("Mail Id :");
                                password.setText("Password :");
                                cnfrimpwd.setText("Confrim Password :");
                                speak("Please tell the mail address");
                            }
                    }
                }
            } else {
                switch (numberofclicks) {
                    case 1:
                        speak("tell your mail address");
                        break;
                    case 2:
                        speak("tell your password");
                        break;
                    default:
                        speak("say yes or no");
                        break;
                }
                numberofclicks--;
            }
        }
        IsInitialVoiceFinshed = true;
    }
}