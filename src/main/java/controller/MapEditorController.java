package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.Coordinate;
import model.Node;
import service.CoordinateService;
import service.NodeService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapEditorController extends Controller{

    //ImageView Objects
    @FXML
    private ScrollPane FirstFloorScrollPane;
    @FXML
    private Slider FirstFloorSlider;
    @FXML
    private ScrollPane SecondFloorScrollPane;
    @FXML
    private Slider SecondFloorSlider;
    @FXML
    private ScrollPane ThirdFloorScrollPane;
    @FXML
    private Slider ThirdFloorSlider;
    @FXML
    private ScrollPane FourthFloorScrollPane;
    @FXML
    private Slider FourthFloorSlider;
    @FXML
    private ScrollPane FifthFloorScrollPane;
    @FXML
    private Slider FifthFloorSlider;
    @FXML
    private ScrollPane SixthFloorScrollPane;
    @FXML
    private Slider SixthFloorSlider;
    @FXML
    private ScrollPane SeventhFloorScrollPane;
    @FXML
    private Slider SeventhFloorSlider;
    //---------------------
    @FXML
    private Text MapEditorInstructionText;
    @FXML
    private TabPane FloorViewsTabPane;
    @FXML
    private VBox MapEditorVBox;

    // Back and logout buttons
    @FXML
    private Button backBtn;
    @FXML
    private Button logoutBtn;

    // Remove Node objects
    @FXML
    private TextField removeNode_searchField;
    @FXML
    private ListView<String> removeNode_searchList;
    @FXML
    private Button removeNode_searchBtn;
    @FXML
    private Button removeNode_removeBtn;
    @FXML
    private Text RemoveNodeIndicatorText;


    // Add node objects
    @FXML
    private TextField addNode_nameField;
    @FXML
    private TextField addNode_xPos;
    @FXML
    private TextField addNode_yPos;
    @FXML
    private TextField addNode_floor;
    @FXML
    private ListView<String> addNode_connectedNodesList;
    @FXML
    private Button addNode_createNodeBtn;
    @FXML
    private ListView<String> addNode_unconnectedNodesList;
    @FXML
    private Button addNode_connectToNodeBtn;

    // Edit node objects
    @FXML
    private TextField editNode_searchField;
    @FXML
    private Button editNode_searchBtn;
    @FXML
    private ListView<String> editNode_searchResultsList;
    @FXML
    private ListView<String> editNode_neighborsList;
    @FXML
    private Button editNode_removeNeighborBtn;
    @FXML
    private TextField editNode_addField;
    @FXML
    private Button editNode_addBtn;
    @FXML
    private Text AddNodeIndicatorText;

    // Map imageview and anchorpane
    @FXML
    private ImageView imageView;
    @FXML
    private AnchorPane anchorPane;

    // Images
    private Image floor4Image;

    // database helper

    // arraylist of search terms
    private ArrayList<String> searchList;
    private ArrayList<String> nodeList;
    private NodeService NS;


    public void initialize(){
        InitializeMapViews();
        InitializeIndicatorTextListeners();

        // Set the image view to populate the image
        floor4Image = new Image("file:../Resources/floor4.png");
        //floor4Image.widthProperty().bind(anchorPane.widthProperty());
        imageView = new ImageView(floor4Image);

        // init local lists
        searchList = new ArrayList<>();
        nodeList = new ArrayList<>();

        //Populate the list of all nodes
        this.NS = new NodeService();
        ArrayList<Node> allNode = new ArrayList<Node>(this.NS.getAllNodes());
        for (Node aNode : allNode) {
            this.nodeList.add(aNode.getName());
        }

//        //mouse clicked handler, send x,y data to function
//        anchorPane.setOnMouseClicked(event -> {
//            // get the coordinates
//            double x = event.getX();
//            double y = event.getY();
//            // send to function
//            mouseClicked(x,y);
//        });

    }


    // Methods for the remove node tab
    /**
     * @author Samuel Coache
     *
     * event handler for RemoveNode when the search button is pressed
     *
     */
    public void removeNode_searchBtnPressed(){
        try {
            String searchField = removeNode_searchField.getText();
            //System.out.println("searchField is: " + searchField);
            if(searchField.equals("")){
                ObservableList<String> allOList = FXCollections.observableArrayList(this.nodeList);
                removeNode_searchList.setItems(allOList);
            //} else {
                //String selectedName = (this.NS.findNodeByName(searchField)).getName();
                //System.out.println("selectName is: " + selectedName);
                //ArrayList<String> nodeName = new ArrayList<>();
                //nodeName.add(selectedName);
                //System.out.println("nodeName is: " + nodeName);
                //ObservableList<String> OList = FXCollections.observableArrayList(nodeName);
                //removeNode_searchList.setItems(OList);
            }
        }
        catch (Exception E){
            System.out.println("Searching Error");
            E.printStackTrace();
        }

    }

    /**
     * @author Samuel Coache
     * <p>
     * remove node tab: remove button event handler
     *
     */
    public void removeNode_removeBtnPressed(){
        String selectedItem = removeNode_searchList.getSelectionModel().getSelectedItem();
        System.out.println(selectedItem);
        Node selectNode = NS.findNodeByName(selectedItem);
        System.out.println(selectNode.getName());
        this.searchList.remove(selectNode.getName());
        // print out the node we made
        System.out.println(selectNode.getId());
        // print out the node from the database
        try{
            this.NS.remove(selectNode);
            RemoveNodeIndicatorText.setText("Successfully Removed Node");
            RemoveNodeIndicatorText.setFill(Color.GREEN);
        }catch(Exception e){
            RemoveNodeIndicatorText.setText("Unable to Remove Node");
            RemoveNodeIndicatorText.setFill(Color.RED);
        }

        //repopulate the search list
        ObservableList<String> OList = FXCollections.observableArrayList(this.searchList);
        removeNode_searchList.setItems(OList);
    }

    /**
     * method that populates the search results with the search query
     */
    public void removeNode_searchFieldKeyPressed(){
        // get the query from the field
        String query = removeNode_searchField.getText();
        ArrayList<String> queryList = new ArrayList<>();
        // add each query to the list
        List<Node> nodeList = this.NS.getAllNodes();
        for(Node n: nodeList){
            if (n.getName().contains(query))
                queryList.add(n.getName());
        }
        // Make the list view show the results
        removeNode_searchList.setItems(FXCollections.observableArrayList(queryList));
    }

    /**
     * @author Samuel Coache
     *
     * add node tab: connect button event handler
     *
     */
    public void addNode_connectToNodeBtnPressed(){

    }

    /**
     * @author Samuel Coache
     *
     * add node tab: create button event handler
     *
     */
    public void addNode_createNodeBtnPressed(){
        CoordinateService CS = new CoordinateService();
        float x = Float.parseFloat(addNode_xPos.getText());
        float y = Float.parseFloat(addNode_yPos.getText());
        float floor = Float.parseFloat(addNode_floor.getText());
        Coordinate addCoord = new Coordinate(x, y, 4);
        CS.persist(addCoord);
        Node newNode = new Node(addCoord, addNode_nameField.getText());
        try{
            //TODO make successful text
            NS.merge(newNode);
            AddNodeIndicatorText.setText("Successfully Added Node");
            AddNodeIndicatorText.setFill(Color.GREEN);
        }catch(Exception e){
            //TODO make warning text visible
            AddNodeIndicatorText.setText("Unable to Add Node");
            AddNodeIndicatorText.setFill(Color.RED);
        }
    }

//
//    // methods for the edit node tab
//
    public void editNode_searchBtnPressed(){
//        List<Node> list = NodesHelper.getNodes(null);
//        ArrayList<String> nameList = new ArrayList<>();
//        for(Node node: list){
//            nameList.add(node.getName());
        }
//
//        ObservableList<String> obList = FXCollections.observableArrayList(nameList);
//
//        editNode_searchResultsList.setItems(obList);
//
//        editNode_searchResultsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            Node selectedNode = NodesHelper.getNodeByName(newValue);
//            currNodes[0] = selectedNode;
//            ArrayList<Node> neighbors = EdgesHelper.getNeighbors(selectedNode);
//            ArrayList<String> neighborsS = new ArrayList<>();
//            for(Node node: neighbors){
//                neighborsS.add(node.getName());
//            }
//            ObservableList<String> nList = FXCollections.observableArrayList(neighborsS);
//            editNode_neighborsList.setItems(nList);
//        });
//
//        editNode_neighborsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            Node selectedNode = NodesHelper.getNodeByName(newValue);
//            currNodes[1] = selectedNode;
//        });
//    }
//
//
    public void editNode_removeNeighborBtnPressed(){

//        ArrayList<Edge> currEdges = edgesHelper.getEdgeByNode(currNodes[0], currNodes[1]);
//
//        for(Edge curr : currEdges){
//            edgesHelper.deleteEdge(curr);
//        }
//
//        ArrayList<Node> neighbors = EdgesHelper.getNeighbors(currNodes[0]);
//        ArrayList<String> neighborsS = new ArrayList<>();
//        for(Node node: neighbors){
//            neighborsS.add(node.getName());
//        }
//        ObservableList<String> nList = FXCollections.observableArrayList(neighborsS);
//        editNode_neighborsList.setItems(nList);

    }
//
//
    public void editNode_addBtnPressed(){

//        Node newNode = NodesHelper.getNodeByName(editNode_addField.getText());
//        if (newNode != null){
//            currNodes[0].addEdge(newNode);
//
//            ArrayList<Node> neighbors = EdgesHelper.getNeighbors(currNodes[0]);
//            ArrayList<String> neighborsS = new ArrayList<>();
//            for(Node node: neighbors){
//                neighborsS.add(node.getName());
//            }
//            ObservableList<String> nList = FXCollections.observableArrayList(neighborsS);
//            editNode_neighborsList.setItems(nList);
//        }

    }
    public void imageClicked(){

    }

    /**
     * Handles what happens when mouse is clicked
     *
     * @param x value
     * @param y value
     *
     */
    private void mouseClicked(double x, double y){

    }
    //----------------------------------Indicator Text Listeners------------------------------------
    public void InitializeIndicatorTextListeners(){
        addNode_xPos.textProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2){
                AddNodeIndicatorText.setText("");
            }
        });
        addNode_yPos.textProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2){
                AddNodeIndicatorText.setText("");
            }
        });
        addNode_floor.textProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2){
                AddNodeIndicatorText.setText("");
            }
        });
        addNode_nameField.textProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2){
                AddNodeIndicatorText.setText("");
            }
        });
        removeNode_searchField.textProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2){
                AddNodeIndicatorText.setText("");
            }
        });
    }

    //----------------------------------Sreen Changing Functions-------------------------------------
    /**
     * Back button action event handler. Opens the Admin page
     */
    public void back() throws IOException {
        switchScreen("view/AdminToolMenu.fxml", "Directory Editor", backBtn);
    }

    /**
     * Action event handler for logout button being pressed. Goes to main screen.
     */
    public void logout() throws IOException {
        switchScreen("view/Main.fxml", "Main", logoutBtn);
    }

    //----------------------------------Build Zoomable Maps----------------------------------------------
    public void InitializeMapViews(){
        //FIRST FLOOR
        FirstFloorScrollPane.prefWidthProperty().bind(FloorViewsTabPane.widthProperty());
        FirstFloorScrollPane.prefHeightProperty().bind(FloorViewsTabPane.heightProperty());
        ImageView FirstFloorImageView = new ImageView();
        Image FirstFloorMapPic = new Image("images/1_thefirstfloor.png");
        FirstFloorImageView.setImage(FirstFloorMapPic);
        FirstFloorImageView.setPreserveRatio(true);
        Group FirstFloorGroup = new Group();
        FirstFloorGroup.getChildren().add(FirstFloorImageView);
        FirstFloorScrollPane.setContent(FirstFloorGroup);
        FirstFloorScrollPane.setPannable(true);
        FirstFloorScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        FirstFloorScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        FirstFloorSlider.setMax(FirstFloorMapPic.getWidth());
        FirstFloorSlider.minProperty().bind(FloorViewsTabPane.widthProperty());
        FirstFloorImageView.fitWidthProperty().bind(FirstFloorSlider.valueProperty());
        //SECOND FLOOR
        SecondFloorScrollPane.prefWidthProperty().bind(FloorViewsTabPane.widthProperty());
        SecondFloorScrollPane.prefHeightProperty().bind(FloorViewsTabPane.heightProperty());
        ImageView SecondFloorImageView = new ImageView();
        Image SecondFloorMapPic = new Image("images/2_thesecondfloor.png");
        SecondFloorImageView.setImage(FirstFloorMapPic);
        SecondFloorImageView.setPreserveRatio(true);
        Group SecondFloorGroup = new Group();
        SecondFloorGroup.getChildren().add(SecondFloorImageView);
        SecondFloorScrollPane.setContent(SecondFloorGroup);
        SecondFloorScrollPane.setPannable(true);
        SecondFloorScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        SecondFloorScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        SecondFloorSlider.setMax(SecondFloorMapPic.getWidth());
        SecondFloorSlider.minProperty().bind(FloorViewsTabPane.widthProperty());
        SecondFloorImageView.fitWidthProperty().bind(SecondFloorSlider.valueProperty());
        //THIRD FLOOR
        ThirdFloorScrollPane.prefWidthProperty().bind(FloorViewsTabPane.widthProperty());
        ThirdFloorScrollPane.prefHeightProperty().bind(FloorViewsTabPane.heightProperty());
        ImageView ThirdFloorImageView = new ImageView();
        Image ThirdFloorMapPic = new Image("images/3_thethirdfloor.png");
        ThirdFloorImageView.setImage(ThirdFloorMapPic);
        ThirdFloorImageView.setPreserveRatio(true);
        Group ThirdFloorGroup = new Group();
        ThirdFloorGroup.getChildren().add(ThirdFloorImageView);
        ThirdFloorScrollPane.setContent(ThirdFloorGroup);
        ThirdFloorScrollPane.setPannable(true);
        ThirdFloorScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        ThirdFloorScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        ThirdFloorSlider.setMax(ThirdFloorMapPic.getWidth());
        ThirdFloorSlider.minProperty().bind(FloorViewsTabPane.widthProperty());
        ThirdFloorImageView.fitWidthProperty().bind(ThirdFloorSlider.valueProperty());
        //FOURTH FLOOR
        FourthFloorScrollPane.prefWidthProperty().bind(FloorViewsTabPane.widthProperty());
        FourthFloorScrollPane.prefHeightProperty().bind(FloorViewsTabPane.heightProperty());
        ImageView FourthFloorImageView = new ImageView();
        Image FourthFloorMapPic = new Image("images/4_thefourthfloor.png");
        FourthFloorImageView.setImage(FourthFloorMapPic);
        FourthFloorImageView.setPreserveRatio(true);
        Group FourthFloorGroup = new Group();
        FourthFloorGroup.getChildren().add(FourthFloorImageView);
        FourthFloorScrollPane.setContent(FourthFloorGroup);
        FourthFloorScrollPane.setPannable(true);
        FourthFloorScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        FourthFloorScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        FourthFloorSlider.setMax(FourthFloorMapPic.getWidth());
        FourthFloorSlider.minProperty().bind(FloorViewsTabPane.widthProperty());
        FourthFloorImageView.fitWidthProperty().bind(FourthFloorSlider.valueProperty());
        //FIFTH FLOOR
        FifthFloorScrollPane.prefWidthProperty().bind(FloorViewsTabPane.widthProperty());
        FifthFloorScrollPane.prefHeightProperty().bind(FloorViewsTabPane.heightProperty());
        ImageView FifthFloorImageView = new ImageView();
        Image FifthFloorMapPic = new Image("images/5_thefifthfloor.png");
        FifthFloorImageView.setImage(FifthFloorMapPic);
        FifthFloorImageView.setPreserveRatio(true);
        Group FifthFloorGroup = new Group();
        FifthFloorGroup.getChildren().add(FifthFloorImageView);
        FifthFloorScrollPane.setContent(FifthFloorGroup);
        FifthFloorScrollPane.setPannable(true);
        FifthFloorScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        FifthFloorScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        FifthFloorSlider.setMax(FifthFloorMapPic.getWidth());
        FifthFloorSlider.minProperty().bind(FloorViewsTabPane.widthProperty());
        FifthFloorImageView.fitWidthProperty().bind(FifthFloorSlider.valueProperty());
        //SIXTH FLOOR
        SixthFloorScrollPane.prefWidthProperty().bind(FloorViewsTabPane.widthProperty());
        SixthFloorScrollPane.prefHeightProperty().bind(FloorViewsTabPane.heightProperty());
        ImageView SixthFloorImageView = new ImageView();
        Image SixthFloorMapPic = new Image("images/6_thesixthfloor.png");
        SixthFloorImageView.setImage(SixthFloorMapPic);
        SixthFloorImageView.setPreserveRatio(true);
        Group SixthFloorGroup = new Group();
        SixthFloorGroup.getChildren().add(SixthFloorImageView);
        SixthFloorScrollPane.setContent(SixthFloorGroup);
        SixthFloorScrollPane.setPannable(true);
        SixthFloorScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        SixthFloorScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        SixthFloorSlider.setMax(SixthFloorMapPic.getWidth());
        SixthFloorSlider.minProperty().bind(FloorViewsTabPane.widthProperty());
        SixthFloorImageView.fitWidthProperty().bind(SixthFloorSlider.valueProperty());
        //SEVENTH FLOOR
        SeventhFloorScrollPane.prefWidthProperty().bind(FloorViewsTabPane.widthProperty());
        SeventhFloorScrollPane.prefHeightProperty().bind(FloorViewsTabPane.heightProperty());
        ImageView SeventhFloorImageView = new ImageView();
        Image SeventhFloorMapPic = new Image("images/7_theseventhfloor.png");
        SeventhFloorImageView.setImage(SeventhFloorMapPic);
        SeventhFloorImageView.setPreserveRatio(true);
        Group SeventhFloorGroup = new Group();
        SeventhFloorGroup.getChildren().add(SeventhFloorImageView);
        SeventhFloorScrollPane.setContent(SeventhFloorGroup);
        SeventhFloorScrollPane.setPannable(true);
        SeventhFloorScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        SeventhFloorScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        SeventhFloorSlider.setMax(SeventhFloorMapPic.getWidth());
        SeventhFloorSlider.minProperty().bind(FloorViewsTabPane.widthProperty());
        SeventhFloorImageView.fitWidthProperty().bind(SeventhFloorSlider.valueProperty());
    }
}
