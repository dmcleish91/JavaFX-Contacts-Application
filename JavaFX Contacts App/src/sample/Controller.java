package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import sample.datamodel.Contact;
import sample.datamodel.ContactData;

import java.io.IOException;
import java.util.Optional;

public class Controller {

    private ContactData data;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private TableView<Contact> contactsTable;

    // Create an instance of ContactData and load it into the TableView
    public void initialize() {
        data = new ContactData();
        data.loadContacts();
        contactsTable.setItems(data.getContacts());
    }

    @FXML
    public void showAddContactDialog() {
        // Create a dialog instance and assigning the mainBorderPane as its parent
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Contact");
        dialog.initOwner(mainBorderPane.getScene().getWindow());

        // Create a FXMLLoader instance and load the data defined in the fxml file for the dialog
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("contactDialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        // Created the dialog buttons OK and CANCEL
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        // Opens the dialog and checks if the user presses OK or CANCEL
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Creates a DialogController then get the userinput from the dialogbox
            // and adds the Contact to the ContactData instance then saves the data to an XML file
            DialogController dialogController = fxmlLoader.getController();
            Contact newContact = dialogController.getNewContact();
            data.addContact(newContact);
            data.saveContacts();
        }

    }

    @FXML
    public void showEditContactDialog() {
        // We get the selected contact to edit but if nothing is selected we warn the user
        Contact selectedContact = contactsTable.getSelectionModel().getSelectedItem();
        if (selectedContact == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Contact Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select the contact you want to edit.");
            alert.showAndWait();
            return;
        }

        // If the user selected a contact then we create a dialog instance and assigning the mainBorderPane as its parent
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Contact");
        dialog.initOwner(mainBorderPane.getScene().getWindow());

        // Creates a FXMLLoader instance and load the data defined in the fxml file for the dialog
        // We are reusing the contact dialog because we can display the contact information to edit
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("contactDialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        // Creates the dialog buttons OK and CANCEL
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        DialogController dialogController = fxmlLoader.getController();
        dialogController.editContact(selectedContact);

        // Opens the dialog and shows the data to be edited and checks if the user presses OK or CANCEL
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            dialogController.updateContact(selectedContact);
            data.saveContacts();
        }
    }

    @FXML
    public void showDeleteContact() {
        // We get the contacted to be deleted and display a confirmation to the user
        // If no contact is selected a warning is displayed and we exit the method
        Contact selectedContactToDelete = contactsTable.getSelectionModel().getSelectedItem();
        if (selectedContactToDelete == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Contact Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select the contact you want to delete.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Contact");
        alert.setHeaderText(null);
        alert.setContentText("Delete the select contact: " +
                selectedContactToDelete.getFirstName() + " " + selectedContactToDelete.getLastName());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && (result.get() == ButtonType.OK)) {
            data.deleteContact(selectedContactToDelete);
            data.saveContacts();
        }
    }
}
