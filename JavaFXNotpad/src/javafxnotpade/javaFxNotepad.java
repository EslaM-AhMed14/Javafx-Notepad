package javaFxNotepad;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;


public class javaFxNotepad extends Application {

    private  MenuBar bar;
    private  Menu file;
    private  MenuItem inew ;
    private  MenuItem iopen ;
    private  MenuItem isave ;
    private  MenuItem iexit ;
    private  Menu edit;
    private  MenuItem iundo;
    private  MenuItem icut;
    private  MenuItem icopy;
    private  MenuItem ipast;
    private  MenuItem idelete;
    private  MenuItem iselectall;
    private  Menu help;
    private  MenuItem iabout;
    private  MenuItem ijava;
    private  TextArea txt;
    private  String stack;
    private  int position;
    
    
    
    
    public javaFxNotepad(){
        
    
        bar = new MenuBar();
       
        file = new Menu("File");
        
        inew  = new MenuItem("New");
        inew.setAccelerator(KeyCombination.keyCombination("ctrl+n"));
        iopen = new MenuItem("Open") ;
        iopen.setAccelerator(KeyCombination.keyCombination("ctrl+o"));
        isave = new MenuItem("Save");
        isave.setAccelerator(KeyCombination.keyCombination("ctrl+s"));
        iexit = new MenuItem("Exit");
        iexit.setAccelerator(KeyCombination.keyCombination("ctrl+e"));
        
        file.getItems().addAll(inew ,iopen , isave , new SeparatorMenuItem(), iexit );
        
        
        edit = new Menu("Edit");
        iundo = new MenuItem("Undo");
        icut = new MenuItem("Cut");
        icut.setAccelerator(KeyCombination.keyCombination("ctrl+x"));
        icopy = new MenuItem("Copy");
        icopy.setAccelerator(KeyCombination.keyCombination("ctrl+c"));
        ipast  = new MenuItem("paste");
        ipast.setAccelerator(KeyCombination.keyCombination("ctrl+v"));
        idelete = new MenuItem("Delete");
        idelete.setAccelerator(KeyCombination.keyCombination("ctrl+d"));
        iselectall = new MenuItem("Sellect All");
        iselectall.setAccelerator(KeyCombination.keyCombination("ctrl+a"));
        
        
        edit.getItems().addAll(iundo, new SeparatorMenuItem() ,icut , icopy, ipast , idelete ,new SeparatorMenuItem(), iselectall );
        
        
        help = new Menu("Help");
        iabout = new MenuItem("About NotePad");
        ijava = new MenuItem ("compile Java code");
        help.getItems().addAll(iabout , ijava);
    
        bar.getMenus().addAll(file, edit , help);
        
        txt = new TextArea() ;
        
    }
    
