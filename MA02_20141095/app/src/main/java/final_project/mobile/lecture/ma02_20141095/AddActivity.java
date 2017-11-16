package final_project.mobile.lecture.ma02_20141095;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import java.util.Calendar;

public class AddActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    static final int PLACE_PICKER_REQUEST = 100;
    static final int PHONE_PICKER_REQUEST = 200;

    // date and time
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;

    static final int TIME_12_DIALOG_ID = 0;
    static final int DATE_DIALOG_ID = 2;

    Calendar end_cal = Calendar.getInstance();

    private Button btTime;
    private Button btDate;
    private Button btFriend;
    private Button btPlace;
    private Button btRegister;

    private GoogleApiClient mGoogleApiClient;

    Appoint appointment;

    private EditText etTitle;
    private EditText etContent;

    private MyDBHelper myDBHelper;

    String[] engMonth = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        appointment =  new Appoint();

        myDBHelper = new MyDBHelper(this);

        etTitle = (EditText) findViewById(R.id.etTitle);
        etContent = (EditText) findViewById(R.id.etContent);

        btDate = (Button) findViewById(R.id.pickDate);
        btTime = (Button) findViewById(R.id.pickTime12);
        btFriend = (Button) findViewById(R.id.btFriend);
        btPlace = (Button) findViewById(R.id.btPlace);
        btRegister = (Button) findViewById(R.id.btRegister);

        setDialogOnClickListener(R.id.pickDate, DATE_DIALOG_ID);
        setDialogOnClickListener(R.id.pickTime12, TIME_12_DIALOG_ID);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        updateDisplay();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



//        Add friend
        btFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, PHONE_PICKER_REQUEST);
            }
        });

//        Add place (Google Place Picker API)
        btPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    Intent i = builder.build(AddActivity.this);
                    startActivityForResult(i, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        // Add all information in SQLite
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                appointment.setTitle(etTitle.getText().toString());
                appointment.setContent(etContent.getText().toString());
                appointment.setTime(end_cal.getTimeInMillis());

                SQLiteDatabase db = myDBHelper.getWritableDatabase();

                ContentValues row = new ContentValues();
                row.put("title", appointment.getTitle());
                row.put("content", appointment.getContent());
                row.put("time", appointment.getTime());
                row.put("sName", appointment.getsName());
                row.put("sNumber", appointment.getsNumber());
                row.put("sId", appointment.getsId());
                row.put("latitude", appointment.getLatitude());
                row.put("longitude", appointment.getLongitude());
                row.put("place_name", appointment.getPlace_name());
                row.put("place_address", appointment.getPlace_address());

                db.insert(MyDBHelper.TABLE_NAME, null, row);
                myDBHelper.close();

                Toast.makeText(AddActivity.this, "Your schedule was saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
//            Add friends from contact
            case PHONE_PICKER_REQUEST:
                if(resultCode == RESULT_OK) {
                    Cursor cursor = getContentResolver().query(data.getData(),
                            new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.CONTACT_ID},
                            null, null, null);

                    cursor.moveToFirst();
                    String sName = cursor.getString(0);
                    String sNumber = cursor.getString(1);
                    long sId = cursor.getLong(2);
                    cursor.close();

                    Toast.makeText(AddActivity.this, sName + "  " + sNumber, Toast.LENGTH_SHORT).show();
                    btFriend.setText(sName + ", " + sNumber);

                    appointment.setsName(sName);
                    appointment.setsNumber(sNumber);
                    appointment.setsId(sId);
                }
                break;

//            select place using Google Place Picker API
            case PLACE_PICKER_REQUEST:
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(data, this);
                    LatLng latLng = place.getLatLng();

                    CharSequence cs_place_name = place.getName();
                    String place_name = cs_place_name.toString();

                    CharSequence cs_place_address = place.getAddress();
                    String place_address = cs_place_address.toString();

                    appointment.setPlace_name(place_name);
                    appointment.setPlace_address(place_address);
                    appointment.setLatitude(latLng.latitude);
                    appointment.setLongitude(latLng.longitude);

                    System.out.println("placePicker " + place_name);
                    btPlace.setText(place_name);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    /** DatePicker, TimePicker **/
//    Dialog for picking date & time
    private void setDialogOnClickListener(int buttonId, final int dialogId) {
        Button b = (Button) findViewById(buttonId);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(dialogId);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_12_DIALOG_ID:
                return new TimePickerDialog(this,
                        mTimeSetListener, mHour, mMinute, id == TIME_12_DIALOG_ID);
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case TIME_12_DIALOG_ID:
                ((TimePickerDialog) dialog).updateTime(mHour, mMinute);
                break;
            case DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
                break;
        }
    }

//    change text of button after selection
    private void updateDisplay() {
        btDate.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(engMonth[mMonth]).append(" ")
                        .append(mDay).append(", ")
                        .append(mYear).append(""));


        btTime.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(pad(mHour)).append(":")
                        .append(pad(mMinute)).append(""));
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    end_cal.set(Calendar.YEAR, mYear);
                    end_cal.set(Calendar.MONTH, mMonth);
                    end_cal.set(Calendar.DATE, mDay);
                    updateDisplay();
                }
            };

    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mHour = hourOfDay;
                    mMinute = minute;
                    end_cal.set(Calendar.HOUR_OF_DAY, mHour);
                    end_cal.set(Calendar.MINUTE, mMinute);
                    updateDisplay();
                }
            };


    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }



}
