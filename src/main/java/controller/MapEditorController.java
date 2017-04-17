package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import model.Coordinate;
import model.Edge;
import model.Node;
import service.CoordinateService;
import service.EdgeService;
import service.NodeService;

import java.io.IOException;
import java.util.*;


public class MapEditorController extends Controller {

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
    private AutocompletionlTextField editNode_addField;
    @FXML
    private Button editNode_addBtn;
    @FXML
    private Text AddNodeIndicatorText;

    @FXML
    protected TabPane tabPane;

    // Map imageview and anchorpane
    @FXML
    private ImageView imageView;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ListView<String> disableEdge_searchResultsList;

    @FXML
    private Text node1NameText;

    @FXML
    private Text node2NameText;

    @FXML
    private Text ifDisableText;

    @FXML
    private Text ifUndoDisableText;

    @FXML
    private Button dragNode1;
    @FXML
    private Button dragNode2;
    @FXML
    private Button dragNode3;
    @FXML
    private Button dragNode4;
    @FXML
    private Button dragNode5;
    @FXML
    private Button dragNode6;
    @FXML
    private Button dragNode7;

    // Images
    private Image floor4Image;

    // arraylist of search terms
    private ArrayList<String> searchList;
    private ArrayList<String> nodeList;
    private NodeService NS;
    private EdgeService ES;

    private Node[] currNodes = new Node[2];

    private static int currFloor;

    private List<floorCircles> floorCircles;

    class floorCircles{
        int floor;
        List<Circle> circles;
        floorCircles(int floor, List<Circle> circles){
            this.floor = floor;
            this.circles = circles;
        }
    }
    public MapEditorController() {
    }

    public void initialize() {
        this.NS = new NodeService();
        this.ES = new EdgeService();
        List<Node> nodes = NS.getNodesByFloor(1);
        List<String> names = new ArrayList<>();
        for (Node n : nodes) {
            names.add(n.getName());
        }
        editNode_addField.getEntries().addAll(names);

        new ShowNodesEdgesHelper(FirstFloorScrollPane, SecondFloorScrollPane, ThirdFloorScrollPane,
                FourthFloorScrollPane, FifthFloorScrollPane, SixthFloorScrollPane,
                SeventhFloorScrollPane, FirstFloorSlider, SecondFloorSlider,
                ThirdFloorSlider, FourthFloorSlider, FifthFloorSlider, SixthFloorSlider,
                SeventhFloorSlider, FloorViewsTabPane);

        ShowNodesEdgesHelper.InitializeMapViews();
        InitializeIndicatorTextListeners();

        // init local lists
        searchList = new ArrayList<>();
        nodeList = new ArrayList<>();

        //Populate the list of all nodes
        ArrayList<Node> allNode = new ArrayList<>(this.NS.getAllNodes());
        for (Node aNode : allNode) {
            this.nodeList.add(aNode.getName());
        }

        currFloor = 1;

        ArrayList<String> nameList = new ArrayList<>();
        for (Node n : NS.getNodesByFloor(currFloor)) {
            nameList.add(n.getName());
        }
        Collections.sort(nameList, String.CASE_INSENSITIVE_ORDER);
        ObservableList<String> obList = FXCollections.observableArrayList(nameList);

        editNode_searchResultsList.setItems(obList);
        disableEdge_searchResultsList.setItems(obList);


        tabPaneListen();
        System.out.println("INITRemoveNaighboreListener:");
        removeNeighborListen();
        System.out.println("INITShowNodes:");
        List<Circle> circles = ShowNodesEdgesHelper.showNodes(currFloor);
        System.out.println("INITcirclesListen:");
        circlesListen(circles, currFloor);
        //List<Edge> Edges = ShowNodesEdgesHelper.getEdges(currFloor);
    }