    @Override
    public void start(Stage primaryStage) {
    
        BorderPane pane = new BorderPane();
        pane.setTop(bar);
        pane.setCenter(txt);
    
        
        //------------ file menu fun  -----------
     
        inew.setOnAction(new EventHandler<ActionEvent> (){
            @Override
            public void handle(ActionEvent event) {
                   txt.setText("");
            }

        });
        
        iopen.setOnAction(new EventHandler<ActionEvent> (){
            @Override
            public void handle(ActionEvent event) {
                
                File file = chooseFileToRead(primaryStage);
                readFile(file);
            }
 
        });

         
            //save contant of textarea in .txt file
         isave.setOnAction(new EventHandler<ActionEvent> () {

            @Override
            public void handle(ActionEvent event) {
             
                File file = chooseFileToSave(primaryStage);
                saveFile(txt.getText() , file);
                
                
            }
        });
         
         
        iexit.setOnAction((e)->{
        
          showDialogToSaveOrExite(primaryStage);
        
        });
        
        
        //------------ edit fun  ----------------
        
           //func  for undo change
        iundo.setOnAction(new EventHandler<ActionEvent> (){
            
            @Override
            public void handle(ActionEvent event) {
                undo();
            }
  
        });
        
        
            // fun  for cut the selected words 
        icut.setOnAction(new EventHandler<ActionEvent> (){
            @Override
            public void handle(ActionEvent event) {
                //stack.add(txt.getSelectedText());
                stack = txt.getSelectedText();
                position = txt.getSelection().getStart();
                txt.cut();
            }
        
        });
            // fun for copy the select words
        icopy.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                   
                   txt.copy();
            }
            
        });
        
        // fun : to paste the copy or cut words 
       ipast.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                paste();
            }
 
       });
        
              // func to delete selected word
        idelete.setOnAction(new EventHandler<ActionEvent>(){
            @Override
              public void handle(ActionEvent event) {
                //stack.add(txt.getSelectedText());
                stack = txt.getSelectedText();
                position = txt.getSelection().getStart();
                txt.setText(txt.getText().replace(txt.getSelectedText(),""));
            }
        
        });
       
       
            //select All word in text Area
        iselectall.setOnAction(new EventHandler<ActionEvent> () {
            @Override
            public void handle(ActionEvent event) {
              txt.selectAll();
             
            }
            
        });
        
        
        // --------- help menu  fun -------------
        iabout.setOnAction(new EventHandler<ActionEvent> (){
            @Override
            public void handle(ActionEvent event) {
                showDialog("Help" , "Eslam Ahmed");
            }
            
        });
        
        ijava.setOnAction(e->{
        
            compile();
        });
   
        Scene scene = new Scene(pane, 800, 400);
        
        primaryStage.setTitle("Fx Notepad");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    
    //---------------------My functions----------------------------
    
     private File chooseFileToRead(Stage primaryStage){
         
            FileChooser fil_chooser = new FileChooser();
            File file = fil_chooser.showOpenDialog(primaryStage);

            return file;
     }
    
    private File chooseFileToSave(Stage primaryStage){
    
            FileChooser fileChooser = new FileChooser();

                //Set extension filter
            FileChooser.ExtensionFilter extFilter = 
                    new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);

               //Show save file dialog
            File file = fileChooser.showSaveDialog(primaryStage);

            return file;
            
    }
    private void readFile(File file){
        
            try (BufferedReader br = new BufferedReader(new FileReader(file)) ) {
                String line ;

                while ((line = br.readLine()) != null) {
                   
                     txt.appendText(line);
                     txt.appendText("\n");
                }
                 //txt.setText();
            } catch (IOException e) {
               e.getStackTrace();
            }

    }
    
    // to save contant of textarea to txt file
    private void saveFile(String s ,File file){
            try {
                FileWriter fileWriter;

                fileWriter = new FileWriter(file);
                fileWriter.write(s);
                fileWriter.close();
            } catch (IOException e) {
                e.getStackTrace();
            }
    }
    
     // past cut or copy data in the cursor position
    private void paste(){
    
        String clipboardText = Clipboard.getSystemClipboard().getString()+ " ";
        int caretPosition = txt.getCaretPosition();
        txt.insertText(caretPosition, clipboardText);

    }

     // func to return the last edit
    private void undo(){
       
       // int caretPosition = txt.getCaretPosition();
        txt.insertText( position, stack);
    }


    // method for display dialog take  window title , content of dialog 
    private void  showDialog(String title , String cont){
     
        Dialog<String> dialog = new Dialog<String>();
        dialog.setTitle(title);
        ButtonType type = new ButtonType("Ok", ButtonData.OK_DONE);
        dialog.setContentText(cont);
        dialog.getDialogPane().getButtonTypes().add(type);
        dialog.showAndWait();
    }
    
    // func : to save text in file or close the Note
     private void  showDialogToSaveOrExite(Stage s){
     
        Label secondLabel = new Label("So you want save file..");

        Button exit = new Button("Exit");
        Button save = new Button("Save");

        GridPane pane = new GridPane();
        pane.addColumn(5, save);
        pane.setHgap(15);
        pane.addColumn(9, exit);

        BorderPane secondaryLayout = new BorderPane();
        secondaryLayout.setBottom(pane);
        secondaryLayout.setCenter(secondLabel);
      

        Scene secondScene = new Scene(secondaryLayout, 300, 150);

				
	Stage newWindow = new Stage();
        newWindow.initModality(Modality.APPLICATION_MODAL);
        newWindow.initStyle(StageStyle.UTILITY);
	newWindow.setTitle("Exit");
	newWindow.setScene(secondScene);
        

	
        newWindow.setX(s.getX() + 200);
	newWindow.setY(s.getY() + 100);
        
         exit.setOnAction(e->{
            
            newWindow.close(); 
            s.close();
           
          });
         
         save.setOnAction(e->{
         
            File file = chooseFileToSave(s);
            saveFile(txt.getText() , file);
            newWindow.close(); 
            s.close();
         
         });

        newWindow.show();
              
    }

     
     private void compile (){
     
        String code = txt.getText();

        // Save the code to a temporary file
        Path tempFilePath;
        try {
            tempFilePath = Files.createTempFile("DynamicCompilation", ".java");
            Files.write(tempFilePath, code.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Compile the code
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int compilationResult = compiler.run(null, null, null, tempFilePath.toString());
        

        if (compilationResult == 0) {
            // Compilation successful
            showDialog("Comile" , "Compilation successful.");
            
            try {
                // Load the compiled class
                CustomClassLoader classLoader = new CustomClassLoader();
                Class<?> compiledClass = classLoader.loadClass(tempFilePath.getFileName().toString().replace(".java", ""));

                // Instantiate and execute a method from the compiled class
                Object instance = compiledClass.getDeclaredConstructor().newInstance();
                compiledClass.getMethod("execute").invoke(instance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Compilation failed
            showDialog("Comile" , "Compilation failed.");
           
        }
    }

    public static class CustomClassLoader extends ClassLoader {
        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException {
            try {
                byte[] bytes = Files.readAllBytes(Paths.get(name + ".class"));
                return defineClass(null, bytes, 0, bytes.length);
            } catch (IOException e) {
                throw new ClassNotFoundException(name, e);
            }
        }
    }
         
     
    public static void main(String[] args) {
        launch(args);
    }
    
}
