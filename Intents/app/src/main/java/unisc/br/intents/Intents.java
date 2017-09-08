package unisc.br.intents;

import android.Manifest;
import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Intents extends AppCompatActivity {

    static class Request {
        static class Permission {
            private static final int CAMERA = 1;
            private static final int READ_CONTACTS = 2;
            private static final int CALL_PHONE = 3;

            private static boolean allow(int[] grantResults) {
                return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            }
        }

        static class Action {
            private static final int CAMERA = 1;
            private static final int READ_CONTACTS = 2;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intents);
    }

    public void openCamera(View view) {
        if (hasPermission(permission.CAMERA, Request.Permission.CAMERA)) {
            startActivityForResult(
                    new Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                    Request.Action.CAMERA
            );
        }
    }

    public void openWeb(View view) {
        startActivity(generateIntent(
                "http://www.unisc.br",
                Intent.ACTION_VIEW
        ));
    }

    public void openMapsInStreet(View view) {
        openMaps("geo:0,0?q=Sete+Setembro,Curitiba");
    }

    public void openMapsInSpecificPosition(View view) {
        openMaps("geo:-25.443195, -49.280977");
    }

    public void openMapsWithRoute(View view) {
        String start = "-25.443195, -49.280977";
        String destination = "-25.442207, -49.278403";
        openMaps(String.format(
                "http://maps.google.com/maps?f=d&saddr=%s&daddr=%s&hl=pt",
                start,
                destination
        ));
    }

    private void openMaps(String uri) {
        startActivity(generateIntent(uri, Intent.ACTION_VIEW));
    }

    public void openContats(View view) {
        if (hasPermission(Manifest.permission.READ_CONTACTS, Request.Permission.READ_CONTACTS)) {
            startActivityForResult(
                    generateIntent("content://com.android.contacts/contacts", Intent.ACTION_PICK),
                    Request.Action.READ_CONTACTS
            );
        }
    }

    public void openCall(View view) {
        if (hasPermission(Manifest.permission.CALL_PHONE, Request.Permission.CALL_PHONE)) {
            startActivity(generateIntent("tel: 01551980445036", Intent.ACTION_CALL));
        }
    }

    private boolean hasPermission(String permissionId, int request) {
        int permission = ContextCompat.checkSelfPermission(this, permissionId);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permissionId}, request);
            return false;
        }
        return true;
    }

    private Intent generateIntent(String url, String action) {
        return new Intent(action, Uri.parse(url));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case Request.Action.READ_CONTACTS:
                showContact(data);
                break;
            case Request.Action.CAMERA:
                showImage(data);
        }
    }

    private void showImage(Intent data) {
        Bitmap photo = (Bitmap) data.getExtras().get("data");
        ImageView imageView = (ImageView) this.findViewById(R.id.imageView1);
        imageView.setImageBitmap(photo);
    }

    private void showContact(Intent data) {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(data.getData(), null, null, null, null);
            if (cursor.moveToFirst()) {
                showContactByCursor(cursor);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showContactByCursor(Cursor cursor) {
        Cursor phones = getContentResolver().query(
                Phone.CONTENT_URI,
                null,
                Phone.CONTACT_ID + " = ?",
                new String[]{getStringByCursor(cursor, Contacts._ID)},
                null
        );
        while (phones.moveToNext()) {
            setTextView(R.id.contact_number, getStringByCursor(phones, Phone.NUMBER));
            setTextView(R.id.contact_name, getStringByCursor(phones, Phone.DISPLAY_NAME));
        }
        phones.close();
    }

    private void setTextView(int id, String value) {
        ((TextView) findViewById(id)).setText(value);
    }

    private String getStringByCursor(Cursor cursor, String column) {
        return cursor.getString(cursor.getColumnIndex(column));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Request.Permission.CAMERA: {
                if (Request.Permission.allow(grantResults)) {
                    openCamera(null);
                }
                return;
            }
            case Request.Permission.CALL_PHONE: {
                if (Request.Permission.allow(grantResults)) {
                    openCall(null);
                }
                return;
            }
            case Request.Permission.READ_CONTACTS: {
                if (Request.Permission.allow(grantResults)) {
                    openContats(null);
                }
                return;
            }
        }
    }
}