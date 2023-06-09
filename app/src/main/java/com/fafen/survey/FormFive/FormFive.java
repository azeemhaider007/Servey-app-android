package com.fafen.survey.FormFive;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fafen.survey.FormOne.FormOne;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.fafen.survey.FormFour.FormFour;
import com.fafen.survey.FormTwo.FormTwo;
import com.fafen.survey.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import static com.fafen.survey.Util.Utiles.alert;
import static com.fafen.survey.Util.Utiles.hideSoftKeyboard;


public class FormFive extends AppCompatActivity
{

    private Location currentLocation;

    SharedPreferences sharedPreferences;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final String TAG = "FormOne";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    boolean doubleBackToExitPressedOnce = false;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionsGranted = false;




    static final int NUMBER_OF_PAGES = 5;
    public static String ans1="",ans2="",ans3="",ans4="",ans5="";
    public static boolean questionOneAsnwered = false;
    public static boolean questionTwoAsnwered = false;
    public static boolean questionThreeAsnwered = false;
    public static boolean questionFourAsnwered = false;
    public static boolean questionFiveAsnwered = false;
    MyAdapter mAdapter;
    ViewPager mPager;
    static int currentPage = 0;

    static Button nextButton;
    static Button backButton;
    static Button doneButton;

    private Context context;

    private static int pHour;


    private static TimePicker timePicker12;



//    EditText ans1EditText, ans5EditText,anyOtherEditText;
//
//    RadioGroup ans2RadioGroup, ans4RadioGroup;
//
//    TimePicker ans3TimePicker;

    public void initValues(){
        currentPage = 0;
        FormFive.questionOneAsnwered=false;
        FormFive.questionTwoAsnwered=false;
        FormFive.questionThreeAsnwered=false;
        FormFive.questionFourAsnwered=false;
        FormFive.questionFiveAsnwered=false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_five);

        context =this;
        setupUI(findViewById(R.id.parent));
        initValues();

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        final String currentDateandTime = df.format(Calendar.getInstance().getTime());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mAdapter = new MyAdapter(getSupportFragmentManager());
        mPager = findViewById(R.id.viewpager);
        mPager.setAdapter(mAdapter);
        doneButton= findViewById(R.id.doneButton);
        nextButton = findViewById(R.id.nextButton);
        backButton= findViewById(R.id.backButton);

        backButton.setVisibility(View.GONE);


        sharedPreferences  = getApplicationContext().getSharedPreferences("USER_ID",MODE_PRIVATE);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(FormFive.this);
        doneButton.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v)
            {
                @SuppressLint("MissingPermission") final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Log.d(TAG, "getLocations: in onComplete function");
                        if(task.isSuccessful())
                        {
                            currentLocation = (Location) task.getResult();
                            Log.d(TAG, "getLocations: data of location got in currentLocation variable ");


                            if(validateInternet())
                            {
//                                Toast.makeText(FormFive.this, " Latitude: " + currentLocation.getLatitude() + " Longitude: " + currentLocation.getLongitude(), Toast.LENGTH_LONG).show();
                                DatabaseAsyncFormFive worker = new DatabaseAsyncFormFive(FormFive.this);


                                worker.execute((String.valueOf(sharedPreferences.getString("ID",""))),
                                        ans1,
                                        ans2,
                                        ans3,
                                        ans4,
                                        ans5,
                                        currentDateandTime,
                                        currentLocation.getLatitude()+"",
                                        currentLocation.getLongitude()+""
                                );

                            }
                            else
                            {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                String query =sharedPreferences.getString("query","");


                                StringBuilder sb = new StringBuilder();
                                sb.append("\'"+String.valueOf(sharedPreferences.getString("ID","")+"\'"));
                                sb.append(",");
                                sb.append("\'"+ans1+"\'");
                                sb.append(",");
                                sb.append("\'"+ans2+"\'");
                                sb.append(",");
                                sb.append("\'"+ans3+"\'");
                                sb.append(",");
                                sb.append("\'"+ans4+"\'");
                                sb.append(",");
                                sb.append("\'"+ans5+"\'");
                                sb.append(",");
                                sb.append("\'"+currentDateandTime+"\'");
                                sb.append(",");
                                sb.append("\'"+currentLocation.getLatitude()+"\'");
                                sb.append(",");
                                sb.append("\'"+currentLocation.getLongitude()+"\'");
                                query += "INSERT INTO form5survey (email,ans1 ,ans2, ans3, ans4, ans5,date, lati, longi) VALUES ("+sb.toString()+")&";

                                editor.putBoolean("checkSync",true);
                                editor.putString("query", query);

                                editor.apply();
                            }


                        }else{
                            Log.d(TAG, "getLocations: unable to complete location task");
//                            Toast.makeText(FormFive.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }

                        currentLocation = (Location) task.getResult();
                        sharedPreferences.edit().putString("FormFive",sharedPreferences.getString("ID","")+ans1+ans2+ans3+ans4+ans5+currentDateandTime+currentLocation.getLongitude()+""+currentLocation.getLongitude()+"").apply();

                    }
                });
