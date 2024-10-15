package com.shafi.sbf.spinner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.io.Serializable;
import java.util.List;

public class ListDialog extends DialogFragment implements
        SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private static final String ITEMS = "items";

    private ArrayAdapter<String> listAdapter;
    private ListView listViewItems;
    private SearchableItem searchableItemListener;
    private OnSearchTextChangedListener onSearchTextChangedListener;
    private SearchView searchView;
    private String dialogTitle;
    private String positiveButtonText;
    private DialogInterface.OnClickListener positiveButtonListener;

    public ListDialog() {
        // Default constructor
    }

    public static ListDialog newInstance(List<String> items) {
        ListDialog dialog = new ListDialog();
        Bundle args = new Bundle();
        args.putSerializable(ITEMS, (Serializable) items);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            searchableItemListener = (SearchableItem) savedInstanceState.getSerializable("searchableItem");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View rootView = inflater.inflate(R.layout.searchable_list_dialog, null);
        initializeData(rootView);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(rootView);

        // Setting dialog title
        String title = (dialogTitle == null) ? "Select Item" : dialogTitle;
        alertDialogBuilder.setTitle(title);

        // Setting dialog positive button
        String positiveButton = (positiveButtonText == null) ? "CLOSE" : positiveButtonText;
        alertDialogBuilder.setPositiveButton(positiveButton, positiveButtonListener);

        return alertDialogBuilder.create();
    }

    private void initializeData(View rootView) {
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = rootView.findViewById(R.id.search);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);

        List<String> items = (List<String>) getArguments().getSerializable(ITEMS);
        listViewItems = rootView.findViewById(R.id.listItems);
        listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, items);
        listViewItems.setAdapter(listAdapter);

        listViewItems.setOnItemClickListener((parent, view, position, id) -> {
            if (searchableItemListener != null) {
                searchableItemListener.onSearchableItemClicked(listAdapter.getItem(position), position);
            }
            dismiss();
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("searchableItem", searchableItemListener);
        super.onSaveInstanceState(outState);
    }

    public void setDialogTitle(String title) {
        this.dialogTitle = title;
    }

    public void setPositiveButton(String text, DialogInterface.OnClickListener listener) {
        this.positiveButtonText = text;
        this.positiveButtonListener = listener;
    }

    public void setOnSearchableItemClickListener(SearchableItem listener) {
        this.searchableItemListener = listener;
    }

    public void setOnSearchTextChangedListener(OnSearchTextChangedListener listener) {
        this.onSearchTextChangedListener = listener;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        listAdapter.getFilter().filter(newText);
        if (onSearchTextChangedListener != null) {
            onSearchTextChangedListener.onSearchTextChanged(newText);
        }
        return true;
    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        return true;
    }

    public interface SearchableItem extends Serializable {
        void onSearchableItemClicked(String item, int position);
    }

    public interface OnSearchTextChangedListener {
        void onSearchTextChanged(String newText);
    }
}

