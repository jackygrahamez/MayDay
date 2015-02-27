package com.mayday.md.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

import com.mayday.md.MainActivity;
import com.mayday.md.R;
import com.mayday.md.WizardActivity;
import com.mayday.md.adapter.PageItemAdapter;
import com.mayday.md.common.AppConstants;
import com.mayday.md.common.AppUtil;
import com.mayday.md.common.ApplicationSettings;
import com.mayday.md.common.ContactEditTexts;
import com.mayday.md.common.MyTagHandler;
import com.mayday.md.data.PBDatabase;
import com.mayday.md.model.Page;
import com.mayday.md.model.PageItem;
import com.mayday.md.model.SMSSettings;

import static android.content.Intent.ACTION_GET_CONTENT;
import static android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;

/**
 * Created by aoe on 12/11/13.
 */
public class SetupContactsFragment extends Fragment {

    private static final int PICK_CONTACT_REQUEST_ID_1 = 101, PICK_CONTACT_REQUEST_ID_2 = 102, PICK_CONTACT_REQUEST_ID_3 = 103;

    private ContactEditTexts contactEditTexts;

    private static final String PAGE_ID = "page_id";
    private static final String PARENT_ACTIVITY = "parent_activity";
    private Activity activity;

    DisplayMetrics metrics;

    TextView tvTitle, tvContent, tvIntro, tvWarning;
    Button bAction;
    ListView lvItems;
    LinearLayout llWarning;

    Page currentPage;
    PageItemAdapter pageItemAdapter;

    EditText etContact1, etContact2, etContact3;
    ImageButton ibContact1, ibContact2, ibContact3;

    public static SetupContactsFragment newInstance(String pageId, int parentActivity) {
        SetupContactsFragment f = new SetupContactsFragment();
        Bundle args = new Bundle();
        args.putString(PAGE_ID, pageId);
        args.putInt(PARENT_ACTIVITY, parentActivity);
        f.setArguments(args);
        return(f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_type_interactive_contacts, container, false);

        tvTitle = (TextView) view.findViewById(R.id.fragment_title);
        tvIntro = (TextView) view.findViewById(R.id.fragment_intro);
        tvContent = (TextView) view.findViewById(R.id.fragment_contents);

        bAction = (Button) view.findViewById(R.id.fragment_action);
        bAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(">>>>", "action button pressed");
                SMSSettings newSMSSettings = getSMSSettingsFromView();

                SMSSettings.saveContacts(activity, newSMSSettings);
                displaySettings(newSMSSettings);

                String pageId = currentPage.getAction().get(0).getLink();
                int parentActivity = getArguments().getInt(PARENT_ACTIVITY);
                Intent i;

                if(parentActivity == AppConstants.FROM_WIZARD_ACTIVITY){
                    i = new Intent(activity, WizardActivity.class);
                } else{
                    String confirmation = (currentPage.getAction().get(0).getConfirmation() == null)
                            ? AppConstants.DEFAULT_CONFIRMATION_MESSAGE
                            : currentPage.getAction().get(0).getConfirmation();
                    Toast.makeText(activity, confirmation, Toast.LENGTH_SHORT).show();

                    i = new Intent(activity, MainActivity.class);
                }
                i.putExtra("page_id", pageId);
                startActivity(i);

                if(parentActivity == AppConstants.FROM_MAIN_ACTIVITY){
                    activity.finish();
                }
            }
        });


        lvItems = (ListView) view.findViewById(R.id.fragment_item_list);

        llWarning = (LinearLayout) view.findViewById(R.id.ll_fragment_warning);
        tvWarning  = (TextView) view.findViewById(R.id.fragment_warning);

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PageItem selectedItem = (PageItem) parent.getItemAtPosition(position);

                String pageId = selectedItem.getLink();
                int parentActivity = getArguments().getInt(PARENT_ACTIVITY);
                Intent i;

                if(parentActivity == AppConstants.FROM_WIZARD_ACTIVITY){
                    i = new Intent(activity, WizardActivity.class);
                } else{
                    i = new Intent(activity, MainActivity.class);
                }
                i.putExtra("page_id", pageId);
                startActivity(i);

            }
        });

        etContact1 = (EditText) view.findViewById(R.id.contact_edit_text1);
        etContact2 = (EditText) view.findViewById(R.id.contact_edit_text2);
        etContact3 = (EditText) view.findViewById(R.id.contact_edit_text3);

        ibContact1 = (ImageButton) view.findViewById(R.id.contact_picker_button1);
        ibContact2 = (ImageButton) view.findViewById(R.id.contact_picker_button2);
        ibContact3 = (ImageButton) view.findViewById(R.id.contact_picker_button3);

        ibContact1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int wizardState = ApplicationSettings.getWizardState(getActivity());
//            	 if(wizardState != AppConstants.WIZARD_FLAG_HOME_READY){
                AppConstants.IS_BACK_BUTTON_PRESSED = true;
                AppConstants.IS_BACK_BUTTON_PRESSED = true;
