/**
 * @author paul
 * controller for pathfinding algorithm selector
 */
package controller;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;

public class PathfindingAlgorithmSelectorController  extends Controller{

    @FXML
    private Button logoutBtn;

    @FXML
    private Button backBtn;

    @FXML
    private RadioButton bfsRadioButton;

    @FXML
    private RadioButton dfsRadioButton;

    @FXML
    private RadioButton aRadioButton;

    @FXML
    private Button submitBtn;

    @FXML
    private Text successText;

    ToggleGroup group;

    private int selected; // 0 is A*, 1 is bfs, 2 is dfs


    public void initialize(){

        //ensure only one dude is selected
        group = new ToggleGroup();


        // initialize the right checkbox to be selected


        // initialize the success text to be invisible
        successText.setVisible(false);

    }

    @FXML
    /**
     * @author paul
     *
     */
    public void submitBtnAction(){

        // init selected algorithm
        if(aRadioButton.selectedProperty().getValue()){
            selected = 0;
        } else if (bfsRadioButton.selectedProperty().getValue()){
            selected = 1;
        } else if (dfsRadioButton.selectedProperty().getValue()){
            selected = 2;
        } else selected = -1;

        // push



        //indicate success
        successText.setVisible(true);

    }


    @FXML
    void backBtnAction(ActionEvent event) throws Exception{
        switchScreen("view/AdminToolMenu.fxml", "Admin tool menu", backBtn);
    }

    @FXML
    void logoutBtnAction(ActionEvent event) throws Exception{
        switchScreen("view/Main.fxml", "Main menu", logoutBtn);
    }

}