//                Toast.makeText(FormFive.this, "Done", Toast.LENGTH_LONG).show();
                finish();

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                backButton.setEnabled(true);
                doneButton.setVisibility(View.INVISIBLE);
                if(!questionOneAsnwered&&currentPage ==0)
                {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        // Make sure that we are currently visible
                        builder = new AlertDialog.Builder(v.getContext(), android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(v.getContext());
                    }
                    builder.setTitle("Please enter details")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }
                else if(!questionTwoAsnwered && currentPage==1)
                {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        // Make sure that we are currently visible
                        builder = new AlertDialog.Builder(v.getContext(), android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(v.getContext());
                    }
                    builder.setTitle("Please select your answer")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }

                else if(!questionThreeAsnwered && currentPage==2)
                {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        // Make sure that we are currently visible
                        builder = new AlertDialog.Builder(v.getContext(), android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(v.getContext());
                    }
                    builder.setTitle("Please select time between 8 AM to 8 PM")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }

                else if (questionThreeAsnwered && currentPage == 2) {
                    int currentHour = 0;
                    int currentMinute = 0;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        currentHour = timePicker12.getHour();
                        currentMinute = timePicker12.getMinute();
                    } else {
                        currentHour = timePicker12.getCurrentHour();
                        currentMinute = timePicker12.getCurrentMinute();
                    }

                    if(currentHour < 8 || currentHour > 20) {
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            // Make sure that we are currently visible
                            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(context);
                        }
                        builder.setTitle("Please select time between 8 AM to 8 PM")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                        questionThreeAsnwered = false;
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return;
                    }else if(currentHour == 20 && currentMinute >0){
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            // Make sure that we are currently visible
                            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(context);
                        }
                        builder.setTitle("Please select time between 8 AM to 8 PM")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                        questionThreeAsnwered = false;
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return;
                    }
                }

                else if(!questionFourAsnwered && currentPage==3)
                {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        // Make sure that we are currently visible
                        builder = new AlertDialog.Builder(v.getContext(), android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(v.getContext());
                    }
                    builder.setTitle("Please select time between 8 AM to 8 PM")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }

                else if (questionFourAsnwered && currentPage == 3) {
                    int currentHour = 0;
                    int currentMinute = 0;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        currentHour = timePicker12.getHour();
                        currentMinute = timePicker12.getMinute();
                    } else {
                        currentHour = timePicker12.getCurrentHour();
                        currentMinute = timePicker12.getCurrentMinute();
                    }

                    if(currentHour < 8 || currentHour > 20) {
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            // Make sure that we are currently visible
                            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(context);
                        }
                        builder.setTitle("Please select time between 8 AM to 8 PM")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                        questionFourAsnwered = false;
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return;
                    }else if(currentHour == 20 && currentMinute >0){
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            // Make sure that we are currently visible
                            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(context);
                        }
                        builder.setTitle("Please select time between 8 AM to 8 PM")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                        questionFourAsnwered = false;
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return;
                    }
                }
                else
                {
                    nextButton.setEnabled(true);
                }
                if(currentPage < NUMBER_OF_PAGES-1)
                {
                    currentPage++;
                }
                if(NUMBER_OF_PAGES-1 == currentPage)
                {
                    nextButton.setEnabled(false);
                    nextButton.setVisibility(View.INVISIBLE);
                }
                setCurrentItem (currentPage, true);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                doneButton.setVisibility(View.INVISIBLE);
                nextButton.setVisibility(View.VISIBLE);
                nextButton.setEnabled(true);
                if(currentPage > 0)
                {
                    currentPage--;
                }
                if(currentPage == 0)
                {
                    backButton.setEnabled(false);
                }
                setCurrentItem (currentPage, true);
            }
        });

    }

    public void setCurrentItem (int item, boolean smoothScroll) {
        mPager.setCurrentItem(item, smoothScroll);
    }
    public static class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public int getCount()
        {
            return NUMBER_OF_PAGES;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return FragmentOne.newInstance(0, Color.WHITE);
                case 1:
                    return FragmentTwo.newInstance(1, Color.CYAN);
                case 2:
                    return FragmentThree.newInstance(2, Color.CYAN);
                case 3:
                    return FragmentFour.newInstance(3, Color.CYAN);
                case 4:
                    return FragmentFive.newInstance(4, Color.CYAN);
                default:
                    return null;
            }
        }
    }

    private boolean validateInternet()
    {
        ConnectivityManager cm = (ConnectivityManager)(this).getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isAvailable() && activeNetwork.isConnected();
    }


    public static class FragmentOne extends Fragment {


        View v;
        EditText editText;
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);

            // Make sure that we are currently visible
            if (this.isVisible())
            {
                backButton.setVisibility(View.GONE);
                // If we are becoming invisible, then...
                if (!isVisibleToUser)
                {
                    backButton.setVisibility(View.VISIBLE);
                    ans1 =(editText.getText().toString());
                }
            }
        }

        // You can modify the parameters to pass in whatever you want
        static FragmentOne newInstance(int num, int color) {
            FragmentOne f = new FragmentOne();
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            v = inflater.inflate(R.layout.fragment_form_five_q1, container, false);
            editText = v.findViewById(R.id.ans1EditTextFormFive);
            editText.setFilters(new InputFilter[]{new FormTwo.InputFilterMinMax("1", "999")});
            editText.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    if(!editText.getText().toString().isEmpty()){
                        String value = editText.getText().toString();
                        value = value.replace(" ","");
                        if (value.length()>0)
                            FormFive.questionOneAsnwered = true;
                        else
                            FormFive.questionOneAsnwered = false;
                    }
                    else
                        FormFive.questionOneAsnwered = false;
                }

                @Override
                public void afterTextChanged(Editable s)
                {

                }
            });

            return v;

        }
    }
    public static class FragmentTwo extends Fragment implements View.OnClickListener {

        //RadioGroup radioGroup;
        View v;
        Button mButton1, mButton2, mButton3;

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser)
        {
            super.setUserVisibleHint(isVisibleToUser);

            // Make sure that we are currently visible
            if (this.isVisible())
            {
                // If we are becoming invisible, then...
                if (!isVisibleToUser)
                {
                   // FormOne.answers.add(((RadioButton) v.findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString());
                }
            }
        }

        // You can modify the parameters to pass in whatever you want
        static FragmentTwo newInstance(int num, int color) {
            FragmentTwo f = new FragmentTwo();
            Bundle args = new Bundle();
            f.setArguments(args);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            v = inflater.inflate(R.layout.fragment_form_five_q2, container, false);
            mButton1 = v.findViewById(R.id.maleButtonFormFive);
            mButton2 = v.findViewById(R.id.femaleButtonFormFive);
            mButton3 = v.findViewById(R.id.combinedButtonFormFive);

            mButton1.setOnClickListener(this);
            mButton2.setOnClickListener(this);
            mButton3.setOnClickListener(this);


            if(questionTwoAsnwered)
            {
                if(Objects.equals(ans2, mButton1.getText().toString()))
                {
                    mButton1.setSelected(true);
                }
                else if(Objects.equals(ans2, mButton2.getText().toString()))
                {
                    mButton2.setSelected(true);
                }
                else if(Objects.equals(ans2, mButton3.getText().toString()))
                {
                    mButton3.setSelected(true);
                }
            }

            //radioGroup = v.findViewById(R.id.formOneQ2RadioGroup);
           // radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
           // {
             //   @Override
             //   public void onCheckedChanged(RadioGroup group, int checkedId)
             //   {
           //         questionTwoAsnwered = true;
           //     }
          //  });
            return v;

        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v)
        {
            FormFive.questionTwoAsnwered=true;
            Button button = (Button) v;

//        mButton1.setBackgroundResource(R.color.bg_selector);
//        mButton2.setBackgroundResource(R.color.bg_selector);
//        mButton3.setBackgroundResource(R.color.bg_selector);
//        button.setBackgroundResource(R.drawable.bg_selected);

            // clear state
            mButton1.setSelected(false);
            mButton1.setPressed(false);
            mButton2.setSelected(false);
            mButton2.setPressed(false);
            mButton3.setSelected(false);
            mButton3.setPressed(false);

            // change state
            button.setSelected(true);
            button.setPressed(false);
            ans2=button.getText().toString();
            nextButton.performClick();
        }
    }
    public static class FragmentThree extends Fragment {
        View v;
        TimePicker timePicker;
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);

            // Make sure that we are currently visible
            if (this.isVisible())
            {
                if (ans3!=null && !ans3.equals("")){
                    try{
                        String[] time = ans3.split(":");
                        timePicker.setCurrentHour(Integer.parseInt(time[0]));
                        timePicker.setCurrentMinute(Integer.parseInt(time[1]));
                    }catch (Exception e){}
                }
                // If we are becoming invisible, then...
                if (!isVisibleToUser)
                {

                }
            }
        }


        // You can modify the parameters to pass in whatever you want
        static FragmentThree newInstance(int num, int color) {
            FragmentThree f = new FragmentThree();
            Bundle args = new Bundle();
            f.setArguments(args);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_form_five_q3, container, false);
            timePicker= v.findViewById(R.id.q3TimePickerFormFive);
            timePicker.setCurrentHour(8);
            timePicker.setCurrentMinute(0);
            timePicker12 = timePicker;
            questionThreeAsnwered=true;

            timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                    timePicker12 = timePicker;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    {
                        ans3 = (timePicker.getHour() + ":" + timePicker.getMinute() + "");
                        questionThreeAsnwered=true;
                    }
                    else {
                        ans3 = (timePicker.getCurrentHour().toString() + ":" + timePicker.getCurrentMinute().toString());
                        questionThreeAsnwered=true;
                    }
                }
            });
            return v;

        }
    }
    public static class FragmentFour extends Fragment implements View.OnClickListener {


        View v;
        TimePicker timePicker;
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);

            // Make sure that we are currently visible
            if (this.isVisible())
            {
                if (ans4!=null && !ans4.equals("")){
                    try{
                        String[] time = ans4.split(":");
                        timePicker.setCurrentHour(Integer.parseInt(time[0]));
                        timePicker.setCurrentMinute(Integer.parseInt(time[1]));
                    }catch (Exception e){}
                }
                // If we are becoming invisible, then...
                if (!isVisibleToUser) {


                }
            }
        }

        // You can modify the parameters to pass in whatever you want
        static FragmentFour newInstance(int num, int color)
        {
            FragmentFour f = new FragmentFour();
            Bundle args = new Bundle();
            f.setArguments(args);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
             v = inflater.inflate(R.layout.fragment_form_five_q4, container, false);
            timePicker= v.findViewById(R.id.q4TimePickerFormFive);
            timePicker.setCurrentHour(8);
            timePicker.setCurrentMinute(0);
            timePicker12 = timePicker;
            questionFourAsnwered = true;
            timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                    timePicker12 = timePicker;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ans4 = (timePicker.getHour() + ":" + timePicker.getMinute() + "");
                        questionFourAsnwered = true;
                    } else {
                        ans4 = (timePicker.getCurrentHour().toString() + ":" + timePicker.getCurrentMinute().toString());
                        questionFourAsnwered=true;
                    }
                }
            });
            return v;

        }


        @Override
        public void onClick(View v)
        {

        }
    }
    public static class FragmentFive extends Fragment {

        View v;
        EditText editText;

        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);

            // Make sure that we are currently visible
            if (this.isVisible())
            {
                if(editText != null){
                    if (TextUtils.isEmpty(editText.getText().toString().trim()))
                    {
                        doneButton.setVisibility(View.INVISIBLE);
                        questionFiveAsnwered = false;
                    }
                    else {
                        if(currentPage == NUMBER_OF_PAGES - 1) {

                            questionFiveAsnwered = true;
                            doneButton.setVisibility(View.VISIBLE);
                            ans5 = editText.getText().toString();
                        }
                    }
                }
                // If we are becoming invisible, then...
                if (!isVisibleToUser) {


                }
            }
        }


        static FragmentFive newInstance(int num, int color)
        {
            FragmentFive f = new FragmentFive();
            Bundle args = new Bundle();
            f.setArguments(args);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
            v = inflater.inflate(R.layout.fragment_form_five_q5, container, false);
            editText = v.findViewById(R.id.q5EditTextFormFive);
            if (!(Objects.equals(ans5, "")))
            {
               // doneButton.setVisibility(v.VISIBLE);

            }


            editText.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    if (TextUtils.isEmpty(editText.getText().toString().trim()))
                    {
                        doneButton.setVisibility(View.INVISIBLE);
                        questionFiveAsnwered = false;
                    }
                    else {
                        if(currentPage == NUMBER_OF_PAGES - 1) {
                            String value = editText.getText().toString();
                            value = value.replace(" ","");
                            if (value.length()>0){
                                questionFiveAsnwered = true;
                                doneButton.setVisibility(View.VISIBLE);
                            }else{
                                questionFiveAsnwered = false;
                                doneButton.setVisibility(View.INVISIBLE);
                            }

                            ans5 = editText.getText().toString();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s)
                {

                }
            });
            return v;

        }
    }
    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "getLocationPermission: fine location permission granted");
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, "getLocationPermission: coarse location permission granted boolean set to true");
                mLocationPermissionsGranted = true;
                // initMap();
            }
            else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
                Log.d(TAG, "getLocationPermission: requesting location permission");
            }
        }
        else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    private Location getLocations() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(FormFive.this);

        try{
            if(mLocationPermissionsGranted){

                Log.d(TAG, "getLocations: location permission was granted");
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Log.d(TAG, "getLocations: in onComplete function");
                        if(task.isSuccessful()){
                            currentLocation = (Location) task.getResult();
                            Log.d(TAG, "getLocations: data of location got in currentLocation variable ");
                            //Toast.makeText(UserHome.this, " Latitude: " + currentLocation.getLatitude() + " Longitude: " + currentLocation.getLongitude(), Toast.LENGTH_LONG).show();

                        }else{
                            Log.d(TAG, "getLocations: unable to complete location task");
//                            Toast.makeText(FormFive.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return currentLocation;
            }
            else {
                Log.d(TAG, "getLocations: location permission was not granted granted");

            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
        return null;
    }



    @Override
    public void onBackPressed() {

        if (mPager.getCurrentItem() == 0) {
            alert(FormFive.this);

            return;
        }else if(doubleBackToExitPressedOnce){
            alert(FormFive.this);

            return;
        }


        else{
            backButton.performClick();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

    }

    public  void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(FormFive.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
}