//            	 }
                launchContactPicker(v, PICK_CONTACT_REQUEST_ID_1);
            }
        });

        ibContact2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int wizardState = ApplicationSettings.getWizardState(getActivity());
//            	 if(wizardState != AppConstants.WIZARD_FLAG_HOME_READY){
                AppConstants.IS_BACK_BUTTON_PRESSED = true;
                AppConstants.IS_BACK_BUTTON_PRESSED = true;
//            	 }
                launchContactPicker(v, PICK_CONTACT_REQUEST_ID_2);
            }
        });

        ibContact3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int wizardState = ApplicationSettings.getWizardState(getActivity());
//            	 if(wizardState != AppConstants.WIZARD_FLAG_HOME_READY){
                AppConstants.IS_BACK_BUTTON_PRESSED = true;
                AppConstants.IS_BACK_BUTTON_PRESSED = true;
//            	 }
                launchContactPicker(v, PICK_CONTACT_REQUEST_ID_3);
            }
        });

//        initializeViews(view);

        return view;
    }

    public void launchContactPicker(View view, int requestCode) {
        Intent contactPickerIntent = new Intent(ACTION_GET_CONTENT);
        contactPickerIntent.setType(CONTENT_ITEM_TYPE);
        startActivityForResult(contactPickerIntent, requestCode);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = getActivity();
        if (activity != null) {
            metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            contactEditTexts = new ContactEditTexts(getChildFragmentManager(), bAction, activity, etContact1, etContact2, etContact3);

            SMSSettings currentSettings = SMSSettings.retrieve(activity);
            if(currentSettings.isConfigured()) {
                displaySettings(currentSettings);
            }
            bAction.setEnabled(contactEditTexts.hasAtleastOneValidPhoneNumber());

            String pageId = getArguments().getString(PAGE_ID);
            String selectedLang = ApplicationSettings.getSelectedLanguage(activity);

            PBDatabase dbInstance = new PBDatabase(activity);
            dbInstance.open();
            currentPage = dbInstance.retrievePage(pageId, selectedLang);
            dbInstance.close();

            tvTitle.setText(currentPage.getTitle());

            if(currentPage.getContent() == null)
                tvContent.setVisibility(View.GONE);
            else
                tvContent.setText(Html.fromHtml(currentPage.getContent(), null, new MyTagHandler()));

            if(currentPage.getIntroduction() == null)
                tvIntro.setVisibility(View.GONE);
            else
                tvIntro.setText(currentPage.getIntroduction());

            if(currentPage.getWarning() == null)
                llWarning.setVisibility(View.GONE);
            else
                tvWarning.setText(currentPage.getWarning());

            bAction.setText(currentPage.getAction().get(0).getTitle());

            pageItemAdapter = new PageItemAdapter(activity, null);
            lvItems.setAdapter(pageItemAdapter);
            pageItemAdapter.setData(currentPage.getItems());

            AppUtil.updateImages(true, currentPage.getContent(), activity, metrics, tvContent, AppConstants.IMAGE_INLINE);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("","onActivityResult SetupContactsFragments");
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("ContactPickerFragment", "onActivityResult requestCode "+requestCode);
        Log.e("ContactPickerFragment", "onActivityResult resultCode "+resultCode);
        Log.e("ContactPickerFragment", "onActivityResult data "+data);

        if (resultCode == Activity.RESULT_OK) {

            ContentResolver cr = getActivity().getContentResolver();
            Cursor cur = cr.query(data.getData(), null, null, null, null);
            String phone = "";
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts._ID));
                    Log.e("ContactPickerFragment", "onActivityResult id "+id);
                    String name = cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Log.e("ContactPickerFragment", "onActivityResult name "+name);

                    Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone._ID + " = " + id, null, null);
                    while (phones.moveToNext() && (phone == null || "".equals(phone))) {
                        phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    phones.close();

                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        //Query phone here.  Covered next






                        /*
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                new String[]{id}, null);
                        Log.e("WizardActivity", "onActivityResult pCur "+pCur);
                        while (pCur.moveToNext()) {
                            // Do something with phones
                            phone = pCur.getString(
                                    pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            Log.e("ContactPickerFragment", "onActivityResult phone "+phone);
                        }
                        pCur.close();
                        */
                    }
                }
            }

            Log.e("ContactPickerFragment", "onActivityResult phone "+phone);
            phone = phone==null?"":phone.trim();

            switch(requestCode) {
                case PICK_CONTACT_REQUEST_ID_1:
                    etContact1.setText(phone);
                    break;
                case PICK_CONTACT_REQUEST_ID_2:
                    etContact2.setText(phone);
                    break;
                case PICK_CONTACT_REQUEST_ID_3:
                    etContact3.setText(phone);
                    break;
                default:
                    return;
            }
        }
    }

    private void displaySettings(SMSSettings settings) {
        contactEditTexts.maskPhoneNumbers();
    }


    private SMSSettings getSMSSettingsFromView() {
        List<String> phoneNumbers = contactEditTexts.getPhoneNumbers();
        return new SMSSettings(phoneNumbers);
    }
}