package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.model.SearchResult;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Created by leroy on 24/07/15.
 */
public class SearchView implements FxmlView<SearchViewModel>, Initializable {
    public static final int MAX_RESULTS_BEFORE_SCROLLBAR = 8;
    @FXML
    private TextField searchTextField;
    @FXML
    private ListView<SearchResult> searchResultsListView;
    @InjectViewModel
    private SearchViewModel viewModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        viewModel.queryProperty().bind(searchTextField.textProperty());
        searchResultsListView.setItems(viewModel.getResults());
        searchTextField.setOnKeyReleased(event -> {
            if (event.getCode() != KeyCode.ENTER) {
                viewModel.getSearchCommand().execute();
                searchResultsListView.getSelectionModel().selectFirst();
            }
        });

        searchResultsListView.setCellFactory(param -> new ListCell<SearchResult>() {
            @Override
            protected void updateItem(SearchResult item, boolean empty) {
                setPadding(new Insets(0));
                super.updateItem(item, empty);
                if (!empty) {
                    setGraphic(viewModel.generateSearchResultRow(item));
                } else {
                    setGraphic(null);
                    setText("");
                }
            }
        });

        searchResultsListView.visibleProperty().bind(Bindings.size(viewModel.getResults()).greaterThan(0));
        searchResultsListView.managedProperty().bind(Bindings.size(viewModel.getResults()).greaterThan(0));

        viewModel.getResults().addListener((ListChangeListener<? super SearchResult>) c -> {
            if (viewModel.getResults().size() > 0) {
                searchResultsListView.setPrefHeight(Integer.min(MAX_RESULTS_BEFORE_SCROLLBAR, viewModel.getResults().size())*30 + 2);
                searchResultsListView.getScene().getWindow().setHeight(
                        Integer.min(MAX_RESULTS_BEFORE_SCROLLBAR, viewModel.getResults().size())*30 + 55);
            } else {
                searchResultsListView.getScene().getWindow().setHeight(52);
            }
        });
    }


}
