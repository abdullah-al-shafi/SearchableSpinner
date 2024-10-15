package com.shafi.sbf.spinner;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchableSpinner extends androidx.appcompat.widget.AppCompatSpinner implements View.OnTouchListener,
        ListDialog.SearchableItem {

    public static final int NO_ITEM_SELECTED = -1;
    private Context context;
    private List<String> items;
    private ListDialog listDialog;
    private boolean isDirty;
    private ArrayAdapter<String> arrayAdapter;
    private String hintText;
    private boolean isInitializing;

    public SearchableSpinner(Context context) {
        super(context);
        this.context = context;
        initialize();
    }

    public SearchableSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.MySpinner);
        hintText = attributes.getString(R.styleable.MySpinner_hint);
        attributes.recycle();
        initialize();
    }

    public SearchableSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initialize();
    }

    private void initialize() {
        items = new ArrayList<>();
        listDialog = ListDialog.newInstance(items);
        listDialog.setOnSearchableItemClickListener(this);
        setOnTouchListener(this);

        arrayAdapter = (ArrayAdapter<String>) getAdapter();
        if (!TextUtils.isEmpty(hintText)) {
            ArrayAdapter<String> hintAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, new String[]{hintText});
            isInitializing = true;
            setAdapter(hintAdapter);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (arrayAdapter != null) {
                // Populate items from adapter
                items.clear();
                for (int i = 0; i < arrayAdapter.getCount(); i++) {
                    items.add(arrayAdapter.getItem(i));
                }

                listDialog.show(scanForActivity(context).getFragmentManager(), "SearchableSpinnerDialog");
            }
        }
        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        arrayAdapter = (ArrayAdapter<String>) adapter;
        if (!isDirty && !TextUtils.isEmpty(hintText)) {
            ArrayAdapter<String> hintAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, new String[]{hintText});
            super.setAdapter(hintAdapter);
        } else {
            super.setAdapter(adapter);
        }
    }

    @Override
    public void onSearchableItemClicked(String item, int position) {
        setSelection(items.indexOf(item));
        if (!isDirty) {
            isDirty = true;
            setAdapter(arrayAdapter);
            setSelection(items.indexOf(item));
        }
    }

    private Activity scanForActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return scanForActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    @Override
    public int getSelectedItemPosition() {
        if (!TextUtils.isEmpty(hintText) && !isDirty) {
            return NO_ITEM_SELECTED;
        } else {
            return super.getSelectedItemPosition();
        }
    }

    @Override
    public Object getSelectedItem() {
        if (!TextUtils.isEmpty(hintText) && !isDirty) {
            return null;
        } else {
            return super.getSelectedItem();
        }
    }

    public void setDialogTitle(String title) {
        listDialog.setDialogTitle(title);
    }

    public void setPositiveButtonText(String text, DialogInterface.OnClickListener listener) {
        listDialog.setPositiveButton(text, listener);
    }

    public void setOnSearchTextChangedListener(ListDialog.OnSearchTextChangedListener listener) {
        listDialog.setOnSearchTextChangedListener(listener);
    }
}


