package com.contract.extractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import simple.contacts.Contact;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.BarcodeTest.Contents;
import com.example.BarcodeTest.QRCodeEncoder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

public class MainActivity extends ActionBarActivity {
	private ArrayList<Contact> contacts=new ArrayList<Contact>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		File root = Environment.getExternalStorageDirectory();
		Toast.makeText(this, root.toURI().toString(), Toast.LENGTH_LONG).show();
		extractContacts();
		writeContacts(root);
	}

	

	private void writeContacts(File root){
		File dir=new File(Environment.getExternalStorageDirectory()+"/extractor");
		dir.mkdir();
	
	
	FileOutputStream f;
	try {
		f = new FileOutputStream(new File(root, "extractor/contactslist.html"));
		f.write("<html><body>".getBytes());
		for (int i=0;i<contacts.size();i++){
			Contact contact=contacts.get(i);
			String html = "<div> <img height='100px' width='100px' src=\""+contact.getNumber()+".jpeg\"> "+contact.getName() + ","+contact.getNumber()+"</div>";
			f.write(html.getBytes());
			createImage(contact.getNumber());
		}
		f.write("</body></html>".getBytes());
		f.close();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
			
		
	}
	
	private void extractContacts(){
		
		ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                  String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                  String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                  if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                     Cursor pCur = cr.query(
                               ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                               null,
                               ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                               new String[]{id}, null);
                     while (pCur.moveToNext()) {
                         String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                         contacts.add(new Contact(name,phoneNo));
                         Toast.makeText(this, "Name: " + name + ", Phone No: " + phoneNo, Toast.LENGTH_SHORT).show();
                     }
                    pCur.close();
                }
            }
        }
	
	}
	
	
	private void createImage(String phoneNumber){
		
			
			FileOutputStream out;		
			int qrCodeDimention=500;
			QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(phoneNumber, null,
			        Contents.Type.PHONE, BarcodeFormat.QR_CODE.toString(), qrCodeDimention);
			//writeContacts(root);
			try {
				 Log.e("out",Environment.getExternalStorageDirectory()+"/extractor/"+phoneNumber+".jpeg");
					
				out = new FileOutputStream(Environment.getExternalStorageDirectory()+"/extractor/"+phoneNumber+".jpeg");
			   Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
			    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			} catch (WriterException e) {
			    e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch bloc
				e.printStackTrace();
			}
	
	}
}
