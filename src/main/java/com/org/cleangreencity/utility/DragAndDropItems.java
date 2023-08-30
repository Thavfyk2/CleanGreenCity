package com.org.cleangreencity.utility;

import com.org.cleangreencity.controller.DatabaseConnection;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.org.cleangreencity.controller.DashboardOfflineController.onLineMode;


public class DragAndDropItems {


    private static final BooleanProperty itemDroppedSuccessfully = new SimpleBooleanProperty(false);

    public DragAndDropItems(ListView<String> sourceListView, ObservableList<String> sourceList, ListView<String> targetListView, ObservableList<String> targetList) {

        sourceListView.setOnDragDetected(event -> {
            ObservableList<String> selectedItems = sourceListView.getSelectionModel().getSelectedItems();
            if (!selectedItems.isEmpty()) {
                Dragboard dragboard = sourceListView.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(selectedItems.get(0));
                dragboard.setContent(content);
            }
            event.consume();
        });


        targetListView.setOnDragOver(event -> {
            if (event.getGestureSource() != targetListView && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });


        targetListView.setOnDragDropped(event -> {
            String selectedItem = event.getDragboard().getString();
            targetList.add(selectedItem);
            itemDroppedSuccessfully.set(true);
            if (onLineMode) {
                try {
                    updateCardPosition(getListViewId(targetListView), selectedItem);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        sourceListView.setOnDragDone(event -> {
            if (itemDroppedSuccessfully.get()) {
                String selectedItem = sourceListView.getSelectionModel().getSelectedItem();
                sourceList.remove(selectedItem);
                itemDroppedSuccessfully.set(false);  // Reset for the next drag operation
            }
        });
    }

    private void updateCardPosition(int listViewId, String selectedCard) throws SQLException {


        int equalIndex = selectedCard.indexOf("-");
        int commaIndex = selectedCard.indexOf('\n', equalIndex);
        String idOfSelectedCard = selectedCard.substring(equalIndex + 1, commaIndex).trim();
        String query;

        if (listViewId == -1)

            query = "Delete FROM t_card WHERE 10 = ? OR card_id = ? ";
        else
            query = "UPDATE t_card SET position = ? WHERE card_id = ?";


        try {
            Connection connectDB = DatabaseConnection.getConnection();
            PreparedStatement updateCardPositionStatement = connectDB.prepareStatement(query);


            // Set the new value and condition
            updateCardPositionStatement.setInt(1, listViewId);  // Replace with the new value
            updateCardPositionStatement.setInt(2, Integer.parseInt(idOfSelectedCard));  // Replace with the condition value

            // Execute the update statement
            int rowsAffected = updateCardPositionStatement.executeUpdate();

            // Close the resources
            updateCardPositionStatement.close();
            connectDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public int getListViewId(ListView<String> targetListView) {

        String targetListString = String.valueOf(targetListView);

        int equalIndex = targetListString.indexOf("=");
        int commaIndex = targetListString.indexOf(',', equalIndex);
        String listName = targetListString.substring(equalIndex + 1, commaIndex).trim();

        return switch (listName) {
            case "backlogList" -> 0;
            case "inProgressList" -> 1;
            case "toTestList" -> 2;
            case "doneList" -> 3;
            default -> -1;
        };
    }
}