    public void circlesListen(List<Circle> circles, int floor) {
        System.out.println("circlesListen:");
        final Circle[] firstCircle = new Circle[1];
        for (Circle circle : circles) {


            circle.setOnMouseClicked(event -> {
                System.out.println("Clicked on node: " + NS.find(Long.parseLong(circle.getId())).getName());
                List<String> ItemsInListView = editNode_searchResultsList.getItems();
                System.out.println(ItemsInListView);
                int i = 0;
                for (i = 0; !(ItemsInListView.get(i).equals(NS.find(Long.parseLong(circle.getId())).getName())); i++) {
                    //System.out.println(ItemsInListView.get(i));
                }
                editNode_searchResultsList.getSelectionModel().select(i);
                removeNode_searchList.getSelectionModel().select(i);
                if (firstCircle[0] == null) {
                    System.out.println("two0 is null");
                    firstCircle[0] = circle;
                    circle.fillProperty().setValue(Paint.valueOf(Color.TEAL.toString()));
                } else {
                    System.out.println("two0 is not null");
                    circle.fillProperty().setValue(Paint.valueOf(Color.TEAL.toString()));

                    Edge edge1 = new Edge(NS.find(Long.parseLong(firstCircle[0].getId())),
                            NS.find(Long.parseLong(circle.getId())), 0);
                    Edge edge2 = new Edge(NS.find(Long.parseLong(circle.getId())),
                            NS.find(Long.parseLong(firstCircle[0].getId())), 0);

                    firstCircle[0] = null;
                    ES.persist(edge1);
                    ES.persist(edge2);

                    circlesListen(ShowNodesEdgesHelper.showNodes(currFloor), currFloor);
                    return;
                }
            });

            //Listener on the Map such that when map is clicked it removes any highlights
            //also resets the value for firstCircle such that it will begin the path adding again
            ScrollPane scrolly = ShowNodesEdgesHelper.checkScroll(floor);
            Group group = (Group) scrolly.getContent();
            ImageView Map = (ImageView) group.getChildren().get(0);
            Map.setOnMouseClicked(event -> {
                System.out.println("ClickedOnMap");
                ShowNodesEdgesHelper.resetDrawnShapeColors(floor);
                firstCircle[0] = null;
            });
        }
    }

    //This function listens for a click on anything that isnt a circle.
    //Then it removes all the highlights and clears the firstCircle[0]
    public void ClickOnMapListener(int floor) {


    }


    public void tabPaneListen() {
        System.out.println("TabPaneListener");
        FloorViewsTabPane.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Tab>() {
                    @Override
                    public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
                        System.out.println("Tab Selection changed " + t.getText() + " to " + t1.getText());
                        //Update Current Floor
                        currFloor = Integer.parseInt(t1.getText().charAt(6) + "");
                        //Update EditTab Listviews with new floors data
                        ArrayList<String> nameList = new ArrayList<>();
                        for (Node n : NS.getNodesByFloor(currFloor)) {
                            nameList.add(n.getName());
                        }
                        Collections.sort(nameList, String.CASE_INSENSITIVE_ORDER);
                        ObservableList<String> obList = FXCollections.observableArrayList(nameList);
                        editNode_searchResultsList.setItems(obList);
                        removeNode_searchList.setItems(obList);
                        //Update AutoComplete suggestion of neighbors with new data
                        List<Node> nodes = NS.getNodesByFloor(currFloor);
                        List<String> names = new ArrayList<>();
                        for (Node n : nodes) {
                            names.add(n.getName());
                        }
                        editNode_addField.getEntries().clear();
                        editNode_addField.getEntries().addAll(names);

                        List<Circle> circles = ShowNodesEdgesHelper.showNodes(currFloor);
                        List<Edge> Edges = ShowNodesEdgesHelper.getEdges(currFloor);

                        circlesListen(circles, currFloor);
                    }
                }
        );


    }

