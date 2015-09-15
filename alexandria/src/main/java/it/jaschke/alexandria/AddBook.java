package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.DownloadImage;


public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";
    private EditText ean;
    private final int LOADER_ID = 1;
    private View rootView;
    private final String EAN_CONTENT="eanContent";
    private static final String SCAN_FORMAT = "scanFormat";
    private static final String SCAN_CONTENTS = "scanContents";

    private String mScanFormat = "Format:";
    private String mScanContents = "Contents:";

    private static final int ISBN_LENGTH = 10;

    public AddBook(){
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(ean!=null) {
            outState.putString(EAN_CONTENT, ean.getText().toString());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        ean = (EditText) rootView.findViewById(R.id.ean);

        ean.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //no need
            }

            @Override
            public void afterTextChanged(Editable s) {
                /**
                 * All ISBN-13 start with 978 so I explicitly display it next to the EditText field
                 * and just handle the relevant 10 digits after it.
                 */
                String ean = s.toString();
                if(ean.length() < ISBN_LENGTH){
                    clearFields();
                    return;
                }

                //Once we have an ISBN, start a book intent
                final Intent bookIntent = new Intent(getActivity(), BookService.class);
                final String newIsbnPrefix = getString(R.string.new_isbn_prefix);
                bookIntent.putExtra(BookService.EAN, newIsbnPrefix + ean);
                bookIntent.setAction(BookService.FETCH_BOOK);
                getActivity().startService(bookIntent);
                AddBook.this.restartLoader();
            }
        });

        rootView.findViewById(R.id.scan_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Embedded barcode scanner using https://github.com/journeyapps/zxing-android-embedded/blob/master/zxing-android-embedded
                final IntentIntegrator integrator =
                        IntentIntegrator.forSupportFragment(AddBook.this);
                integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
            }
        });

        rootView.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ean.setText("");
                if(isTablet()) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.right_container, new Fragment(), Fragment.class.getSimpleName())
                            .commit();
                }
            }
        });

        rootView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean.getText().toString());
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                ean.setText("");
            }
        });

        if(savedInstanceState != null){
            ean.setText(savedInstanceState.getString(EAN_CONTENT));
            if(!ean.getText().toString().isEmpty()) {
                ean.setHint("");                                                                    // hint should only be blanked out when there is actual text being set
            }
        }

        return rootView;
    }

    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(ean.getText().length() == 0){
            return null;
        }
        final String newIsbnPrefix = getString(R.string.new_isbn_prefix);
        final String eanStr = newIsbnPrefix + ean.getText().toString();
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        if(!isTablet()) {
            String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
            ((TextView) rootView.findViewById(R.id.bookTitle)).setText(bookTitle);

            String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
            ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText(bookSubTitle);

            String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
            String[] authorsArr = authors.split(",");
            ((TextView) rootView.findViewById(R.id.authors)).setLines(authorsArr.length);
            ((TextView) rootView.findViewById(R.id.authors)).setText(authors.replace(",", "\n"));
            String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
            if (Patterns.WEB_URL.matcher(imgUrl).matches()) {
                new DownloadImage((ImageView) rootView.findViewById(R.id.bookCover)).execute(imgUrl);
                rootView.findViewById(R.id.bookCover).setVisibility(View.VISIBLE);
            }

            String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
            ((TextView) rootView.findViewById(R.id.categories)).setText(categories);
        } else {
            final Bundle args = new Bundle();
            final String newIsbnPrefix = getString(R.string.new_isbn_prefix);
            final String eanStr = newIsbnPrefix + ean.getText().toString();
            args.putString(BookDetail.EAN_KEY, eanStr);
            args.putBoolean(BookDetail.HIDE_DELETE_BUTTON_KEY, true);

            final BookDetail fragment = new BookDetail();
            fragment.setArguments(args);

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.right_container, fragment, BookDetail.class.getSimpleName())
                            .commit();
                }
            });
        }

        rootView.findViewById(R.id.save_button).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.delete_button).setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
    }

    private void clearFields(){
        ((TextView) rootView.findViewById(R.id.bookTitle)).setText("");
        ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText("");
        ((TextView) rootView.findViewById(R.id.authors)).setText("");
        ((TextView) rootView.findViewById(R.id.categories)).setText("");
        rootView.findViewById(R.id.bookCover).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.save_button).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.delete_button).setVisibility(View.INVISIBLE);
        if(isTablet() && getActivity().findViewById(R.id.fullBookTitle) != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.right_container, new Fragment(), Fragment.class.getSimpleName())
                    .commit();
            getActivity().setTitle(R.string.scan);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.scan);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final IntentResult intentResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(intentResult != null && intentResult.getContents() != null) {
            final String barcode = intentResult.getContents();
            if(isBarcodeValid(barcode)) {
                ean.setText(barcode.substring(getString(R.string.new_isbn_prefix).length()));       // don't need to deal with universal ISBN prefix 978
            } else {
                showScanFailedMsg();
            }
        }
    }

    private boolean isBarcodeValid(final String barcode) {
        final String newIsbnPrefix = getString(R.string.new_isbn_prefix);
        final int validIsbnLength = ISBN_LENGTH + newIsbnPrefix.length();
        return barcode.length() == validIsbnLength && barcode.startsWith(newIsbnPrefix);
    }

    private void showScanFailedMsg() {
        final Intent messageIntent = new Intent(MainActivity.MESSAGE_EVENT);
        final String msg = getString(R.string.scan_failed_invalid_barcode_msg);
        messageIntent.putExtra(MainActivity.MESSAGE_KEY, msg);
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext())
                .sendBroadcast(messageIntent);
    }

    private boolean isTablet() {
        return getResources().getBoolean(R.bool.isTablet);
    }

    public static class CaptureActivityAnyOrientation extends CaptureActivity {}
}