//    public void dragNode(ActionEvent ae){
//        DropShadow ds = new DropShadow();
//        switch(((Control)ae.getSource()).getId()) {
//            case "dragNode1":
//                if(dragNode1.getStyle().equals("-fx-text-fill: white;")){
//                    dragNode1.setStyle("-fx-background-color: #4c7eaa;");
//                    dragNode1.setStyle("-fx-text-fill: #2f3332;");
//                    System.out.println("selected");
//                } else {
//                    dragNode1.setStyle("-fx-background-color: #00579f;");
//                    dragNode1.setStyle("-fx-text-fill: white;");
//                    System.out.println("not selected");
//                }
//                break;
//            case "dragNode2":
//                System.out.println("dragNode2");
//                break;
//            case "dragNode3":
//                System.out.println("dragNode3");
//                break;
//            case "dragNode4":
//                System.out.println("dragNode4");
//                break;
//            case "dragNode5":
//                System.out.println("dragNode5");
//                break;
//            case "dragNode6":
//                System.out.println("dragNode6");
//                break;
//            case "dragNode7":
//                System.out.println("dragNode7");
//                break;
//            default:
//        }
//    }


    public void removeNeighborListen() {
        editNode_searchResultsList.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    System.out.println("editNode_SearchResultsList Listener");
                    //Update Current Node
                    Node selectedNode = NS.findNodeByName(newValue);
                    currNodes[0] = selectedNode;
                    //Update Neighbors List
                    ObservableList<String> nList = FXCollections.observableArrayList(neighborNames(currNodes[0]));
                    editNode_neighborsList.setItems(nList);
                    //Clear old highlights
                    ShowNodesEdgesHelper.resetDrawnShapeColors(currFloor);
                    //HighLight Selected Node
                    // - get all Drawn Objects on current Map
                    // - find the ones with the same ID
                    // - If its a Circle Highlight it
                    String SelectedNodeID = NS.findNodeByName(newValue).getId().toString();
                    ScrollPane Scrolly = ShowNodesEdgesHelper.checkScroll(currFloor);
                    Group group = (Group) Scrolly.getContent();
                    List<javafx.scene.Node> DrawnObjects = group.getChildren();
                    for (int i = 1; i < DrawnObjects.size(); i++) {
                        if (DrawnObjects.get(i).getId().equals(SelectedNodeID)) {
                            try {
                                Circle circle = (Circle) DrawnObjects.get(i);
                                circle.fillProperty().setValue(Color.TEAL);
                            }
                            //found an edge instead
                            catch (Exception e) {
                            }
                        }
                    }
                });

        editNode_neighborsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("EditNode_NeighborsList Listener");
            //clear Old Highlights
            ShowNodesEdgesHelper.resetDrawnShapeColors(currFloor);
            //find start and end nodes
            Node end = NS.findNodeByName(newValue);
            Node start = NS.findNodeByName(editNode_searchResultsList.getSelectionModel().getSelectedItem());
            //Search Through Edges on Floor for one with same start/end
            String ID = null;
            List<Edge> edges = ShowNodesEdgesHelper.getEdges(currFloor);
            for (Edge e : edges) {
                if ((e.getStart().getId().equals(start.getId())) && (e.getEnd().getId().equals(end.getId()))) {
                    ID = e.getId().toString();
                }
            }
            //HighLight Selected Edge
            // - get all Drawn Objects on current Map
            // - find the ones with the same ID
            // - If its a Line, Highlight it
            ScrollPane Scrolly = ShowNodesEdgesHelper.checkScroll(currFloor);
            Group group = (Group) Scrolly.getContent();
            List<javafx.scene.Node> DrawnObjects = group.getChildren();
            for (int i = 1; i < DrawnObjects.size(); i++) {
                if (DrawnObjects.get(i).getId().equals(ID)) {
                    try {
                        //Line has been found
                        Line line = (Line) DrawnObjects.get(i);
                        line.setStroke(Color.BLUEVIOLET);
                        line.setStrokeWidth(3);
                    }
                    //Found a circle Instead
                    catch (Exception e) {
                    }
                }
            }
            //Update Current Node
            currNodes[1] = end;
        });


    }


    // Methods for the remove node tab

    /**
     * @author Samuel Coache
     * <p>
     * event handler for RemoveNode when the search button is pressed
     * Back button action event handler. Opens the Admin page
     */
    public void removeNode_searchBtnPressed() {
        try {
            String searchField = removeNode_searchField.getText();
            //System.out.println("searchField is: " + searchField);
            if (searchField.equals("")) {
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
        } catch (Exception E) {
            System.out.println("Searching Error");
            E.printStackTrace();
        }

    }

    /**
     * @author Samuel Coache
     * <p>
     * remove node tab: remove button event handler
     * Action event handler for logout button being pressed. Goes to main screen.
     */
    public void removeNode_removeBtnPressed() {
        String selectedItem = removeNode_searchList.getSelectionModel().getSelectedItem();
        System.out.println(selectedItem);
        Node selectNode = NS.findNodeByName(selectedItem);
        System.out.println(selectNode.getName());
        this.searchList.remove(selectNode.getName());
        // print out the node we made
        System.out.println(selectNode.getId());
        // print out the node from the database
        try {
            this.NS.remove(selectNode);
            RemoveNodeIndicatorText.setText("Successfully Removed Node");
            RemoveNodeIndicatorText.setFill(Color.GREEN);
        } catch (Exception e) {
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
    public void removeNode_searchFieldKeyPressed() {
        // get the query from the field
        String query = removeNode_searchField.getText();
        ArrayList<String> queryList = new ArrayList<>();
        // add each query to the list
        List<Node> nodeList = this.NS.getAllNodes();
        for (Node n : nodeList) {
            if (n.getName().contains(query))
                queryList.add(n.getName());
        }
        // Make the list view show the results
        Collections.sort(queryList, String.CASE_INSENSITIVE_ORDER);
        removeNode_searchList.setItems(FXCollections.observableArrayList(queryList));
    }

    /**
     * @author Samuel Coache
     * <p>
     * add node tab: connect button event handler
     */
    public void addNode_connectToNodeBtnPressed() {

    }

    /**
     * @author Samuel Coache
     * <p>
     * add node tab: create button event handler
     */
    public void addNode_createNodeBtnPressed() {
        CoordinateService CS = new CoordinateService();
        float x = Float.parseFloat(addNode_xPos.getText());
        float y = Float.parseFloat(addNode_yPos.getText());
        float floor = Float.parseFloat(addNode_floor.getText());
        Coordinate addCoord = new Coordinate(x, y, 4);
        CS.persist(addCoord);
        Node newNode = new Node(addNode_nameField.getText(), addCoord);
        try {
            NS.merge(newNode);
            AddNodeIndicatorText.setText("Successfully Added Node");
            AddNodeIndicatorText.setFill(Color.GREEN);
        } catch (Exception e) {
            AddNodeIndicatorText.setText("Unable to Add Node");
            AddNodeIndicatorText.setFill(Color.RED);
        }
    }

    //    // methods for the edit node tab
//
//    public void editNode_searchBtnPressed() {
//    }

    public void editNode_removeNeighborBtnPressed() {
        Node start = NS.findNodeByName(editNode_searchResultsList.getSelectionModel().getSelectedItem());
        Node end = NS.findNodeByName(editNode_neighborsList.getSelectionModel().getSelectedItem());
        List<Edge> currEdges = ES.findByNodes(start, end);
        for (Edge curr : currEdges) {
            ES.remove(curr);
        }

        ObservableList<String> nList = FXCollections.observableArrayList(neighborNames(currNodes[0]));
        editNode_neighborsList.setItems(nList);

        List<Circle> circles = ShowNodesEdgesHelper.showNodes(currFloor);
        circlesListen(circles, currFloor);
    }

    private List<String> neighborNames(Node node) {
        Set<Node> neighbors = NS.neighbors(node.getId());
        System.out.println("currNode: " + node.getId());
        List<String> neighborsS = new ArrayList<>();
        for (Node n : neighbors) {
            if (!Objects.equals(n.getId(), node.getId())) {
                neighborsS.add(n.getName());
            }
        }
        Collections.sort(neighborsS, String.CASE_INSENSITIVE_ORDER);
        return neighborsS;
    }


    public void editNode_addBtnPressed() {

        Node newNode = NS.findNodeByName(editNode_addField.getText());
        if (newNode != null) {
            EdgeService es = new EdgeService();
            es.persist(new Edge(currNodes[0], newNode, 0));
            es.persist(new Edge(newNode, currNodes[0], 0));

            ObservableList<String> nList = FXCollections.observableArrayList(neighborNames(currNodes[0]));
            editNode_neighborsList.setItems(nList);
        }
        List<Circle> circles = ShowNodesEdgesHelper.showNodes(currFloor);
        circlesListen(circles, currFloor);
    }

    public void HandleEditNodes_NeighborsListClicked() {
    }

    public void disableEdgeSelectedNodeListen() {
//        disableEdge_searchResultsList.getSelectionModel().selectedItemProperty()
//                .addListener((observable, oldValue, newValue) -> {
//                    Node selectedNode = NS.findNodeByName(newValue);
//
//                    currNodes[0] = selectedNode;


    }

    public void Node1ButtonPressed() {
        NodeService selectedNS = new NodeService();
        //selectedNS.findNodeByName(disableEdge_searchResultsList.getSelectionModel().getSelectedItem().toString());

        node1NameText.setText(disableEdge_searchResultsList.getSelectionModel().getSelectedItem().toString());

    }

    public void Node2ButtonPressed() {
        NodeService selectedNS = new NodeService();
        //selectedNS.findNodeByName(disableEdge_searchResultsList.getSelectionModel().getSelectedItem().toString());

        node2NameText.setText(disableEdge_searchResultsList.getSelectionModel().getSelectedItem().toString());
    }

    public void DisableEdgeButtonPressed() {
        Node node1 = NS.findNodeByName(node1NameText.getText());
        Node node2 = NS.findNodeByName(node2NameText.getText());
        EdgeService es = new EdgeService();
        List<Edge> selectedEdges = es.findByNodes(node1, node2);

        for (Edge curr : selectedEdges) {
            es.disableEdge(curr);
            System.out.println("Disabled : " + curr.getStart().getName() + " " + curr.getEnd().getName());
        }
        System.out.println("successful");
        ifDisableText.setText("Disable Successful!");
    }

    public void UndoDisableEdgeButtonPressed() {

        Node node1 = NS.findNodeByName(node1NameText.getText());
        Node node2 = NS.findNodeByName(node2NameText.getText());
        EdgeService es = new EdgeService();
        List<Edge> selectedEdges = es.findByNodes(node1, node2);

        for (Edge curr : selectedEdges) {
            es.ableEdge(curr);
            System.out.println("Undo disable : " + curr.getStart().getName() + " " + curr.getEnd().getName());
        }
        System.out.println("successful");
        ifUndoDisableText.setText("Undo Successful!");
    }

    //----------------------------------Indicator Text Listeners------------------------------------

    public void InitializeIndicatorTextListeners() {
        addNode_xPos.textProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2) {
                AddNodeIndicatorText.setText("");
            }
        });
        addNode_yPos.textProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2) {
                AddNodeIndicatorText.setText("");
            }
        });
        addNode_floor.textProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2) {
                AddNodeIndicatorText.setText("");
            }
        });
        addNode_nameField.textProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2) {
                AddNodeIndicatorText.setText("");
            }
        });
        removeNode_searchField.textProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2) {
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

    public static int getCurrFloor(){
        return currFloor;
    }
}


